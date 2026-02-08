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
 * Suite di test per l'intera catena di compilazione.
 * <p>
 * Verifica che la classe Compiler prenda in input un sorgente e produca
 * correttamente il file di output Main.j.
 * </p>
 */
public class CompilerTest {

    private static final String TEST_FILE = "test_integration.txt";
    private static final String OUTPUT_FILE = "Main.j";

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
        assertTrue(outputFile.exists(), "Il file Main.j deve essere generato");

        // Leggo il contenuto del file generato per verificare che sia Jasmin valido
        String content = Files.readString(Path.of(OUTPUT_FILE));

        // Controllo la presenza delle direttive essenziali
        assertTrue(content.contains(".class public Main"), "Il file deve contenere la definizione della classe");
        assertTrue(content.contains(".method public static main"), "Il file deve contenere il metodo main");
        assertTrue(content.contains("ldc 5"), "Il file deve contenere il caricamento della costante 5");
        assertTrue(content.contains("istore 0"), "Il file deve contenere il salvataggio della prima variabile");
        assertTrue(content.contains("invokevirtual java/io/PrintStream/println(I)V"), "Il file deve contenere la stampa di interi");
        assertTrue(content.contains("return"), "Il metodo deve terminare correttamente");
    }

    /**
     * Test con un file che non esiste.
     * Verifico che il compilatore gestisca l'errore senza crashare brutalmente (gestito dal try-catch nel main).
     */
    @Test
    void testFileInesistente() {
        // Eseguo il main con un file che non c'è
        // Mi aspetto che stampi l'errore su System.err ma non lanci eccezione non gestita fuori dal main
        assertDoesNotThrow(() -> Compiler.main(new String[]{"file_fantasma.txt"}));

        // Verifico che NON sia stato creato nessun output
        File outputFile = new File(OUTPUT_FILE);
        assertFalse(outputFile.exists(), "Non deve essere generato output se il file di input non esiste");
    }
}