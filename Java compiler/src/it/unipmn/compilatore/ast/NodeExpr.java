package it.unipmn.compilatore.ast;

/**
 * Classe astratta base per tutte le espressioni.
 * <p>
 * Le espressioni sono costrutti che, quando valutati, restituiscono un valore
 * (es. operazioni binarie, costanti, dereferenziazione di variabili).
 * </p>
 */
public abstract class NodeExpr extends NodeAST {

    /**
     * Costruttore per le espressioni.
     * @param riga Il numero di riga dell'espressione.
     */
    public NodeExpr(int riga) {
        // Passo il riferimento di riga alla classe radice
        super(riga);
    }
}