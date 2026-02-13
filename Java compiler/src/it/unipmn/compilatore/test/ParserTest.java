package it.unipmn.compilatore.test;

import it.unipmn.compilatore.parser.Parser;
import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.exceptions.SyntacticException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per l'Analizzatore Sintattico.
 * Verifico che la struttura grammaticale errata sollevi eccezioni.
 */
public class ParserTest {

    private File creaFileTemporaneo(String contenuto) throws IOException {
        File temp = File.createTempFile("testParser", ".txt");
        temp.deleteOnExit();
        FileWriter writer = new FileWriter(temp);
        writer.write(contenuto);
        writer.close();
        return temp;
    }

    /**
     * Verifica errore se manca il punto e virgola finale.
     */
    @Test
    void testMancaPuntoVirgola() throws Exception {
        File file = creaFileTemporaneo("int a = 5"); // Manca ;
        Scanner scanner = new Scanner(file.getAbsolutePath());
        Parser parser = new Parser(scanner);

        // Il parser deve fallire alla fine dell'istruzione
        assertThrows(SyntacticException.class, () -> parser.parse());
        
        scanner.close();
    }

    /**
     * Verifica errore se manca l'identificatore nella dichiarazione.
     */
    @Test
    void testDichiarazioneErrata() throws Exception {
        File file = creaFileTemporaneo("int = 10;"); // Manca il nome variabile
        Scanner scanner = new Scanner(file.getAbsolutePath());
        Parser parser = new Parser(scanner);

        assertThrows(SyntacticException.class, () -> parser.parse());
        
        scanner.close();
    }

    /**
     * Verifica errore se le parentesi non sono bilanciate.
     */
    @Test
    void testParentesiNonBilanciate() throws Exception {
        File file = creaFileTemporaneo("x = (5 + 2 * 3;"); // Manca parentesi chiusa
        Scanner scanner = new Scanner(file.getAbsolutePath());
        Parser parser = new Parser(scanner);

        assertThrows(SyntacticException.class, () -> parser.parse());
        
        scanner.close();
    }

    /**
     * Verifica errore se l'espressione è incompleta (operatore senza secondo operando).
     */
    @Test
    void testEspressioneIncompleta() throws Exception {
        File file = creaFileTemporaneo("x = 5 + ;"); // Manca numero dopo il +
        Scanner scanner = new Scanner(file.getAbsolutePath());
        Parser parser = new Parser(scanner);

        assertThrows(SyntacticException.class, () -> parser.parse());
        
        scanner.close();
    }
}