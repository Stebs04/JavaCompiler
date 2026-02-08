package it.unipmn.compilatore.symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Gestisce la tabella dei simboli organizzata a stack per supportare gli scope (ambiti di visibilità).
 * <p>
 * Utilizzo uno Stack di Mappe: l'elemento in cima rappresenta lo scope più interno (locale),
 * mentre quello in fondo rappresenta lo scope globale.
 * </p>
 */
public class SymbolTable {

    private final Stack<Map<String, Symbol>> scopes;

    /**
     * Costruisce la Symbol Table.
     * Inizializzo la struttura dati e attivo immediatamente lo scope globale.
     */
    public SymbolTable() {
        this.scopes = new Stack<>();
        enterScope();
    }

    /**
     * Entra in un nuovo scope.
     * Creo una nuova mappa vuota per le variabili locali e la inserisco in cima allo stack.
     */
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Esce dallo scope corrente.
     * Rimuovo la mappa in cima allo stack per "dimenticare" le variabili locali,
     * ma solo se non sto rimuovendo l'ultimo livello (scope globale).
     */
    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.pop();
        }
    }

    /**
     * Inserisce una nuova variabile nello scope corrente.
     *
     * @param name   Il nome della variabile.
     * @param symbol L'oggetto Simbolo contenente i metadati.
     * @return true se l'inserimento ha successo, false se la variabile esisteva già in questo scope.
     */
    public boolean insert(String name, Symbol symbol) {
        // Recupero la mappa dello scope corrente (senza rimuoverla)
        Map<String, Symbol> currentScope = scopes.peek();

        // Controllo se il nome è già presente nel livello corrente
        if (currentScope.containsKey(name)) {
            return false;
        }

        // Inserisco la variabile nella mappa corrente
        currentScope.put(name, symbol);
        return true;
    }

    /**
     * Cerca una variabile attiva in qualsiasi scope visibile.
     * Itero lo stack partendo dallo scope corrente (cima) scendendo verso quello globale (fondo).
     *
     * @param name Il nome della variabile da cercare.
     * @return Il simbolo associato se trovato, null altrimenti.
     */
    public Symbol lookup(String name) {
        // Scorro lo stack all'indietro per rispettare la regola di visibilità (il più vicino vince)
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, Symbol> scope = scopes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }
}