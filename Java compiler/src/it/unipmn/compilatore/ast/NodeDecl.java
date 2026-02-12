package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Classe che rappresenta una dichiarazione di variabile nell'albero sintattico (AST).
 * Memorizza il nome della variabile, il suo tipo (es. intero o decimale) e un'eventuale
 * espressione di inizializzazione se la variabile viene valorizzata subito.
 */
public class NodeDecl extends NodeDecSt {
    
    // Il nodo che contiene il nome della nuova variabile
    private final NodeId id;
    // Il tipo semantico scelto per questa variabile
    private final LangType type;
    // Il valore o calcolo iniziale da assegnare alla variabile (può essere null se non presente)
    private NodeExpr init;

    /**
     * Costruttore per il nodo di dichiarazione.
     * @param id Il nodo identificatore della variabile.
     * @param type Il tipo della variabile (es. LangType.INT).
     * @param init L'espressione di inizializzazione (oppure null).
     * @param riga Il numero di riga nel file sorgente in cui si trova l'istruzione.
     */
    public NodeDecl(NodeId id, LangType type, NodeExpr init, int riga) {
        // Passo il numero di riga alla superclasse NodeDecSt per salvarlo
        super(riga);
        this.id = id;
        this.type = type;
        this.init = init;
    }

    /**
     * Restituisce l'identificatore della variabile dichiarata.
     * @return Il nodo NodeId associato.
     */
    public NodeId getId() {
        return id;
    }

    /**
     * Restituisce il tipo dichiarato per la variabile.
     * @return Il tipo LangType.
     */
    public LangType getType() {
        return type;
    }

    /**
     * Restituisce l'espressione di inizializzazione, se presente.
     * @return Il nodo NodeExpr, oppure null.
     */
    public NodeExpr getInit() {
        return init;
    }

    /**
     * Permette di sostituire l'espressione di inizializzazione corrente con una nuova.
     * @param init La nuova espressione NodeExpr.
     */
    public void setInit(NodeExpr init) {
        // Aggiorno il nodo di inizializzazione (molto utile quando il Type Checker deve inserire un cast implicito a float)
        this.init = init;
    }

    /**
     * Permette al visitor di attraversare questo nodo dell'albero.
     * @param visitor Il visitor in esecuzione.
     */
    @Override
    public void accept(IVisitor visitor) {
        // Invoco il metodo di visita specifico per le dichiarazioni
        visitor.visit(this);
    }
}