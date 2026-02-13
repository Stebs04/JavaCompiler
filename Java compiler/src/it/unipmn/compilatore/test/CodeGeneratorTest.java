package it.unipmn.compilatore.test;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.visitor.CodeGeneratorVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per la generazione del codice target (linguaggio dc).
 * Verifica la corretta traduzione dei nodi e l'ordine delle istruzioni.
 */
public class CodeGeneratorTest {

    /**
     * Verifica che i numeri negativi vengano tradotti con l'underscore.
     */
    @Test
    void testNumeriNegativi() {
        NodeCost num = new NodeCost(LangType.INT, "-5", 1);
        CodeGeneratorVisitor gen = new CodeGeneratorVisitor();
        
        num.accept(gen);
        
        // Controllo la sintassi specifica di dc (_ invece di -)
        assertTrue(gen.getCode().contains("_5"));
        assertFalse(gen.getCode().contains("-5"));
    }

    /**
     * Verifica la generazione di un blocco base di codice.
     */
    @Test
    void testGenerazioneBase() {
        NodeProgram p = new NodeProgram(1);
        NodeId id = new NodeId("a", 1);
        // Creo dichiarazione con inizializzazione: int a = 10;
        p.addStatement(new NodeDecl(id, LangType.INT, new NodeCost(LangType.INT, "10", 1), 1));
        
        CodeGeneratorVisitor gen = new CodeGeneratorVisitor();
        p.accept(gen);
        
        String code = gen.getCode();
        // Controllo che ci siano le istruzioni fondamentali
        assertTrue(code.contains("20 k")); // Precisione
        assertTrue(code.contains("10"));   // Valore numerico
        assertTrue(code.contains("sa"));   // Store nel registro
    }

    /**
     * Verifica l'ordine delle operazioni nella notazione postfissa (RPN).
     * Espressione: 2 + 3 * 4
     * Ordine atteso stack: 2, 3, 4, *, +
     */
    @Test
    void testPrecedenzaOperatori() {
        // Costruisco manualmente l'albero per: 2 + (3 * 4)
        NodeCost n2 = new NodeCost(LangType.INT, "2", 1);
        NodeCost n3 = new NodeCost(LangType.INT, "3", 1);
        NodeCost n4 = new NodeCost(LangType.INT, "4", 1);
        
        // Prima creo la moltiplicazione (priorità alta)
        NodeBinOp mul = new NodeBinOp(LangOper.TIMES, n3, n4, 1);
        // Poi la somma che usa il risultato della moltiplicazione
        NodeBinOp sum = new NodeBinOp(LangOper.PLUS, n2, mul, 1);

        CodeGeneratorVisitor gen = new CodeGeneratorVisitor();
        sum.accept(gen);

        String code = gen.getCode();
        
        // Rimuovo tutti gli spazi e a capo per controllare solo la sequenza dei simboli
        String sequence = code.replaceAll("\\s+", "");
        
        // Verifico che l'ordine di esecuzione sia quello corretto
        assertEquals("234*+", sequence, 
            "L'ordine delle istruzioni non rispetta la notazione postfissa!");
    }
}