package it.unipmn.compilatore.exceptions;

/**
 * Classe che rappresenta un errore sintattico o semantico.
 * Viene utilizzata principalmente dal Parser o dal TypeChecker per interrompere 
 * la compilazione quando il codice sorgente non rispetta le regole grammaticali 
 * o di tipo del linguaggio (es. parentesi mancanti o tipi incompatibili).
 */
public class SyntacticException extends IllegalArgumentException {
	
    private static final long serialVersionUID = 2539087378908483358L;

    /**
     * Costruttore dell'eccezione sintattica.
     * @param message Il messaggio testuale che descrive l'errore trovato (es. cosa ci si aspettava e in quale riga).
     */
    public SyntacticException(String message) {
        // Passo il messaggio di errore alla superclasse per poterne tenere traccia e stamparlo a video
        super(message);
    }
}