package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Test strutturali per i nodi dell'Abstract Syntax Tree.
 * Verifica che i nodi vengano creati correttamente e che lancino eccezioni
 * se inizializzati con dati mancanti (null).
 */
public class ASTTest {

    @Test
    void testNodeId() {
        // Creo un nodo identificatore valido
        NodeId id = new NodeId("x", 1);
        assertEquals("x", id.getName());

        // Verifico che non accetti nomi nulli
        assertThrows(SyntacticException.class, () -> new NodeId(null, 1));
    }

    @Test
    void testNodeCost() {
        // Creo una costante intera
        NodeCost cost = new NodeCost(LangType.INT, "10", 1);
        assertEquals(LangType.INT, cost.getType());
        assertEquals("10", cost.getValue());

        // Verifico controlli su tipo e valore nulli
        assertThrows(SyntacticException.class, () -> new NodeCost(null, "10", 1));
        assertThrows(SyntacticException.class, () -> new NodeCost(LangType.INT, "", 1));
    }

    @Test
    void testNodeBinOp() {
        NodeId left = new NodeId("a", 1);
        NodeCost right = new NodeCost(LangType.INT, "5", 1);

        // Creo un'operazione binaria
        NodeBinOp op = new NodeBinOp(LangOper.PLUS, left, right, 1);

        assertEquals(LangOper.PLUS, op.getOp());
        // Verifico che i figli siano corretti
        assertEquals(left, op.getLeft());
        assertEquals(right, op.getRight());
    }

    @Test
    void testNodeDecl() {
        NodeId id = new NodeId("a", 1);

        // Testo dichiarazione senza inizializzazione
        NodeDecl decl = new NodeDecl(id, LangType.INT, null, 1);
        assertNull(decl.getInit());

        // Testo dichiarazione con init
        NodeCost init = new NodeCost(LangType.INT, "5", 1);
        decl.setInit(init);
        assertEquals(init, decl.getInit());
    }
}