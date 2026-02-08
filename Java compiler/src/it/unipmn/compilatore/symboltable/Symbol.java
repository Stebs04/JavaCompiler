package it.unipmn.compilatore.symboltable;

import it.unipmn.compilatore.ast.LangType;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta un'entità all'interno della Symbol Table.
 * <p>
 * Ogni simbolo mantiene le informazioni semantiche di una variabile,
 * principalmente il suo tipo (INT o FLOAT).
 * </p>
 */
public class Symbol {

    private final LangType type;

    /**
     * Crea un nuovo simbolo con il tipo specificato.
     *
     * @param type Il tipo da associare al simbolo.
     * @throws SyntacticException Se il tipo è nullo.
     */
    public Symbol(LangType type) {
        // Verifico che il tipo sia valido prima di crearlo
        if (type == null) {
            throw new SyntacticException("Mancata dichiarazione del tipo");
        }
        this.type = type;
    }

    public LangType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Symbol{type=" + type + "}";
    }
}