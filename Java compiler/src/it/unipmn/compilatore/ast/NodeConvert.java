package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Nodo speciale per la conversione di tipo (Casting).
 * <p>
 * Rappresento un nodo che non viene generato dal parser, ma inserito
 * dinamicamente durante l'analisi semantica per gestire la promozione
 * dei tipi (es. da INT a FLOAT).
 * </p>
 */
public class NodeConvert extends NodeAST {
    private final NodeAST expr;
    private final LangType targetType;

    /**
     * Costruisco il nodo di conversione avvolgendo l'espressione originale.
     *
     * @param expr L'espressione da convertire.
     * @param targetType Il tipo di destinazione.
     */
    public NodeConvert(NodeAST expr, LangType targetType) {
        // Inizializzo il nodo padre recuperando la riga dall'espressione originale
        super(expr.getRiga());
        this.expr = expr;
        this.targetType = targetType;
    }

    public NodeAST getExpr() {
        return expr;
    }

    public LangType getTargetType() {
        return targetType;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}