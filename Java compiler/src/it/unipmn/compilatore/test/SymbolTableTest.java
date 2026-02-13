package it.unipmn.compilatore.test;

import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;
import it.unipmn.compilatore.ast.LangType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per la Tabella dei Simboli.
 * Verifico la gestione dei duplicati e la visibilità.
 */
public class SymbolTableTest {

    /**
     * Verifica che non sia possibile inserire due variabili uguali nello stesso scope.
     */
    @Test
    void testInserimentoDuplicato() {
        SymbolTable st = new SymbolTable();
        Symbol sym = new Symbol(LangType.INT);
        
        // Inserisco la prima volta: deve tornare true
        assertTrue(st.insert("x", sym));
        
        // Inserisco la seconda volta: deve tornare false (errore)
        assertFalse(st.insert("x", sym));
    }

    /**
     * Verifica che la ricerca di una variabile inesistente ritorni null.
     */
    @Test
    void testRicercaFallita() {
        SymbolTable st = new SymbolTable();
        // Cerco una variabile mai inserita
        assertNull(st.lookup("pippo"));
    }

    /**
     * Verifica che una variabile in uno scope interno non sia visibile fuori.
     */
    @Test
    void testScopeIsolamento() {
        SymbolTable st = new SymbolTable();
        
        st.enterScope();
        st.insert("temp", new Symbol(LangType.INT));
        // Qui deve esistere
        assertNotNull(st.lookup("temp"));
        
        st.exitScope();
        // Qui non deve più esistere
        assertNull(st.lookup("temp"));
    }
}