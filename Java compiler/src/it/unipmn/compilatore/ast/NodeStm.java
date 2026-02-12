package it.unipmn.compilatore.ast;

/**
 * Classe astratta base per tutti gli statement (istruzioni).
 * <p>
 * Un statement rappresenta un'azione eseguibile dal programma,
 * come un assegnamento o una stampa.
 * </p>
 */
public abstract class NodeStm extends NodeDecSt {

    /**
     * Costruttore per gli statement.
     * @param riga Il numero di riga dell'istruzione.
     */
    public NodeStm(int riga) {
        // Invoco il costruttore della superclasse NodeDecSt
        super(riga);
    }
}