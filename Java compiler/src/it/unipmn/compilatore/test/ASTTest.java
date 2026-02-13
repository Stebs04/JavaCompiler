package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Classe di test per verificare la struttura dei nodi dell'AST.
 * Controllo che i nodi vengano creati con i dati corretti e che i costruttori
 * rifiutino i valori nulli obbligatori.
 */
public class ASTTest {

    /**
     * Verifica la creazione del nodo identificatore.
     */
    @Test
    void testNodeId() {
        NodeId id = new NodeId("x", 1);
        assertEquals("x", id.getName());
        // Controllo che il costruttore rifiuti un nome nullo
        assertThrows(SyntacticException.class, () -> new NodeId(null, 1));
    }

    /**
     * Verifica la creazione del nodo costante numerica.
     */
    @Test
    void testNodeCost() {
        NodeCost cost = new NodeCost(LangType.INT, "10", 1);
        assertEquals(LangType.INT, cost.getType());
        // Controllo che il costruttore rifiuti valori o tipi nulli
        assertThrows(SyntacticException.class, () -> new NodeCost(null, "10", 1));
        assertThrows(SyntacticException.class, () -> new NodeCost(LangType.INT, null, 1));
    }

    /**
     * Verifica la creazione di un'operazione binaria.
     */
    @Test
    void testNodeBinOp() {
        NodeId left = new NodeId("a", 1);
        NodeCost right = new NodeCost(LangType.INT, "5", 1);
        // Collego i due operandi con l'operatore di somma
        NodeBinOp op = new NodeBinOp(LangOper.PLUS, left, right, 1);

        assertEquals(LangOper.PLUS, op.getOp());
        assertEquals(left, op.getLeft());
        assertEquals(right, op.getRight());
    }

    /**
     * Verifica la creazione del nodo di stampa.
     */
    @Test
    void testNodePrint() {
        NodeId id = new NodeId("res", 1);
        NodePrint p = new NodePrint(id, 1);
        assertEquals(id, p.getId());
        // Controllo che non si possa stampare il nulla
        assertThrows(SyntacticException.class, () -> new NodePrint(null, 1));
    }
    
    /**
     * Verifica la gestione della lista istruzioni nel nodo programma.
     */
    @Test
    void testNodeProgram() {
        NodeProgram prog = new NodeProgram(1);
        prog.addStatement(new NodePrint(new NodeId("x", 1), 1));
        // Controllo che l'istruzione sia stata aggiunta
        assertEquals(1, prog.getStatements().size());
        
        // Provo ad aggiungere un nodo nullo, la lista non deve cambiare
        prog.addStatement(null);
        assertEquals(1, prog.getStatements().size());
    }

    /**
     * Verifica la creazione del nodo di conversione tipo (Cast).
     * Questo nodo viene generato automaticamente dal compilatore, non dall'utente.
     */
    @Test
    void testNodeConvert() {
        NodeCost num = new NodeCost(LangType.INT, "5", 1);
        // Simulo la creazione di un cast esplicito a float
        NodeConvert conv = new NodeConvert(num, LangType.FLOAT);
        
        assertEquals(LangType.FLOAT, conv.getTargetType());
        // Controllo che il nodo contenga l'espressione originale
        assertEquals(num, conv.getExpr());
    }

    /**
     * Verifica la creazione e la modifica del nodo di assegnamento.
     */
    @Test
    void testNodeAssign() {
        NodeId id = new NodeId("x", 1);
        NodeCost val = new NodeCost(LangType.INT, "10", 1);
        NodeAssign assign = new NodeAssign(id, val, 1);

        assertEquals(id, assign.getId());
        assertEquals(val, assign.getExpr());
        
        // Simulo l'aggiornamento dell'espressione, utile quando il TypeChecker inserisce un cast
        NodeConvert conv = new NodeConvert(val, LangType.FLOAT);
        assign.setExpr(conv);
        // Verifico che l'espressione sia stata sostituita correttamente
        assertEquals(conv, assign.getExpr());
    }
}