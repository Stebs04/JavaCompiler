package it.unipmn.compilatore.token;

/** 
 * Enumeratori per definire la tipologia dei token
 * @author Bellan Stefano 20054330
 **/
public enum TokenType {
    //Tipi di dato e Identificatori
    INT, // Numeri interi (5, 50)
    FLOAT, // Numeri decimali (3.14, 0.5)
    ID, // Identificatori variabili (es temp, x, somma)

    //Parola chiave (Keywords)
    TYINT, // Parola chiave 'int'
    TYFLOAT, // Parola chiave 'float'
    PRINT, // Parola chiave 'print'

    //Operatori di assegnamento
    ASSIGN, // '='
    OP_ASSIGN, // '+=' '-=' '*=' '/='

    //Operatori aritmetici
    PLUS, // '+'
    MINUS, // '-'
    TIMES, // '*'
    DIVIDE, // '/'

    //Delimitatori e simboli
    SEMI, // ?;

    //Gestione fine file
    EOF // Fine del file
}
