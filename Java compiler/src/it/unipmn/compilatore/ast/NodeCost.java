package it.unipmn.compilatore.ast;

import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.visitor.IVisitor;

/**
 * Classe che rappresenta una costante letterale numerica nell'albero sintattico (AST).
 * Questo nodo gestisce i numeri espliciti scritti nel codice sorgente, 
 * che possono essere di tipo intero (es. 5) o decimale (es. 3.14).
 */
public class NodeCost extends NodeExpr {

    // Il valore testuale del numero letto dal file sorgente
    private final String value;
    // Il tipo associato a questo numero (INT o FLOAT)
    private final LangType type;

    /**
     * Costruttore per il nodo della costante numerica.
     * @param type Il tipo del dato (LangType.INT o LangType.FLOAT).
     * @param value Il valore testuale della costante (es. "3.14").
     * @param riga La riga in cui appare la costante nel file sorgente.
     * @throws SyntacticException Se i parametri obbligatori sono nulli o vuoti.
     */
    public NodeCost(LangType type, String value, int riga) {
        // Salvo la riga passandola alla superclasse NodeExpr
        super(riga);

        // Verifico che il tipo sia stato impostato correttamente dal parser
        if (type == null) {
            throw new SyntacticException("Tipo mancante nella Costante alla riga " + riga);
        }
        // Controllo che la stringa del valore contenga effettivamente un numero
        if (value == null || value.isBlank()) {
            throw new SyntacticException("Valore mancante o vuoto nella Costante alla riga " + riga);
        }

        this.type = type;
        this.value = value;
    }

    /**
     * Restituisce il valore del numero salvato.
     * @return La stringa con il valore.
     */
    public String getValue() {
        return value;
    }

    /**
     * Restituisce il tipo matematico della costante.
     * @return Il tipo LangType (INT o FLOAT).
     */
    public LangType getType() {
        return type;
    }

    /**
     * Fornisce una stringa leggibile del nodo per le operazioni di stampa e debug.
     */
    @Override
    public String toString() {
        // Formatto l'output racchiudendo le informazioni tra parentesi angolari
        return "<Cost: " + type + ", " + value + ">";
    }

    /**
     * Permette al visitor di ispezionare questo nodo.
     * @param visitor Il visitor in esecuzione.
     */
    @Override
    public void accept(IVisitor visitor){
        // Invoco il metodo di visita specifico per le costanti numeriche
        visitor.visit(this);
    }
}