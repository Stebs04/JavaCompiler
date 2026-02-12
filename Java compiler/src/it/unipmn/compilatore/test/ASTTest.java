package it.unipmn.compilatore.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.SyntacticException;

/**
 * Classe di test per verificare la robustezza strutturale dei nodi dell'AST.
 * Mi assicuro che la creazione dei vari elementi dell'albero avvenga correttamente 
 * e che i controlli interni blocchino i tentativi di creare nodi non validi 
 * (ad esempio passando parametri nulli dove non consentito).
 */
public class ASTTest {

    /**
     * Verifica la corretta creazione e i controlli di validità del nodo identificatore (variabile).
     */
    @Test
    void testNodeId() {
        // Creo un nodo identificatore simulando di aver letto la variabile "x"
        NodeId id = new NodeId("x", 1);
        
        // Verifico che il nome memorizzato corrisponda esattamente a quello fornito
        assertEquals("x", id.getName());

        // Verifico che il costruttore mi blocchi sollevando un'eccezione se provo a passare un nome nullo
        assertThrows(SyntacticException.class, () -> new NodeId(null, 1));
    }

    /**
     * Verifica la corretta creazione e i controlli di validità per i nodi che rappresentano numeri.
     */
    @Test
    void testNodeCost() {
        // Creo un nodo per simulare la lettura del numero intero 10
        NodeCost cost = new NodeCost(LangType.INT, "10", 1);
        
        // Verifico che tipo e valore siano stati salvati correttamente
        assertEquals(LangType.INT, cost.getType());
        assertEquals("10", cost.getValue());

        // Verifico che il sistema sollevi un'eccezione se dimentico di specificare il tipo del numero
        assertThrows(SyntacticException.class, () -> new NodeCost(null, "10", 1));
        
        // Verifico che il sistema sollevi un'eccezione se provo a creare una costante senza un valore testuale
        assertThrows(SyntacticException.class, () -> new NodeCost(LangType.INT, "", 1));
    }

    /**
     * Verifica il collegamento corretto tra un'operazione matematica e i suoi operandi (figli).
     */
    @Test
    void testNodeBinOp() {
        // Preparo i due nodi foglia: una variabile a sinistra e un numero a destra
        NodeId left = new NodeId("a", 1);
        NodeCost right = new NodeCost(LangType.INT, "5", 1);

        // Creo un nodo radice per l'operazione di somma collegando i due figli creati prima
        NodeBinOp op = new NodeBinOp(LangOper.PLUS, left, right, 1);

        // Verifico che il tipo di operazione salvata sia corretto
        assertEquals(LangOper.PLUS, op.getOp());
        
        // Verifico che i collegamenti tra padre e figlio siano rimasti intatti
        assertEquals(left, op.getLeft());
        assertEquals(right, op.getRight());
    }

    /**
     * Verifica la creazione di dichiarazioni di variabili con e senza inizializzazione immediata.
     */
    @Test
    void testNodeDecl() {
        // Preparo il nodo per il nome della variabile
        NodeId id = new NodeId("a", 1);

        // Creo una semplice dichiarazione del tipo "int a;" (senza valore iniziale)
        NodeDecl decl = new NodeDecl(id, LangType.INT, null, 1);
        
        // Verifico che il nodo di inizializzazione sia effettivamente vuoto
        assertNull(decl.getInit());

        // Creo un nodo costante e lo uso per simulare un aggiornamento della dichiarazione "int a = 5;"
        NodeCost init = new NodeCost(LangType.INT, "5", 1);
        decl.setInit(init);
        
        // Verifico che il nuovo ramo dell'albero sia stato agganciato correttamente
        assertEquals(init, decl.getInit());
    }
}