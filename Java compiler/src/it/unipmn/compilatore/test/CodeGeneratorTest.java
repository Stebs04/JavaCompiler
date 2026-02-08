package it.unipmn.compilatore.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.visitor.CodeGeneratorVisitor;
import it.unipmn.compilatore.ast.*;

/**
 * Test per il CodeGeneratorVisitor.
 * Verifica che vengano prodotti i comandi corretti per la calcolatrice 'dc' (notazione polacca inversa).
 */
public class CodeGeneratorTest {

    private CodeGeneratorVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new CodeGeneratorVisitor();
    }

    @Test
    void testHeaderPrecisione() {
        NodeProgram prog = new NodeProgram(0);
        prog.accept(visitor);

        // Verifico che venga impostata la precisione
        assertTrue(visitor.getCode().contains("20 k"));
    }

    @Test
    void testOperazioniMatematiche() {
        // Genero: 5 + 3
        NodeCost c1 = new NodeCost(LangType.INT, "5", 1);
        NodeCost c2 = new NodeCost(LangType.INT, "3", 1);
        NodeBinOp op = new NodeBinOp(LangOper.PLUS, c1, c2, 1);

        op.accept(visitor);
        String code = visitor.getCode();

        // Verifico la presenza degli operandi e dell'operatore
        assertTrue(code.contains("5"));
        assertTrue(code.contains("3"));
        assertTrue(code.contains("+"));
    }

    @Test
    void testCostanteNegativa() {
        // Genero: -10
        NodeCost c = new NodeCost(LangType.INT, "-10", 1);

        c.accept(visitor);

        // Verifico che il meno standard sia convertito in underscore per dc
        assertTrue(visitor.getCode().contains("_10"));
    }

    @Test
    void testStoreLoadVariabile() {
        // Simulo: int a = 100;
        NodeId id = new NodeId("a", 1);
        NodeCost val = new NodeCost(LangType.INT, "100", 1);
        NodeDecl decl = new NodeDecl(id, LangType.INT, val, 1);

        decl.accept(visitor);
        String code = visitor.getCode();

        // Verifico comando 's' (store) e il valore
        assertTrue(code.contains("100"));
        assertTrue(code.contains("sa")); // 'a' è il primo registro libero
    }

    @Test
    void testPrint() {
        // Necessito di dichiarare prima per popolare la symbol table del visitor
        NodeId id = new NodeId("a", 1);
        NodeDecl decl = new NodeDecl(id, LangType.INT, null, 1);
        decl.accept(visitor);

        // Simulo print a;
        NodePrint print = new NodePrint(id, 2);
        print.accept(visitor);

        String code = visitor.getCode();
        // Verifico caricamento (l), stampa (p) e pulizia stack (si)
        assertTrue(code.contains("la"));
        assertTrue(code.contains("p"));
        assertTrue(code.contains("si"));
    }
}