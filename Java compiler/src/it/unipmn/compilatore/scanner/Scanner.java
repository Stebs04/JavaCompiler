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
 * La classe Scanner implementa l'analizzatore lessicale del compilatore.
 * <p>
 * Il suo compito è leggere il file sorgente carattere per carattere, eliminare
 * le sequenze non significative (spazi, commenti) e raggruppare i caratteri
 * in unità logiche dette Token (es. numeri, identificatori, parole chiave).
 * </p>
 */
public class Scanner {

    // Caratteri di spaziatura (whitespace) da ignorare durante il parsing.
    private static final String SKIP_CHARS = " \t\n\r";

    // Nome del file sorgente, utile per contestualizzare gli errori.
    private final String fileName;

    // Buffer di lettura che supporta l'operazione di 'unread', essenziale per
    // gestire il lookahead (guardare avanti) necessario per identificare numeri e parole.
    private final PushbackReader buffer;

    // Contatore della riga corrente per il tracciamento della posizione nel file.
    private int riga;

    // Mappa per la risoluzione efficiente delle parole chiave (Keywords).
    // Associa la stringa della parola chiave (es. "print") al suo TokenType.
    private final Map<String, TokenType> keywordsMap;

    /**
     * Costruisce un nuovo Scanner per l'analisi lessicale.
     * <p>
     * Inizializza il buffer di lettura sul file specificato e popola la struttura dati
     * per il riconoscimento delle parole riservate del linguaggio.
     * </p>
     *
     * @param fileName Il percorso del file sorgente da analizzare.
     * @throws FileNotFoundException Se il file specificato non viene trovato o non è accessibile.
     */
    public Scanner(String fileName) throws FileNotFoundException {
        // Utilizziamo il pattern Decorator: avvolgiamo il FileReader in un PushbackReader
        // per ottenere la funzionalità di "tornare indietro" di un carattere.
        this.buffer = new PushbackReader(new FileReader(fileName));
        this.fileName = fileName;
        this.riga = 1; // La numerazione delle righe parte da 1

        // Inizializzazione della mappa delle parole chiave.
        // Usiamo una HashMap per garantire tempi di accesso costanti O(1) durante il controllo.
        this.keywordsMap = new HashMap<>();
        this.keywordsMap.put("print", TokenType.PRINT);
        this.keywordsMap.put("int", TokenType.TYINT);
        this.keywordsMap.put("float", TokenType.TYFLOAT);
    }

    /**
     * Restituisce il numero della riga corrente che lo scanner sta analizzando.
     * Utile per la segnalazione precisa degli errori.
     *
     * @return Il numero di riga.
     */
    public int getRiga() {
        return riga;
    }

    /**
     * Chiude il buffer di lettura liberando le risorse di sistema associate al file.
     */
    public void close() {
        try {
            buffer.close();
        } catch (IOException e) {
            System.err.println("Errore nella chiusura del file: " + e.getMessage());
        }
    }

    /**
     * Analizza il flusso di input e restituisce il prossimo Token valido.
     * <p>
     * Il metodo esegue un ciclo per consumare i caratteri di spaziatura e,
     * una volta trovato un carattere significativo, delega l'analisi ai metodi specifici
     * (per numeri o identificatori) oppure riconosce direttamente i simboli operatori.
     * </p>
     *
     * @return Il prossimo Token identificato.
     * @throws LexicalException Se viene incontrato un carattere non valido o malformato.
     */
    public Token nextToken() throws LexicalException {
        try {
            int next;

            // Ciclo principale per consumare i caratteri di skip (spazi, tab, a capo)
            while (true) {
                next = buffer.read();

                // Caso A: Fine del file (EOF)
                if (next == -1) {
                    return new Token(TokenType.EOF, riga);
                }

                char c = (char) next;

                // Caso B: Verifica se è un carattere da saltare
                if (SKIP_CHARS.indexOf(c) != -1) {
                    // Gestione specifica del newline per mantenere corretto il conteggio righe
                    if (c == '\n') {
                        riga++;
                    }
                    // Continua il ciclo while per leggere il prossimo carattere
                } else {
                    // Trovato un carattere significativo: esco dal ciclo di skip
                    break;
                }
            }

            // A questo punto 'next' contiene il primo carattere del nuovo token
            char c = (char) next;

            // DISPATCHER: Decido come analizzare in base al tipo di carattere

            // 1. Se è una cifra -> Analisi Numerica (Interi o Float)
            if (Character.isDigit(c)) {
                buffer.unread(next); // Rimetto il carattere nel buffer per analizzarlo nel metodo dedicato
                return scanNumber();
            }

            // 2. Se è una lettera -> Analisi Identificatori o Parole Chiave
            else if (Character.isLetter(c)) {
                buffer.unread(next); // Rimetto il carattere nel buffer
                return scanId();
            }

            // 3. Se è un simbolo -> Riconoscimento Operatori e Delimitatori
            switch (c) {
                case ';': return new Token(TokenType.SEMI, riga);
                case '/': return new Token(TokenType.DIVIDE, riga);
                case '*': return new Token(TokenType.TIMES, riga);
                case '-': return new Token(TokenType.MINUS, riga);
                case '+': return new Token(TokenType.PLUS, riga);
                case '=': return new Token(TokenType.ASSIGN, riga);
                default:
                    // Se il carattere non rientra in nessuno dei casi precedenti, è illegale nel linguaggio
                    throw new LexicalException("Carattere non riconosciuto alla riga " + riga + ": " + c);
            }

        } catch (IOException e) {
            throw new LexicalException("Errore di I/O durante la scansione: " + e.getMessage());
        }
    }

    /**
     * Metodo helper per la scansione dei numeri (INT e FLOAT).
     * <p>
     * Implementa la logica per distinguere tra interi e decimali.
     * Gestisce il vincolo sintattico che impone un massimo di 5 cifre decimali per i float.
     * </p>
     *
     * @return Un Token di tipo INT o FLOAT con il valore letto.
     * @throws LexicalException Se il formato numerico viola le specifiche (es. troppe cifre decimali).
     */
    private Token scanNumber() throws LexicalException {
        StringBuilder sb = new StringBuilder();
        int next;

        try {
            // FASE 1: Scansione della parte intera
            while (true) {
                next = buffer.read();

                if (next == -1) break;

                char c = (char) next;

                if (Character.isDigit(c)) {
                    sb.append(c);
                } else {
                    // Trovato un carattere non cifra: fine della parte intera.
                    // Usciamo per analizzare se è un punto o altro.
                    break;
                }
            }

            // FASE 2: Analisi eventuale parte decimale
            if (next != -1 && (char) next == '.') {

                // --- GESTIONE FLOAT ---
                sb.append('.');

                int decimalDigits = 0; // Contatore cifre decimali

                // Ciclo di lettura cifre decimali
                while (true) {
                    next = buffer.read();

                    if (next == -1) break;

                    char c = (char) next;

                    if (Character.isDigit(c)) {
                        decimalDigits++;

                        // Controllo semantico rigoroso sulle specifiche AC (max 5 cifre)
                        if (decimalDigits > 5) {
                            throw new LexicalException("Errore Lessicale alla riga " + riga + ": I numeri float non possono avere più di 5 cifre decimali.");
                        }
                        sb.append(c);
                    } else {
                        break; // Fine del numero float
                    }
                }

                // Un float deve avere almeno una cifra dopo il punto (es. "3." non è valido)
                if (decimalDigits == 0) {
                    throw new LexicalException("Errore Lessicale alla riga " + riga + ": Formato float non valido (attese cifre decimali).");
                }

                // Gestione Lookahead: Rimetto nel buffer l'ultimo carattere letto (che non faceva parte del numero)
                if (next != -1) {
                    buffer.unread(next);
                }

                return new Token(TokenType.FLOAT, riga, sb.toString());

            } else {

                // --- GESTIONE INT ---
                // Non era un punto, quindi è un intero normale.

                // Gestione Lookahead: Rimetto nel buffer il carattere che ha interrotto la sequenza di cifre
                if (next != -1) {
                    buffer.unread(next);
                }

                return new Token(TokenType.INT, riga, sb.toString());
            }

        } catch (IOException e) {
            throw new LexicalException("Errore di I/O durante la lettura del numero: " + e.getMessage());
        }
    }

    /**
     * Metodo helper per la scansione di Identificatori e Parole Chiave.
     * <p>
     * Implementa la regola del "Longest Match" per leggere l'intera parola alfanumerica.
     * Successivamente consulta la tabella delle parole chiave (keywordsMap) per determinare
     * se il token è una parola riservata (es. print) o un identificatore utente (ID).
     * </p>
     *
     * @return Un Token ID (con valore) o un Token parola chiave (senza valore).
     * @throws LexicalException In caso di errori di lettura I/O.
     */
    private Token scanId() throws LexicalException {
        StringBuilder sb = new StringBuilder();
        int next;

        try {
            // Ciclo di lettura: accumula caratteri finché sono validi per un identificatore
            while (true) {
                next = buffer.read();

                if (next == -1) break;

                char c = (char) next;

                // Specifica: un ID può contenere lettere e cifre (purché inizi con lettera, già verificato)
                if (Character.isLetterOrDigit(c)) {
                    sb.append(c);
                } else {
                    // Carattere separatore trovato: fine della parola.
                    break;
                }
            }

            // Gestione Lookahead: Rimetto nel buffer il carattere separatore
            if (next != -1) {
                buffer.unread(next);
            }

            String word = sb.toString();

            // Controllo nella mappa se la parola estratta è una parola riservata
            if (keywordsMap.containsKey(word)) {
                // È una parola chiave (es. "print", "float", "int")
                TokenType type = keywordsMap.get(word);
                return new Token(type, riga);
            } else {
                // Non è riservata, quindi è un identificatore di variabile
                return new Token(TokenType.ID, riga, word);
            }

        } catch (IOException e) {
            throw new LexicalException("Errore di I/O durante la scansione dell'identificatore: " + e.getMessage());
        }
    }
}