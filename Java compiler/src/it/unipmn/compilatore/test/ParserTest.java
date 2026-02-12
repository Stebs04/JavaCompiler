package it.unipmn.compilatore.test;

import it.unipmn.compilatore.parser.Parser;
import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per l'Analizzatore Sintattico (Parser).
 * Verifico la costruzione dell'Abstract Syntax Tree (AST) assicurandomi 
 * che la gerarchia dei nodi rispetti le regole grammaticali.
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
     * Verifica la costruzione dell'albero per una dichiarazione di variabile.
     */
    @Test
    void testDichiarazione() throws Exception {
        File file = creaFileTemporaneo("float y = 3.5;");
        Scanner scanner = new Scanner(file.getAbsolutePath());
        Parser parser = new Parser(scanner);
        
        // Avvio l'analisi e ottengo la radice dell'albero
        NodeProgram program = parser.parse();
        
        // Estraggo la prima istruzione
        NodeDecSt stmt = program.getStatements().get(0);
        
        // Verifico che il nodo creato sia una dichiarazione
        assertTrue(stmt instanceof NodeDecl);
        NodeDecl decl = (NodeDecl) stmt;
        
        // Controllo i parametri interni della dichiarazione
        assertEquals("y", decl.getId().getName());
        assertEquals(LangType.FLOAT, decl.getType());
        assertTrue(decl.getInit() instanceof NodeCost);

        scanner.close();
    }

    /**
     * Verifica la priorità degli operatori matematici nell'albero.
     */
    @Test
    void testEspressioneMatematica() throws Exception {
        // Scrivo un calcolo che richiede di rispettare la precedenza tra somma e moltiplicazione
        File file = creaFileTemporaneo("x = 5 + 2 * 3;");
        Scanner scanner = new Scanner(file.getAbsolutePath());
        Parser parser = new Parser(scanner);
        
        NodeProgram program = parser.parse();
        NodeAssign assign = (NodeAssign) program.getStatements().get(0);
        
        // L'operazione più in alto nell'albero deve essere la somma (valutata per ultima)
        assertTrue(assign.getExpr() instanceof NodeBinOp);
        NodeBinOp somma = (NodeBinOp) assign.getExpr();
        assertEquals(LangOper.PLUS, somma.getOp());
        
        // Il figlio destro della somma deve contenere la moltiplicazione (valutata prima)
        assertTrue(somma.getRight() instanceof NodeBinOp);
        NodeBinOp moltiplicazione = (NodeBinOp) somma.getRight();
        assertEquals(LangOper.TIMES, moltiplicazione.getOp());

        scanner.close();
    }

    /**
     * Verifica il rilevamento di un errore di sintassi.
     */
    @Test
    void testSintassiScorretta() throws Exception {
        // Dichiaro una variabile omettendo il punto e virgola finale
        File file = creaFileTemporaneo("int x = 5");
        Scanner scanner = new Scanner(file.getAbsolutePath());
        Parser parser = new Parser(scanner);

        // Verifico che venga sollevata l'eccezione prevista per la sintassi errata
        assertThrows(SyntacticException.class, () -> {
            parser.parse();
        });
        
        scanner.close();
    }
}