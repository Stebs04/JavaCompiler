package it.unipmn.compilatore.token;

import it.unipmn.compilatore.exceptions.LexicalException;
import org.junit.platform.commons.function.Try;

/**
 * La classe {@code Token} rappresenta l'unità lessicale minima identificata dallo Scanner durante
 * l'analisi del codice sorgente.
 * <p>
 * Ogni token memorizza informazioni fondamentali per le fasi successive della compilazione:
 * <ul>
 * <li>Il tipo del token (definito nell'enumerazione {@link TokenType}).</li>
 * <li>La riga del file sorgente in cui il token è stato trovato (utile per la segnalazione errori).</li>
 * <li>L'eventuale valore semantico associato (es. il valore numerico per costanti o il nome per identificatori).</li>
 * </ul>
 * La classe è progettata per essere immutabile.
 */
public class Token {

    private final int riga;
    private final TokenType type;
    private final String val;

    /**
     * Costruttore principale per la creazione di un Token completo.
     * Viene utilizzato per token che possiedono un valore semantico, come identificatori (ID)
     * o costanti numeriche (INT, FLOAT).
     *
     * @param type Il tipo del token (non può essere null).
     * @param riga Il numero di riga nel file sorgente dove inizia il token.
     * @param val  Il valore testuale associato al token (es. "100", "pippo").
     * @throws LexicalException Se viene passato un tipo nullo (errore interno dello scanner).
     */
    public Token(TokenType type, int riga, String val) {
        //Controllo di sicurezza
        if(type == null){
            throw new LexicalException("Il tipo non deve essere nullo");
        }
        this.type = type;
        this.val = val;
        this.riga = riga;
    }

    /**
     * Costruttore semplificato per token che non necessitano di un valore associato.
     * Viene utilizzato per parole chiave (es. PRINT), operatori (es. PLUS) e delimitatori (es. SEMI),
     * dove il tipo stesso è sufficiente a identificare il token.
     *
     * @param type Il tipo del token.
     * @param riga Il numero di riga nel file sorgente.
     */
    public Token(TokenType type, int riga){
        this(type, riga, null);
    }

    /**
     * Restituisce il numero di riga in cui appare il token.
     *
     * @return Il numero della riga.
     */
    public int getRiga() {
        return riga;
    }

    /**
     * Restituisce la tipologia del token.
     *
     * @return L'elemento dell'enum {@link TokenType} corrispondente.
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Restituisce il valore semantico del token, se presente.
     *
     * @return La stringa rappresentante il valore (es. "3.14"), oppure {@code null} se il token non ha valore.
     */
    public String getVal() {
        return val;
    }

    /**
     * Restituisce una rappresentazione testuale del token, formattata secondo le specifiche
     * del corso per facilitare il debugging.
     * Formato: &lt;TIPO, r:RIGA, valore&gt; oppure &lt;TIPO,r:RIGA&gt;
     *
     * @return La stringa formattata del token.
     */
    @Override
    public String toString() {
        if (val != null) {
            return "<" + type + ",r:" + riga + "," + val + ">";
        }
        return "<" + type + ",r:" + riga + ">";
    }
}