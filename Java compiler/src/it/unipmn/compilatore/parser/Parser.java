package it.unipmn.compilatore.parser;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.*;

/**
 * Classe che implementa l'analizzatore sintattico (Parser).
 * Costruisce l'albero sintattico (AST) verificando se la sequenza di token
 * passata dallo Scanner rispetta le regole grammaticali del nostro linguaggio.
 */
public class Parser {

    private final Scanner scanner;
    private Token currentToken;

    /**
     * Costruttore del Parser.
     * Inizializza l'analisi chiedendo subito il primo token allo Scanner.
     * * @param scanner L'analizzatore lessicale che fornisce i token.
     */
    public Parser(Scanner scanner) throws LexicalException, SyntacticException {
        if (scanner == null) {
            throw new SyntacticException("Errore interno: Scanner non definito per il Parser.");
        }
        this.scanner = scanner;
        // Salvo il primo token per preparare l'analisi
        this.currentToken = scanner.nextToken();
    }

    /**
     * Verifica che il token in lettura sia esattamente quello che ci si aspetta.
     * Se corrisponde, si fa avanzare l'analisi caricando il token successivo.
     * * @param expected Il tipo di token che mi aspetto di trovare in questo punto.
     */
    private void match(TokenType expected) throws LexicalException, SyntacticException {
        if (currentToken.getType() == expected) {
            // Il token è quello giusto, procedo caricando il successivo
            this.currentToken = scanner.nextToken();
        } else {
            throw new SyntacticException(
                    "Errore Sintattico alla riga " + currentToken.getRiga() +
                            ": Atteso " + expected + ", trovato " + currentToken.getType()
            );
        }
    }

    /**
     * Funzione principale che avvia la traduzione dell'intero programma.
     * * @return Il nodo radice di tutto l'albero (NodeProgram).
     */
    public NodeProgram parse() throws LexicalException, SyntacticException {
        // Creo il blocco principale che conterrà tutto il codice
        NodeProgram rootNode = new NodeProgram(currentToken.getRiga());

        // Itero finché non incontro la fine del file per estrarre le istruzioni
        while (currentToken.getType() != TokenType.EOF) {
            NodeDecSt node = parseStatement();
            // Aggiungo il nodo dell'istruzione appena letta al blocco del programma
            rootNode.addStatement(node);
        }

        return rootNode;
    }

    /**
     * Smista l'analisi verso regole più specifiche in base a come inizia l'istruzione.
     * * @return Il nodo generico corrispondente all'istruzione trovata.
     */
    private NodeDecSt parseStatement() throws LexicalException, SyntacticException {
        switch (currentToken.getType()) {
            case TYINT:
            case TYFLOAT:
                // Se inizia con un tipo (int/float) è sicuramente una dichiarazione
                return parseDecl();
            case PRINT:
                // Se inizia con 'print' avvio l'analisi della stampa
                return parsePrint();
            case ID:
                // Se inizia con un nome di variabile è un assegnamento
                return parseAssign();
            default:
                throw new SyntacticException("Istruzione non valida o inattesa alla riga " + currentToken.getRiga() +
                        ": trovato " + currentToken.getType());
        }
    }

    /**
     * Analizza una dichiarazione di variabile, con o senza inizializzazione associata.
     * * @return Il nodo NodeDecl costruito.
     */
    private NodeDecl parseDecl() throws LexicalException, SyntacticException {
        LangType type;

        // Estraggo il tipo della variabile e avanzo col match
        if (currentToken.getType() == TokenType.TYINT) {
            type = LangType.INT;
            match(TokenType.TYINT);
        } else if (currentToken.getType() == TokenType.TYFLOAT) {
            type = LangType.FLOAT;
            match(TokenType.TYFLOAT);
        } else {
            throw new SyntacticException("Atteso tipo (int o float) alla riga " + currentToken.getRiga());
        }

        // Il nome della variabile è obbligatorio dopo il tipo
        if (currentToken.getType() != TokenType.ID) {
            throw new SyntacticException("Atteso identificatore dopo il tipo alla riga " + currentToken.getRiga());
        }
        
        // Mi salvo le informazioni della variabile per inserirle nel nodo
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());
        match(TokenType.ID);

        NodeExpr init = null;
        // Verifico se c'è un simbolo "=" per gestire l'inizializzazione immediata
        if (currentToken.getType() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            // Salvo l'espressione a destra dell'uguale
            init = parseExpression();
        }

        // Finisco l'istruzione col punto e virgola obbligatorio
        match(TokenType.SEMI);
        return new NodeDecl(id, type, init, id.getRiga());
    }

    /**
     * Analizza l'istruzione di stampa a video.
     * * @return Il nodo NodePrint costruito.
     */
    private NodePrint parsePrint() throws LexicalException, SyntacticException {
        match(TokenType.PRINT);

        if (currentToken.getType() != TokenType.ID) {
            throw new SyntacticException("Atteso identificatore dopo 'print' alla riga " + currentToken.getRiga());
        }
        
        // Estraggo l'identificatore della variabile che voglio stampare
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());
        match(TokenType.ID);

        match(TokenType.SEMI);
        return new NodePrint(id, id.getRiga());
    }

    /**
     * Analizza un'operazione di assegnamento a una variabile.
     * * @return Il nodo NodeAssign.
     */
    private NodeAssign parseAssign() throws LexicalException, SyntacticException {
        // Estraggo il nome della variabile da sovrascrivere
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());
        match(TokenType.ID);
        
        match(TokenType.ASSIGN);

        // Risolvo tutto il blocco matematico a destra dell'uguale
        NodeExpr expr = parseExpression();

        match(TokenType.SEMI);
        return new NodeAssign(id, expr, id.getRiga());
    }

    /**
     * Analizza le operazioni aritmetiche con la precedenza più bassa (somma e sottrazione).
     * * @return Il nodo radice dell'espressione analizzata.
     */
    private NodeExpr parseExpression() throws LexicalException, SyntacticException {
        // Valuto prima il termine, che ha priorità maggiore sulle somme
        NodeExpr left = parseTerm();

        // Itero per costruire catene di somme o sottrazioni in sequenza (es: a + b - c)
        while (currentToken.getType() == TokenType.PLUS || currentToken.getType() == TokenType.MINUS) {
            // Salvo qual è l'operatore matematico trovato
            LangOper op = (currentToken.getType() == TokenType.PLUS) ? LangOper.PLUS : LangOper.MINUS;
            int rigaOp = currentToken.getRiga();
            match(currentToken.getType());
            
            // Valuto il termine alla destra dell'operatore
            NodeExpr right = parseTerm();

            // Aggiorno l'albero: il nuovo nodo operazione diventa padre di left e right
            left = new NodeBinOp(op, left, right, rigaOp);
        }

        return left;
    }

    /**
     * Analizza le operazioni con precedenza intermedia (moltiplicazione e divisione).
     * * @return Il nodo espressione corrispondente al termine.
     */
    private NodeExpr parseTerm() throws LexicalException, SyntacticException {
        // Salgo ancora di priorità valutando prima i singoli fattori
        NodeExpr left = parseFactor();

        // Itero per accorpare catene di moltiplicazioni o divisioni (es: a * b / c)
        while (currentToken.getType() == TokenType.TIMES || currentToken.getType() == TokenType.DIVIDE) {
            LangOper op = (currentToken.getType() == TokenType.TIMES) ? LangOper.TIMES : LangOper.DIVIDE;
            int rigaOp = currentToken.getRiga();
            match(currentToken.getType());
            
            NodeExpr right = parseFactor();
            
            // Collego i due operandi appena trovati sotto un nuovo nodo operazione
            left = new NodeBinOp(op, left, right, rigaOp);
        }

        return left;
    }

    /**
     * Analizza gli elementi indivisibili di un'espressione, con la massima priorità.
     * Gestisce costanti, l'uso di variabili e le parentesi.
     * * @return Il nodo espressione del fattore base.
     */
    private NodeExpr parseFactor() throws LexicalException, SyntacticException {
        if (currentToken.getType() == TokenType.INT) {
            // Riconosco un numero senza virgola
            NodeCost node = new NodeCost(LangType.INT, currentToken.getVal(), currentToken.getRiga());
            match(TokenType.INT);
            return node;
        }
        else if (currentToken.getType() == TokenType.FLOAT) {
            // Riconosco un numero decimale
            NodeCost node = new NodeCost(LangType.FLOAT, currentToken.getVal(), currentToken.getRiga());
            match(TokenType.FLOAT);
            return node;
        }
        else if (currentToken.getType() == TokenType.ID) {
            // Riconosco l'uso di una variabile per prelevarne il valore
            NodeId nodeId = new NodeId(currentToken.getVal(), currentToken.getRiga());
            match(TokenType.ID);
            // Incapsulo la variabile in NodeDeref per indicare che stiamo leggendo il suo dato
            return new NodeDeref(nodeId.getRiga(), nodeId);
        }
        else if (currentToken.getType() == TokenType.LPAREN) {
            // Do massima priorità ai blocchi racchiusi tra parentesi
            match(TokenType.LPAREN);
            
            // Ricomincio l'analisi per risolvere l'espressione interna alla parentesi
            NodeExpr expr = parseExpression();
            
            match(TokenType.RPAREN);
            return expr;
        }

        throw new SyntacticException("Atteso numero, variabile o parentesi aperta alla riga " + currentToken.getRiga() +
                ", trovato " + currentToken.getType());
    }
}