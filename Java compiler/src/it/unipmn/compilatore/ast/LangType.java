package it.unipmn.compilatore.ast;

/**
 * Rappresenta i tipi di dato primitivi supportati dal linguaggio durante
 * la fase di analisi semantica e generazione del codice.
 * Questa enumerazione viene utilizzata all'interno dei nodi dell'AST per
 * la verifica della compatibilità dei tipi (Type Checking).
 */
public enum LangType {

    /** Tipo intero a 32 bit */
    INT,

    /** Tipo a virgola mobile (precisione singola/doppia) */
    FLOAT;

}
