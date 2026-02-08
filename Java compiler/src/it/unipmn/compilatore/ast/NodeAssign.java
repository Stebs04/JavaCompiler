package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

public class NodeAssign extends NodeAST {
    private final NodeId id;
    private NodeAST expr;

    public NodeAssign(NodeId id, NodeAST expr, int riga) {
        super(riga);
        this.id = id;
        this.expr = expr;
    }

    public NodeId getId() {
        return id;
    }

    public NodeAST getExpr() {
        return expr;
    }

    // Permetto di aggiornare l'espressione (es. inserimento cast)
    public void setExpr(NodeAST expr) {
        this.expr = expr;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}