package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;

/**
 * Visitatore semantico che controlla i tipi e gestisce le conversioni implicite.
 * <p>
 * Analizzo l'albero per verificare la compatibilità dei tipi. Se trovo
 * un'operazione tra tipi diversi ma compatibili (es. float + int),
 * modifico l'AST inserendo un NodeConvert.
 * </p>
 */
public class TypeCheckVisitor implements IVisitor {

    private SymbolTable scopes;
    // Tengo traccia del tipo dell'ultima espressione analizzata per propagarlo verso l'alto
    private LangType lastType;

    public TypeCheckVisitor() {
        this.scopes = new SymbolTable();
    }

    @Override
    public void visit(NodeProgram node) {
        // Itero su tutte le istruzioni del programma per validarle sequenzialmente
        for (NodeAST stmt : node.getStatements()) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(NodeDecl node) {
        // Verifico nel context corrente se la variabile è già stata definita
        if (scopes.lookup(node.getId().getName()) != null) {
            throw new SyntacticException("Variabile '" + node.getId().getName() + "' già dichiarata.");
        }

        // Registro la variabile nella symbol table col suo tipo semantico
        scopes.insert(node.getId().getName(), new Symbol(node.getType()));

        if (node.getInit() != null) {
            // Analizzo l'espressione di inizializzazione
            node.getInit().accept(this);

            // Controllo se è necessaria una promozione di tipo (Int -> Float)
            if (node.getType() == LangType.FLOAT && lastType == LangType.INT) {
                // Modifico l'AST iniettando il nodo di conversione
                NodeConvert convert = new NodeConvert(node.getInit(), LangType.FLOAT);
                node.setInit(convert);
            } else if (node.getType() != lastType) {
                throw new SyntacticException("Tipo non compatibile nell'inizializzazione di " + node.getId().getName());
            }
        }
    }

    @Override
    public void visit(NodeAssign node) {
        Symbol symbol = scopes.lookup(node.getId().getName());
        if (symbol == null) {
            throw new SyntacticException("Variabile non dichiarata: " + node.getId().getName());
        }

        node.getExpr().accept(this);

        // Verifico la compatibilità per l'assegnamento ed eventuale casting
        if (symbol.getType() == LangType.FLOAT && lastType == LangType.INT) {
            NodeConvert convert = new NodeConvert(node.getExpr(), LangType.FLOAT);
            // Aggiorno il riferimento nell'AST
            node.setExpr(convert);
            // Aggiorno il tipo corrente al risultato della conversione
            lastType = LangType.FLOAT;
        } else if (symbol.getType() != lastType) {
            throw new SyntacticException("Assegnazione non compatibile per " + node.getId().getName());
        }
    }

    @Override
    public void visit(NodeBinOp node) {
        // Analizzo l'operando sinistro
        node.getLeft().accept(this);
        LangType leftType = lastType;

        // Analizzo l'operando destro
        node.getRight().accept(this);
        LangType rightType = lastType;

        // Determino il tipo risultante dell'operazione e gestisco il widening
        if (leftType == LangType.INT && rightType == LangType.INT) {
            lastType = LangType.INT;
        } else {
            // Se almeno un operando è Float, il risultato dell'operazione è Float
            lastType = LangType.FLOAT;

            // Converto l'operando sinistro se è di tipo intero
            if (leftType == LangType.INT) {
                node.setLeft(new NodeConvert(node.getLeft(), LangType.FLOAT));
            }
            // Converto l'operando destro se è di tipo intero
            if (rightType == LangType.INT) {
                node.setRight(new NodeConvert(node.getRight(), LangType.FLOAT));
            }
        }
    }

    @Override
    public void visit(NodeId node) {
        Symbol symbol = scopes.lookup(node.getName());
        if (symbol == null) {
            throw new SyntacticException("Uso di variabile non dichiarata: " + node.getName());
        }
        // Propago il tipo recuperato dalla symbol table
        lastType = symbol.getType();
    }

    @Override
    public void visit(NodeCost node) {
        // Propago il tipo nativo della costante
        lastType = node.getType();
    }

    @Override
    public void visit(NodePrint node) {
        // Verifico solo l'esistenza della variabile, la stampa supporta ogni tipo
        if (scopes.lookup(node.getId().getName()) == null) {
            throw new SyntacticException("Tentativo di stampa di variabile non dichiarata");
        }
    }

    @Override
    public void visit(NodeConvert node) {
        // Visito l'espressione interna per validarla
        node.getExpr().accept(this);
        // Il tipo risultante di questo nodo è forzato al target type
        lastType = node.getTargetType();
    }
}