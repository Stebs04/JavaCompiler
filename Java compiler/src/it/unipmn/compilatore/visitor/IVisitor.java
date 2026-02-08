package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;

/**
 * Interfaccia per il pattern Visitor.
 * <p>
 * Definisce un contratto per visitare ogni singolo tipo di nodo concreto dell'AST.
 * Chi implementerà questa interfaccia (es. TypeChecker, CodeGenerator) fornirà
 * la logica specifica per trattare ogni costrutto del linguaggio.
 * </p>
 */
public interface IVisitor {

    // Visita la radice del programma
    void visit(NodeProgram node);

    // Visita le dichiarazioni (int a; float b;)
    void visit(NodeDecl node);

    // Visita le istruzioni di assegnamento (a = 5;)
    void visit(NodeAssign node);

    // Visita le istruzioni di stampa (print a;)
    void visit(NodePrint node);

    // Visita le operazioni matematiche (a + b)
    void visit(NodeBinOp node);

    // Visita le costanti numeriche (5, 3.14)
    void visit(NodeCost node);

    // Visita gli identificatori (a, x, somma)
    void visit(NodeId node);
}
