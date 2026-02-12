package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Classe che rappresenta l'identificatore di una variabile (ad esempio "x" o "somma") 
 * all'interno dell'albero sintattico (AST).
 * Questo nodo è una foglia dell'albero e ha il solo compito di memorizzare il nome 
 * testuale della variabile definita nel codice sorgente.
 */
public class NodeId extends NodeExpr {

    // Il nome testuale della variabile
    private final String name;

    /**
     * Costruttore per il nodo identificatore.
     * @param name Il nome della variabile letto dallo scanner.
     * @param riga Il numero di riga in cui compare la variabile.
     */
    public NodeId(String name, int riga) {
        // Passo il numero di riga alla superclasse NodeExpr per salvarlo
        super(riga);
        
        // Verifico che il nome non sia nullo per garantire l'integrità del nodo
        if (name == null) {
            throw new SyntacticException("Nome identificatore nullo alla riga " + riga);
        }
        
        this.name = name;
    }

    /**
     * Restituisce il nome della variabile.
     * @return La stringa con il nome.
     */
    public String getName() {
        return name;
    }

    /**
     * Fornisce una rappresentazione testuale del nodo, utile per la stampa e il debug.
     */
    @Override
    public String toString() {
        // Formatto l'output racchiudendo il nome tra parentesi angolari
        return "<ID: " + name + ">";
    }

    /**
     * Permette al visitor di ispezionare questo nodo dell'albero.
     * @param visitor Il visitor in esecuzione.
     */
    @Override
    public void accept(IVisitor visitor){
        // Invoco il metodo di visita specifico per gli identificatori
        visitor.visit(this);
    }
}