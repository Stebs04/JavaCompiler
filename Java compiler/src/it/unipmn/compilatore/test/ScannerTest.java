package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.exceptions.LexicalException;

/**
 * Test per l'Analizzatore Lessicale (Scanner).
 * Controlla il riconoscimento di keyword, numeri, identificatori, operatori
 * e la gestione di caratteri illegali o formati numerici errati.
 */
public class ScannerTest {

    @Test
    void testKeywordEIdentificatori(@TempDir Path tempDir) throws Exception {
        // Scrivo un file temporaneo con keyword e ID misti
        Path file = tempDir.resolve("test_kw.txt");
        Files.writeString(file, "int myVar print");

        Scanner scanner = new Scanner(file.toString());

        // Verifico la sequenza corretta dei token
        assertEquals(TokenType.TYINT, scanner.nextToken().getType());
        assertEquals(TokenType.ID, scanner.nextToken().getType()); // myVar
        assertEquals(TokenType.PRINT, scanner.nextToken().getType());
        assertEquals(TokenType.EOF, scanner.nextToken().getType());

        scanner.close();
    }

    @Test
    void testNumeri(@TempDir Path tempDir) throws Exception {
        // Scrivo interi e float validi
        Path file = tempDir.resolve("test_num.txt");
        Files.writeString(file, "123 45.67");

        Scanner scanner = new Scanner(file.toString());

        // Controllo il riconoscimento dell'intero
        assertEquals(TokenType.INT, scanner.nextToken().getType());
        // Controllo il riconoscimento del float
        assertEquals(TokenType.FLOAT, scanner.nextToken().getType());

        scanner.close();
    }

    @Test
    void testSimboliEOperatori(@TempDir Path tempDir) throws Exception {
        // Scrivo tutti i simboli supportati
        Path file = tempDir.resolve("test_sym.txt");
        Files.writeString(file, "+ - * / = ; ( )");

        Scanner scanner = new Scanner(file.toString());

        // Verifico uno a uno che vengano riconosciuti
        assertEquals(TokenType.PLUS, scanner.nextToken().getType());
        assertEquals(TokenType.MINUS, scanner.nextToken().getType());
        assertEquals(TokenType.TIMES, scanner.nextToken().getType());
        assertEquals(TokenType.DIVIDE, scanner.nextToken().getType());
        assertEquals(TokenType.ASSIGN, scanner.nextToken().getType());
        assertEquals(TokenType.SEMI, scanner.nextToken().getType());
        assertEquals(TokenType.LPAREN, scanner.nextToken().getType());
        assertEquals(TokenType.RPAREN, scanner.nextToken().getType());

        scanner.close();
    }

    @Test
    void testFloatTroppoLungo(@TempDir Path tempDir) throws Exception {
        // Scrivo un float con 6 cifre decimali (il limite è 5)
        Path file = tempDir.resolve("test_float_err.txt");
        Files.writeString(file, "1.123456");

        Scanner scanner = new Scanner(file.toString());

        // Verifico che scatti l'eccezione lessicale
        assertThrows(LexicalException.class, scanner::nextToken);
        scanner.close();
    }

    @Test
    void testFloatMalformato(@TempDir Path tempDir) throws Exception {
        // Scrivo un float senza cifre decimali
        Path file = tempDir.resolve("test_float_bad.txt");
        Files.writeString(file, "1.");

        Scanner scanner = new Scanner(file.toString());

        // Verifico che scatti l'errore
        assertThrows(LexicalException.class, scanner::nextToken);
        scanner.close();
    }
}