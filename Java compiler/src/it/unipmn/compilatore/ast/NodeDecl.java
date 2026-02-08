package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

public class NodeDecl extends NodeAST {
    private final NodeId id;
    private final LangType type;
    private NodeAST init;

    public NodeDecl(NodeId id, LangType type, NodeAST init, int riga) {
        super(riga);
        this.id = id;
        this.type = type;
        this.init = init;
    }

    public NodeId getId() {
        return id;
    }

    public LangType getType() {
        return type;
    }

    public NodeAST getInit() {
        return init;
    }

    // Permetto di aggiornare il nodo di inizializzazione (es. inserimento cast)
    public void setInit(NodeAST init) {
        this.init = init;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}