package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta una dichiarazione di variabile.
 * Gestisce sia dichiarazioni semplici ("int a") che con inizializzazione ("int a = 5").
 */
public class NodeDecl extends NodeAST{

    private final NodeId id;
    private final LangType type;
    private final NodeAST init;

    /**
     * Costruisce un nodo dichiarazione.
     *
     * @param id   L'identificatore della variabile. Non null.
     * @param type Il tipo (INT/FLOAT). Non null.
     * @param init L'espressione di inizializzazione (può essere null).
     * @param riga La riga della dichiarazione.
     */
    public NodeDecl(NodeId id, LangType type, NodeAST init, int riga) {
        super(riga);

        // Controlliamo solo ciò che è obbligatorio
        if (id == null) throw new SyntacticException("ID mancante nella Dichiarazione");
        if (type == null) throw new SyntacticException("Tipo mancante nella Dichiarazione");

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
}
