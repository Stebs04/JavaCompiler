package it.unipmn.compilatore.symboltable;

import it.unipmn.compilatore.ast.LangType;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Rappresenta un'entità all'interno della Symbol Table.
 * <p>
 * Ogni simbolo mantiene le informazioni semantiche di una variabile:
 * 1. Il tipo (INT o FLOAT).
 * 2. L'offset (indirizzo di memoria relativo) per la generazione del codice bytecode.
 * </p>
 */
public class Symbol {

    private final LangType type;
    private final int offset;

    /**
     * Costruttore principale per creare un simbolo completo.
     *
     * @param type   Il tipo della variabile (INT o FLOAT).
     * @param offset L'indirizzo di memoria assegnato alla variabile.
     */
    public Symbol(LangType type, int offset) {
        // Verifico che il tipo sia valido prima di creare l'oggetto
        if (type == null) {
            throw new SyntacticException("Mancata dichiarazione del tipo");
        }

        this.type = type;
        this.offset = offset;
    }

    /**
     * Costruttore di supporto per la fase di analisi semantica (Type Checking).
     * <p>
     * In questa fase l'offset non è ancora rilevante, quindi lo inizializzo a -1.
     * Richiamo il costruttore principale per centralizzare la logica di controllo.
     * </p>
     *
     * @param type Il tipo della variabile.
     */
    public Symbol(LangType type) {
        this(type, -1);
    }

    /**
     * Restituisce il tipo della variabile.
     */
    public LangType getType() {
        return type;
    }

    /**
     * Restituisce l'offset (indirizzo di memoria) della variabile.
     * Necessario per la fase di generazione del codice.
     */
    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        // Aggiungo l'offset alla rappresentazione stringa per facilitare il debug
        return "Symbol{type=" + type + ", offset=" + offset + "}";
    }
}