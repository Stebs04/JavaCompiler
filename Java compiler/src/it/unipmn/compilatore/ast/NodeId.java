package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta un identificatore (variabile) all'interno dell'AST.
 * Es: in "a = 5 + b", "a" e "b" sono NodeId.
 */
public class NodeId extends NodeAST{
    private final String name;

    /**
     * Crea un nodo identificatore.
     * * @param name Il nome della variabile (es. "somma").
     * @param riga La riga in cui appare la variabile.
     */
    public NodeId(String name, int riga){
        super(riga);
        if (name == null) throw new SyntacticException("Il nome non può essere null!");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "<ID:"+name+">";
    }
}
