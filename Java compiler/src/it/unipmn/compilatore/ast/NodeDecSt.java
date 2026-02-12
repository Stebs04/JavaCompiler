package it.unipmn.compilatore.ast;

/**
 * Classe astratta che raggruppa sia le dichiarazioni che gli statement.
 * <p>
 * Serve come categoria generale per i nodi che non restituiscono un valore diretto
 * come fanno le espressioni.
 * </p>
 */
public abstract class NodeDecSt extends NodeAST {

    /**
     * Costruttore che propaga la riga al costruttore di NodeAST.
     * @param riga Il numero di riga nel file sorgente.
     */
    public NodeDecSt(int riga) {
        // Passo la riga alla superclasse NodeAST
        super(riga);
    }
}