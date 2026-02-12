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
        // Inizio la stampa indicando l'intestazione del programma
        sb.append("PROGRAM:\n");
        
        // Itero per stampare ogni singola istruzione e dichiarazione presente nella lista
        for (NodeDecSt stmt : node.getStatements()) {
            // Aggiungo un po' di indentazione per rendere l'output più leggibile
            sb.append("  ");
            stmt.accept(this);
            // Vado a capo dopo ogni istruzione
            sb.append("\n");
        }
    }

    /**
     * Visita un'operazione matematica binaria.
     * @param node Il nodo dell'operazione.
     */
    @Override
    public void visit(NodeBinOp node) {
        // Racchiudo ogni operazione tra parentesi per far capire bene l'ordine di valutazione e la precedenza
        sb.append("(");
        
        // Visito la parte sinistra dell'espressione
        node.getLeft().accept(this);
        
        // Aggiungo il simbolo dell'operatore in mezzo, staccato da spazi
        sb.append(" ").append(node.getOp()).append(" ");
        
        // Visito la parte destra dell'espressione
        node.getRight().accept(this);
        
        sb.append(")");
    }

    /**
     * Visita un numero esplicito.
     * @param node Il nodo costante.
     */
    @Override
    public void visit(NodeCost node) {
        // Aggiungo semplicemente il testo del numero
        sb.append(node.getValue());
    }

    /**
     * Visita la lettura di una variabile all'interno di un'espressione.
     * @param node Il nodo di dereferenziazione.
     */
    @Override
    public void visit(NodeDeref node) {
        // Per stampare la variabile letta, visito semplicemente l'identificatore che la rappresenta
        node.getId().accept(this);
    }

    /**
     * Visita l'identificatore puro (il nome di una variabile).
     * @param node Il nodo identificatore.
     */
    @Override
    public void visit(NodeId node) {
        // Aggiungo il nome della variabile al testo
        sb.append(node.getName());
    }

    /**
     * Visita un'operazione di assegnamento.
     * @param node Il nodo di assegnamento.
     */
    @Override
    public void visit(NodeAssign node) {
        // Visito la variabile che sta a sinistra
        node.getId().accept(this);
        
        // Aggiungo il simbolo di uguaglianza
        sb.append(" = ");
        
        // Visito tutto il calcolo o valore che sta a destra
        node.getExpr().accept(this);
        
        // Chiudo l'istruzione
        sb.append(";");
    }

    /**
     * Visita il comando di stampa a video.
     * @param node Il nodo di stampa.
     */
    @Override
    public void visit(NodePrint node) {
        sb.append("print ");
        
        // Visito l'identificatore della variabile da stampare
        node.getId().accept(this);
        
        // Chiudo l'istruzione
        sb.append(";");
    }

    /**
     * Visita la dichiarazione di una nuova variabile.
     * @param node Il nodo di dichiarazione.
     */
    @Override
    public void visit(NodeDecl node) {
        // Converto il tipo logico dell'AST nella parola chiave corrispondente del linguaggio
        if (node.getType() == LangType.INT) {
            sb.append("int ");
        } else if (node.getType() == LangType.FLOAT) {
            sb.append("float ");
        }

        // Visito il nome della variabile
        node.getId().accept(this);

        // Controllo se l'utente ha anche inizializzato la variabile sulla stessa riga
        if (node.getInit() != null) {
            sb.append(" = ");
            // Visito l'espressione iniziale
            node.getInit().accept(this);
        }

        sb.append(";");
    }

    /**
     * Visita un nodo di conversione di tipo.
     * @param node Il nodo di casting.
     */
    @Override
    public void visit(NodeConvert node) {
        // Scrivo la conversione tra parentesi tonde, come si fa in Java o in C
        sb.append("(");
        if (node.getTargetType() == LangType.INT) {
            sb.append("int");
        } else if (node.getTargetType() == LangType.FLOAT) {
            sb.append("float");
        }
        sb.append(") ");

        // Visito l'espressione a cui sto applicando la conversione
        node.getExpr().accept(this);
    }
}