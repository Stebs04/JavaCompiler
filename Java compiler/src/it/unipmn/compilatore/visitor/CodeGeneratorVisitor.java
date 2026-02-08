package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;

/**
 * Visitatore che genera il codice Bytecode (Assembly Jasmin) per la JVM.
 * <p>
 * Percorro l'AST e traduco ogni nodo in istruzioni leggibili dalla Java Virtual Machine.
 * Gestisco l'assegnazione della memoria (offset) per le variabili, la manipolazione dello stack
 * e la selezione delle istruzioni corrette in base al tipo di dato (INT o FLOAT).
 * </p>
 */
public class CodeGeneratorVisitor implements IVisitor {

    private StringBuilder sb;
    private SymbolTable scopes;
    private int nextOffset;
    /** Traccia il tipo dell'ultima espressione valutata sullo stack */
    private LangType lastType;

    /**
     * Inizializzo il generatore di codice.
     * Imposto l'offset iniziale a 0 per la prima variabile locale.
     */
    public CodeGeneratorVisitor() {
        this.sb = new StringBuilder();
        this.scopes = new SymbolTable();
        this.nextOffset = 0;
        this.lastType = LangType.INT; // Valore di default
    }

    /**
     * Restituisce la stringa contenente l'intero codice Assembly generato.
     *
     * @return Il codice Jasmin pronto per essere salvato su file.
     */
    public String getCode() {
        return sb.toString();
    }

    @Override
    public void visit(NodeProgram node) {
        // Scrivo l'intestazione standard richiesta dal formato Jasmin per definire una classe
        sb.append(".class public Main\n");
        sb.append(".super java/lang/Object\n");
        sb.append(".method public static main([Ljava/lang/String;)V\n");

        // Definisco i limiti dello stack (operandi) e delle variabili locali
        sb.append(".limit locals 100\n");
        sb.append(".limit stack 100\n");

        // Itero su tutte le istruzioni del programma per generare il relativo bytecode
        for (NodeAST stmt : node.getStatements()) {
            stmt.accept(this);
        }

        // Chiudo il metodo main con l'istruzione di ritorno e la direttiva di fine
        sb.append("return\n");
        sb.append(".end method\n");
    }

    @Override
    public void visit(NodeDecl node) {
        // Assegno un nuovo offset univoco alla variabile e incremento il contatore per la successiva
        Symbol symbol = new Symbol(node.getType(), nextOffset++);

        // Inserisco il simbolo nella tabella per poter recuperare il suo indirizzo (offset) in seguito
        scopes.insert(node.getId().getName(), symbol);

        // Se è presente un'inizializzazione, genero il codice per calcolarla
        if (node.getInit() != null) {
            // Visito l'espressione: questo lascerà il risultato in cima allo stack
            node.getInit().accept(this);

            // Prelevo il valore dallo stack e lo salvo nella variabile locale all'indirizzo specifico
            if (node.getType() == LangType.INT) {
                // istore: salva un intero dallo stack alla variabile locale X
                sb.append("istore ").append(symbol.getOffset()).append("\n");
            } else if (node.getType() == LangType.FLOAT) {
                // fstore: salva un float dallo stack alla variabile locale X
                sb.append("fstore ").append(symbol.getOffset()).append("\n");
            }
        }
    }

    @Override
    public void visit(NodeCost node) {
        // Carico una costante numerica sullo stack (ldc = Load Constant)
        sb.append("ldc ").append(node.getValue()).append("\n");

        // Aggiorno il tipo corrente per le operazioni successive
        this.lastType = node.getType();
    }

    @Override
    public void visit(NodeId node) {
        // Recupero le informazioni sulla variabile (in particolare l'offset) dalla tabella
        Symbol symbol = scopes.lookup(node.getName());

        // Carico il valore della variabile locale sullo stack
        if (symbol.getType() == LangType.INT) {
            // iload: carica un intero dalla variabile locale X allo stack
            sb.append("iload ").append(symbol.getOffset()).append("\n");
        } else if (symbol.getType() == LangType.FLOAT) {
            // fload: carica un float dalla variabile locale X allo stack
            sb.append("fload ").append(symbol.getOffset()).append("\n");
        }

        // Aggiorno il tipo corrente in base alla variabile letta
        this.lastType = symbol.getType();
    }

    @Override
    public void visit(NodeAssign node) {
        // Recupero l'offset della variabile destinazione
        Symbol symbol = scopes.lookup(node.getId().getName());

        // Genero il codice per l'espressione a destra (il risultato finirà sullo stack)
        node.getExpr().accept(this);

        // Salvo il valore presente sullo stack nella variabile locale corrispondente
        if (symbol.getType() == LangType.INT) {
            sb.append("istore ").append(symbol.getOffset()).append("\n");
        } else if (symbol.getType() == LangType.FLOAT) {
            sb.append("fstore ").append(symbol.getOffset()).append("\n");
        }
    }

    @Override
    public void visit(NodePrint node) {
        // Carico sullo stack il campo statico 'out' di System (oggetto PrintStream)
        sb.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n");

        // Recupero il simbolo per sapere dove leggere il valore da stampare
        Symbol symbol = scopes.lookup(node.getId().getName());

        if (symbol.getType() == LangType.INT) {
            // Carico l'intero sullo stack
            sb.append("iload ").append(symbol.getOffset()).append("\n");
            // Invoco il metodo println che accetta un Intero (I) e restituisce Void (V)
            sb.append("invokevirtual java/io/PrintStream/println(I)V\n");
        } else if (symbol.getType() == LangType.FLOAT) {
            // Carico il float sullo stack
            sb.append("fload ").append(symbol.getOffset()).append("\n");
            // Invoco il metodo println che accetta un Float (F) e restituisce Void (V)
            sb.append("invokevirtual java/io/PrintStream/println(F)V\n");
        }
    }

    @Override
    public void visit(NodeBinOp node) {
        // Genero il codice per l'operando sinistro (il valore va sullo stack)
        node.getLeft().accept(this);
        // Memorizzo il tipo dell'operando sinistro per decidere l'istruzione successiva
        LangType type = this.lastType;

        // Genero il codice per l'operando destro (il valore va sopra il sinistro sullo stack)
        node.getRight().accept(this);

        // Seleziono l'istruzione aritmetica in base al tipo (INT o FLOAT)
        if (type == LangType.FLOAT) {
            switch (node.getOp()) {
                case PLUS:
                    sb.append("fadd\n"); // Somma float
                    break;
                case MINUS:
                    sb.append("fsub\n"); // Sottrazione float
                    break;
                case TIMES:
                    sb.append("fmul\n"); // Moltiplicazione float
                    break;
                case DIVIDE:
                    sb.append("fdiv\n"); // Divisione float
                    break;
            }
        } else {
            // Default: INT
            switch (node.getOp()) {
                case PLUS:
                    sb.append("iadd\n"); // Somma intera
                    break;
                case MINUS:
                    sb.append("isub\n"); // Sottrazione intera
                    break;
                case TIMES:
                    sb.append("imul\n"); // Moltiplicazione intera
                    break;
                case DIVIDE:
                    sb.append("idiv\n"); // Divisione intera
                    break;
            }
        }

        // Il risultato dell'operazione mantiene lo stesso tipo degli operandi
        this.lastType = type;
    }
}