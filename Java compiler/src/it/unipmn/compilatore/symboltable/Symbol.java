package it.unipmn.compilatore.symboltable;

import it.unipmn.compilatore.ast.LangType;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta un'entità all'interno della Symbol Table.
 * <p>
 * Ogni simbolo mantiene le informazioni semantiche di una variabile:
 * 1. Il tipo (INT o FLOAT).
 * 2. Il registro (un carattere 'a'-'z') utilizzato come indirizzo di memoria in dc.
 * </p>
 */
public class Symbol {

    private final LangType type;
    private final char register;

    /**
     * Costruttore principale per creare un simbolo completo.
     *
     * @param type     Il tipo della variabile (INT o FLOAT).
     * @param register Il carattere che identifica il registro di memoria in dc.
     */
    public Symbol(LangType type, char register) {
        // Verifico che il tipo sia valido prima di creare l'oggetto
        if (type == null) {
            throw new SyntacticException("Mancata dichiarazione del tipo");
        }

        this.type = type;
        this.register = register;
    }

    /**
     * Costruttore di supporto per la fase di analisi semantica (Type Checking).
     * <p>
     * In questa fase il registro fisico non è ancora rilevante, quindi lo inizializzo
     * con il carattere nullo ('\0').
     * </p>
     *
     * @param type Il tipo della variabile.
     */
    public Symbol(LangType type) {
        // Richiamo il costruttore principale passando un carattere nullo come placeholder
        this(type, '\0');
    }

    /**
     * Restituisce il tipo della variabile.
     *
     * @return Il tipo (INT o FLOAT).
     */
    public LangType getType() {
        return type;
    }

    /**
     * Restituisce il registro associato alla variabile.
     * Necessario per la fase di generazione del codice dc (es. 'a', 'b').
     *
     * @return Il carattere identificativo del registro.
     */
    public char getRegister() {
        return register;
    }

    @Override
    public String toString() {
        // Includo il registro nella rappresentazione stringa per facilitare il debug
        return "Symbol{type=" + type + ", register=" + register + "}";
    }
}