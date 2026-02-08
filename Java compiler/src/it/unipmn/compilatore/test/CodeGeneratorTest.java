package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.parser.Parser;
import it.unipmn.compilatore.ast.NodeProgram;
import it.unipmn.compilatore.visitor.CodeGeneratorVisitor;
import it.unipmn.compilatore.exceptions.LexicalException;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Suite di test unitari per il CodeGeneratorVisitor.
 * <p>
 * Verifica che il codice Assembly Jasmin generato corrisponda alle aspettative
 * per le diverse strutture del linguaggio (dichiarazioni, assegnamenti, operazioni, stampa).
 * </p>
 */
public class CodeGeneratorTest {

    /**
     * Metodo ausiliario per compilare una stringa di codice sorgente e ottenerne il bytecode.
     * <p>
     * Questo metodo simula l'intero processo: salvataggio su file, parsing, generazione dell'AST
     * e visita con il CodeGenerator.
     * </p>
     *
     * @param code Il codice sorgente da testare.
     * @return La stringa contenente il codice Jasmin generato.
     */
    private String getGeneratedCode(String code) throws IOException, LexicalException, SyntacticException {
        // Creo un file temporaneo per il test
        File file = new File("test_codegen.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }

        // Inizializzo Scanner e Parser sul file appena creato
        Scanner scanner = new Scanner("test_codegen.txt");
        Parser parser = new Parser(scanner);

        // Ottengo l'AST dal parser
        NodeProgram program = parser.parse();

        // Istanzio il visitatore per la generazione del codice
        CodeGeneratorVisitor generator = new CodeGeneratorVisitor();

        // Avvio la visita dell'AST
        program.accept(generator);

        // Restituisco il codice assembly accumulato
        return generator.getCode();
    }

    /**
     * Verifica la struttura di base del file Jasmin generato.
     * <p>
     * Controllo che siano presenti l'intestazione della classe, il metodo main,
     * i limiti dello stack e l'istruzione di ritorno.
     * </p>
     */
    @Test
    void testStrutturaBase() throws Exception {
        String code = "int x;";
        String output = getGeneratedCode(code);

        // Verifico la presenza dell'intestazione di classe standard
        assertTrue(output.contains(".class public Main"));
        assertTrue(output.contains(".super java/lang/Object"));

        // Verifico la definizione del metodo main
        assertTrue(output.contains(".method public static main([Ljava/lang/String;)V"));

        // Verifico che siano definiti i limiti di memoria
        assertTrue(output.contains(".limit locals"));
        assertTrue(output.contains(".limit stack"));

        // Verifico la chiusura corretta del metodo
        assertTrue(output.contains("return"));
        assertTrue(output.contains(".end method"));
    }

    /**
     * Verifica la generazione del codice per la dichiarazione e inizializzazione di interi.
     */
    @Test
    void testDichiarazioneIntero() throws Exception {
        String code = "int x = 5;";
        String output = getGeneratedCode(code);

        // Verifico che venga caricata la costante 5
        assertTrue(output.contains("ldc 5"));

        // Verifico che il valore venga salvato nella prima variabile locale (offset 0)
        assertTrue(output.contains("istore 0"));
    }

    /**
     * Verifica la generazione del codice per la dichiarazione e inizializzazione di float.
     */
    @Test
    void testDichiarazioneFloat() throws Exception {
        String code = "float pi = 3.14;";
        String output = getGeneratedCode(code);

        // Verifico che venga caricata la costante decimale
        assertTrue(output.contains("ldc 3.14"));

        // Verifico l'uso dell'istruzione specifica per i float (fstore)
        assertTrue(output.contains("fstore 0"));
    }

    /**
     * Verifica la generazione del codice per le operazioni aritmetiche di base.
     */
    @Test
    void testOperazioniMatematiche() throws Exception {
        String code = "int res; res = 5 + 3 * 2;";
        // Precedenza: 3 * 2 = 6, poi 5 + 6 = 11
        String output = getGeneratedCode(code);

        // Verifico il caricamento degli operandi
        assertTrue(output.contains("ldc 5"));
        assertTrue(output.contains("ldc 3"));
        assertTrue(output.contains("ldc 2"));

        // Verifico la presenza delle istruzioni di moltiplicazione e addizione
        assertTrue(output.contains("imul"));
        assertTrue(output.contains("iadd"));

        // Verifico che il risultato venga salvato (res è la variabile 0)
        assertTrue(output.contains("istore 0"));
    }

    /**
     * Verifica la generazione del codice per l'istruzione di stampa.
     */
    @Test
    void testPrint() throws Exception {
        String code = "int x = 10; print x;";
        String output = getGeneratedCode(code);

        // Verifico il caricamento dell'oggetto System.out
        assertTrue(output.contains("getstatic java/lang/System/out Ljava/io/PrintStream;"));

        // Verifico il caricamento della variabile da stampare (iload per interi)
        assertTrue(output.contains("iload 0"));

        // Verifico l'invocazione del metodo println
        assertTrue(output.contains("invokevirtual java/io/PrintStream/println(I)V"));
    }

    /**
     * Verifica la gestione corretta degli offset per variabili multiple.
     */
    @Test
    void testOffsetVariabili() throws Exception {
        String code = "int a = 1; int b = 2; a = b;";
        String output = getGeneratedCode(code);

        // Verifico il salvataggio della prima variabile all'offset 0
        assertTrue(output.contains("istore 0"));

        // Verifico il salvataggio della seconda variabile all'offset 1
        assertTrue(output.contains("istore 1"));

        // Verifico il caricamento di b (iload 1) per assegnarlo ad a (istore 0)
        assertTrue(output.contains("iload 1"));
    }

    /**
     * Verifica le operazioni con i numeri in virgola mobile (Float).
     */
    @Test
    void testOperazioniFloat() throws Exception {
        String code = "float a = 5.5; float b = 2.0; float c; c = a - b;";
        String output = getGeneratedCode(code);

        // Verifico l'uso delle istruzioni specifiche per float (fload, fsub, fstore)
        assertTrue(output.contains("fload 0")); // Carico a
        assertTrue(output.contains("fload 1")); // Carico b
        assertTrue(output.contains("fsub"));    // Sottraggo
        assertTrue(output.contains("fstore 2")); // Salvo in c
    }
}