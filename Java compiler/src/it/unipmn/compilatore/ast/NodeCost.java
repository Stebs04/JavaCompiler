package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Rappresenta una costante letterale (numerica) nell'AST.
 * Può essere di tipo intero (es. 5) o floating point (es. 3.14).
 */
public class NodeCost extends NodeAST {

    private final String value;
    private final LangType type;

    /**
     * Crea un nodo costante.
     * * @param type  Il tipo del dato (LangType.INT o LangType.FLOAT).
     * @param value Il valore testuale della costante (es. "3.14").
     * @param riga  La riga in cui appare la costante (sempre come ultimo parametro per convenzione).
     * @throws SyntacticException Se i parametri obbligatori sono nulli o vuoti.
     */
    public NodeCost(LangType type, String value, int riga) {
        super(riga);

        // Controlli Fail Fast
        if (type == null) {
            throw new SyntacticException("Tipo mancante nella Costante alla riga " + riga);
        }
        if (value == null || value.isBlank()) {
            throw new SyntacticException("Valore mancante o vuoto nella Costante alla riga " + riga);
        }

        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public LangType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "<Cost: " + type + ", " + value + ">";
    }

    @Override
    public void accept(IVisitor visitor){
        visitor.visit(this);
    }
}