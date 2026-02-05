package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Suite di test unitari per i nodi dell'Abstract Syntax Tree (AST).
 * <p>
 * Verifica la corretta costruzione dei nodi e l'applicazione del principio "Fail Fast".
 * Ogni nodo deve impedire la propria creazione se i parametri essenziali sono nulli,
 * lanciando una SyntacticException. Questo garantisce l'integrità strutturale dell'AST.
 * </p>
 */
public class ASTTest {

    @Test
    void testNodiFoglia() {
        // Test NodeId
        NodeId id = new NodeId("temp", 1);
        assertEquals("<ID: temp>", id.toString());
        // Verifica eccezione per nome nullo
        assertThrows(SyntacticException.class, () -> new NodeId(null, 1));

        // Test NodeCost (Costruttore: LangType, String, int)
        NodeCost cost = new NodeCost(LangType.INT, "5", 1);
        assertEquals("<Cost: INT, 5>", cost.toString());

        // Verifica eccezioni per parametri nulli
        assertThrows(SyntacticException.class, () -> new NodeCost(null, "5", 1));
        assertThrows(SyntacticException.class, () -> new NodeCost(LangType.INT, null, 1));
    }

    @Test
    void testNodeBinOp() {
        NodeId left = new NodeId("a", 2);
        NodeCost right = new NodeCost(LangType.INT, "10", 2);

        // Test creazione valida NodeBinOp
        NodeBinOp binOp = new NodeBinOp(LangOper.PLUS, left, right, 2);
        assertEquals("(<ID: a> PLUS <Cost: INT, 10>)", binOp.toString());

        // Test integrità: manca operando
        assertThrows(SyntacticException.class, () -> new NodeBinOp(LangOper.PLUS, left, null, 2));
    }

    @Test
    void testAssegnamento() {
        NodeId id = new NodeId("x", 4);
        NodeCost val = new NodeCost(LangType.INT, "100", 4);

        // Test creazione valida NodeAssign
        NodeAssign assign = new NodeAssign(id, val, 4);
        assertEquals("<Assign: <ID: x> = <Cost: INT, 100>>", assign.toString());

        // Test integrità: mancano argomenti
        assertThrows(SyntacticException.class, () -> new NodeAssign(null, val, 4));
        assertThrows(SyntacticException.class, () -> new NodeAssign(id, null, 4));
    }

    @Test
    void testPrint() {
        NodeId id = new NodeId("res", 5);

        // Test creazione valida NodePrint
        NodePrint print = new NodePrint(id, 5);
        assertEquals("<Print: <ID: res>>", print.toString());

        // Test eccezione
        assertThrows(SyntacticException.class, () -> new NodePrint(null, 5));
    }

    @Test
    void testDichiarazioni() {
        NodeId id = new NodeId("x", 3);
        NodeCost val = new NodeCost(LangType.FLOAT, "3.14", 3);

        // Caso 1: Dichiarazione semplice (senza init)
        NodeDecl declSimple = new NodeDecl(id, LangType.INT, null, 3);
        assertTrue(declSimple.toString().contains("Decl: INT <ID: x>"));

        // Caso 2: Dichiarazione con inizializzazione
        NodeDecl declInit = new NodeDecl(id, LangType.FLOAT, val, 3);
        assertTrue(declInit.toString().contains(" = <Cost: FLOAT, 3.14>"));

        // Test integrità: manca il tipo
        assertThrows(SyntacticException.class, () -> new NodeDecl(id, null, null, 3));
    }

    @Test
    void testNodeProgram() {
        NodeProgram prog = new NodeProgram(0);
        prog.addStatement(new NodeDecl(new NodeId("a", 1), LangType.INT, null, 1));

        assertEquals(1, prog.getStatements().size());
        assertTrue(prog.toString().startsWith("PROGRAM:"));
    }
}