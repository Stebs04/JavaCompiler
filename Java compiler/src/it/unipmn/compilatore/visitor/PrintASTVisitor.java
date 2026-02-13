package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;

/**
 * Classe che implementa un visitatore per la formattazione testuale dell'AST.
 * Visita i nodi dell'albero e ricostruisce il programma sotto forma di stringa, 
 * rendendo visibili anche le operazioni implicite aggiunte dal compilatore 
 * (come ad esempio le conversioni di tipo).
 */
public class PrintASTVisitor implements IVisitor {

    // Uso uno StringBuilder per accumulare il testo pezzo per pezzo in modo efficiente
    private StringBuilder sb = new StringBuilder();

    /**
     * Restituisce la stringa completa generata dopo aver visitato l'albero.
     * @return Il codice formattato.
     */
    public String getOutput() {
        return sb.toString();
    }

    /**
     * Visita il nodo radice che rappresenta l'intero programma.
     * @param node Il nodo del programma.
     */
    @Override
    public void visit(NodeProgram node) {
        // Scrivo l'intestazione per identificare l'inizio dell'albero
        sb.append("PROGRAM:\n");
        
        // Itero per stampare ogni singola istruzione e dichiarazione presente nella lista
        for (NodeDecSt stmt : node.getStatements()) {
            // Aggiungo un po' di indentazione per rendere l'output visivamente gerarchico
            sb.append("  ");
            stmt.accept(this);
            // Vado a capo dopo ogni istruzione per pulizia
            sb.append("\n");
        }
    }

    /**
     * Visita un'operazione matematica binaria.
     * @param node Il nodo dell'operazione.
     */
    @Override
    public void visit(NodeBinOp node) {
        // Racchiudo l'operazione tra parentesi per evidenziare la precedenza nell'albero
        sb.append("(");
        
        // Visito la parte sinistra dell'espressione (ricorsione)
        node.getLeft().accept(this);
        
        // Aggiungo il simbolo dell'operatore staccato da spazi
        sb.append(" ").append(node.getOp()).append(" ");
        
        // Visito la parte destra dell'espressione (ricorsione)
        node.getRight().accept(this);
        
        sb.append(")");
    }

    /**
     * Visita un numero esplicito.
     * @param node Il nodo costante.
     */
    @Override
    public void visit(NodeCost node) {
        // Aggiungo direttamente il valore numerico al testo
        sb.append(node.getValue());
    }

    /**
     * Visita la lettura di una variabile all'interno di un'espressione.
     * @param node Il nodo di dereferenziazione.
     */
    @Override
    public void visit(NodeDeref node) {
        // Visito l'identificatore interno per stamparne il nome
        node.getId().accept(this);
    }

    /**
     * Visita l'identificatore puro (il nome di una variabile).
     * @param node Il nodo identificatore.
     */
    @Override
    public void visit(NodeId node) {
        // Aggiungo il nome della variabile allo StringBuilder
        sb.append(node.getName());
    }

    /**
     * Visita un'operazione di assegnamento.
     * @param node Il nodo di assegnamento.
     */
    @Override
    public void visit(NodeAssign node) {
        // Visito la variabile a sinistra dell'uguale
        node.getId().accept(this);
        
        sb.append(" = ");
        
        // Visito l'espressione a destra che genera il valore
        node.getExpr().accept(this);
        
        sb.append(";");
    }

    /**
     * Visita il comando di stampa a video.
     * @param node Il nodo di stampa.
     */
    @Override
    public void visit(NodePrint node) {
        sb.append("print ");
        
        // Visito la variabile che deve essere stampata
        node.getId().accept(this);
        
        sb.append(";");
    }

    /**
     * Visita la dichiarazione di una nuova variabile.
     * @param node Il nodo di dichiarazione.
     */
    @Override
    public void visit(NodeDecl node) {
        // Controllo il tipo della variabile per scrivere la parola chiave corretta
        if (node.getType() == LangType.INT) {
            sb.append("int ");
        } else if (node.getType() == LangType.FLOAT) {
            sb.append("float ");
        }

        // Visito il nome della variabile dichiarata
        node.getId().accept(this);

        // Controllo se c'è anche una inizializzazione contestuale
        if (node.getInit() != null) {
            sb.append(" = ");
            // Visito l'espressione di inizializzazione
            node.getInit().accept(this);
        }

        sb.append(";");
    }

    /**
     * Visita un nodo di conversione di tipo (Cast).
     * Questo è fondamentale per il debug per vedere se il TypeChecker ha lavorato.
     * @param node Il nodo di casting.
     */
    @Override
    public void visit(NodeConvert node) {
        // Scrivo il cast esplicito tra parentesi come in Java/C
        sb.append("(");
        if (node.getTargetType() == LangType.INT) {
            sb.append("int");
        } else if (node.getTargetType() == LangType.FLOAT) {
            sb.append("float");
        }
        sb.append(") ");

        // Visito l'espressione che viene convertita
        node.getExpr().accept(this);
    }
}