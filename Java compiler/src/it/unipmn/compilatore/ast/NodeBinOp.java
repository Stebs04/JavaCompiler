package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

public class NodeBinOp extends NodeAST {
    private final LangOper op;
    private NodeAST left;
    private NodeAST right;

    public NodeBinOp(LangOper op, NodeAST left, NodeAST right, int riga) {
        super(riga);
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public LangOper getOp() {
        return op;
    }

    public NodeAST getLeft() {
        return left;
    }

    // Permetto di aggiornare l'operando sinistro (es. inserimento cast)
    public void setLeft(NodeAST left) {
        this.left = left;
    }

    public NodeAST getRight() {
        return right;
    }

    // Permetto di aggiornare l'operando destro (es. inserimento cast)
    public void setRight(NodeAST right) {
        this.right = right;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}