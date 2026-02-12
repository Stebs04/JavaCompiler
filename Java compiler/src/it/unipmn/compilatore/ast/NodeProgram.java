package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta la radice dell'albero sintattico (AST).
 * Contiene la lista sequenziale di tutte le dichiarazioni e le istruzioni 
 * che compongono il programma scritto dall'utente.
 */
public class NodeProgram extends NodeAST {

    // Lista che memorizza tutte le istruzioni e dichiarazioni in ordine di lettura
    private final List<NodeDecSt> statements;

    /**
     * Costruttore per il nodo principale del programma.
     * @param riga Il numero di riga da cui inizia il programma (solitamente la riga 1).
     */
    public NodeProgram(int riga) {
        // Passo il riferimento della riga alla superclasse NodeAST
        super(riga);
        // Inizializzo la lista vuota pronta per ricevere i nodi dell'albero man mano che il parser li trova
        this.statements = new ArrayList<>();
    }

    /**
     * Aggiunge una nuova istruzione o dichiarazione alla sequenza del programma.
     * @param stmt Il nodo (NodeDecSt) da aggiungere alla lista.
     */
    public void addStatement(NodeDecSt stmt) {
        // Mi assicuro di non inserire nodi nulli che potrebbero causare errori nei visitor
        if (stmt != null) {
            // Aggiungo il nodo corrente alla fine della sequenza del programma
            statements.add(stmt);
        }
    }

    /**
     * Restituisce la lista di tutte le istruzioni lette e salvate.
     * @return La lista dei nodi NodeDecSt.
     */
    public List<NodeDecSt> getStatements() {
        return statements;
    }

    /**
     * Fornisce una rappresentazione testuale dell'intero programma.
     * Molto utile per capire se l'albero è stato costruito correttamente durante le fasi di debug.
     */
    @Override
    public String toString() {
        // Uso StringBuilder per concatenare le stringhe in modo efficiente
        StringBuilder sb = new StringBuilder();
        sb.append("PROGRAM:\n");
        
        // Itero sulle istruzioni per formattarle nella visualizzazione testuale aggiungendo un po' di indentazione
        for (NodeDecSt stmt : statements) {
            sb.append("  ").append(stmt.toString()).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Permette al visitor di ispezionare il nodo radice.
     * Essendo il punto di partenza, avvia l'attraversamento dell'intero albero.
     * @param visitor Il visitor in esecuzione.
     */
    @Override
    public void accept(IVisitor visitor){
        // Invoco il metodo di visita specifico per il nodo radice del programma
        visitor.visit(this);
    }
}