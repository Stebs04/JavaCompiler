package it.unipmn.compilatore.ast;

/**
 * Enumerazione dei tipi di dato primitivi gestiti dal compilatore.
 * <p>
 * Serve per assegnare un tipo semantico ai nodi (es. costanti o dichiarazioni).
 * Sarà cruciale nella fase successiva di Type Checking.
 * </p>
 */
public enum LangType {
    /** Tipo intero (int) */
    INT,
    /** Tipo virgola mobile (float) */
    FLOAT;
}