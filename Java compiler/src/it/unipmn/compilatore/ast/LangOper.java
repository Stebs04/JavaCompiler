package it.unipmn.compilatore.ast;

/**
 * Enumerazione degli operatori binari per le espressioni matematiche.
 * <p>
 * Disaccoppia l'AST dai Token dello scanner. Ogni operazione matematica
 * (somma, sottrazione, ecc.) viene rappresentata da uno di questi valori.
 * </p>
 */
public enum LangOper {
    PLUS,   // +
    MINUS,  // -
    TIMES,  // *
    DIVIDE  // /
}