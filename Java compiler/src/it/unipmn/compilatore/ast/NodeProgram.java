package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta la radice dell'AST.
 * Contiene la lista sequenziale di tutte le istruzioni del programma.
 */
public class NodeProgram extends NodeAST {

    private final List<NodeAST> statements;

    public NodeProgram(int riga) {
        super(riga);
        this.statements = new ArrayList<>();
    }

    public void addStatement(NodeAST stmt) {
        if (stmt != null) {
            statements.add(stmt);
        }
    }

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

    @Override
    public void accept(IVisitor visitor){
        visitor.visit(this);
    }
}