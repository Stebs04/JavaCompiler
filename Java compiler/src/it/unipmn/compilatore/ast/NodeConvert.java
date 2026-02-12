package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Classe che rappresenta un nodo speciale per la conversione di tipo (Casting).
 * Questo nodo non viene creato direttamente dal parser leggendo il file, 
 * ma viene inserito dinamicamente durante il controllo semantico (Type Checking) 
 * per gestire la promozione implicita dei tipi (ad esempio trasformare un int in float).
 */
public class NodeConvert extends NodeExpr {
    
    // L'espressione originale che deve essere convertita di tipo
    private final NodeExpr expr;
    // Il nuovo tipo che l'espressione deve assumere al termine della conversione
    private final LangType targetType;

    /**
     * Costruttore del nodo di conversione.
     * Avvolge l'espressione originale per forzarne il cambio di tipo.
     * @param expr L'espressione matematica o il valore da convertire.
     * @param targetType Il tipo finale desiderato (es. LangType.FLOAT).
     */
    public NodeConvert(NodeExpr expr, LangType targetType) {
        // Estraggo la riga dall'espressione originale e la passo alla superclasse per mantenere il riferimento
        super(expr.getRiga());
        this.expr = expr;
        this.targetType = targetType;
    }

    /**
     * Restituisce l'espressione racchiusa nel nodo di conversione.
     * @return Il nodo NodeExpr originale.
     */
    public NodeExpr getExpr() {
        return expr;
    }

    /**
     * Restituisce il tipo di destinazione della conversione.
     * @return Il tipo LangType (es. FLOAT).
     */
    public LangType getTargetType() {
        return targetType;
    }

    /**
     * Permette al visitor di ispezionare questo nodo dell'albero.
     * @param visitor Il visitor in esecuzione.
     */
    @Override
    public void accept(IVisitor visitor) {
        // Invoco la visita specifica per il nodo di conversione
        visitor.visit(this);
    }
}