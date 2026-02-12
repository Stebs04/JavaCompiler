package it.unipmn.compilatore.test;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.visitor.TypeCheckVisitor;
import it.unipmn.compilatore.exceptions.SyntacticException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per il controllore dei tipi (Type Checker).
 * Verifico che le operazioni tra tipi diversi generino conversioni
 * esplicite (Casting) o blocchino la compilazione se incompatibili.
 */
public class TypeCheckTest {

    /**
     * Verifica la conversione automatica da intero a decimale.
     */
    @Test
    void testConversioneImplicita() {
        NodeProgram program = new NodeProgram(1);
        NodeId id = new NodeId("y", 1);
        // Preparo un numero intero
        NodeCost valoreIntero = new NodeCost(LangType.INT, "5", 1);
        // Tento di assegnare l'intero a una variabile dichiarata float
        NodeDecl dichiarazione = new NodeDecl(id, LangType.FLOAT, valoreIntero, 1);
        program.addStatement(dichiarazione);

        // Faccio analizzare l'albero al controllore dei tipi
        TypeCheckVisitor visitor = new TypeCheckVisitor();
        program.accept(visitor);

        // Recupero l'espressione aggiornata
        NodeExpr initDopoVisita = dichiarazione.getInit();
        
        // Verifico che il numero intero sia stato racchiuso in un nodo di conversione a float
        assertTrue(initDopoVisita instanceof NodeConvert);
        NodeConvert convertNode = (NodeConvert) initDopoVisita;
        assertEquals(LangType.FLOAT, convertNode.getTargetType());
    }

    /**
     * Verifica l'uso di una variabile inesistente.
     */
    @Test
    void testUsoVariabileNonDichiarata() {
        NodeProgram program = new NodeProgram(1);
        NodeId id = new NodeId("z", 1);
        // Costruisco l'azione di stampa di una variabile mai dichiarata
        NodePrint stampa = new NodePrint(id, 1);
        program.addStatement(stampa);

        TypeCheckVisitor visitor = new TypeCheckVisitor();
        
        // Verifico l'interruzione del programma per violazione semantica
        assertThrows(SyntacticException.class, () -> {
            program.accept(visitor);
        });
    }

    /**
     * Verifica il rifiuto di operazioni senza conversione possibile.
     */
    @Test
    void testTipoIncompatibile() {
        NodeProgram program = new NodeProgram(1);
        NodeId id = new NodeId("x", 1);
        NodeCost valoreDecimale = new NodeCost(LangType.FLOAT, "3.14", 1);
        // Tento di assegnare un float a una variabile intera (perderebbe dati)
        NodeDecl dichiarazione = new NodeDecl(id, LangType.INT, valoreDecimale, 1);
        program.addStatement(dichiarazione);

        TypeCheckVisitor visitor = new TypeCheckVisitor();
        
        // Verifico l'interruzione della compilazione
        assertThrows(SyntacticException.class, () -> {
            program.accept(visitor);
        });
    }
}