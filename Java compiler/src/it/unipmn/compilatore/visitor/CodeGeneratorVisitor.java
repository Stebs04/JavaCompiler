package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;

/**
 * Classe che implementa il visitatore per generare il codice finale.
 * Scorre l'albero sintattico (AST) e produce in output le istruzioni
 * scritte nel linguaggio della calcolatrice 'dc'.
 * Mantiene un log interno per il debug della generazione.
 */
public class CodeGeneratorVisitor implements IVisitor {

    // Contenitore dove accumulo stringhe per costruire il codice finale
    private StringBuilder sb;
    // Tabella per associare i nomi delle variabili ai registri della calcolatrice
    private SymbolTable scopes;
    // Carattere per tenere traccia dell'ultimo registro utilizzato
    private char nextRegister;
    // StringBuilder per accumulare i log di debug (separato dal codice generato)
    private StringBuilder log;

    /**
     * Costruttore del generatore di codice.
     * Prepara l'ambiente di traduzione e il log.
     */
    public CodeGeneratorVisitor() {
        this.sb = new StringBuilder();
        this.scopes = new SymbolTable();
        this.log = new StringBuilder();
        // Imposto il primo registro disponibile usando la prima lettera dell'alfabeto
        this.nextRegister = 'a';
    }

    /**
     * Restituisce tutto il codice generato fino a questo momento.
     * @return Una stringa contenente il codice 'dc' completo.
     */
    public String getCode() {
        return sb.toString();
    }

    /**
     * Restituisce il log delle operazioni di generazione codice.
     * @return La stringa con i log.
     */
    public String getLog() {
        return log.toString();
    }

    /**
     * Traduce il punto di partenza del programma.
     * @param node Il nodo radice dell'albero.
     */
    @Override
    public void visit(NodeProgram node) {
        log.append("Inizio generazione codice programma.\n");
        // Scrivo il comando 'k' per impostare la precisione dei numeri decimali a 20 cifre
        sb.append("20 k\n");
        // Itero su tutte le istruzioni per tradurle sequenzialmente
        for (NodeDecSt stmt : node.getStatements()) {
            stmt.accept(this);
        }
        log.append("Generazione completata.\n");
    }

    /**
     * Traduce la dichiarazione di una nuova variabile.
     * Le assegna un registro fisico temporaneo nella calcolatrice.
     * @param node Il nodo di dichiarazione.
     */
    @Override
    public void visit(NodeDecl node) {
        String varName = node.getId().getName();
        char reg = nextRegister++;
        log.append("Assegno registro '").append(reg).append("' alla variabile '").append(varName).append("'\n");

        // Creo un simbolo, gli assegno la lettera corrente per il registro e incremento alla successiva
        Symbol symbol = new Symbol(node.getType(), reg);
        scopes.insert(varName, symbol);

        if (node.getInit() != null) {
            log.append("Genero codice inizializzazione per: ").append(varName).append("\n");
            // Traduco l'espressione associata per caricare il valore in cima allo stack
            node.getInit().accept(this);
            // Scrivo il comando 's' (store) seguito dal registro per salvare il valore calcolato
            sb.append("s").append(symbol.getRegister()).append("\n");
        }
    }

    /**
     * Traduce un'operazione di assegnamento a una variabile.
     * @param node Il nodo di assegnamento.
     */
    @Override
    public void visit(NodeAssign node) {
        String varName = node.getId().getName();
        log.append("Genero codice assegnamento per: ").append(varName).append("\n");
        
        // Recupero dalla tabella la lettera di registro associata a questa variabile
        Symbol symbol = scopes.lookup(varName);
        // Traduco la parte destra dell'uguale per spingere il risultato sullo stack
        node.getExpr().accept(this);
        // Scrivo il comando 's' per prelevare il valore dallo stack e salvarlo nel registro
        sb.append("s").append(symbol.getRegister()).append("\n");
    }

    /**
     * Traduce il nodo di conversione di tipo.
     * @param node Il nodo di casting.
     */
    @Override
    public void visit(NodeConvert node) {
        log.append("Genero codice conversione tipo.\n");
        // La calcolatrice 'dc' gestisce interi e decimali automaticamente, visito solo il contenuto
        node.getExpr().accept(this);
    }

    /**
     * Traduce le operazioni matematiche binarie.
     * Genera codice usando la notazione postfissa tipica dei linguaggi a stack.
     * @param node Il nodo dell'operazione.
     */
    @Override
    public void visit(NodeBinOp node) {
        log.append("Genero operazione binaria: ").append(node.getOp()).append("\n");
        // Traduco l'elemento a sinistra che verrà posizionato sullo stack
        node.getLeft().accept(this);
        // Traduco l'elemento a destra che finirà sopra al precedente nello stack
        node.getRight().accept(this);

        // Scrivo il simbolo matematico per consumare gli ultimi due elementi estratti
        switch (node.getOp()) {
            case PLUS: sb.append("+\n"); break;
            case MINUS: sb.append("-\n"); break;
            case TIMES: sb.append("*\n"); break;
            case DIVIDE: sb.append("/\n"); break;
        }
    }

    /**
     * Traduce la fase in cui una variabile viene letta per usarne il valore.
     * @param node Il nodo di dereferenziazione.
     */
    @Override
    public void visit(NodeDeref node) {
        String varName = node.getId().getName();
        Symbol symbol = scopes.lookup(varName);
        log.append("Leggo valore variabile '").append(varName).append("' dal registro '").append(symbol.getRegister()).append("'\n");
        // Scrivo il comando 'l' (load) per copiare il dato dal registro e metterlo in cima allo stack
        sb.append("l").append(symbol.getRegister()).append(" ");
    }

    /**
     * Visita l'identificatore puro.
     * @param node Il nodo identificatore.
     */
    @Override
    public void visit(NodeId node) {
        // Questo nodo non genera codice direttamente
    }

    /**
     * Traduce i numeri inseriti direttamente all'interno delle espressioni.
     * @param node Il nodo costante.
     */
    @Override
    public void visit(NodeCost node) {
        String val = node.getValue();
        if (val.startsWith("-")) {
            // Sostituisco il meno '-' con il trattino basso '_' richiesto da 'dc' per i numeri negativi
            val = "_" + val.substring(1);
        }
        // Scrivo il numero in output aggiungendo uno spazio per separarlo dagli altri elementi
        sb.append(val).append(" ");
    }

    /**
     * Traduce l'istruzione di stampa del valore di una variabile a schermo.
     * @param node Il nodo print.
     */
    @Override
    public void visit(NodePrint node) {
        log.append("Genero istruzione print.\n");
        Symbol symbol = scopes.lookup(node.getId().getName());
        // Richiamo in cima allo stack il valore della variabile da stampare
        sb.append("l").append(symbol.getRegister()).append("\n");
        // Scrivo il comando 'p' (print) che stampa a video ma lascia il valore in cima allo stack
        sb.append("p\n");
        // Scrivo 'si' per svuotare lo stack spostando il valore nel registro 'i', che uso come cestino
        sb.append("si\n");
    }
}