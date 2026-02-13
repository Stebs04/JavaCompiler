package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;

/**
 * Classe che implementa il visitatore semantico per il controllo dei tipi.
 * Naviga l'albero sintattico (AST) per verificare che le operazioni
 * rispettino le regole di tipo del linguaggio e gestisce le conversioni
 * implicite da intero a decimale.
 * Mantiene un log delle operazioni svolte e degli errori riscontrati.
 */
public class TypeCheckVisitor implements IVisitor {

    // Tabella dei simboli per memorizzare le variabili dichiarate e i loro tipi
    private SymbolTable scopes;
    // Variabile di appoggio per propagare il tipo calcolato dal basso verso l'alto nell'albero
    private LangType lastType;
    // StringBuilder per accumulare i log delle operazioni e degli errori
    private StringBuilder log;

    /**
     * Costruttore del visitatore per il controllo dei tipi.
     * Inizializza una nuova tabella dei simboli vuota e il log.
     */
    public TypeCheckVisitor() {
        this.scopes = new SymbolTable();
        this.log = new StringBuilder();
    }

    /**
     * Restituisce il contenuto del log accumulato durante la visita.
     * @return La stringa con la cronologia delle operazioni ed eventuali errori.
     */
    public String getLog() {
        return log.toString();
    }

    /**
     * Avvia il controllo semantico sull'intero programma.
     * @param node Il nodo radice del programma.
     */
    @Override
    public void visit(NodeProgram node) {
        log.append("Inizio visita del Programma.\n");
        // Itero su tutte le istruzioni del programma per controllarne la semantica
        for (NodeDecSt stmt : node.getStatements()) {
            stmt.accept(this);
        }
        log.append("Fine visita del Programma.\n");
    }

    /**
     * Analizza una dichiarazione di variabile e la sua eventuale inizializzazione.
     * @param node Il nodo di dichiarazione.
     */
    @Override
    public void visit(NodeDecl node) {
        String varName = node.getId().getName();
        log.append("Controllo dichiarazione variabile: ").append(varName).append("\n");

        // Verifico se la variabile che si sta dichiarando esiste già nella tabella dei simboli
        if (scopes.lookup(varName) != null) {
            String errorMsg = "Errore: Variabile '" + varName + "' già dichiarata.";
            log.append(errorMsg).append("\n");
            throw new SyntacticException(errorMsg);
        }

        // Inserisco la nuova variabile e il suo tipo all'interno della tabella dei simboli
        scopes.insert(varName, new Symbol(node.getType()));
        log.append("Variabile '").append(varName).append("' inserita nella Symbol Table.\n");

        // Se c'è un'espressione di inizializzazione associata, la analizzo
        if (node.getInit() != null) {
            node.getInit().accept(this);

            // Verifico se serve una conversione implicita da intero a decimale
            if (node.getType() == LangType.FLOAT && lastType == LangType.INT) {
                log.append("Conversione implicita (Cast) da INT a FLOAT per: ").append(varName).append("\n");
                // Creo un nodo di conversione e lo sostituisco all'espressione originale
                NodeConvert convert = new NodeConvert(node.getInit(), LangType.FLOAT);
                node.setInit(convert);
            } else if (node.getType() != lastType) {
                String errorMsg = "Errore: Tipo non compatibile nell'inizializzazione di " + varName;
                log.append(errorMsg).append("\n");
                throw new SyntacticException(errorMsg);
            }
        }
    }

    /**
     * Analizza un'operazione di assegnamento a una variabile esistente.
     * @param node Il nodo di assegnamento.
     */
    @Override
    public void visit(NodeAssign node) {
        String varName = node.getId().getName();
        log.append("Controllo assegnamento a: ").append(varName).append("\n");

        // Cerco la variabile nella tabella dei simboli per estrarre il suo tipo
        Symbol symbol = scopes.lookup(varName);
        if (symbol == null) {
            String errorMsg = "Errore: Variabile non dichiarata: " + varName;
            log.append(errorMsg).append("\n");
            throw new SyntacticException(errorMsg);
        }

        // Analizzo l'espressione a destra dell'uguale per calcolarne il tipo
        node.getExpr().accept(this);

        // Controllo se il tipo della variabile e quello dell'espressione combaciano
        if (symbol.getType() == LangType.FLOAT && lastType == LangType.INT) {
            log.append("Conversione implicita (Cast) da INT a FLOAT nell'assegnamento a: ").append(varName).append("\n");
            // Inserisco il nodo di conversione nell'albero per trasformare l'intero in decimale
            NodeConvert convert = new NodeConvert(node.getExpr(), LangType.FLOAT);
            node.setExpr(convert);
            // Aggiorno il tipo propagato per riflettere la conversione appena eseguita
            lastType = LangType.FLOAT;
        } else if (symbol.getType() != lastType) {
            String errorMsg = "Errore: Assegnazione non compatibile per " + varName;
            log.append(errorMsg).append("\n");
            throw new SyntacticException(errorMsg);
        }
    }

    /**
     * Analizza le operazioni binarie (somma, sottrazione, moltiplicazione, divisione).
     * @param node Il nodo dell'operazione matematica.
     */
    @Override
    public void visit(NodeBinOp node) {
        log.append("Controllo operazione binaria: ").append(node.getOp()).append("\n");

        // Visito il figlio sinistro e mi salvo il tipo che restituisce
        node.getLeft().accept(this);
        LangType leftType = lastType;

        // Visito il figlio destro e mi salvo il tipo che restituisce
        node.getRight().accept(this);
        LangType rightType = lastType;

        // Calcolo il tipo finale dell'operazione matematica
        if (leftType == LangType.INT && rightType == LangType.INT) {
            // Una operazione tra interi restituisce sempre un intero
            lastType = LangType.INT;
        } else {
            // Se c'è almeno un operando decimale, il risultato totale diventa decimale
            lastType = LangType.FLOAT;

            // Converto esplicitamente il sotto-albero sinistro se era intero
            if (leftType == LangType.INT) {
                log.append("Cast operando sinistro a FLOAT.\n");
                node.setLeft(new NodeConvert(node.getLeft(), LangType.FLOAT));
            }
            // Converto esplicitamente il sotto-albero destro se era intero
            if (rightType == LangType.INT) {
                log.append("Cast operando destro a FLOAT.\n");
                node.setRight(new NodeConvert(node.getRight(), LangType.FLOAT));
            }
        }
        log.append("Tipo risultante operazione: ").append(lastType).append("\n");
    }

    /**
     * Analizza l'utilizzo di una variabile all'interno di un'espressione.
     * @param node Il nodo di dereferenziazione.
     */
    @Override
    public void visit(NodeDeref node) {
        String varName = node.getId().getName();
        // Cerco la variabile per assicurarmi che sia stata dichiarata in precedenza
        Symbol symbol = scopes.lookup(varName);
        if (symbol == null) {
            String errorMsg = "Errore: Uso di variabile non dichiarata: " + varName;
            log.append(errorMsg).append("\n");
            throw new SyntacticException(errorMsg);
        }
        // Imposto il tipo della variabile come tipo corrente per passarlo alle operazioni superiori
        lastType = symbol.getType();
    }

    /**
     * Analizza il nodo che rappresenta il nome puro della variabile.
     * @param node Il nodo identificatore.
     */
    @Override
    public void visit(NodeId node) {
        // L'identificatore puro non esegue operazioni qui
    }

    /**
     * Analizza una costante numerica.
     * @param node Il nodo costante.
     */
    @Override
    public void visit(NodeCost node) {
        // Estraggo il tipo della costante (intero o decimale) e lo propago verso l'alto
        lastType = node.getType();
    }

    /**
     * Analizza l'istruzione di stampa a video.
     * @param node Il nodo print.
     */
    @Override
    public void visit(NodePrint node) {
        String varName = node.getId().getName();
        log.append("Controllo istruzione Print per: ").append(varName).append("\n");
        // Verifico solo che la variabile che si vuole stampare esista nella tabella dei simboli
        if (scopes.lookup(varName) == null) {
            String errorMsg = "Errore: Tentativo di stampa di variabile non dichiarata '" + varName + "'";
            log.append(errorMsg).append("\n");
            throw new SyntacticException(errorMsg);
        }
    }

    /**
     * Analizza un nodo di conversione di tipo.
     * @param node Il nodo di cast dinamico.
     */
    @Override
    public void visit(NodeConvert node) {
        log.append("Controllo nodo conversione esplicita.\n");
        // Analizzo l'espressione racchiusa nel nodo di conversione
        node.getExpr().accept(this);
        // Il tipo risultante è forzato al tipo destinazione della conversione
        lastType = node.getTargetType();
    }
}