package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta una costante letterale (numerica) nell'AST.
 * Può essere di tipo intero (es. 5) o floating point (es. 3.14).
 */
public class NodeCost extends NodeAST{
    private final String value;
    LangType type;
    int riga;

    /**
     * Crea un nodo costante.
     * * @param type Il tipo del dato (LangType.INT o LangType.FLOAT).
     * @param value Il valore testuale della costante (es. "3.14").
     * @param riga La riga in cui appare la costante.
     */
    public NodeCost(int riga, String value, LangType type) {
        super(riga);
        if(type == null) throw new SyntacticException("Tipo mancante nella Dichiarazione");
        if(value == null || value.isBlank()) throw new SyntacticException("Valore mancante nella dichiarazione");
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public LangType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "<"+"Cost: "+value+","+type+">";
    }
}
