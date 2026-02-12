package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Classe che rappresenta un'istruzione di assegnamento nell'albero sintattico (AST).
 * Memorizza la variabile (a sinistra dell'uguale) e l'espressione da calcolare e
 * assegnare (a destra dell'uguale).
 */
public class NodeAssign extends NodeStm {
    
    // Il nodo che rappresenta il nome della variabile da sovrascrivere
    private final NodeId id;
    // Il nodo che rappresenta il calcolo o il valore da assegnare alla variabile
    private NodeExpr expr;

    /**
     * Costruttore del nodo di assegnamento.
     * @param id Il nodo identificatore della variabile.
     * @param expr Il nodo dell'espressione da valutare.
     * @param riga Il numero di riga nel file sorgente in cui si trova l'istruzione.
     */
    public NodeAssign(NodeId id, NodeExpr expr, int riga) {
        // Passo la riga alla superclasse NodeStm per tenerne traccia
        super(riga);
        this.id = id;
        this.expr = expr;
    }

    /**
     * Restituisce la variabile a cui stiamo assegnando il valore.
     * @return Il nodo NodeId associato.
     */
    public NodeId getId() {
        return id;
    }

    /**
     * Restituisce l'espressione che si trova a destra dell'uguale.
     * @return Il nodo NodeExpr associato.
     */
    public NodeExpr getExpr() {
        return expr;
    }

    /**
     * Sostituisce l'espressione corrente con una nuova.
     * @param expr La nuova espressione da collegare al nodo.
     */
    public void setExpr(NodeExpr expr) {
        // Aggiorno il riferimento all'espressione (molto utile quando il visitatore semantico deve inserire una conversione di tipo implicita)
        this.expr = expr;
    }

    /**
     * Permette al visitor di attraversare questo nodo dell'albero.
     * @param visitor L'oggetto visitor in esecuzione.
     */
    @Override
    public void accept(IVisitor visitor) {
        // Richiamo il metodo specifico per gestire l'assegnamento nel visitor corrente
        visitor.visit(this);
    }
}