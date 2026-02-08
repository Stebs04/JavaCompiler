package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;

/**
 * Visitatore che genera il codice per la calcolatrice a stack 'dc' (Desk Calculator).
 * <p>
 * Percorro l'AST e traduco ogni nodo in comandi per la calcolatrice dc.
 * Gestisco l'assegnazione dei registri (lettere 'a'-'z') per le variabili e
 * la sequenza di operazioni in Notazione Polacca Inversa (RPN).
 * </p>
 */
public class CodeGeneratorVisitor implements IVisitor {

    private StringBuilder sb;
    private SymbolTable scopes;
    private char nextRegister;

    /**
     * Inizializzo il generatore di codice.
     * Imposto il registro iniziale al carattere 'a'.
     */
    public CodeGeneratorVisitor() {
        this.sb = new StringBuilder();
        this.scopes = new SymbolTable();
        // In dc usiamo i registri a, b, c... quindi parto da 'a'
        this.nextRegister = 'a';
    }

    /**
     * Restituisce la stringa contenente l'intero codice dc generato.
     *
     * @return Il codice pronto per essere salvato su file.
     */
    public String getCode() {
        return sb.toString();
    }

    @Override
    public void visit(NodeProgram node) {
        // Imposto la precisione decimale a 20 cifre (comando 'k') per supportare i float
        sb.append("20 k\n");

        // Itero su tutte le istruzioni del programma
        for (NodeAST stmt : node.getStatements()) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(NodeDecl node) {
        // Creo un nuovo simbolo associando il tipo e il prossimo registro disponibile
        Symbol symbol = new Symbol(node.getType(), nextRegister++);

        // Inserisco il simbolo nella tabella per tracciare l'associazione nome-registro
        scopes.insert(node.getId().getName(), symbol);

        // Se è presente un'inizializzazione, calcolo il valore e lo salvo nel registro
        if (node.getInit() != null) {
            // Visito l'espressione di inizializzazione che lascia il risultato sullo stack principale
            node.getInit().accept(this);

            // Genero il comando 's' (store) seguito dal registro assegnato per salvare il valore
            sb.append("s").append(symbol.getRegister()).append("\n");
        }
    }

    @Override
    public void visit(NodeCost node) {
        String value = node.getValue();

        // Converto il segno meno standard (-) nel formato richiesto da dc (_)
        if (value.startsWith("-")) {
            value = "_" + value.substring(1);
        }

        // Aggiungo il valore allo stream seguito da uno spazio per separarlo dai comandi successivi
        sb.append(value).append(" ");
    }

    @Override
    public void visit(NodeId node) {
        // Recupero il simbolo associato alla variabile dalla tabella
        Symbol symbol = scopes.lookup(node.getName());

        // Genero il comando 'l' (load) seguito dal registro per copiare il valore sullo stack
        sb.append("l").append(symbol.getRegister()).append(" ");
    }

    @Override
    public void visit(NodeAssign node) {
        // Recupero il simbolo della variabile a cui assegnare il valore
        Symbol symbol = scopes.lookup(node.getId().getName());

        // Visito l'espressione a destra dell'uguale per mettere il risultato sullo stack
        node.getExpr().accept(this);

        // Genero il comando 's' (store) per prelevare il valore dallo stack e salvarlo nel registro
        sb.append("s").append(symbol.getRegister()).append("\n");
    }

    @Override
    public void visit(NodePrint node) {
        // Recupero il simbolo della variabile da stampare
        Symbol symbol = scopes.lookup(node.getId().getName());

        // Carico il valore dal registro allo stack
        sb.append("l").append(symbol.getRegister()).append("\n");

        // Genero il comando 'p' per stampare il valore in cima allo stack (senza rimuoverlo)
        sb.append("p\n");

        // Genero il comando 'si' per rimuovere il valore stampato dallo stack (pop nel registro 'i')
        // Questo mantiene lo stack pulito
        sb.append("si\n");
    }

    @Override
    public void visit(NodeBinOp node) {
        // Visito l'operando sinistro (push sullo stack)
        node.getLeft().accept(this);

        // Visito l'operando destro (push sullo stack sopra il sinistro)
        node.getRight().accept(this);

        // Appendo l'operatore aritmetico corrispondente; dc gestisce automaticamente int e float
        switch (node.getOp()) {
            case PLUS:
                sb.append("+\n");
                break;
            case MINUS:
                sb.append("-\n");
                break;
            case TIMES:
                sb.append("*\n");
                break;
            case DIVIDE:
                sb.append("/\n");
                break;
        }
    }
}