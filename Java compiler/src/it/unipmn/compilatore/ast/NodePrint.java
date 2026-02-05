package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta l'istruzione di stampa (print).
 * <p>
 * Secondo le specifiche del linguaggio, accetta un identificatore come argomento.
 * Esempio: "print x;"
 * </p>
 */
public class NodePrint extends NodeAST {

    private final NodeId id;

    /**
     * Costruisce un nodo di stampa.
     *
     * @param id   L'identificatore della variabile da stampare.
     * @param riga La riga dell'istruzione print.
     * @throws SyntacticException Se l'identificatore è nullo.
     */
    public NodePrint(NodeId id, int riga) {
        super(riga);
        if (id == null) {
            throw new SyntacticException("Argomento mancante per l'istruzione print alla riga " + riga);
        }
        this.id = id;
    }

    public NodeId getId() {
        return id;
    }

    @Override
    public String toString() {
        return "<Print: " + id + ">";
    }
}