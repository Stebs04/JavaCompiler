package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta un'istruzione di assegnamento.
 * <p>
 * Associa un valore calcolato da un'espressione a una variabile (identificatore).
 * Esempio: "x = a + b;"
 * </p>
 */
public class NodeAssign extends NodeAST {

    private final NodeId id;
    private final NodeAST expr;

    /**
     * Costruisce un nodo di assegnamento.
     *
     * @param id   L'identificatore della variabile che riceve il valore.
     * @param expr L'espressione che calcola il valore da assegnare.
     * @param riga La riga dell'operatore di assegnamento.
     * @throws SyntacticException Se manca l'ID o l'espressione.
     */
    public NodeAssign(NodeId id, NodeAST expr, int riga) {
        super(riga);
        if (id == null) throw new SyntacticException("ID mancante nell'assegnamento alla riga " + riga);
        if (expr == null) throw new SyntacticException("Espressione mancante nell'assegnamento alla riga " + riga);

        this.id = id;
        this.expr = expr;
    }

    public NodeId getId() { return id; }
    public NodeAST getExpr() { return expr; }

    @Override
    public String toString() {
        return "<Assign: " + id + " = " + expr + ">";
    }
}