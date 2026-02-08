package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.parser.Parser;
import it.unipmn.compilatore.ast.NodeProgram;
import it.unipmn.compilatore.visitor.TypeCheckVisitor;
import it.unipmn.compilatore.exceptions.SyntacticException;
import it.unipmn.compilatore.exceptions.LexicalException;

/**
 * Suite di test per il TypeCheckVisitor (Analizzatore Semantico).
 * <p>
 * Verifico che il visitatore rilevi correttamente gli errori semantici
 * (tipi incompatibili, variabili non dichiarate, ecc.) e accetti il codice valido.
 * </p>
 */
public class TypeCheckTest {

    /**
     * Metodo ausiliario che esegue l'intera pipeline di compilazione fino al Type Checking.
     * <p>
     * Scrivo il codice su un file temporaneo, creo lo scanner e il parser,
     * ottengo l'AST e infine eseguo il visitatore semantico.
     * </p>
     *
     * @param code Il codice sorgente da analizzare.
     * @throws IOException        In caso di errori di I/O.
     * @throws LexicalException   In caso di errori lessicali.
     * @throws SyntacticException In caso di errori sintattici o semantici.
     */
    private void runTypeCheck(String code) throws IOException, LexicalException, SyntacticException {
        // Creo un file temporaneo per il test
        File file = new File("test_typecheck.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }

        // Inizializzo Scanner e Parser
        Scanner scanner = new Scanner("test_typecheck.txt");
        Parser parser = new Parser(scanner);

        // Costruisco l'AST
        NodeProgram program = parser.parse();

        // Eseguo l'analisi semantica visitando l'AST
        TypeCheckVisitor visitor = new TypeCheckVisitor();
        program.accept(visitor);
    }

    /**
     * Test di un programma sintatticamente e semanticamente corretto.
     * Verifico che non venga lanciata alcuna eccezione.
     */
    @Test
    void testCodiceValido() {
        String code = "int x = 5; float y = 3.14; x = x + 2; print y;";

        assertDoesNotThrow(() -> runTypeCheck(code), "Il codice valido non deve generare eccezioni");
    }

    /**
     * Test per l'uso di una variabile non dichiarata.
     * Verifico che il visitatore segnali l'errore.
     */
    @Test
    void testVariabileNonDichiarata() {
        // Provo a stampare 'z' senza averla dichiarata
        String code = "int x; print z;";

        assertThrows(SyntacticException.class, () -> runTypeCheck(code),
                "L'uso di una variabile non dichiarata deve lanciare un'eccezione");
    }

    /**
     * Test per la doppia dichiarazione di una variabile nello stesso scope.
     * Verifico che il visitatore impedisca di dichiarare due volte 'x'.
     */
    @Test
    void testDoppiaDichiarazione() {
        String code = "int x; float x;";

        assertThrows(SyntacticException.class, () -> runTypeCheck(code),
                "La doppia dichiarazione di una variabile deve lanciare un'eccezione");
    }

    /**
     * Test per l'incompatibilità di tipo nell'inizializzazione.
     * Provo ad assegnare un float a una variabile dichiarata int.
     */
    @Test
    void testIncompatibilitaInizializzazione() {
        String code = "int a = 3.14;";

        assertThrows(SyntacticException.class, () -> runTypeCheck(code),
                "L'assegnamento di un float a un int in inizializzazione deve fallire");
    }

    /**
     * Test per l'incompatibilità di tipo in un assegnamento successivo.
     * Dichiaro correttamente, ma poi provo ad assegnare un tipo sbagliato.
     */
    @Test
    void testIncompatibilitaAssegnamento() {
        String code = "float b; b = 10;"; // 10 è INT, b è FLOAT

        assertThrows(SyntacticException.class, () -> runTypeCheck(code),
                "L'assegnamento di un int a un float (senza cast implicito) deve fallire");
    }

    /**
     * Test per l'incompatibilità di tipo nelle operazioni binarie.
     * Il linguaggio richiede che gli operandi siano dello stesso tipo.
     */
    @Test
    void testIncompatibilitaOperazione() {
        // Provo a sommare un int (5) con un float (2.5)
        String code = "float res = 5 + 2.5;";

        assertThrows(SyntacticException.class, () -> runTypeCheck(code),
                "Operazioni tra tipi diversi devono lanciare un'eccezione");
    }

    /**
     * Test per assegnamento corretto con espressioni complesse.
     * Verifico che un'espressione valida venga accettata.
     */
    @Test
    void testEspressioneComplessaValida() {
        // (5 + 3) * 2 è tutto INT, quindi assegnabile a 'ris' che è INT
        String code = "int ris; ris = (5 + 3) * 2;";

        assertDoesNotThrow(() -> runTypeCheck(code), "Un'espressione omogenea valida deve essere accettata");
    }
}