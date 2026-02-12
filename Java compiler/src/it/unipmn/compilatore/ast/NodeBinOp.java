package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Classe che rappresenta un'operazione binaria nell'albero sintattico (AST).
 * Gestisce le operazioni matematiche tra due espressioni (come somma, sottrazione,
 * moltiplicazione e divisione), mantenendo il riferimento all'operatore e ai due operandi.
 */
public class NodeBinOp extends NodeExpr {
    
    // L'operatore matematico
    private final LangOper op;
    // L'espressione che si trova a sinistra dell'operatore
    private NodeExpr left;
    // L'espressione che si trova a destra dell'operatore
    private NodeExpr right;

    /**
     * Costruttore per il nodo di operazione binaria.
     * @param op L'operatore matematico da applicare.
     * @param left Il nodo dell'operando sinistro.
     * @param right Il nodo dell'operando destro.
     * @param riga Il numero di riga in cui si trova l'operazione.
     */
    public NodeBinOp(LangOper op, NodeExpr left, NodeExpr right, int riga) {
        // Salvo la riga passandola alla superclasse NodeExpr
        super(riga);
        this.op = op;
        this.left = left;
        this.right = right;
    }

    /**
     * Restituisce l'operatore matematico del nodo.
     * @return L'operatore LangOper.
     */
    public LangOper getOp() {
        return op;
    }

    /**
     * Restituisce il figlio sinistro dell'operazione.
     * @return Il nodo NodeExpr di sinistra.
     */
    public NodeExpr getLeft() {
        return left;
    }

    /**
     * Sostituisce l'operando sinistro con un nuovo nodo.
     * @param left La nuova espressione da impostare a sinistra.
     */
    public void setLeft(NodeExpr left) {
        // Aggiorno il ramo sinistro (utile quando il visitatore semantico deve inserire un cast implicito a float)
        this.left = left;
    }

    /**
     * Restituisce il figlio destro dell'operazione.
     * @return Il nodo NodeExpr di destra.
     */
    public NodeExpr getRight() {
        return right;
    }

    /**
     * Sostituisce l'operando destro con un nuovo nodo.
     * @param right La nuova espressione da impostare a destra.
     */
    public void setRight(NodeExpr right) {
        // Aggiorno il ramo destro (utile quando il visitatore semantico deve inserire un cast implicito a float)
        this.right = right;
    }

    /**
     * Permette al visitor di ispezionare questo nodo dell'albero.
     * @param visitor Il visitor in esecuzione.
     */
    @Override
    public void accept(IVisitor visitor) {
        // Chiamo il metodo visit specifico per le operazioni binarie
        visitor.visit(this);
    }
}