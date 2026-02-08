package it.unipmn.compilatore.parser;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.*;

/**
 * Analizzatore sintattico a discesa ricorsiva.
 * <p>
 * Costruisce l'AST (Abstract Syntax Tree) verificando che la sequenza di token
 * in ingresso rispetti la grammatica del linguaggio.
 * </p>
 */
public class Parser {

    private final Scanner scanner;
    private Token currentToken;

    /**
     * Inizializza il parser e legge immediatamente il primo token (lookahead).
     */
    public Parser(Scanner scanner) throws LexicalException, SyntacticException {
        if (scanner == null) {
            throw new SyntacticException("Errore interno: Scanner non definito per il Parser.");
        }
        this.scanner = scanner;
        this.currentToken = scanner.nextToken();
    }

    /**
     * Verifica che il token corrente corrisponda a quello atteso e avanza al successivo.
     * <p>
     * Se il token è diverso da quello atteso, lancia un'eccezione sintattica.
     * </p>
     */
    private void match(TokenType expected) throws LexicalException, SyntacticException {
        if (currentToken.getType() == expected) {
            this.currentToken = scanner.nextToken();
        } else {
            throw new SyntacticException(
                    "Errore Sintattico alla riga " + currentToken.getRiga() +
                            ": Atteso " + expected + ", trovato " + currentToken.getType()
            );
        }
    }

    /**
     * Analizza l'intero programma.
     * <p>
     * Corrisponde alla regola iniziale: Program -> Statement* EOF
     * </p>
     */
    public NodeProgram parse() throws LexicalException, SyntacticException {
        NodeProgram rootNode = new NodeProgram(currentToken.getRiga());

        // Itero finché non raggiungo la fine del file
        while (currentToken.getType() != TokenType.EOF) {
            NodeAST nodeAST = parseStatement();
            rootNode.addStatement(nodeAST);
        }

        return rootNode;
    }

    /**
     * Smista l'analisi dell'istruzione in base al token corrente.
     */
    private NodeAST parseStatement() throws LexicalException, SyntacticException {
        switch (currentToken.getType()) {
            case TYINT:
            case TYFLOAT:
                return parseDecl();
            case PRINT:
                return parsePrint();
            case ID:
                return parseAssign();
            default:
                throw new SyntacticException("Istruzione non valida o inattesa alla riga " + currentToken.getRiga() +
                        ": trovato " + currentToken.getType());
        }
    }

    /**
     * Analizza una dichiarazione di variabile.
     * <p>
     * Regola: Decl -> Type ID ('=' Expression)? ';'
     * </p>
     */
    private NodeDecl parseDecl() throws LexicalException, SyntacticException {
        LangType type;

        // Determino il tipo della variabile
        if (currentToken.getType() == TokenType.TYINT) {
            type = LangType.INT;
            match(TokenType.TYINT);
        } else if (currentToken.getType() == TokenType.TYFLOAT) {
            type = LangType.FLOAT;
            match(TokenType.TYFLOAT);
        } else {
            throw new SyntacticException("Atteso tipo (int o float) alla riga " + currentToken.getRiga());
        }

        // Verifico e consumo l'identificatore
        if (currentToken.getType() != TokenType.ID) {
            throw new SyntacticException("Atteso identificatore dopo il tipo alla riga " + currentToken.getRiga());
        }
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());
        match(TokenType.ID);

        // Gestisco l'inizializzazione opzionale
        NodeAST init = null;
        if (currentToken.getType() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            init = parseExpression();
        }

        match(TokenType.SEMI);
        return new NodeDecl(id, type, init, id.getRiga());
    }

    /**
     * Analizza un'istruzione di stampa.
     */
    private NodePrint parsePrint() throws LexicalException, SyntacticException {
        match(TokenType.PRINT);

        if (currentToken.getType() != TokenType.ID) {
            throw new SyntacticException("Atteso identificatore dopo 'print' alla riga " + currentToken.getRiga());
        }
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());
        match(TokenType.ID);

        match(TokenType.SEMI);
        return new NodePrint(id, id.getRiga());
    }

    /**
     * Analizza un assegnamento.
     */
    private NodeAssign parseAssign() throws LexicalException, SyntacticException {
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());
        match(TokenType.ID);
        match(TokenType.ASSIGN);

        // Analizzo l'espressione a destra dell'uguale
        NodeAST expr = parseExpression();

        match(TokenType.SEMI);
        return new NodeAssign(id, expr, id.getRiga());
    }

    /**
     * Analizza un'espressione (livello più basso di precedenza: somma/sottrazione).
     * <p>
     * Regola: Expression -> Term { (+|-) Term }
     * </p>
     */
    private NodeAST parseExpression() throws LexicalException, SyntacticException {
        NodeAST left = parseTerm();

        // Itero finché trovo operatori di somma o sottrazione
        while (currentToken.getType() == TokenType.PLUS || currentToken.getType() == TokenType.MINUS) {
            LangOper op = (currentToken.getType() == TokenType.PLUS) ? LangOper.PLUS : LangOper.MINUS;
            int rigaOp = currentToken.getRiga();
            match(currentToken.getType());
            NodeAST right = parseTerm();

            // Costruisco l'albero crescendo verso sinistra
            left = new NodeBinOp(op, left, right, rigaOp);
        }

        return left;
    }

    /**
     * Analizza un termine (livello medio di precedenza: moltiplicazione/divisione).
     * <p>
     * Regola: Term -> Factor { (*|/) Factor }
     * </p>
     */
    private NodeAST parseTerm() throws LexicalException, SyntacticException {
        NodeAST left = parseFactor();

        // Itero finché trovo operatori di moltiplicazione o divisione
        while (currentToken.getType() == TokenType.TIMES || currentToken.getType() == TokenType.DIVIDE) {
            LangOper op = (currentToken.getType() == TokenType.TIMES) ? LangOper.TIMES : LangOper.DIVIDE;
            int rigaOp = currentToken.getRiga();
            match(currentToken.getType());
            NodeAST right = parseFactor();
            left = new NodeBinOp(op, left, right, rigaOp);
        }

        return left;
    }

    /**
     * Analizza un fattore (livello massimo di precedenza: atomi e parentesi).
     * <p>
     * Regola: Factor -> INT | FLOAT | ID | '(' Expression ')'
     * </p>
     */
    private NodeAST parseFactor() throws LexicalException, SyntacticException {
        if (currentToken.getType() == TokenType.INT) {
            NodeCost node = new NodeCost(LangType.INT, currentToken.getVal(), currentToken.getRiga());
            match(TokenType.INT);
            return node;
        }
        else if (currentToken.getType() == TokenType.FLOAT) {
            NodeCost node = new NodeCost(LangType.FLOAT, currentToken.getVal(), currentToken.getRiga());
            match(TokenType.FLOAT);
            return node;
        }
        else if (currentToken.getType() == TokenType.ID) {
            NodeId node = new NodeId(currentToken.getVal(), currentToken.getRiga());
            match(TokenType.ID);
            return node;
        }
        // Gestisco il caso delle parentesi tonde per le espressioni complesse
        else if (currentToken.getType() == TokenType.LPAREN) {
            match(TokenType.LPAREN); // Consumo la parentesi aperta
            NodeAST expr = parseExpression(); // Ricomincio l'analisi dall'espressione (reset priorità)
            match(TokenType.RPAREN); // Consumo la parentesi chiusa
            return expr; // Restituisco il nodo interno
        }

        throw new SyntacticException("Atteso numero, variabile o parentesi aperta alla riga " + currentToken.getRiga() +
                ", trovato " + currentToken.getType());
    }
}