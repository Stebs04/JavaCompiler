package it.unipmn.compilatore.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta la radice dell'Abstract Syntax Tree (AST).
 * <p>
 * Corrisponde all'intero programma analizzato. Contiene una lista ordinata
 * di tutte le istruzioni (Statements) che compongono il codice sorgente.
 * </p>
 */
public class NodeProgram extends NodeAST {

    private final List<NodeAST> statements;

    /**
     * Inizializza un programma vuoto.
     *
     * @param riga La riga iniziale del programma (solitamente 0 o 1).
     */
    public NodeProgram(int riga) {
        super(riga);
        this.statements = new ArrayList<>();
    }

    /**
     * Aggiunge un'istruzione alla lista degli statement del programma.
     *
     * @param stmt Il nodo istruzione da aggiungere (es. Decl, Assign, Print).
     */
    public void addStatement(NodeAST stmt) {
        if (stmt != null) {
            statements.add(stmt);
        }
    }

    /**
     * Restituisce la lista completa delle istruzioni del programma.
     *
     * @return Una lista di NodeAST.
     */
    public List<NodeAST> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PROGRAM:\n");
        for (NodeAST stmt : statements) {
            sb.append("  ").append(stmt.toString()).append("\n");
        }
        return sb.toString();
    }
}