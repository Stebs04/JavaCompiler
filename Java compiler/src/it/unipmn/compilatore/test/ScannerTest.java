package it.unipmn.compilatore.test;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.exceptions.LexicalException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per l'Analizzatore Lessicale (Scanner).
 * Verifico la traduzione del testo in Token e la corretta rimozione di spazi e ritorni a capo.
 */
public class ScannerTest {

    /**
     * Metodo di supporto che crea un file fisico per simulare l'input.
     */
    private File creaFileTemporaneo(String contenuto) throws IOException {
        // Creo un file nel filesystem e indico di cancellarlo a fine esecuzione
        File temp = File.createTempFile("testScanner", ".txt");
        temp.deleteOnExit();
        
        // Scrivo il codice sorgente nel file
        FileWriter writer = new FileWriter(temp);
        writer.write(contenuto);
        writer.close();
        
        return temp;
    }

    /**
     * Verifica il riconoscimento di un'istruzione di dichiarazione e salto riga.
     */
    @Test
    void testLetturaSimboli() throws Exception {
        // Preparo il file con del codice di prova
        File file = creaFileTemporaneo("int x = 5; \n 3.14");
        Scanner scanner = new Scanner(file.getAbsolutePath());

        // Verifico in sequenza la creazione di ogni singolo token
        assertEquals(TokenType.TYINT, scanner.nextToken().getType());
        
        Token t2 = scanner.nextToken();
        assertEquals(TokenType.ID, t2.getType());
        assertEquals("x", t2.getVal());
        
        assertEquals(TokenType.ASSIGN, scanner.nextToken().getType());
        
        Token t4 = scanner.nextToken();
        assertEquals(TokenType.INT, t4.getType());
        assertEquals("5", t4.getVal());
        
        assertEquals(TokenType.SEMI, scanner.nextToken().getType());

        // Verifico che dopo l'a capo il numero di riga sia aumentato
        Token t6 = scanner.nextToken();
        assertEquals(TokenType.FLOAT, t6.getType());
        assertEquals("3.14", t6.getVal());
        assertEquals(2, t6.getRiga());

        scanner.close();
    }

    /**
     * Verifica il riconoscimento di caratteri estranei al linguaggio.
     */
    @Test
    void testCarattereNonValido() throws Exception {
        // Preparo un file contenente un simbolo illegale come la chiocciola
        File file = creaFileTemporaneo("int x = 5 @ 2;");
        Scanner scanner = new Scanner(file.getAbsolutePath());

        // Itero per consumare i primi token corretti
        for (int i = 0; i < 4; i++) {
            scanner.nextToken();
        }

        // Verifico che il carattere illegale generi un'eccezione
        assertThrows(LexicalException.class, () -> {
            scanner.nextToken();
        });
        
        scanner.close();
    }
}