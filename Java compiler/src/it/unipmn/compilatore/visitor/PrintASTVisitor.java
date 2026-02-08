package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;

/**
 * Visitor concreto che serve a "stampare" l'AST ricostruendo il codice sorgente.
 * <p>
 * È molto utile per il debug: permette di verificare se il Parser ha costruito
 * </p>
 */
public class PrintASTVisitor implements IVisitor {

    private StringBuilder sb = new StringBuilder();

    /**
     * Restituisce la stringa accumulata durante la visita.
     */
    public String getOutput() {
        return sb.toString();
    }

    @Override
    public void visit(NodeProgram node) {
        sb.append("PROGRAM:\n");
        // Itero su tutte le istruzioni del programma
        for (NodeAST stmt : node.getStatements()) {
            sb.append("  "); // Aggiungo un po' di indentazione per bellezza
            // Dico all'istruzione figlia di accettarmi.
            // Sarà lei a richiamare il metodo visit() specifico per il suo tipo (es. visit(NodePrint)).
            stmt.accept(this);
            sb.append("\n"); // A capo dopo ogni istruzione
        }
    }

    @Override
    public void visit(NodeBinOp node) {
        // Ricostruisco l'operazione: (sinistra op destra)
        sb.append("(");

        // Visito il figlio sinistro (ricorsione)
        node.getLeft().accept(this);

        sb.append(" ").append(node.getOp()).append(" ");

        // Visito il figlio destro (ricorsione)
        node.getRight().accept(this);

        sb.append(")");
    }

    @Override
    public void visit(NodeCost node) {
        // È una foglia, stampo semplicemente il valore
        sb.append(node.getValue());
    }

    @Override
    public void visit(NodeId node) {
        // È una foglia, stampo il nome della variabile
        sb.append(node.getName());
    }

    @Override
    public void visit(NodeAssign node) {
        // Ricostruisco l'assegnamento: id = espressione;
        node.getId().accept(this); // Visito l'ID
        sb.append(" = ");
        node.getExpr().accept(this); // Visito l'espressione a destra
        sb.append(";");
    }

    @Override
    public void visit(NodePrint node) {
        // Ricostruisco la stampa: print id;
        sb.append("print ");
        node.getId().accept(this);
        sb.append(";");
    }

    @Override
    public void visit(NodeDecl node) {
        // Ricostruisco la dichiarazione: tipo id [= init];

        // Stampo il tipo (es. INT -> "int")
        if (node.getType() == LangType.INT) sb.append("int ");
        else if (node.getType() == LangType.FLOAT) sb.append("float ");

        node.getId().accept(this); // Stampo il nome variabile

        // Se c'è un'inizializzazione, la stampo
        if (node.getInit() != null) {
            sb.append(" = ");
            node.getInit().accept(this);
        }

        sb.append(";");
    }
}