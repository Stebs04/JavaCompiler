package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.symboltable.Symbol;
import it.unipmn.compilatore.symboltable.SymbolTable;
import it.unipmn.compilatore.ast.LangType;

/**
 * Suite di test per la Symbol Table.
 * Verifica la corretta gestione degli scope, l'inserimento e la ricerca dei simboli.
 */
public class SymbolTableTest {

    @Test
    void testInserimentoELookupGlobale() {
        SymbolTable st = new SymbolTable();
        Symbol sym = new Symbol(LangType.INT);

        // Inserisco una variabile 'x'
        boolean inserito = st.insert("x", sym);
        assertTrue(inserito, "L'inserimento in tabella vuota deve avere successo");

        // Cerco 'x' e verifico che ci sia
        Symbol trovato = st.lookup("x");
        assertNotNull(trovato, "La variabile 'x' deve essere trovata");
        assertEquals(LangType.INT, trovato.getType());

        // Cerco una variabile inesistente
        assertNull(st.lookup("y"), "La ricerca di variabili inesistenti deve restituire null");
    }

    @Test
    void testScopeVisibilita() {
        SymbolTable st = new SymbolTable();

        // Inserisco 'a' nel globale
        st.insert("a", new Symbol(LangType.INT));

        // Entro in un nuovo scope (es. funzione)
        st.enterScope();

        // 'a' deve essere visibile anche qui
        assertNotNull(st.lookup("a"), "Le variabili globali devono essere visibili negli scope interni");

        // Inserisco 'b' locale
        st.insert("b", new Symbol(LangType.FLOAT));
        assertNotNull(st.lookup("b"));

        // Esco dallo scope
        st.exitScope();

        // 'a' deve esserci ancora, ma 'b' deve essere sparita
        assertNotNull(st.lookup("a"));
        assertNull(st.lookup("b"), "Le variabili locali non devono esistere fuori dal loro scope");
    }

    @Test
    void testShadowing() {
        // Shadowing: una variabile locale nasconde una globale con lo stesso nome
        SymbolTable st = new SymbolTable();

        // x globale è INT
        st.insert("x", new Symbol(LangType.INT));
        assertEquals(LangType.INT, st.lookup("x").getType());

        st.enterScope();

        // x locale è FLOAT (nasconde quella globale)
        st.insert("x", new Symbol(LangType.FLOAT));

        Symbol xTrovato = st.lookup("x");
        assertEquals(LangType.FLOAT, xTrovato.getType(), "La variabile locale deve avere precedenza (shadowing)");

        st.exitScope();

        // Tornati al globale, x deve essere di nuovo INT
        assertEquals(LangType.INT, st.lookup("x").getType(), "Usciti dallo scope, deve tornare visibile la variabile globale");
    }

    @Test
    void testDoppiaDichiarazione() {
        SymbolTable st = new SymbolTable();

        st.insert("x", new Symbol(LangType.INT));

        // Provo a reinserire 'x' nello stesso scope
        boolean risultato = st.insert("x", new Symbol(LangType.FLOAT));

        assertFalse(risultato, "Non deve essere possibile dichiarare due volte la stessa variabile nello stesso scope");
    }
}