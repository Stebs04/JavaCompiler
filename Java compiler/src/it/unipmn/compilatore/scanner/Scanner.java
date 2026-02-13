package it.unipmn.compilatore.scanner;

import it.unipmn.compilatore.exceptions.LexicalException;
import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe che implementa l'analizzatore lessicale (Scanner).
 * Legge il file di testo sorgente carattere per carattere e raggruppa 
 * i caratteri in entità logiche chiamate Token (parole chiave, numeri, simboli).
 * Mantiene un log delle operazioni per il debug.
 */
public class Scanner {

    // Definisco i caratteri di spaziatura che lo scanner deve ignorare
    private static final String SKIP_CHARS = " \t\n\r";

    private final String fileName;
    // Uso un PushbackReader per poter "rimettere dentro" i caratteri letti in eccesso
    private final PushbackReader buffer;
    private int riga;
    private final Map<String, TokenType> keywordsMap;
    // StringBuilder per accumulare la storia dei token letti ed eventuali errori
    private StringBuilder log;

    /**
     * Costruttore dello scanner.
     * Prepara il file per la lettura e inizializza il dizionario delle parole riservate.
     * @param fileName Il percorso del file di testo da compilare.
     * @throws FileNotFoundException Se il file specificato non viene trovato.
     */
    public Scanner(String fileName) throws FileNotFoundException {
        this.buffer = new PushbackReader(new FileReader(fileName));
        this.fileName = fileName;
        // Inizializzo il contatore delle righe partendo dalla prima
        this.riga = 1;
        this.log = new StringBuilder();

        // Popolo la mappa associando ogni stringa riservata al suo tipo di Token corrispondente
        this.keywordsMap = new HashMap<>();
        this.keywordsMap.put("print", TokenType.PRINT);
        this.keywordsMap.put("int", TokenType.TYINT);
        this.keywordsMap.put("float", TokenType.TYFLOAT);
        
        log.append("Scanner inizializzato sul file: ").append(fileName).append("\n");
    }

    /**
     * Restituisce il log delle operazioni lessicali.
     * @return La stringa con i log.
     */
    public String getLog() {
        return log.toString();
    }

    /**
     * Restituisce il numero della riga attualmente in lettura.
     * @return Il numero di riga.
     */
    public int getRiga() {
        return riga;
    }

    /**
     * Chiude il flusso di lettura del file liberando la memoria.
     */
    public void close() {
        try {
            buffer.close();
            log.append("Scanner chiuso correttamente.\n");
        } catch (IOException e) {
            log.append("Errore nella chiusura del file.\n");
            System.err.println("Errore nella chiusura del file: " + e.getMessage());
        }
    }

    /**
     * Estrae e restituisce il prossimo Token valido dal file.
     * Salta automaticamente tutti gli spazi bianchi e riconosce la categoria 
     * del Token guardando il primo carattere utile.
     * @return L'oggetto Token costruito.
     * @throws LexicalException Se viene letto un carattere non appartenente al linguaggio.
     */
    public Token nextToken() throws LexicalException {
        try {
            int next;

            // Itero per consumare tutti gli spazi bianchi e gli a capo prima del prossimo token
            while (true) {
                next = buffer.read();

                if (next == -1) {
                    log.append("Raggiunta fine file (EOF).\n");
                    // Restituisco il token speciale End Of File se il file è terminato
                    return new Token(TokenType.EOF, riga);
                }

                char c = (char) next;

                // Verifico se il carattere letto fa parte di quelli da saltare
                if (SKIP_CHARS.indexOf(c) != -1) {
                    if (c == '\n') {
                        // Incremento il contatore quando incontro un carattere di a capo
                        riga++; 
                    }
                } else {
                    // Ho trovato un carattere significativo, interrompo la lettura degli spazi
                    break; 
                }
            }

            char c = (char) next;

            if (Character.isDigit(c)) {
                // Rimetto il primo numero nel buffer per farlo elaborare per intero da scanNumber
                buffer.unread(next); 
                return scanNumber();
            }
            else if (Character.isLetter(c)) {
                // Rimetto la prima lettera nel buffer per delegare la costruzione a scanId
                buffer.unread(next);
                return scanId();
            }

            // Associo direttamente i simboli composti da un solo carattere al loro Token
            Token t = null;
            switch (c) {
                case ';': t = new Token(TokenType.SEMI, riga); break;
                case '/': t = new Token(TokenType.DIVIDE, riga); break;
                case '*': t = new Token(TokenType.TIMES, riga); break;
                case '-': t = new Token(TokenType.MINUS, riga); break;
                case '+': t = new Token(TokenType.PLUS, riga); break;
                case '=': t = new Token(TokenType.ASSIGN, riga); break;
                case '(': t = new Token(TokenType.LPAREN, riga); break;
                case ')': t = new Token(TokenType.RPAREN, riga); break;
                default:
                    String msg = "Carattere non riconosciuto alla riga " + riga + ": " + c;
                    log.append("ERRORE LESSICALE: ").append(msg).append("\n");
                    throw new LexicalException(msg);
            }
            
            log.append("Letto simbolo: ").append(t).append("\n");
            return t;

        } catch (IOException e) {
            log.append("Errore I/O critico durante la scansione.\n");
            throw new LexicalException("Errore di I/O durante la scansione: " + e.getMessage());
        }
    }

    /**
     * Costruisce un token di tipo numerico, distinguendo tra interi e decimali.
     * @return Il Token numerico (INT o FLOAT).
     * @throws LexicalException Se il formato del numero decimale non è corretto.
     */
    private Token scanNumber() throws LexicalException {
        // Uso StringBuilder per accumulare progressivamente le cifre del numero
        StringBuilder sb = new StringBuilder();
        int next;

        try {
            // Itero per estrarre tutte le cifre che compongono la parte intera del numero
            while (true) {
                next = buffer.read();
                if (next == -1) break;
                
                char c = (char) next;
                if (Character.isDigit(c)) {
                    // Aggiungo la cifra alla stringa in costruzione
                    sb.append(c);
                } else {
                    // Non è più una cifra, esco dal ciclo
                    break;
                }
            }

            Token t;
            // Verifico se il numero ha una virgola (punto), il che lo rende un float
            if (next != -1 && (char) next == '.') {
                sb.append('.');
                int decimalDigits = 0;

                // Itero per leggere tutte le cifre della parte decimale
                while (true) {
                    next = buffer.read();
                    if (next == -1) break;
                    
                    char c = (char) next;
                    if (Character.isDigit(c)) {
                        decimalDigits++;
                        
                        // Limito la precisione massima a 5 cifre decimali
                        if (decimalDigits > 5) {
                            String msg = "I numeri float non possono avere più di 5 cifre decimali.";
                            log.append("ERRORE: ").append(msg).append("\n");
                            throw new LexicalException("Errore Lessicale alla riga " + riga + ": " + msg);
                        }
                        sb.append(c);
                    } else {
                        break;
                    }
                }

                if (decimalDigits == 0) {
                    String msg = "Formato float non valido (attese cifre decimali).";
                    log.append("ERRORE: ").append(msg).append("\n");
                    throw new LexicalException("Errore Lessicale alla riga " + riga + ": " + msg);
                }

                // Rimetto nel buffer l'ultimo carattere letto perché non fa parte del numero
                if (next != -1) buffer.unread(next);
                t = new Token(TokenType.FLOAT, riga, sb.toString());

            } else {
                // Rimetto nel buffer l'ultimo carattere e restituisco il numero come intero
                if (next != -1) buffer.unread(next);
                t = new Token(TokenType.INT, riga, sb.toString());
            }

            log.append("Letto numero: ").append(t).append("\n");
            return t;

        } catch (IOException e) {
            throw new LexicalException("Errore di I/O durante la lettura del numero: " + e.getMessage());
        }
    }

    /**
     * Costruisce un token testuale analizzando se si tratta di una variabile o di una parola chiave.
     * @return Il Token testuale (ID o parola chiave).
     */
    private Token scanId() throws LexicalException {
        StringBuilder sb = new StringBuilder();
        int next;

        try {
            // Itero per estrarre lettere o numeri che formano l'identificatore
            while (true) {
                next = buffer.read();
                if (next == -1) break;
                
                char c = (char) next;
                if (Character.isLetterOrDigit(c)) {
                    sb.append(c);
                } else {
                    // Il nome è terminato, esco dal ciclo
                    break;
                }
            }

            // Rimetto nel buffer il carattere che ha fatto interrompere il ciclo
            if (next != -1) buffer.unread(next);

            // Converto i caratteri accumulati in una parola finale
            String word = sb.toString();
            Token t;

            // Interrogo la mappa per scoprire se la parola appena letta ha un significato speciale
            if (keywordsMap.containsKey(word)) {
                t = new Token(keywordsMap.get(word), riga);
                log.append("Letta keyword: ").append(t).append("\n");
            } else {
                // Altrimenti, la considero come un normale nome di variabile
                t = new Token(TokenType.ID, riga, word);
                log.append("Letto identificatore: ").append(t).append("\n");
            }
            return t;

        } catch (IOException e) {
            throw new LexicalException("Errore di I/O durante la scansione dell'identificatore: " + e.getMessage());
        }
    }
}