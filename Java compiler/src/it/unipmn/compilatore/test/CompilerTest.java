package it.unipmn.compilatore.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import it.unipmn.compilatore.Compiler;

/**
 * Suite di test per l'intera catena di compilazione (Integration Test).
 * <p>
 * Verifico che la classe Compiler prenda in input un sorgente e produca
 * correttamente il file di output out.dc per la calcolatrice dc.
 * </p>
 */
public class CompilerTest {

    private static final String TEST_FILE = "test_integration.txt";
    private static final String OUTPUT_FILE = "out.dc";

    /**
     * Pulizia dopo ogni test.
     * Rimuovo i file temporanei creati per non lasciare sporcizia nel progetto.
     */
    @AfterEach
    void cleanUp() {
        File source = new File(TEST_FILE);
        File output = new File(OUTPUT_FILE);

        if (source.exists()) {
            source.delete();
        }
        if (output.exists()) {
            output.delete();
        }
    }

    /**
     * Test di un ciclo completo di compilazione con codice valido.
     */
    @Test
    void testCompilazioneCompleta() throws IOException {
        // Preparo un codice sorgente completo che usa tutte le funzionalità
        String sourceCode =
                "int a = 5;" +
                        "int b = 3;" +
                        "int c;" +
                        "c = a + b * 2;" + // c = 5 + 6 = 11
                        "print c;" +
                        "float x = 3.14;" +
                        "print x;";

        // Scrivo il codice su un file temporaneo
        try (FileWriter writer = new FileWriter(TEST_FILE)) {
            writer.write(sourceCode);
        }

        // Eseguo il compilatore passando il file come argomento
        // Non deve lanciare eccezioni
        assertDoesNotThrow(() -> Compiler.main(new String[]{TEST_FILE}),
                "La compilazione non deve fallire con codice valido");

        // Verifico che il file di output sia stato creato
        File outputFile = new File(OUTPUT_FILE);
        assertTrue(outputFile.exists(), "Il file out.dc deve essere generato");

        // Leggo il contenuto del file generato per verificare che sia codice dc valido
        String content = Files.readString(Path.of(OUTPUT_FILE));

        // Controllo la presenza delle istruzioni essenziali per dc
        assertTrue(content.contains("20 k"), "Il file deve impostare la precisione decimale");
        assertTrue(content.contains("sa"), "Il file deve contenere il salvataggio nel registro 'a'");
        assertTrue(content.contains("sb"), "Il file deve contenere il salvataggio nel registro 'b'");
        assertTrue(content.contains("la"), "Il file deve contenere il caricamento dal registro 'a'");
        assertTrue(content.contains("5 "), "Il file deve contenere la costante 5");
        assertTrue(content.contains("+"), "Il file deve contenere l'operatore somma");
        assertTrue(content.contains("*"), "Il file deve contenere l'operatore moltiplicazione");
        assertTrue(content.contains("p"), "Il file deve contenere il comando di stampa");
        assertTrue(content.contains("si"), "Il file deve contenere il comando di pulizia stack (pop)");
    }

    /**
     * Test con un file che non esiste.
     * Verifico che il compilatore gestisca l'errore senza crashare.
     */
    @Test
    void testFileInesistente() {
        // Eseguo il main con un file che non c'è
        assertDoesNotThrow(() -> Compiler.main(new String[]{"file_fantasma.txt"}));

        // Verifico che NON sia stato creato nessun output
        File outputFile = new File(OUTPUT_FILE);
        assertFalse(outputFile.exists(), "Non deve essere generato output se il file di input non esiste");
    }
}