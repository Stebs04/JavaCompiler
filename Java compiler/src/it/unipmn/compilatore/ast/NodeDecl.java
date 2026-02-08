package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Rappresenta la dichiarazione di una variabile.
 * L'inizializzazione (init) è opzionale, quindi può essere null.
 */
public class NodeDecl extends NodeAST {

    private final NodeId id;
    private final LangType type;
    private final NodeAST init; // Opzionale

    public NodeDecl(NodeId id, LangType type, NodeAST init, int riga) {
        super(riga);
        // ID e Tipo sono obbligatori, l'inizializzazione no.
        if (id == null) throw new SyntacticException("ID mancante nella dichiarazione riga " + riga);
        if (type == null) throw new SyntacticException("Tipo mancante nella dichiarazione riga " + riga);

        this.id = id;
        this.type = type;
        this.init = init;
    }

    public NodeId getId() { return id; }
    public LangType getType() { return type; }
    public NodeAST getInit() { return init; }

    @Override
    public String toString() {
        return "Decl: " + type + " " + id + (init != null ? " = " + init : "");
    }

    @Override
    public void accept(IVisitor visitor){
        visitor.visit(this);
    }
}