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
 * Analizzatore lessicale del compilatore.
 * <p>
 * Legge il file sorgente carattere per carattere e produce una sequenza di Token.
 * Gestisce l'eliminazione dei caratteri di spaziatura e il riconoscimento
 * di numeri, identificatori, parole chiave e simboli.
 * </p>
 */
public class Scanner {

    // Caratteri da ignorare (spazi, tabulazioni, a capo)
    private static final String SKIP_CHARS = " \t\n\r";

    private final String fileName;
    private final PushbackReader buffer;
    private int riga;
    private final Map<String, TokenType> keywordsMap;

    /**
     * Inizializza lo scanner aprendo il file e preparando la mappa delle parole chiave.
     *
     * @param fileName Percorso del file sorgente.
     * @throws FileNotFoundException Se il file non esiste.
     */
    public Scanner(String fileName) throws FileNotFoundException {
        this.buffer = new PushbackReader(new FileReader(fileName));
        this.fileName = fileName;
        this.riga = 1;

        // Popolo la mappa delle parole riservate per distinguerle dagli identificatori
        this.keywordsMap = new HashMap<>();
        this.keywordsMap.put("print", TokenType.PRINT);
        this.keywordsMap.put("int", TokenType.TYINT);
        this.keywordsMap.put("float", TokenType.TYFLOAT);
    }

    /**
     * Restituisce la riga corrente per la gestione degli errori.
     */
    public int getRiga() {
        return riga;
    }

    /**
     * Chiude il buffer di lettura per liberare le risorse.
     */
    public void close() {
        try {
            buffer.close();
        } catch (IOException e) {
            System.err.println("Errore nella chiusura del file: " + e.getMessage());
        }
    }

    /**
     * Analizza il prossimo token dal flusso di input.
     * <p>
     * Itera per saltare gli spazi bianchi e identifica il tipo di token in base
     * al primo carattere significativo incontrato.
     * </p>
     *
     * @return Il prossimo Token valido.
     * @throws LexicalException Se incontra caratteri non validi.
     */
    public Token nextToken() throws LexicalException {
        try {
            int next;

            // Itero per consumare i caratteri di skip (spazi) finché non trovo qualcosa di utile
            while (true) {
                next = buffer.read();

                if (next == -1) {
                    return new Token(TokenType.EOF, riga);
                }

                char c = (char) next;

                // Controllo se il carattere è tra quelli da saltare
                if (SKIP_CHARS.indexOf(c) != -1) {
                    if (c == '\n') {
                        riga++; // Incremento il contatore righe se vado a capo
                    }
                } else {
                    break; // Trovato un carattere significativo, esco dal ciclo
                }
            }

            char c = (char) next;

            // Se è una cifra, avvio la scansione di un numero
            if (Character.isDigit(c)) {
                buffer.unread(next); // Rimetto il carattere nel buffer per scanNumber
                return scanNumber();
            }
            // Se è una lettera, avvio la scansione di un identificatore o parola chiave
            else if (Character.isLetter(c)) {
                buffer.unread(next);
                return scanId();
            }

            // Gestisco i simboli singoli tramite uno switch
            switch (c) {
                case ';': return new Token(TokenType.SEMI, riga);
                case '/': return new Token(TokenType.DIVIDE, riga);
                case '*': return new Token(TokenType.TIMES, riga);
                case '-': return new Token(TokenType.MINUS, riga);
                case '+': return new Token(TokenType.PLUS, riga);
                case '=': return new Token(TokenType.ASSIGN, riga);
                // Aggiungo la gestione delle parentesi tonde
                case '(': return new Token(TokenType.LPAREN, riga);
                case ')': return new Token(TokenType.RPAREN, riga);
                default:
                    throw new LexicalException("Carattere non riconosciuto alla riga " + riga + ": " + c);
            }

        } catch (IOException e) {
            throw new LexicalException("Errore di I/O durante la scansione: " + e.getMessage());
        }
    }

    /**
     * Esegue la scansione di un numero intero o float.
     */
    private Token scanNumber() throws LexicalException {
        StringBuilder sb = new StringBuilder();
        int next;

        try {
            // Itero per leggere la parte intera del numero
            while (true) {
                next = buffer.read();
                if (next == -1) break;
                char c = (char) next;
                if (Character.isDigit(c)) {
                    sb.append(c);
                } else {
                    break;
                }
            }

            // Verifico se c'è il punto decimale per trattarlo come float
            if (next != -1 && (char) next == '.') {
                sb.append('.');
                int decimalDigits = 0;

                // Itero per leggere la parte decimale
                while (true) {
                    next = buffer.read();
                    if (next == -1) break;
                    char c = (char) next;
                    if (Character.isDigit(c)) {
                        decimalDigits++;
                        // Controllo di precisione massima (opzionale ma realistico)
                        if (decimalDigits > 5) {
                            throw new LexicalException("Errore Lessicale alla riga " + riga + ": I numeri float non possono avere più di 5 cifre decimali.");
                        }
                        sb.append(c);
                    } else {
                        break;
                    }
                }

                // Controllo che ci sia almeno una cifra dopo il punto
                if (decimalDigits == 0) {
                    throw new LexicalException("Errore Lessicale alla riga " + riga + ": Formato float non valido (attese cifre decimali).");
                }

                if (next != -1) buffer.unread(next);
                return new Token(TokenType.FLOAT, riga, sb.toString());

            } else {
                // Se non c'è il punto, è un intero
                if (next != -1) buffer.unread(next);
                return new Token(TokenType.INT, riga, sb.toString());
            }

        } catch (IOException e) {
            throw new LexicalException("Errore di I/O durante la lettura del numero: " + e.getMessage());
        }
    }

    /**
     * Esegue la scansione di un identificatore o parola chiave.
     */
    private Token scanId() throws LexicalException {
        StringBuilder sb = new StringBuilder();
        int next;

        try {
            // Itero per leggere lettere o numeri (un ID può contenere numeri ma non iniziare con essi)
            while (true) {
                next = buffer.read();
                if (next == -1) break;
                char c = (char) next;
                if (Character.isLetterOrDigit(c)) {
                    sb.append(c);
                } else {
                    break;
                }
            }

            if (next != -1) buffer.unread(next);

            String word = sb.toString();

            // Controllo se la parola letta è una parola chiave riservata
            if (keywordsMap.containsKey(word)) {
                return new Token(keywordsMap.get(word), riga);
            } else {
                return new Token(TokenType.ID, riga, word);
            }

        } catch (IOException e) {
            throw new LexicalException("Errore di I/O durante la scansione dell'identificatore: " + e.getMessage());
        }
    }
}