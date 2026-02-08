package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;

/**
 * Visitatore per l'Analisi Semantica (Type Checking).
 * <p>
 * Percorre l'AST per verificare la coerenza dei tipi e la corretta dichiarazione delle variabili.
 * Utilizza una SymbolTable per tracciare le dichiarazioni e un campo resType per propagare
 * i tipi dalle foglie verso la radice delle espressioni.
 * </p>
 */
public class TypeCheckVisitor implements IVisitor {

    private SymbolTable symbolTable;
    private LangType resType;

    /**
     * Inizializza la Symbol Table.
     */
    public TypeCheckVisitor() {
        this.symbolTable = new SymbolTable();
    }

    @Override
    public void visit(NodeProgram node) {
        // Itero sulle istruzioni del programma
        for (NodeAST stmt : node.getStatements()) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(NodeDecl node) {
        String nome = node.getId().getName();
        LangType tipoDichiarato = node.getType();
        Symbol symbol = new Symbol(tipoDichiarato);

        // Tento l'inserimento nella Symbol Table
        if (!symbolTable.insert(nome, symbol)) {
            throw new SyntacticException("Errore Semantico: Variabile '" + nome + "' già dichiarata nello scope corrente.");
        }

        // Se presente, verifico l'inizializzazione
        if (node.getInit() != null) {
            node.getInit().accept(this);
            // Controllo che il tipo dell'espressione di inizializzazione coincida con il dichiarato
            if (resType != tipoDichiarato) {
                throw new SyntacticException("Errore Semantico: Impossibile assegnare " + resType +
                        " alla variabile '" + nome + "' di tipo " + tipoDichiarato);
            }
        }
    }

    @Override
    public void visit(NodeAssign node) {
        String nome = node.getId().getName();
        Symbol symbol = symbolTable.lookup(nome);

        // Verifico che la variabile esista
        if (symbol == null) {
            throw new SyntacticException("Errore Semantico: Variabile '" + nome + "' non dichiarata.");
        }

        // Calcolo il tipo dell'espressione
        node.getExpr().accept(this);

        // Verifico la compatibilità dei tipi
        if (resType != symbol.getType()) {
            throw new SyntacticException("Errore Semantico: Impossibile assegnare " + resType +
                    " alla variabile '" + nome + "' di tipo " + symbol.getType());
        }
    }

    @Override
    public void visit(NodePrint node) {
        String nome = node.getId().getName();
        // Verifico solo l'esistenza della variabile nella tabella
        if (symbolTable.lookup(nome) == null) {
            throw new SyntacticException("Errore Semantico: Impossibile stampare la variabile '" + nome + "' perché non dichiarata.");
        }
    }

    @Override
    public void visit(NodeBinOp node) {
        // Visito l'operando sinistro e salvo il tipo
        node.getLeft().accept(this);
        LangType leftType = resType;

        // Visito l'operando destro e salvo il tipo
        node.getRight().accept(this);
        LangType rightType = resType;

        // Controllo che i tipi siano identici (strict typing)
        if (leftType != rightType) {
            throw new SyntacticException("Errore Semantico: Operazione '" + node.getOp() +
                    "' non consentita tra " + leftType + " e " + rightType);
        }

        // Il risultato ha lo stesso tipo degli operandi
        this.resType = leftType;
    }

    @Override
    public void visit(NodeCost node) {
        // Registro il tipo della costante nel campo risultato
        this.resType = node.getType();
    }

    @Override
    public void visit(NodeId node) {
        Symbol symbol = symbolTable.lookup(node.getName());
        // Controllo che la variabile usata nell'espressione sia stata dichiarata
        if (symbol == null) {
            throw new SyntacticException("Errore Semantico: Variabile '" + node.getName() + "' non dichiarata.");
        }
        // Registro il tipo della variabile trovata
        this.resType = symbol.getType();
    }
}