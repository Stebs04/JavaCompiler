package it.unipmn.compilatore.ast;

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
    public NodeCost(int riga, String value, LangType type, int riga1) {
        super(riga);
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
