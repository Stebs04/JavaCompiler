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
 * Test di Integrazione Completa.
 * Simula l'esecuzione del programma principale (main) leggendo un file sorgente
 * e verificando che venga prodotto il file di output 'out.dc' corretto.
 */
public class CompilerTest {

    private static final String TEST_FILE = "integration_test.txt";
    private static final String OUTPUT_FILE = "out.dc";

    @AfterEach
    void cleanUp() {
        // Rimuovo i file creati dopo ogni test per pulizia
        new File(TEST_FILE).delete();
        new File(OUTPUT_FILE).delete();
    }

    @Test
    void testCompilazioneEndToEnd() throws IOException {
        // Preparo un codice sorgente che usa tutte le feature
        String sourceCode =
                "int a = 10; " +
                        "float b = 2.5; " +
                        "b = b + a; " + // Test conversione implicita e aritmetica
                        "print b;";

        try (FileWriter writer = new FileWriter(TEST_FILE)) {
            writer.write(sourceCode);
        }

        // Eseguo il main del compilatore
        assertDoesNotThrow(() -> Compiler.main(new String[]{TEST_FILE}));

        // Verifico che il file di output esista
        File output = new File(OUTPUT_FILE);
        assertTrue(output.exists(), "Il file out.dc non è stato creato");

        // Leggo il contenuto dell'output
        String dcCode = Files.readString(Path.of(OUTPUT_FILE));

        // Verifiche sul contenuto generato
        assertTrue(dcCode.contains("20 k"), "Manca header precisione");
        assertTrue(dcCode.contains("sa"), "Manca salvataggio registro a");
        assertTrue(dcCode.contains("sb"), "Manca salvataggio registro b");
        assertTrue(dcCode.contains("+"), "Manca operazione somma");
        assertTrue(dcCode.contains("p"), "Manca comando stampa");
    }
}