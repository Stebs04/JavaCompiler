package it.unipmn.compilatore.symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Classe che gestisce la tabella dei simboli organizzata a stack.
 * Serve per supportare gli scope, ovvero gli ambiti di visibilità delle variabili.
 * Utilizzo uno stack di mappe: la mappa in cima rappresenta lo scope più interno (locale),
 * mentre la mappa in fondo rappresenta lo scope globale.
 */
public class SymbolTable {

    // Stack che contiene le mappe di variabili per ogni livello di scope
    private final Stack<Map<String, Symbol>> scopes;

    /**
     * Costruttore della Symbol Table.
     * Prepara la struttura dati e attiva subito il primo livello, ovvero lo scope globale.
     */
    public SymbolTable() {
        this.scopes = new Stack<>();
        // Creo subito il primo scope richiamando il metodo apposito
        enterScope();
    }

    /**
     * Apre un nuovo ambito di visibilità (scope locale).
     */
    public void enterScope() {
        // Creo una nuova mappa vuota per le variabili di questo livello e la metto in cima allo stack
        scopes.push(new HashMap<>());
    }

    /**
     * Chiude l'ambito di visibilità corrente.
     */
    public void exitScope() {
        // Rimuovo la mappa in cima allo stack per dimenticare le variabili locali
        // Lo faccio solo se non sto eliminando lo scope globale (che deve restare sempre)
        if (scopes.size() > 1) {
            scopes.pop();
        }
    }

    /**
     * Inserisce una nuova variabile nello scope attualmente attivo.
     * @param name Il nome della variabile.
     * @param symbol L'oggetto Symbol che contiene le informazioni della variabile.
     * @return true se l'inserimento è andato a buon fine, false se la variabile esisteva già in questo scope.
     */
    public boolean insert(String name, Symbol symbol) {
        // Leggo la mappa che si trova in cima allo stack senza toglierla
        Map<String, Symbol> currentScope = scopes.peek();

        // Controllo se in questo specifico livello esiste già una variabile con questo nome
        if (currentScope.containsKey(name)) {
            return false;
        }

        // Inserisco la nuova variabile nella mappa del livello corrente
        currentScope.put(name, symbol);
        return true;
    }

    /**
     * Cerca una variabile partendo dallo scope più interno fino a quello globale.
     * @param name Il nome della variabile da cercare.
     * @return Il simbolo associato se la variabile viene trovata, altrimenti null.
     */
    public Symbol lookup(String name) {
        // Itero lo stack all'indietro partendo dalla cima verso il fondo
        for (int i = scopes.size() - 1; i >= 0; i--) {
            
            // Prendo la mappa del livello corrispondente
            Map<String, Symbol> scope = scopes.get(i);
            
            // Se la variabile è in questa mappa, restituisco il suo simbolo
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        
        // Se finisco il ciclo e non ho trovato nulla in nessuno scope, restituisco null
        return null;
    }
}