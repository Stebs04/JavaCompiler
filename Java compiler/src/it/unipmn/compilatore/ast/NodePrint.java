package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Classe che rappresenta l'istruzione di stampa a video nell'albero sintattico (AST).
 * Memorizza la variabile che si desidera leggere e stampare (ad esempio "print x;").
 */
public class NodePrint extends NodeStm {

    // Il nodo che contiene l'identificatore della variabile di cui stampare il valore
    private final NodeId id;

    /**
     * Costruttore per il nodo di stampa.
     * @param id Il nodo identificatore della variabile da stampare.
     * @param riga Il numero di riga nel file sorgente in cui si trova l'istruzione.
     */
    public NodePrint(NodeId id, int riga) {
        // Passo il numero di riga alla superclasse NodeStm per salvarlo
        super(riga);
        
        // Verifico la presenza dell'argomento per garantire la corretta costruzione dell'istruzione
        if (id == null) {
            throw new SyntacticException("Argomento mancante per print alla riga " + riga);
        }
        this.id = id;
    }

    /**
     * Restituisce l'identificatore della variabile associata a questa istruzione di stampa.
     * @return Il nodo NodeId contenente il nome della variabile.
     */
    public NodeId getId() { 
        return id; 
    }

    /**
     * Fornisce una rappresentazione testuale del nodo, molto utile per visualizzare la struttura dell'albero.
     */
    @Override
    public String toString() {
        // Formatto l'output racchiudendo le informazioni dell'istruzione tra parentesi angolari
        return "<Print: " + id + ">";
    }

    /**
     * Permette al visitor di ispezionare questo nodo dell'albero.
     * @param visitor Il visitor in esecuzione.
     */
    @Override
    public void accept(IVisitor visitor){
        // Invoco il metodo di visita specifico per le istruzioni di stampa
        visitor.visit(this);
    }
}