package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta l'istruzione di stampa (print id).
 */
public class NodePrint extends NodeAST {

    private final NodeId id;

    public NodePrint(NodeId id, int riga) {
        super(riga);
        if (id == null) throw new SyntacticException("Argomento mancante per print alla riga " + riga);
        this.id = id;
    }

    public NodeId getId() { return id; }

    @Override
    public String toString() {
        return "<Print: " + id + ">";
    }
}