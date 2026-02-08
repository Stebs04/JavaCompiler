package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.exceptions.LexicalException;

/**
 * Test unitari per la classe Token.
 * Verifica che i token vengano costruiti correttamente e che il metodo toString
 * restituisca il formato atteso per il debug.
 */
public class TokenTest {

    @Test
    void testCostruzioneTokenSemplice() {
        // Creo un token che non necessita di valore (es. operatore somma)
        Token t = new Token(TokenType.PLUS, 10);

        // Verifico che il tipo e la riga siano corretti
        assertEquals(TokenType.PLUS, t.getType());
        assertEquals(10, t.getRiga());
        // Controllo che il valore sia nullo come atteso
        assertNull(t.getVal());
    }

    @Test
    void testCostruzioneTokenConValore() {
        // Creo un token Identificatore con valore associato
        Token t = new Token(TokenType.ID, 5, "variabileX");

        // Verifico che il valore sia stato memorizzato
        assertEquals("variabileX", t.getVal());
        assertEquals(TokenType.ID, t.getType());
    }

    @Test
    void testToStringFormat() {
        // Creo un token FLOAT per testare la rappresentazione in stringa
        Token t = new Token(TokenType.FLOAT, 1, "3.14");
        String s = t.toString();

        // Verifico che la stringa contenga tipo, riga e valore
        assertTrue(s.contains("FLOAT"));
        assertTrue(s.contains("r:1"));
        assertTrue(s.contains("3.14"));
    }

    @Test
    void testTipoNullo() {
        // Verifico che il costruttore lanci eccezione se il tipo è null
        assertThrows(LexicalException.class, () -> new Token(null, 1, "test"));
    }
}