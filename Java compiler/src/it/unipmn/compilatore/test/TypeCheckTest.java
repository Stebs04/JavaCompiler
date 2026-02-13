package it.unipmn.compilatore.test;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.visitor.TypeCheckVisitor;
import it.unipmn.compilatore.exceptions.SyntacticException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per il controllo semantico dei tipi.
 * Verifica che vengano rilevati errori di tipo e che le conversioni implicite
 * vengano inserite correttamente nell'albero.
 */
public class TypeCheckTest {

    /**
     * Verifica che l'uso di una variabile non dichiarata sollevi eccezione.
     */
    @Test
    void testVariabileNonDichiarata() {
        NodeProgram p = new NodeProgram(1);
        // Cerco di stampare 'z' che non esiste
        p.addStatement(new NodePrint(new NodeId("z", 1), 1));

        TypeCheckVisitor visitor = new TypeCheckVisitor();
        // Mi aspetto un errore semantico durante la visita
        assertThrows(SyntacticException.class, () -> p.accept(visitor));
    }

    /**
     * Verifica che la dichiarazione duplicata di una variabile sollevi eccezione.
     */
    @Test
    void testDoppiaDichiarazione() {
        NodeProgram p = new NodeProgram(1);
        NodeId id = new NodeId("x", 1);
        
        // Dichiaro int x;
        p.addStatement(new NodeDecl(id, LangType.INT, null, 1));
        // Dichiaro float x; (stesso nome nello stesso scope)
        p.addStatement(new NodeDecl(id, LangType.FLOAT, null, 2));

        TypeCheckVisitor visitor = new TypeCheckVisitor();
        assertThrows(SyntacticException.class, () -> p.accept(visitor));
    }

    /**
     * Verifica che l'assegnamento di un tipo incompatibile (float in int) sollevi eccezione.
     */
    @Test
    void testAssegnamentoTipoErrato() {
        NodeProgram p = new NodeProgram(1);
        NodeId id = new NodeId("k", 1);
        
        // Dichiaro int k;
        p.addStatement(new NodeDecl(id, LangType.INT, null, 1));
        
        // Assegno 3.14 a k (errore per perdita di precisione)
        NodeCost val = new NodeCost(LangType.FLOAT, "3.14", 2);
        p.addStatement(new NodeAssign(id, val, 2));

        TypeCheckVisitor visitor = new TypeCheckVisitor();
        assertThrows(SyntacticException.class, () -> p.accept(visitor));
    }

    /**
     * Verifica che il TypeChecker modifichi fisicamente l'albero inserendo un cast
     * quando si esegue un'operazione mista (INT + FLOAT).
     */
    @Test
    void testCastImplicitoFunzionante() {
        NodeProgram p = new NodeProgram(1);
        
        // Dichiaro float f;
        NodeId f = new NodeId("f", 1);
        p.addStatement(new NodeDecl(f, LangType.FLOAT, null, 1));
        
        // Creo l'espressione: 5 + 2.5
        NodeCost nInt = new NodeCost(LangType.INT, "5", 2);
        NodeCost nFloat = new NodeCost(LangType.FLOAT, "2.5", 2);
        NodeBinOp somma = new NodeBinOp(LangOper.PLUS, nInt, nFloat, 2);
        
        // Assegno il risultato a f
        p.addStatement(new NodeAssign(f, somma, 2));

        TypeCheckVisitor visitor = new TypeCheckVisitor();
        // Eseguo il controllo dei tipi
        p.accept(visitor);

        // Controllo che il nodo intero '5' sia stato avvolto in un nodo di conversione
        assertTrue(somma.getLeft() instanceof NodeConvert, 
                   "Il TypeChecker deve aver inserito il nodo di conversione!");
        
        // Verifico che la conversione sia verso il tipo FLOAT
        NodeConvert cast = (NodeConvert) somma.getLeft();
        assertEquals(LangType.FLOAT, cast.getTargetType());
    }
}