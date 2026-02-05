package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta un identificatore di variabile (es. "x", "somma").
 * È una foglia dell'albero.
 */
public class NodeId extends NodeAST {

    private final String name;

    public NodeId(String name, int riga) {
        super(riga);
        // Controllo strutturale: un identificatore deve avere un nome
        if (name == null) {
            throw new SyntacticException("Nome identificatore nullo alla riga " + riga);
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "<ID: " + name + ">";
    }
}