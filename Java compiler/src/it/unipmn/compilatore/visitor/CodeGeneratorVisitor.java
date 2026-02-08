package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;

/**
 * Visitatore che genera codice per la calcolatrice dc.
 * <p>
 * Traduco l'AST in una sequenza di operazioni su stack e registri.
 * Gestisco i registri alfabetici 'a'-'z' per le variabili.
 * </p>
 */
public class CodeGeneratorVisitor implements IVisitor {

    private StringBuilder sb;
    private SymbolTable scopes;
    private char nextRegister;

    public CodeGeneratorVisitor() {
        this.sb = new StringBuilder();
        this.scopes = new SymbolTable();
        // Inizializzo il contatore dei registri partendo da 'a'
        this.nextRegister = 'a';
    }

    public String getCode() {
        return sb.toString();
    }

    @Override
    public void visit(NodeProgram node) {
        // Imposto la precisione decimale a 20 cifre (comando 'k')
        sb.append("20 k\n");
        // Itero su tutti gli statement per generare il codice
        for (NodeAST stmt : node.getStatements()) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(NodeDecl node) {
        // Creo un simbolo associando il tipo e il prossimo registro disponibile
        Symbol symbol = new Symbol(node.getType(), nextRegister++);
        scopes.insert(node.getId().getName(), symbol);

        if (node.getInit() != null) {
            // Calcolo il valore di inizializzazione (va sullo stack)
            node.getInit().accept(this);
            // Genero il comando 's' (store) per salvare nel registro assegnato
            sb.append("s").append(symbol.getRegister()).append("\n");
        }
    }

    @Override
    public void visit(NodeAssign node) {
        Symbol symbol = scopes.lookup(node.getId().getName());
        // Calcolo l'espressione a destra dell'assegnamento
        node.getExpr().accept(this);
        // Salvo il risultato nel registro della variabile
        sb.append("s").append(symbol.getRegister()).append("\n");
    }

    @Override
    public void visit(NodeConvert node) {
        // Visito l'espressione interna per mettere il valore sullo stack
        node.getExpr().accept(this);

        // In dc, la gestione tra interi e float è dinamica in base alla precisione 'k'.
        // Non serve un comando esplicito di conversione per Int -> Float.
    }

    @Override
    public void visit(NodeBinOp node) {
        // Carico il primo operando sullo stack
        node.getLeft().accept(this);
        // Carico il secondo operando sullo stack
        node.getRight().accept(this);

        // Scrivo l'operatore aritmetico corrispondente
        switch (node.getOp()) {
            case PLUS: sb.append("+\n"); break;
            case MINUS: sb.append("-\n"); break;
            case TIMES: sb.append("*\n"); break;
            case DIVIDE: sb.append("/\n"); break;
        }
    }

    @Override
    public void visit(NodeId node) {
        Symbol symbol = scopes.lookup(node.getName());
        // Genero il comando 'l' (load) per copiare il valore dal registro allo stack
        sb.append("l").append(symbol.getRegister()).append(" ");
    }

    @Override
    public void visit(NodeCost node) {
        String val = node.getValue();
        // Converto il meno standard (-) in quello specifico di dc (_)
        if (val.startsWith("-")) {
            val = "_" + val.substring(1);
        }
        sb.append(val).append(" ");
    }

    @Override
    public void visit(NodePrint node) {
        Symbol symbol = scopes.lookup(node.getId().getName());
        // Carico la variabile
        sb.append("l").append(symbol.getRegister()).append("\n");
        // 'p' stampa il valore in cima allo stack senza rimuoverlo (peek)
        sb.append("p\n");
        // 'si' rimuove il valore dallo stack salvandolo nel registro 'i' (pop)
        sb.append("si\n");
    }
}