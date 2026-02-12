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
 * Classe di test per l'integrazione completa del sistema (End-to-End).
 * Simula l'esecuzione del programma principale passandogli un file di testo reale,
 * per verificare che tutte le fasi (Scanner, Parser, TypeCheck, CodeGen) lavorino 
 * bene insieme e producano il file finale 'out.dc'.
 */
public class CompilerTest {

    // Definisco i nomi dei file fisici che uso per simulare input e output
    private static final String TEST_FILE = "integration_test.txt";
    private static final String OUTPUT_FILE = "out.dc";

    /**
     * Metodo eseguito in automatico da JUnit al termine di ogni singolo test.
     * Pulisce l'ambiente di lavoro eliminando i file temporanei creati.
     */
    @AfterEach
    void cleanUp() {
        // Elimino il file sorgente fittizio che ho creato per il test
        new File(TEST_FILE).delete();
        // Elimino il file di output generato dal compilatore
        new File(OUTPUT_FILE).delete();
    }

    /**
     * Verifica l'intero ciclo di vita della compilazione.
     * @throws IOException Se ci sono problemi nella scrittura o lettura dei file sul disco.
     */
    @Test
    void testCompilazioneEndToEnd() throws IOException {
        // Preparo una stringa con un piccolo programma che sfrutta le istruzioni principali (inclusa la conversione implicita)
        String sourceCode = 
                "int a = 10; " +
                "float b = 2.5; " +
                "b = b + a; " + 
                "print b;";

        // Scrivo il codice sorgente all'interno del file di testo temporaneo
        try (FileWriter writer = new FileWriter(TEST_FILE)) {
            writer.write(sourceCode);
        }

        // Avvio il compilatore passandogli il nome del file e verifico che concluda senza andare in crash
        assertDoesNotThrow(() -> Compiler.main(new String[]{TEST_FILE}));

        // Istanzio un oggetto File per puntare all'output atteso
        File output = new File(OUTPUT_FILE);
        
        // Verifico che il compilatore abbia effettivamente creato il file di destinazione sul disco
        assertTrue(output.exists(), "Il file out.dc non è stato creato");

        // Leggo tutto il testo contenuto nel file appena generato
        String dcCode = Files.readString(Path.of(OUTPUT_FILE));

        // Controllo che il codice tradotto contenga i comandi essenziali della calcolatrice 'dc'
        assertTrue(dcCode.contains("20 k"), "Manca header precisione");
        assertTrue(dcCode.contains("sa"), "Manca salvataggio registro a");
        assertTrue(dcCode.contains("sb"), "Manca salvataggio registro b");
        assertTrue(dcCode.contains("+"), "Manca operazione somma");
        assertTrue(dcCode.contains("p"), "Manca comando stampa");
    }
}