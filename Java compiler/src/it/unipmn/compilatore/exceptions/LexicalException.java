package it.unipmn.compilatore.exceptions;

/**
 * Rappresenta un'eccezione che si verifica durante la fase di analisi lessicale (Scanner).
 * <p>
 * Questa eccezione viene sollevata quando lo Scanner incontra un carattere o una sequenza
 * di caratteri che non corrisponde a nessun pattern valido definito per i token del linguaggio.
 * </p>
 */
public class LexicalException extends IllegalArgumentException {

    /**
     * Costruisce una nuova LexicalException con un messaggio di dettaglio specifico.
     *
     * @param message Una descrizione dell'errore, che dovrebbe includere il motivo del fallimento
     * (es. "Carattere illegale") e, se possibile, il numero di riga.
     */
    public LexicalException(String message) {
        super(message);
    }
}