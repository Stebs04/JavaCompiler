package it.unipmn.compilatore;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.parser.Parser;
import it.unipmn.compilatore.ast.NodeProgram;
import it.unipmn.compilatore.visitor.TypeCheckVisitor;
import it.unipmn.compilatore.visitor.CodeGeneratorVisitor;
import it.unipmn.compilatore.visitor.PrintASTVisitor;
import it.unipmn.compilatore.exceptions.LexicalException;
import it.unipmn.compilatore.exceptions.SyntacticException;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe principale che coordina l'intera pipeline di compilazione.
 * <p>
 * Unisce tutte le fasi del compilatore: legge il file sorgente,
 * costruisce l'albero sintattico (AST) tramite Scanner e Parser,
 * controlla i tipi con il TypeChecker e infine genera il codice
 * target per la calcolatrice dc scrivendolo in un file di output.
 * </p>
 */
public class Compiler {

    public static void main(String[] args) {
        // Recupero il nome del file da riga di comando, oppure uso un default
        String fileName = (args.length > 0) ? args[0] : "programma.txt";
        
        // Dichiaro le variabili fuori dal try per poter accedere ai log anche nel catch
        Scanner scanner = null;
        Parser parser = null;

        try {
            System.out.println("Inizio compilazione: " + fileName);

            // Inizializzo lo scanner per leggere i token dal file
            scanner = new Scanner(fileName);
            
            // Inizializzo il parser collegandolo allo scanner
            parser = new Parser(scanner);
            
            // Eseguo il parsing per ottenere l'albero sintattico (AST)
            NodeProgram program = parser.parse();

            System.out.println("Parsing completato.");
            
            // Stampo i log delle prime fasi per debug
            System.out.println("\n--- LOG SCANNER ---");
            System.out.println(scanner.getLog());
            System.out.println("-------------------");
            
            System.out.println("\n--- LOG PARSER ---");
            System.out.println(parser.getLog());
            System.out.println("------------------");

            // Preparo il visitatore semantico per la verifica dei tipi
            TypeCheckVisitor typeChecker = new TypeCheckVisitor();
            // Visito l'albero per validare i tipi e inserire cast impliciti se necessari
            program.accept(typeChecker);

            // Stampo il log dettagliato del TypeChecker
            System.out.println("\n--- LOG TYPE CHECKER ---");
            System.out.println(typeChecker.getLog());
            System.out.println("------------------------");

            System.out.println("Controllo tipi completato.");

            // --- FASE DI DEBUG ATTIVO AST ---
            // Inizializzo il visitatore che stampa l'albero
            PrintASTVisitor printVisitor = new PrintASTVisitor();
            // Visito l'albero modificato dal TypeChecker per generare la stringa
            program.accept(printVisitor);

            // Visualizzo a video come il compilatore vede il codice internamente (inclusi i cast)
            System.out.println("\n--- VISUALIZZAZIONE AST (Dopo Type Check) ---");
            System.out.println(printVisitor.getOutput());
            System.out.println("---------------------------------------------");

            // Inizializzo il visitatore per la generazione del codice target
            CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
            // Visito l'albero per produrre le istruzioni 'dc'
            program.accept(codeGen);

            // Stampo il log dettagliato della generazione codice
            System.out.println("\n--- LOG CODE GENERATOR ---");
            System.out.println(codeGen.getLog());
            System.out.println("--------------------------");

            // Apro il file di output per salvare il risultato della compilazione
            try (FileWriter writer = new FileWriter("out.dc")) {
                // Scrivo il codice generato nel file
                writer.write(codeGen.getCode());
            }

            System.out.println("Compilazione terminata. Output in out.dc");

        } catch (LexicalException | SyntacticException e) {
            // Gestisco errori legati al codice sorgente (lessicali o sintattici)
            System.err.println("ERRORE DI COMPILAZIONE: " + e.getMessage());
            
            // Se ho i log parziali dello scanner o del parser, li stampo per aiutare a capire dove si è rotto
            if (scanner != null) {
                System.out.println("\n--- LOG SCANNER (Errore) ---");
                System.out.println(scanner.getLog());
            }
            if (parser != null) {
                System.out.println("\n--- LOG PARSER (Errore) ---");
                System.out.println(parser.getLog());
            }
            
        } catch (IOException e) {
            // Gestisco errori legati al file system (file non trovato, permessi, ecc.)
            System.err.println("Errore I/O: " + e.getMessage());
        }
    }
}