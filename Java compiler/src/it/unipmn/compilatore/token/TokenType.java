package it.unipmn.compilatore.token;

/**
 * Enumerazione che definisce le tipologie di token ammesse nel linguaggio.
 * <p>
 * Include tipi primitivi, parole chiave, operatori matematici,
 * operatori di assegnamento e delimitatori.
 * </p>
 */
public enum TokenType {
    // Tipi di dato e Identificatori
    INT,        // Numeri interi
    FLOAT,      // Numeri decimali
    ID,         // Identificatori

    // Parole chiave
    TYINT,      // int
    TYFLOAT,    // float
    PRINT,      // print

    // Operatori di assegnamento
    ASSIGN,     // =

    // Operatori aritmetici
    PLUS,       // +
    MINUS,      // -
    TIMES,      // *
    DIVIDE,     // /

    // Delimitatori e simboli
    SEMI,       // ;
    LPAREN,     // (
    RPAREN,     // )

    // Gestione fine file
    EOF         // End Of File
}