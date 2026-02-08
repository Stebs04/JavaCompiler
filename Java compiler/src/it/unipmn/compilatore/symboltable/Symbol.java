package it.unipmn.compilatore.symboltable;

import it.unipmn.compilatore.ast.LangType;

/**
 * Rappresenta un'entità (variabile) all'interno della Symbol Table.
 * <p>
 * Mantengo le informazioni semantiche (tipo) e l'indirizzo di memoria
 * fisico (registro 'a'-'z' per dc) di ogni variabile.
 * </p>
 */
public class Symbol {

    private final LangType type;
    private final char register;

    /**
     * Costruisco un simbolo completo di tipo e registro.
     * Usato durante la generazione del codice.
     * * @param type Il tipo della variabile (INT o FLOAT).
     * @param register Il registro assegnato (es. 'a').
     */
    public Symbol(LangType type, char register) {
        this.type = type;
        this.register = register;
    }

    /**
     * Costruisco un simbolo parziale solo col tipo.
     * Usato durante l'analisi semantica (Type Checking) dove il registro non serve ancora.
     * * @param type Il tipo della variabile.
     */
    public Symbol(LangType type) {
        this(type, '\0'); // Uso un carattere nullo come placeholder
    }

    /**
     * Restituisce il tipo semantico della variabile.
     */
    public LangType getType() {
        return type;
    }

    /**
     * Restituisce il registro fisico associato.
     */
    public char getRegister() {
        return register;
    }
}