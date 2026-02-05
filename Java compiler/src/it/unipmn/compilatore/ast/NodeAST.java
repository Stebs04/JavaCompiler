package it.unipmn.compilatore.ast;

/**
 * Classe astratta radice per tutti i nodi dell'Abstract Syntax Tree (AST).
 * <p>
 * Garantisce che ogni nodo, sia esso un'istruzione o un'espressione,
 * mantenga il riferimento alla riga del file sorgente originale.
 * Questo è fondamentale per segnalare errori semantici precisi all'utente.
 * </p>
 */
public abstract class NodeAST {

    // Campo final per garantire l'immutabilità della posizione
    private final int riga;

    /**
     * Costruttore base.
     * @param riga Il numero di riga nel file sorgente.
     */
    public NodeAST(int riga) {
        this.riga = riga;
    }

    /**
     * Restituisce la riga di definizione del nodo.
     */
    public int getRiga() {
        return riga;
    }
}