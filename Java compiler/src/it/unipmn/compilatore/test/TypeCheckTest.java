package it.unipmn.compilatore.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.visitor.TypeCheckVisitor;
import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Test per il TypeCheckVisitor.
 * Verifica la coerenza dei tipi e l'inserimento automatico dei nodi di conversione (Casting).
 */
public class TypeCheckTest {

    private TypeCheckVisitor visitor;

    @BeforeEach
    void setUp() {
        // Inizializzo il visitor prima di ogni test
        visitor = new TypeCheckVisitor();
    }

    @Test
    void testDichiarazioneCorretta() {
        NodeId id = new NodeId("x", 1);
        NodeCost val = new NodeCost(LangType.INT, "5", 1);
        NodeDecl decl = new NodeDecl(id, LangType.INT, val, 1);

        // Verifico che non vengano lanciate eccezioni
        assertDoesNotThrow(() -> decl.accept(visitor));
    }

    @Test
    void testUsoVariabileNonDichiarata() {
        NodeId id = new NodeId("y", 1);
        NodePrint print = new NodePrint(id, 1);

        // Mi aspetto errore perché 'y' non è stata dichiarata
        assertThrows(SyntacticException.class, () -> print.accept(visitor));
    }

    @Test
    void testConversioneImplicitaDecl() {
        // Caso: float f = 5; (5 è int)
        NodeId id = new NodeId("f", 1);
        NodeCost intVal = new NodeCost(LangType.INT, "5", 1);
        NodeDecl decl = new NodeDecl(id, LangType.FLOAT, intVal, 1);

        decl.accept(visitor);

        // Verifico che il nodo di init sia stato wrappato in un NodeConvert
        assertTrue(decl.getInit() instanceof NodeConvert);
        assertEquals(LangType.FLOAT, ((NodeConvert)decl.getInit()).getTargetType());
    }

    @Test
    void testConversioneImplicitaBinOp() {
        // Caso: 5 + 2.5 (Int + Float)
        NodeCost left = new NodeCost(LangType.INT, "5", 1);
        NodeCost right = new NodeCost(LangType.FLOAT, "2.5", 1);
        NodeBinOp op = new NodeBinOp(LangOper.PLUS, left, right, 1);

        op.accept(visitor);

        // Verifico che il lato sinistro (Int) sia stato convertito
        assertTrue(op.getLeft() instanceof NodeConvert);
        // Verifico che il lato destro (Float) sia rimasto invariato (o comunque coerente)
        assertFalse(op.getRight() instanceof NodeConvert);
    }

    @Test
    void testErroreTipoIncompatibile() {
        // Caso: int i = 3.5; (Non si può assegnare float a int)
        NodeId id = new NodeId("i", 1);
        NodeCost floatVal = new NodeCost(LangType.FLOAT, "3.5", 1);
        NodeDecl decl = new NodeDecl(id, LangType.INT, floatVal, 1);

        // Verifico che venga lanciata l'eccezione semantica
        assertThrows(SyntacticException.class, () -> decl.accept(visitor));
    }
}