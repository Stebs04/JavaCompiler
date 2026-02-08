package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Rappresenta un assegnamento (id = espressione).
 */
public class NodeAssign extends NodeAST {

    private final NodeId id;
    private final NodeAST expr;

    public NodeAssign(NodeId id, NodeAST expr, int riga) {
        super(riga);
        if (id == null) throw new SyntacticException("ID mancante nell'assegnamento riga " + riga);
        if (expr == null) throw new SyntacticException("Espressione mancante nell'assegnamento riga " + riga);

        this.id = id;
        this.expr = expr;
    }

    public NodeId getId() { return id; }
    public NodeAST getExpr() { return expr; }

    @Override
    public String toString() {
        return "<Assign: " + id + " = " + expr + ">";
    }

    @Override
    public void accept(IVisitor visitor){
        visitor.visit(this);
    }
}