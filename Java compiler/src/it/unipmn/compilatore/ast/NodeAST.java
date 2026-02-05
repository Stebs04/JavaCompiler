package it.unipmn.compilatore.ast;

/**
 * Classe astratta radice della gerarchia dell'Abstract Syntax Tree (AST).
 * <p>
 * Rappresenta un generico nodo dell'albero sintattico. Mantiene le informazioni
 * comuni a tutti i nodi, come la posizione nel codice sorgente (riga),
 * essenziale per la segnalazione degli errori semantici.
 * </p>
 */
public abstract class NodeAST {
    private final int riga;

    /**
     * Costruttore base per tutti i nodi dell'AST.
     * * @param riga Il numero di riga nel file sorgente dove inizia questo costrutto.
     */
    public NodeAST(int riga) {
        this.riga = riga;
    }

    /**
     * Restituisce la riga di definizione del nodo.
     * @return Il numero di riga.
     */
    public int getRiga() {
        return riga;
    }
}
