package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta un'operazione binaria (es. 3 + a).
 * Ha due figli: parte sinistra (left) e parte destra (right).
 */
public class NodeBinOp extends NodeAST {

    private final LangOper op;
    private final NodeAST left;
    private final NodeAST right;

    public NodeBinOp(LangOper op, NodeAST left, NodeAST right, int riga) {
        super(riga);
        // Controllo integrità: non esiste operazione binaria se manca un operando
        if (op == null) throw new SyntacticException("Operatore mancante alla riga " + riga);
        if (left == null) throw new SyntacticException("Operando sinistro mancante alla riga " + riga);
        if (right == null) throw new SyntacticException("Operando destro mancante alla riga " + riga);

        this.op = op;
        this.left = left;
        this.right = right;
    }

    public LangOper getOp() { return op; }
    public NodeAST getLeft() { return left; }
    public NodeAST getRight() { return right; }

    @Override
    public String toString() {
        return "(" + left + " " + op + " " + right + ")";
    }
}