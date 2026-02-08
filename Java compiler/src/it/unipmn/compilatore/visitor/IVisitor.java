package it.unipmn.compilatore.visitor;

import it.unipmn.compilatore.ast.*;

/**
 * Interfaccia per il pattern Visitor.
 * <p>
 * Definisco un contratto per visitare ogni singolo tipo di nodo concreto dell'AST.
 * </p>
 */
public interface IVisitor {

    void visit(NodeProgram node);

    void visit(NodeDecl node);

    void visit(NodeAssign node);

    void visit(NodePrint node);

    void visit(NodeBinOp node);

    void visit(NodeCost node);

    void visit(NodeId node);

    // Gestisco la visita del nodo di conversione
    void visit(NodeConvert node);
}