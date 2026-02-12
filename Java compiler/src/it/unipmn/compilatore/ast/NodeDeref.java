package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Classe che rappresenta la dereferenziazione di una variabile.
 * <p>
 * Questo nodo viene usato quando una variabile appare in una espressione
 * per ottenerne il valore corrente.
 * </p>
 */
public class NodeDeref extends NodeExpr {

    // L'identificatore della variabile a cui vogliamo accedere
    private NodeId id;

    /**
     * Costruttore per la dereferenziazione.
     * @param riga La riga in cui appare la variabile.
     * @param id Il nodo identificatore associato.
     */
    public NodeDeref(int riga, NodeId id) {
        // Inizializzo la parte di espressione con la riga corretta
        super(riga);
        // Memorizzo l'identificatore
        this.id = id;
    }

    /**
     * Restituisce l'identificatore associato.
     * @return Il nodo NodeId.
     */
    public NodeId getId() {
        return id;
    }

    /**
     * Restituisce una rappresentazione in stringa per il debug.
     */
    @Override
    public String toString() {
        // Costruisco la stringa descrittiva
        return "NodeDeref{id=" + id + "}";
    }

    /**
     * Accetta il visitor per l'attraversamento dell'albero.
     * @param visitor L'oggetto visitor che processerà questo nodo.
     */
    @Override
    public void accept(IVisitor visitor) {
        // Invoco il metodo visit specifico per NodeDeref
        visitor.visit(this);
    }
}