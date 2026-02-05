package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.LexicalException;

/**
 * Rappresenta un'operazione binaria (con due operandi).
 * Esempio: "a + 5" viene rappresentato con left="a", op=PLUS, right="5".
 */
public class NodeBinOp extends NodeAST{
    private final LangOper op;
    private final NodeAST left;
    private final NodeAST right;

    /**
     * Costruisce un nodo operazione binaria.
     *
     * @param op    L'operatore (es. PLUS). Non null.
     * @param left  L'operando sinistro. Non null.
     * @param right L'operando destro. Non null.
     * @param riga  La riga dell'operatore.
     */
    public NodeBinOp(LangOper op, NodeAST left, NodeAST right, int riga){
        super(riga);
        //Controlli Strutturali, impedisco che venga creato un AST incompleto o non funzionante
        if(op == null) throw new LexicalException("Operatore mancante in NodeBinOp");
        if (left == null) throw new LexicalException("Operando sinistro mancante in NodeBinOp");
        if (right == null) throw new LexicalException("Operando destro mancante in NodeBinOp");

        this.op = op;
        this.left = left;
        this.right = right;
    }

    public NodeAST getLeft() {
        return left;
    }

    public LangOper getOp() {
        return op;
    }

    public NodeAST getRight() {
        return right;
    }

}
