package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.exceptions.LexicalException;

/**
 * Suite di test unitari per la classe {@link Scanner}.
 * <p>
 * L'obiettivo è verificare che l'analizzatore lessicale riconosca correttamente
 * tutti i pattern del linguaggio (simboli, numeri, parole chiave) e gestisca
 * adeguatamente le situazioni di errore (caratteri illegali, float malformati).
 * I test simulano l'input scrivendo file temporanei su disco.
 * </p>
 */
public class ScannerTest {

    /**
     * Metodo helper (ausiliario) per facilitare la scrittura dei test.
     * <p>
     * Invece di creare manualmente file di testo multipli, questo metodo accetta una stringa
     * rappresentante il codice sorgente, crea un file temporaneo "test_data.txt",
     * vi scrive il contenuto e restituisce un'istanza di Scanner pronta all'uso.
     * </p>
     *
     * @param stringaCodice Il codice sorgente da testare.
     * @return Un'istanza di Scanner inizializzata sul file temporaneo.
     * @throws IOException In caso di errori di scrittura su disco.
     */
    private Scanner scan(String stringaCodice) throws IOException {
        String filename = "test_data.txt";
        File file = new File(filename);

        // Utilizzo il try-with-resources per garantire che il FileWriter venga chiuso
        // e il file salvato correttamente prima di passarlo allo Scanner.
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(stringaCodice);
        } catch (IOException e) {
            System.err.println("Errore critico nella creazione del file di test: " + e.getMessage());
            throw e;
        }

        // Ritorno lo scanner inizializzato con il percorso del file appena creato
        return new Scanner(filename);
    }

    /**
     * Test dei simboli operatori e delimitatori di base.
     * <p>
     * Verifica anche che lo scanner ignori correttamente gli spazi bianchi tra i token
     * e riconosca il token speciale EOF alla fine dell'input.
     * </p>
     */
    @Test
    void testSimboli() throws Exception {
        // Input con spaziature miste per testare lo skip
        Scanner scanner = scan(" ; + - * / = ");

        // Verifica sequenziale dei tipi di token restituiti
        assertEquals(TokenType.SEMI, scanner.nextToken().getType(), "Il token ';' non è stato riconosciuto correttamente");
        assertEquals(TokenType.PLUS, scanner.nextToken().getType(), "Il token '+' non è stato riconosciuto correttamente");
        assertEquals(TokenType.MINUS, scanner.nextToken().getType(), "Il token '-' non è stato riconosciuto correttamente");
        assertEquals(TokenType.TIMES, scanner.nextToken().getType(), "Il token '*' non è stato riconosciuto correttamente");
        assertEquals(TokenType.DIVIDE, scanner.nextToken().getType(), "Il token '/' non è stato riconosciuto correttamente");
        assertEquals(TokenType.ASSIGN, scanner.nextToken().getType(), "Il token '=' non è stato riconosciuto correttamente");

        // Verifica finale: ci si aspetta EOF
        assertEquals(TokenType.EOF, scanner.nextToken().getType(), "Lo scanner dovrebbe restituire EOF alla fine del file");
    }

    /**
     * Test per il riconoscimento dei letterali numerici (INT e FLOAT).
     * <p>
     * Verifica che lo scanner:
     * 1. Distingua un intero da un float.
     * 2. Memorizzi correttamente il valore testuale nel token (metodo getVal()).
     * </p>
     */
    @Test
    public void testNumeri() throws Exception {
        // Simuliamo un input contenente un intero e un float
        Scanner scanner = scan("123 45.67");

        // 1. Primo token: Ci si aspetta un intero con valore "123"
        Token t1 = scanner.nextToken();
        assertEquals(TokenType.INT, t1.getType(), "Tipo atteso: INT");
        assertEquals("123", t1.getVal(), "Valore atteso: 123");

        // 2. Secondo token: Ci si aspetta un float con valore "45.67"
        Token t2 = scanner.nextToken();
        assertEquals(TokenType.FLOAT, t2.getType(), "Il tipo del secondo token deve essere FLOAT");
        assertEquals("45.67", t2.getVal(), "Il valore del secondo token deve essere 45.67");

        // 3. Terzo token: EOF
        Token t3 = scanner.nextToken();
        assertEquals(TokenType.EOF, t3.getType(), "Dopo i numeri ci si aspetta EOF");
    }

    /**
     * Test per Identificatori e Parole Chiave.
     * <p>
     * Verifica la corretta implementazione della logica di precedenza (Keyword vs ID)
     * e la regola del "Longest Match". Ad esempio, "printer" deve essere un ID, non la keyword "print".
     * </p>
     */
    @Test
    void testKeywords() throws Exception {
        // Input progettato per testare casi limite (print vs printer) e tipi
        Scanner scanner = scan("print printer x int float");

        // "print" è una parola riservata
        assertEquals(TokenType.PRINT, scanner.nextToken().getType(), "Valore atteso: PRINT (keyword)");

        // "printer" non è riservata -> ID
        Token tPrinter = scanner.nextToken();
        assertEquals(TokenType.ID, tPrinter.getType(), "Valore atteso: ID (perché 'printer' non è keyword)");
        assertEquals("printer", tPrinter.getVal(), "Il valore dell'ID deve essere 'printer'");

        // "x" -> ID generico
        assertEquals(TokenType.ID, scanner.nextToken().getType(), "Valore atteso: ID");

        // "int" -> parola riservata TYINT
        assertEquals(TokenType.TYINT, scanner.nextToken().getType(), "Valore atteso: TYINT");

        // "float" -> parola riservata TYFLOAT
        assertEquals(TokenType.TYFLOAT, scanner.nextToken().getType(), "Valore atteso: TYFLOAT");
    }

    /**
     * Test di robustezza e gestione errori (LexicalException).
     * <p>
     * Utilizza assertThrows di JUnit 5 per verificare che lo scanner lanci
     * l'eccezione corretta quando incontra input non validi secondo le specifiche.
     * </p>
     */
    @Test
    void testEccezioni() {
        // Caso 1: Carattere illegale (@ non fa parte dell'alfabeto)
        assertThrows(LexicalException.class, () -> {
            Scanner scanner = scan("@"); // Usa helper scan()
            scanner.nextToken(); // Qui deve esplodere
        }, "Lo scanner dovrebbe lanciare LexicalException per caratteri illegali");

        // Caso 2: Float malformato (punto senza cifre successive, es. "3.")
        assertThrows(LexicalException.class, () -> {
            Scanner scanner = scan("3.");
            scanner.nextToken();
        }, "Lo scanner dovrebbe rifiutare i float che terminano con il punto");

        // Caso 3: Float con troppe cifre decimali (specifica: max 5)
        assertThrows(LexicalException.class, () -> {
            Scanner scanner = scan("3.123456");
            scanner.nextToken();
        }, "Lo scanner dovrebbe lanciare eccezione se ci sono più di 5 cifre decimali");
    }
}