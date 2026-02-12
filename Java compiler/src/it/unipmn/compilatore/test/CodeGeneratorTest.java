package it.unipmn.compilatore.test;

import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.visitor.CodeGeneratorVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per la generazione del codice.
 * Verifico che i nodi dell'albero producano stringhe corrette usando 
 * la notazione postfissa della calcolatrice 'dc'.
 */
public class CodeGeneratorTest {

    /**
     * Verifica le istruzioni base di salvataggio in memoria e recupero.
     */
    @Test
    void testDichiarazioneEStampa() {
        NodeProgram program = new NodeProgram(1);
        
        NodeId id = new NodeId("a", 1);
        NodeCost valore = new NodeCost(LangType.INT, "10", 1);
        NodeDecl dichiarazione = new NodeDecl(id, LangType.INT, valore, 1);
        
        NodePrint stampa = new NodePrint(id, 2);
        
        // Assemblo il programma
        program.addStatement(dichiarazione);
        program.addStatement(stampa);

        // Genero il codice stringa
        CodeGeneratorVisitor visitor = new CodeGeneratorVisitor();
        program.accept(visitor);
        String codiceGenerato = visitor.getCode();

        // Verifico la presenza dei comandi di configurazione e gestione memoria di 'dc'
        assertTrue(codiceGenerato.contains("20 k")); 
        assertTrue(codiceGenerato.contains("10 "));  
        assertTrue(codiceGenerato.contains("sa"));   // Salva in registro 'a'
        assertTrue(codiceGenerato.contains("la"));   // Estrae da registro 'a'
        assertTrue(codiceGenerato.contains("p"));    // Comando di print
    }

    /**
     * Verifica il posizionamento in notazione postfissa degli operatori.
     */
    @Test
    void testNotazionePostfissa() {
        NodeCost cinque = new NodeCost(LangType.INT, "5", 1);
        NodeCost due = new NodeCost(LangType.INT, "2", 1);
        NodeBinOp moltiplicazione = new NodeBinOp(LangOper.TIMES, cinque, due, 1);

        CodeGeneratorVisitor visitor = new CodeGeneratorVisitor();
        
        // Visito solo l'espressione per testarne la conversione isolata
        moltiplicazione.accept(visitor);
        String codiceGenerato = visitor.getCode();

        // Verifico che i numeri vengano inseriti prima dell'operatore matematico
        assertTrue(codiceGenerato.contains("5 "));
        assertTrue(codiceGenerato.contains("2 "));
        assertTrue(codiceGenerato.contains("*\n"));
    }
}