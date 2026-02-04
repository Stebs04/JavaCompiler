package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.exceptions.LexicalException;



public class TokenTest {

    /**
     * Verifica l'inizializzazione del metodo toString per un token numerico o identificatore
     */
    @Test
    void testTokenConValore() {
       //Creo un token di test
        Token token = new Token(TokenType.INT, 5, "100");

        //Controllo che i getters ritornino i giusti valori
       assertEquals(TokenType.INT, token.getType(), "Il tipo del token deve essere INT");
       assertEquals(5, token.getRiga(),  "La riga del token deve essere 5");
       assertEquals("100",token.getVal(), "Il valore del token deve essere '100'");

        //controllo che toString() ritorni esattamente "<INT, r:5,100>"
        assertEquals("<INT,r:5,100>", token.toString(), "Il formato stringa non corrisponde");
    }

    /**
     * Verifica che un token non necessiti di un valore semantico
     */
    @Test
   void testTokenSenzaValore(){
        //Creo un token di test senza un valore
        Token token = new Token(TokenType.PLUS, 10);

        //Controllo che i getters ritornino i giusti valori
        assertEquals(TokenType.PLUS, token.getType(), "Il tipo del token deve essere PLUS");
        assertEquals(10, token.getRiga(),  "La riga del token deve essere 10");
        assertNull(token.getVal(), "in questo caso il valore deve essere null");

        //controllo che toString() ritorni esattamente "<INT, r:10>"
        assertEquals("<PLUS,r:10>", token.toString(), "Il formato stringa non corrisponde");
    }

    /**
     * Verifica che passare null come tokenType lanci una eccezione
     */
    @Test
    void testEccezioneSuTipoNullo(){
       assertThrows(LexicalException.class, ()-> {
           new Token(null, 5, "100");
       }, "Il costruttore dovrebbe lanciare LexicalException se il tipo + nulll");
    }
}
