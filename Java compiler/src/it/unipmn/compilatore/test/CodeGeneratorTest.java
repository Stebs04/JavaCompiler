package it.unipmn.compilatore.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.visitor.CodeGeneratorVisitor;

/**
 * Suite di test per il CodeGeneratorVisitor.
 * <p>
 * Verifico isolatamente che ogni nodo dell'AST venga tradotto nel corretto
 * comando per la calcolatrice dc.
 * </p>
 */
public class CodeGeneratorTest {

    private CodeGeneratorVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new CodeGeneratorVisitor();
    }

    @Test
    void testProgramHeader() {
        NodeProgram program = new NodeProgram(1);
        program.accept(visitor);

        // Verifico che venga impostata la precisione
        assertTrue(visitor.getCode().contains("20 k"), "L'header deve impostare la precisione a 20 cifre");
    }

    @Test
    void testCostanteIntera() {
        NodeCost cost = new NodeCost(LangType.INT, "42", 1);
        cost.accept(visitor);

        // Verifico il formato numero + spazio
        assertEquals("42 ", visitor.getCode());
    }

    @Test
    void testCostanteNegativa() {
        NodeCost cost = new NodeCost(LangType.INT, "-5", 1);
        cost.accept(visitor);

        // Verifico la conversione del meno standard in underscore per dc
        assertEquals("_5 ", visitor.getCode());
    }

    @Test
    void testCostanteFloat() {
        NodeCost cost = new NodeCost(LangType.FLOAT, "3.14", 1);
        cost.accept(visitor);

        assertEquals("3.14 ", visitor.getCode());
    }

    @Test
    void testDichiarazioneVariabile() {
        // Simulo: int x = 10;
        NodeId id = new NodeId("x", 1);
        NodeCost init = new NodeCost(LangType.INT, "10", 1);
        NodeDecl decl = new NodeDecl(id, LangType.INT, init, 1);

        decl.accept(visitor);

        // Deve calcolare 10 e salvarlo nel primo registro disponibile ('a')
        String code = visitor.getCode();
        assertTrue(code.contains("10 "), "Deve caricare il valore");
        assertTrue(code.contains("sa"), "Deve salvare nel registro 'a'");
    }

    @Test
    void testOperazioneBinaria() {
        // Simulo: 5 + 3
        NodeCost left = new NodeCost(LangType.INT, "5", 1);
        NodeCost right = new NodeCost(LangType.INT, "3", 1);
        NodeBinOp op = new NodeBinOp(LangOper.PLUS, left, right, 1);

        op.accept(visitor);

        String code = visitor.getCode();
        assertTrue(code.contains("5 "), "Manca operando sinistro");
        assertTrue(code.contains("3 "), "Manca operando destro");
        assertTrue(code.contains("+"), "Manca operatore somma");
    }

    @Test
    void testStampa() {
        // Per testare la stampa, devo prima dichiarare la variabile affinché esista nella SymbolTable interna al visitor
        NodeId idDecl = new NodeId("res", 1);
        NodeDecl decl = new NodeDecl(idDecl, LangType.INT, null, 1);
        decl.accept(visitor); // Assegna registro 'a' a "res"

        // Ora testo la stampa
        NodeId idPrint = new NodeId("res", 2);
        NodePrint print = new NodePrint(idPrint, 2);

        // Pulisco il buffer per controllare solo la stampa
        CodeGeneratorVisitor printVisitor = new CodeGeneratorVisitor();
        // Trucco: devo reinserire la variabile nella symbol table del nuovo visitor o usare lo stesso
        // Per semplicità continuo con lo stesso visitor e controllo che appaiano i comandi di stampa
        print.accept(visitor);

        String code = visitor.getCode();
        assertTrue(code.contains("la"), "Deve caricare la variabile");
        assertTrue(code.contains("p"), "Deve stampare (peek)");
        assertTrue(code.contains("si"), "Deve pulire lo stack (pop in registro i)");
    }
}