package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;
import it.unipmn.compilatore.ast.LangType;

/**
 * Test per la Tabella dei Simboli.
 * Verifica inserimento, ricerca, gestione degli scope (ambiti) e shadowing.
 */
public class SymbolTableTest {

    @Test
    void testInserimentoERecupero() {
        SymbolTable st = new SymbolTable();
        // Inserisco una variabile 'a' di tipo INT
        st.insert("a", new Symbol(LangType.INT));

        // Verifico di poterla recuperare
        Symbol s = st.lookup("a");
        assertNotNull(s);
        assertEquals(LangType.INT, s.getType());
    }

    @Test
    void testVariabileNonTrovata() {
        SymbolTable st = new SymbolTable();
        // Cerco una variabile mai inserita
        assertNull(st.lookup("pippo"));
    }

    @Test
    void testInserimentoDuplicato() {
        SymbolTable st = new SymbolTable();
        st.insert("x", new Symbol(LangType.INT));

        // Provo a reinserire la stessa variabile nello stesso scope
        boolean result = st.insert("x", new Symbol(LangType.FLOAT));

        // Verifico che l'inserimento fallisca
        assertFalse(result);
    }

    @Test
    void testScopeEShadowing() {
        SymbolTable st = new SymbolTable();

        // Scope Globale: x è INT
        st.insert("x", new Symbol(LangType.INT));

        // Entro in un nuovo scope locale
        st.enterScope();
        // Inserisco una nuova x (FLOAT) che nasconde quella globale
        st.insert("x", new Symbol(LangType.FLOAT));

        // Verifico che lookup restituisca la versione locale
        assertEquals(LangType.FLOAT, st.lookup("x").getType());

        // Esco dallo scope
        st.exitScope();

        // Verifico che sia tornata visibile la x globale
        assertEquals(LangType.INT, st.lookup("x").getType());
    }
}