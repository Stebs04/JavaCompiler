package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.parser.Parser;
import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Test per il Parser (Analizzatore Sintattico).
 * Verifica la costruzione dell'albero sintattico, la precedenza degli operatori
 * e la gestione degli errori di sintassi.
 */
public class ParserTest {

    @Test
    void testProgrammaBase(@TempDir Path tempDir) throws Exception {
        // Scrivo un programma valido minimo
        Path file = tempDir.resolve("ok.txt");
        Files.writeString(file, "int x; x = 5; print x;");

        Scanner scanner = new Scanner(file.toString());
        Parser parser = new Parser(scanner);
        NodeProgram prog = parser.parse();

        // Verifico che ci siano esattamente 3 istruzioni
        assertEquals(3, prog.getStatements().size());
        // Controllo i tipi delle istruzioni nell'ordine corretto
        assertTrue(prog.getStatements().get(0) instanceof NodeDecl);
        assertTrue(prog.getStatements().get(1) instanceof NodeAssign);
        assertTrue(prog.getStatements().get(2) instanceof NodePrint);

        scanner.close();
    }

    @Test
    void testPrecedenzaOperatori(@TempDir Path tempDir) throws Exception {
        // Testo che la moltiplicazione abbia precedenza sulla somma
        Path file = tempDir.resolve("prec.txt");
        Files.writeString(file, "x = 5 + 2 * 3;");

        Scanner scanner = new Scanner(file.toString());
        Parser parser = new Parser(scanner);
        NodeProgram prog = parser.parse();

        NodeAssign assign = (NodeAssign) prog.getStatements().get(0);
        NodeBinOp rootExpr = (NodeBinOp) assign.getExpr();

        // La radice deve essere la somma (+)
        assertEquals(LangOper.PLUS, rootExpr.getOp());

        // Il figlio destro deve essere la moltiplicazione (*)
        assertTrue(rootExpr.getRight() instanceof NodeBinOp);
        assertEquals(LangOper.TIMES, ((NodeBinOp)rootExpr.getRight()).getOp());

        scanner.close();
    }

    @Test
    void testParentesi(@TempDir Path tempDir) throws Exception {
        // Testo che le parentesi alterino la precedenza
        Path file = tempDir.resolve("paren.txt");
        Files.writeString(file, "x = (5 + 2) * 3;");

        Scanner scanner = new Scanner(file.toString());
        Parser parser = new Parser(scanner);
        NodeProgram prog = parser.parse();

        NodeAssign assign = (NodeAssign) prog.getStatements().get(0);
        NodeBinOp rootExpr = (NodeBinOp) assign.getExpr();

        // Ora la radice deve essere la moltiplicazione (*)
        assertEquals(LangOper.TIMES, rootExpr.getOp());

        scanner.close();
    }

    @Test
    void testErrorePuntoEVirgola(@TempDir Path tempDir) throws Exception {
        // Ometto il punto e virgola finale
        Path file = tempDir.resolve("error.txt");
        Files.writeString(file, "print x");

        Scanner scanner = new Scanner(file.toString());
        Parser parser = new Parser(scanner);

        // Verifico che il parser si accorga dell'errore
        assertThrows(SyntacticException.class, parser::parse);
        scanner.close();
    }
}