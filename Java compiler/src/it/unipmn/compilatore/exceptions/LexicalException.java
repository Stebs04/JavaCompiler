package it.unipmn.compilatore.exceptions;

/**
 * Classe che rappresenta un errore durante l'analisi lessicale (fase di Scanner).
 * Viene utilizzata quando lo scanner legge un carattere o una stringa
 * che non appartiene alle regole del nostro linguaggio.
 */
public class LexicalException extends IllegalArgumentException {

    private static final long serialVersionUID = 4440627002147218816L;

    /**
     * Costruttore dell'eccezione lessicale.
     * @param message Il messaggio testuale che descrive l'errore trovato (es. riga e carattere sconosciuto).
     */
    public LexicalException(String message) {
        // Passo il messaggio di errore alla superclasse per gestirne la memorizzazione e la stampa a video
        super(message);
    }
}