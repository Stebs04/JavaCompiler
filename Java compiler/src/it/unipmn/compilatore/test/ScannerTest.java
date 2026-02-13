package it.unipmn.compilatore.test;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.exceptions.LexicalException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per l'Analizzatore Lessicale.
 * Verifico che vengano rilevati caratteri non ammessi e numeri non validi.
 */
public class ScannerTest {

    // Metodo di supporto per creare file al volo
    private File creaFileTemporaneo(String contenuto) throws IOException {
        File temp = File.createTempFile("testScanner", ".txt");
        temp.deleteOnExit();
        FileWriter writer = new FileWriter(temp);
        writer.write(contenuto);
        writer.close();
        return temp;
    }

    /**
     * Verifica che lo scanner lanci eccezione se trova caratteri illegali (@, #, ecc).
     */
    @Test
    void testCaratteriIllegali() throws Exception {
        // Scrivo un carattere '@' che non esiste nella grammatica
        File file = creaFileTemporaneo("int a = 10 @;");
        Scanner scanner = new Scanner(file.getAbsolutePath());

        // Leggo i primi token validi
        scanner.nextToken(); // int
        scanner.nextToken(); // a
        scanner.nextToken(); // =
        scanner.nextToken(); // 10

        // Mi aspetto un errore lessicale leggendo la chiocciola
        assertThrows(LexicalException.class, () -> scanner.nextToken());
        
        scanner.close();
    }

    /**
     * Verifica che lo scanner blocchi i numeri float con troppe cifre decimali (>5).
     */
    @Test
    void testFloatTroppoLungo() throws Exception {
        // Scrivo un numero con 6 cifre decimali
        File file = creaFileTemporaneo("float x = 1.123456;");
        Scanner scanner = new Scanner(file.getAbsolutePath());

        scanner.nextToken(); // float
        scanner.nextToken(); // x
        scanner.nextToken(); // =

        // Verifico l'eccezione specifica per precisione eccessiva
        assertThrows(LexicalException.class, () -> scanner.nextToken());
        
        scanner.close();
    }

    /**
     * Verifica che lo scanner blocchi un float malformato (punto senza cifre dopo).
     */
    @Test
    void testFloatMalformato() throws Exception {
        // Scrivo "5." che è incompleto
        File file = creaFileTemporaneo("x = 5.;");
        Scanner scanner = new Scanner(file.getAbsolutePath());

        scanner.nextToken(); // x
        scanner.nextToken(); // =

        // Verifico l'eccezione poichè il punto deve essere seguito da cifre
        assertThrows(LexicalException.class, () -> scanner.nextToken());

        scanner.close();
    }
    
    /**
     * Verifica il funzionamento standard su codice corretto.
     */
    @Test
    void testTokenCorretti() throws Exception {
        File file = creaFileTemporaneo("print a;");
        Scanner scanner = new Scanner(file.getAbsolutePath());
        
        // Controllo sequenza corretta
        assertEquals(TokenType.PRINT, scanner.nextToken().getType());
        assertEquals(TokenType.ID, scanner.nextToken().getType());
        assertEquals(TokenType.SEMI, scanner.nextToken().getType());
        assertEquals(TokenType.EOF, scanner.nextToken().getType());
        
        scanner.close();
    }
}