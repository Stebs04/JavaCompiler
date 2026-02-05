package it.unipmn.compilatore.ast;

/**
 * Enumerazione che definisce gli operatori aritmetici binari
 * riconosciuti dal parser del linguaggio.
 * Questi token vengono istanziati nei nodi dell'AST per rappresentare
 * le espressioni matematiche durante la fase di visita del decoratore.
 */
public enum LangOper {

    /** Operatore di addizione (+) */
    PLUS,

    /** Operatore di sottrazione (-) */
    MINUS,

    /** Operatore di moltiplicazione (*) */
    TIMES,

    /** Operatore di divisione (/) */
    DIVIDE;
}