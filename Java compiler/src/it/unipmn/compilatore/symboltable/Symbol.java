package it.unipmn.compilatore.symboltable;

import it.unipmn.compilatore.ast.LangType;

/**
 * Classe che rappresenta un singolo simbolo (ovvero una variabile) memorizzato all'interno della Symbol Table.
 * Serve a ricordare le informazioni fondamentali di una variabile dichiarata nel programma, 
 * come il suo tipo matematico (intero o decimale) e la sua posizione di memoria nel codice finale (il registro).
 */
public class Symbol {

    // Il tipo della variabile (ad esempio LangType.INT o LangType.FLOAT)
    private final LangType type;
    // Il carattere che rappresenta il registro di memoria nella calcolatrice dc (es. 'a', 'b')
    private final char register;

    /**
     * Costruttore completo per creare un simbolo con tipo e registro.
     * Lo uso principalmente durante la fase di Code Generation, quando assegno la memoria fisica.
     * @param type Il tipo della variabile.
     * @param register La lettera del registro a cui è associata la variabile.
     */
    public Symbol(LangType type, char register) {
        // Salvo il tipo semantico della variabile
        this.type = type;
        // Salvo il registro di memoria fisico in cui verrà salvato il valore
        this.register = register;
    }

    /**
     * Costruttore parziale che inizializza solo il tipo.
     * Lo uso durante la fase di Type Checking, dove mi interessa solo sapere il tipo della 
     * variabile per fare i controlli semantici e non ho ancora bisogno di assegnare il registro.
     * @param type Il tipo della variabile.
     */
    public Symbol(LangType type) {
        // Richiamo il costruttore principale passandogli un carattere nullo ('\0') come segnaposto temporaneo
        this(type, '\0');
    }

    /**
     * Restituisce il tipo matematico della variabile.
     * @return Il tipo LangType (INT o FLOAT).
     */
    public LangType getType() {
        return type;
    }

    /**
     * Restituisce il registro della calcolatrice 'dc' assegnato alla variabile.
     * @return Il carattere del registro (es. 'a').
     */
    public char getRegister() {
        return register;
    }
}