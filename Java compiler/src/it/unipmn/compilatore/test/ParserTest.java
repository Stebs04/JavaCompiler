package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.parser.Parser;
import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.*;

/**
 * Suite di test per il Parser.
 * <p>
 * Verifica che il parser costruisca correttamente l'AST a partire dal codice sorgente
 * e che rispetti le regole di precedenza degli operatori e la struttura sintattica.
 * </p>
 */
public class ParserTest {

    /**
     * Metodo helper: crea un parser a partire da una stringa di codice.
     * Simula il processo di lettura da file.
     */
    private Parser createParser(String code) throws IOException, LexicalException {
        File file = new File("test_parser.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }
        Scanner scanner = new Scanner("test_parser.txt");
        return new Parser(scanner);
    }

    @Test
    void testPrint() throws Exception {
        String code = "print x;";
        Parser parser = createParser(code);

        NodeProgram program = parser.parse();

        assertEquals(1, program.getStatements().size());
        NodeAST stmt = program.getStatements().get(0);

        // Verifica che il nodo creato sia un NodePrint
        assertTrue(stmt instanceof NodePrint);
        assertEquals("<Print: <ID: x>>", stmt.toString());
    }

    @Test
    void testDichiarazioni() throws Exception {
        String code = "int a; float b = 3.14;";
        Parser parser = createParser(code);
        NodeProgram program = parser.parse();

        assertEquals(2, program.getStatements().size());

        // Controllo prima dichiarazione: int a;
        NodeDecl decl1 = (NodeDecl) program.getStatements().get(0);
        assertEquals(LangType.INT, decl1.getType());
        assertEquals("a", decl1.getId().getName());
        assertNull(decl1.getInit());

        // Controllo seconda dichiarazione: float b = 3.14;
        NodeDecl decl2 = (NodeDecl) program.getStatements().get(1);
        assertEquals(LangType.FLOAT, decl2.getType());
        assertEquals("b", decl2.getId().getName());
        assertNotNull(decl2.getInit());
        assertEquals("<Cost: FLOAT, 3.14>", decl2.getInit().toString());
    }

    @Test
    void testAssegnamento() throws Exception {
        String code = "x = 5;";
        Parser parser = createParser(code);
        NodeProgram program = parser.parse();

        NodeAssign assign = (NodeAssign) program.getStatements().get(0);
        assertEquals("x", assign.getId().getName());
        assertEquals("<Cost: INT, 5>", assign.getExpr().toString());
    }

    @Test
    void testEspressioniPrecedenza() throws Exception {
        // Test fondamentale: la moltiplicazione deve avere precedenza sulla somma.
        // "2 + 3 * 4" deve essere parsato come "2 + (3 * 4)", non "(2 + 3) * 4".
        String code = "x = 2 + 3 * 4;";
        Parser parser = createParser(code);
        NodeProgram program = parser.parse();

        NodeAssign assign = (NodeAssign) program.getStatements().get(0);
        NodeBinOp somma = (NodeBinOp) assign.getExpr();

        // La radice dell'espressione deve essere la somma (+)
        assertEquals(LangOper.PLUS, somma.getOp());

        // A sinistra della somma deve esserci 2
        assertEquals("<Cost: INT, 2>", somma.getLeft().toString());

        // A destra della somma deve esserci la moltiplicazione (3 * 4)
        NodeBinOp moltiplicazione = (NodeBinOp) somma.getRight();
        assertEquals(LangOper.TIMES, moltiplicazione.getOp());
        assertEquals("<Cost: INT, 3>", moltiplicazione.getLeft().toString());
        assertEquals("<Cost: INT, 4>", moltiplicazione.getRight().toString());
    }

    @Test
    void testErroriSintattici() {
        // Caso 1: Manca il punto e virgola
        assertThrows(SyntacticException.class, () -> {
            Parser parser = createParser("print x"); // Senza ;
            parser.parse();
        }, "Dovrebbe fallire se manca il punto e virgola");

        // Caso 2: Assegnamento malformato
        assertThrows(SyntacticException.class, () -> {
            Parser parser = createParser("x = ;"); // Manca espressione
            parser.parse();
        }, "Dovrebbe fallire se manca l'espressione dopo =");

        // Caso 3: Dichiarazione errata
        assertThrows(SyntacticException.class, () -> {
            Parser parser = createParser("int 5;"); // Nome variabile non valido
            parser.parse();
        }, "Dovrebbe fallire se l'ID non è valido");
    }
}