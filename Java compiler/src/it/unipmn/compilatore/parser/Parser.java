package it.unipmn.compilatore.parser;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.token.Token;
import it.unipmn.compilatore.token.TokenType;
import it.unipmn.compilatore.ast.*;
import it.unipmn.compilatore.exceptions.*;

/**
 * La classe Parser implementa l'analisi sintattica del compilatore.
 * <p>
 * Utilizza la tecnica della Discesa Ricorsiva.
 * Il parser richiede i token allo Scanner uno alla volta e verifica che la loro sequenza
 * rispetti la grammatica del linguaggio. Durante questo processo, costruisce
 * l'Abstract Syntax Tree (AST) che rappresenta la struttura logica del programma.
 * </p>
 */
public class Parser {

    // Riferimento allo scanner per ottenere i token
    private final Scanner scanner;

    // Rappresenta il token che stiamo analizzando attualmente.
    private Token currentToken;

    /**
     * Costruisce il Parser e inizializza la lettura.
     * @param scanner L'analizzatore lessicale da cui attingere i token.
     * @throws LexicalException Se il primo token non può essere letto (errore lessicale).
     * @throws SyntacticException Se lo scanner è nullo.
     */
    public Parser(Scanner scanner) throws LexicalException, SyntacticException {
        if (scanner == null) {
            throw new SyntacticException("Errore interno: Scanner non definito per il Parser.");
        }
        this.scanner = scanner;
        // Inizializziamo il parser leggendo il primo token
        this.currentToken = scanner.nextToken();
    }

    /**
     * Metodo helper per verificare e consumare i token.
     * <p>
     * Confronta il tipo del token corrente con quello atteso dalla grammatica:
     * <ul>
     * <li>Se coincidono, avanza al prossimo token (consuma).</li>
     * <li>Se sono diversi, lancia un errore sintattico bloccante.</li>
     * </ul>
     * </p>
     *
     * @param expected Il tipo di token atteso in questo punto della grammatica.
     * @throws LexicalException Se lo scanner fallisce nel leggere il prossimo token.
     * @throws SyntacticException Se il token corrente non è quello atteso.
     */
    private void match(TokenType expected) throws LexicalException, SyntacticException {
        if (currentToken.getType() == expected) {
            // Corrispondenza trovata: passiamo al prossimo token
            this.currentToken = scanner.nextToken();
        } else {
            // Errore grammaticale: trovata una cosa al posto di un'altra
            throw new SyntacticException(
                    "Errore Sintattico alla riga " + currentToken.getRiga() +
                            ": Atteso " + expected + ", trovato " + currentToken.getType()
            );
        }
    }

    /**
     * Avvia l'analisi sintattica dell'intero programma.
     * <p>
     * Crea il nodo radice e continua a leggere istruzioni (Statement) finché
     * non incontra la fine del file (EOF).
     * </p>
     *
     * @return Il nodo radice dell'AST (NodeProgram) contenente tutte le istruzioni.
     * @throws LexicalException In caso di errori nello scanner.
     * @throws SyntacticException In caso di errori nella struttura grammaticale.
     */
    public NodeProgram parse() throws LexicalException, SyntacticException {
        // Creiamo la radice dell'albero usando la riga del primo token
        NodeProgram rootNode = new NodeProgram(currentToken.getRiga());

        // Ciclo finché non finisce il file
        while (currentToken.getType() != TokenType.EOF) {
            // Deleghiamo l'analisi della singola istruzione al metodo specifico
            NodeAST nodeAST = parseStatement();

            // Aggiungiamo il sotto-albero generato alla lista di istruzioni del programma
            rootNode.addStatement(nodeAST);
        }

        return rootNode;
    }

    /**
     * Analizza un'istruzione (Statement) delegando al metodo specifico in base al token corrente.
     * @return Un nodo che rappresenta l'istruzione letta (NodeDecl, NodePrint o NodeAssign).
     * @throws LexicalException In caso di errori di scansione.
     * @throws SyntacticException Se il token corrente non corrisponde all'inizio di un'istruzione valida.
     */
    private NodeAST parseStatement() throws LexicalException, SyntacticException {
        switch (currentToken.getType()) {
            case TYINT:
            case TYFLOAT:
                // Se inizia con un tipo, è una dichiarazione
                return parseDecl();

            case PRINT:
                // Se inizia con 'print', è un'istruzione di stampa
                return parsePrint();

            case ID:
                // Se inizia con un identificatore, è un assegnamento
                return parseAssign();

            default:
                // Qualsiasi altro token qui è un errore (es. un numero o un operatore a inizio riga)
                throw new SyntacticException("Istruzione non valida o inattesa alla riga " + currentToken.getRiga() +
                        ": trovato " + currentToken.getType());
        }
    }

    /**
     * Analizza l'istruzione di stampa.
     * @return Un nodo NodePrint completo.
     * @throws LexicalException In caso di errori di scansione.
     * @throws SyntacticException Se la sintassi (es. punto e virgola mancante) non è rispettata.
     */
    private NodePrint parsePrint() throws LexicalException, SyntacticException {
        // 1. Si consuma la parola chiave 'print'
        match(TokenType.PRINT);

        // 2. Ora currentToken è l'identificatore da stampare.
        // Si verifica che sia un ID prima di leggerne il valore.
        if (currentToken.getType() != TokenType.ID) {
            throw new SyntacticException("Atteso identificatore dopo 'print' alla riga " + currentToken.getRiga());
        }

        // Si crea il nodo foglia per l'identificatore.
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());

        // 3. Si Consuma l'ID
        match(TokenType.ID);

        // 4. Si Consuma il punto e virgola finale
        match(TokenType.SEMI);

        // 5. Creo e restituisco il nodo Print, che "avvolge" l'ID.
        return new NodePrint(id, id.getRiga());
    }

    /**
     * Analizza una dichiarazione di variabile.
     * Gestisce sia la dichiarazione semplice ("int a;") che con inizializzazione ("float b = 5;").
     *
     * @return Un nodo NodeDecl completo.
     * @throws LexicalException In caso di errori di scansione.
     * @throws SyntacticException Se la sintassi è violata (es. tipo sconosciuto, manca il punto e virgola).
     */
    private NodeDecl parseDecl() throws LexicalException, SyntacticException {
        // 1. Identificazione del TIPO (INT o FLOAT)
        LangType type;

        if (currentToken.getType() == TokenType.TYINT) {
            type = LangType.INT;
            match(TokenType.TYINT);
        } else if (currentToken.getType() == TokenType.TYFLOAT) {
            type = LangType.FLOAT;
            match(TokenType.TYFLOAT);
        } else {
            throw new SyntacticException("Atteso tipo (int o float) alla riga " + currentToken.getRiga());
        }

        // 2. Identificazione del NOME (ID)
        if (currentToken.getType() != TokenType.ID) {
            throw new SyntacticException("Atteso identificatore dopo il tipo alla riga " + currentToken.getRiga());
        }
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());
        match(TokenType.ID);

        // 3. Gestione dell'inizializzazione OPZIONALE
        NodeAST init = null;

        if (currentToken.getType() == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            init = parseExpression();
        }

        // 4. Chiusura istruzione
        match(TokenType.SEMI);

        // Ritorniamo il nodo dichiarazione passando anche la riga
        return new NodeDecl(id, type, init, id.getRiga());
    }

    /**
     * Analizza un assegnamento a variabile esistente.
     */
    private NodeAssign parseAssign() throws LexicalException, SyntacticException {
        // Salvo l'ID a sinistra dell'uguale
        NodeId id = new NodeId(currentToken.getVal(), currentToken.getRiga());
        match(TokenType.ID);

        match(TokenType.ASSIGN); // Mangio '='

        // Calcolo l'espressione a destra dell'uguale
        NodeAST expr = parseExpression();

        match(TokenType.SEMI); // Chiudo l'istruzione

        return new NodeAssign(id, expr, id.getRiga());
    }

    /**
     * Analizza un'espressione aritmetica (livello Somma/Sottrazione).
     * <p>
     * Ha la precedenza più bassa. Chiama {@code parseTerm()} per gli operandi
     * e gestisce sequenze di somme/sottrazioni.
     * </p>
     *
     * @return Il nodo radice dell'espressione.
     */
    private NodeAST parseExpression() throws LexicalException, SyntacticException {
        // Leggo il primo termine (che ha precedenza più alta, es. moltiplicazioni)
        NodeAST left = parseTerm();

        // Finché non si trova + o -, si continua a espandere l'albero verso l'alto (associatività a sinistra)
        while (currentToken.getType() == TokenType.PLUS || currentToken.getType() == TokenType.MINUS) {
            // Salvataggio  dell'operatore e la riga
            LangOper op = (currentToken.getType() == TokenType.PLUS) ? LangOper.PLUS : LangOper.MINUS;
            int rigaOp = currentToken.getRiga();
            match(currentToken.getType()); // Consumo l'operatore

            // Lettura del termine destro
            NodeAST right = parseTerm();

            // Creazione di un nuovo nodo binario che diventa la nuova parte sinistra
            left = new NodeBinOp(op, left, right, rigaOp);
        }

        return left;
    }

    /**
     * Analizza un termine (livello Moltiplicazione/Divisione).
     * <p>
     * Ha precedenza media. Chiama {@code parseFactor()} per gli operandi.
     * </p>
     */
    private NodeAST parseTerm() throws LexicalException, SyntacticException {
        // Lettura del primo fattore (numero o ID)
        NodeAST left = parseFactor();

        // Finché troviamo * o /
        while (currentToken.getType() == TokenType.TIMES || currentToken.getType() == TokenType.DIVIDE) {
            LangOper op = (currentToken.getType() == TokenType.TIMES) ? LangOper.TIMES : LangOper.DIVIDE;
            int rigaOp = currentToken.getRiga();
            match(currentToken.getType()); // Consumo l'operatore

            NodeAST right = parseFactor();

            // Combinazione nel nodo binario
            left = new NodeBinOp(op, left, right, rigaOp);
        }

        return left;
    }

    /**
     * Analizza un fattore (livello base).
     * <p>
     * Ha precedenza massima. Gestisce i valori atomici:
     * <ul>
     * <li>Numeri Interi (INT)</li>
     * <li>Numeri Decimali (FLOAT)</li>
     * <li>Identificatori (ID)</li>
     * </ul>
     * </p>
     */
    private NodeAST parseFactor() throws LexicalException, SyntacticException {
        if (currentToken.getType() == TokenType.INT) {
            // Caso Numero Intero
            NodeCost node = new NodeCost(LangType.INT, currentToken.getVal(), currentToken.getRiga());
            match(TokenType.INT);
            return node;
        }
        else if (currentToken.getType() == TokenType.FLOAT) {
            // Caso Numero Float
            NodeCost node = new NodeCost(LangType.FLOAT, currentToken.getVal(), currentToken.getRiga());
            match(TokenType.FLOAT);
            return node;
        }
        else if (currentToken.getType() == TokenType.ID) {
            // Caso Variabile
            NodeId node = new NodeId(currentToken.getVal(), currentToken.getRiga());
            match(TokenType.ID);
            return node;
        }

        // Se arriviamo qui, abbiamo trovato un simbolo non valido in un'espressione (es. un ';')
        throw new SyntacticException("Atteso numero o variabile alla riga " + currentToken.getRiga() +
                ", trovato " + currentToken.getType());
    }
}