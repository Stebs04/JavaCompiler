package it.unipmn.compilatore.token;

import it.unipmn.compilatore.exceptions.LexicalException;

/**
 * Classe che rappresenta un Token, ovvero l'unità logica minima 
 * riconosciuta dallo Scanner leggendo il codice sorgente.
 * Ogni token memorizza il suo tipo, la riga in cui è stato trovato 
 * e un eventuale valore testuale (utile per numeri e nomi di variabili).
 */
public class Token {

    // La riga del file in cui lo scanner ha trovato questo token
    private final int riga;
    // Il tipo del token (es. ID, INT, PLUS) definito nell'enumerazione
    private final TokenType type;
    // Il testo originale estratto (es. "3.14" o "somma")
    private final String val;

    /**
     * Costruttore completo per creare un Token che possiede un valore.
     * Lo uso per i numeri e gli identificatori (variabili) dove il testo letto è importante.
     * @param type Il tipo del token.
     * @param riga Il numero di riga.
     * @param val Il valore testuale associato.
     * @throws LexicalException Se il tipo passato è nullo.
     */
    public Token(TokenType type, int riga, String val) {
        // Verifico per sicurezza che il tipo non sia nullo
        if(type == null) {
            throw new LexicalException("Il tipo non deve essere nullo");
        }
        
        // Salvo tutte le informazioni che descrivono questo token
        this.type = type;
        this.val = val;
        this.riga = riga;
    }

    /**
     * Costruttore semplificato per creare un Token senza un valore specifico.
     * Lo uso per i simboli (come ; + -) e le parole chiave (come print), 
     * perché il loro tipo basta già a identificarli completamente.
     * @param type Il tipo del token.
     * @param riga Il numero di riga.
     */
    public Token(TokenType type, int riga) {
        // Richiamo il costruttore principale passando null come valore testuale
        this(type, riga, null);
    }

    /**
     * Restituisce il numero di riga del token.
     * @return Il numero della riga.
     */
    public int getRiga() {
        return riga;
    }

    /**
     * Restituisce la categoria a cui appartiene il token.
     * @return Il tipo (TokenType).
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Restituisce il testo esatto estratto per questo token, se esiste.
     * @return La stringa del valore, oppure null se non serve.
     */
    public String getVal() {
        return val;
    }

    /**
     * Crea una stringa testuale che rappresenta il token.
     * Serve molto durante le fasi di debug per stampare a video cosa ha letto lo scanner.
     */
    @Override
    public String toString() {
        // Controllo se il token ha un valore salvato
        if (val != null) {
            // Restituisco la versione completa con tipo, riga e valore
            return "<" + type + ",r:" + riga + "," + val + ">";
        }
        // Altrimenti restituisco solo il tipo e la riga
        return "<" + type + ",r:" + riga + ">";
    }
}