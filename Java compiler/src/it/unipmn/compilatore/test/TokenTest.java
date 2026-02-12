package it.unipmn.compilatore.test;

import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.exceptions.LexicalException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per verificare il corretto funzionamento dei Token.
 * Mi assicuro che la creazione, il recupero dei valori e la formattazione
 * in formato stringa avvengano come richiesto.
 */
public class TokenTest {

    /**
     * Verifica la creazione di un token che non necessita di un valore testuale.
     */
    @Test
    void testTokenSenzaValore() {
        // Creo un token per un operatore matematico
        Token t = new Token(TokenType.PLUS, 10);
        
        // Verifico che i dati salvati corrispondano a quelli inseriti
        assertEquals(TokenType.PLUS, t.getType());
        assertEquals(10, t.getRiga());
        assertNull(t.getVal());
        
        // Verifico la stringa generata per il debug
        assertEquals("<PLUS,r:10>", t.toString());
    }

    /**
     * Verifica la creazione di un token che contiene un valore semantico.
     */
    @Test
    void testTokenConValore() {
        // Creo un token per un numero intero
        Token t = new Token(TokenType.INT, 5, "42");
        
        // Controllo l'assegnamento corretto del valore
        assertEquals(TokenType.INT, t.getType());
        assertEquals(5, t.getRiga());
        assertEquals("42", t.getVal());
        
        // Controllo che la stampa includa il valore
        assertEquals("<INT,r:5,42>", t.toString());
    }

    /**
     * Verifica il comportamento di protezione del costruttore.
     */
    @Test
    void testTokenTipoNullo() {
        // Verifico che venga sollevata l'eccezione prevista se non specifico il tipo
        assertThrows(LexicalException.class, () -> {
            new Token(null, 1, "test");
        });
    }
}