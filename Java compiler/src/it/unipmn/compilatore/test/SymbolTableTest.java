package it.unipmn.compilatore.test;

import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;
import it.unipmn.compilatore.ast.LangType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per la Tabella dei Simboli.
 * Verifico che l'inserimento, la ricerca e la gestione dei vari livelli
 * di scope funzionino secondo le regole di visibilità.
 */
public class SymbolTableTest {

    /**
     * Verifica le operazioni base di salvataggio e recupero variabile.
     */
    @Test
    void testInserimentoERicerca() {
        // Inizializzo la tabella
        SymbolTable st = new SymbolTable();
        Symbol sym = new Symbol(LangType.INT);
        
        // Inserisco una nuova variabile nello scope globale
        assertTrue(st.insert("x", sym));
        
        // Cerco la variabile e verifico di ottenere esattamente lo stesso oggetto
        assertEquals(sym, st.lookup("x"));
    }

    /**
     * Verifica la protezione contro le dichiarazioni multiple nello stesso livello.
     */
    @Test
    void testVariabileGiaEsistente() {
        SymbolTable st = new SymbolTable();
        
        // Inserisco la variabile una prima volta
        st.insert("y", new Symbol(LangType.FLOAT));
        
        // Tento di inserirla di nuovo e verifico che l'operazione venga bloccata
        assertFalse(st.insert("y", new Symbol(LangType.INT)));
    }

    /**
     * Verifica la visibilità delle variabili tra scope globale e locale.
     */
    @Test
    void testGestioneScope() {
        SymbolTable st = new SymbolTable();
        Symbol globalSym = new Symbol(LangType.INT);
        st.insert("z", globalSym);
        
        // Entro in un blocco di codice più interno
        st.enterScope();
        
        Symbol localSym = new Symbol(LangType.FLOAT);
        // Inserisco un'altra variabile con lo stesso nome nel nuovo livello
        assertTrue(st.insert("z", localSym));
        
        // Verifico che la ricerca trovi la variabile più interna
        assertEquals(localSym, st.lookup("z"));
        
        // Esco dal blocco di codice
        st.exitScope();
        
        // Verifico che la variabile locale sia sparita e torni visibile quella globale
        assertEquals(globalSym, st.lookup("z"));
    }
}