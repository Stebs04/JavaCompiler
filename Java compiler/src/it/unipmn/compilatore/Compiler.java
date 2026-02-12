package it.unipmn.compilatore;

import it.unipmn.compilatore.scanner.Scanner;
import it.unipmn.compilatore.parser.Parser;
import it.unipmn.compilatore.ast.NodeProgram;
import it.unipmn.compilatore.visitor.TypeCheckVisitor;
import it.unipmn.compilatore.visitor.CodeGeneratorVisitor;
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
        // Prendo il nome del file passandolo da terminale, altrimenti uso un file di default
        String fileName = (args.length > 0) ? args[0] : "programma.txt";

        try {
            System.out.println("Inizio compilazione: " + fileName);

            // Inizializzo l'analizzatore lessicale passandogli il file sorgente
            Scanner scanner = new Scanner(fileName);
            // Inizializzo l'analizzatore sintattico collegandolo allo scanner
            Parser parser = new Parser(scanner);
            // Avvio la costruzione dell'albero sintattico e mi salvo il nodo radice
            NodeProgram program = parser.parse();

            System.out.println("Parsing completato.");

            // Preparo il visitatore semantico per la verifica dei tipi
            TypeCheckVisitor typeChecker = new TypeCheckVisitor();
            // Faccio visitare l'intero albero per verificare le variabili e inserire i cast impliciti
            program.accept(typeChecker);

            System.out.println("Controllo tipi completato.");

            // Preparo il visitatore per la traduzione in linguaggio dc
            CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
            // Attraverso di nuovo l'albero per generare il codice finale basato su stack
            program.accept(codeGen);

            // Apro il file di destinazione (lo crea se non esiste) per salvare il codice generato
            try (FileWriter writer = new FileWriter("out.dc")) {
                // Estraggo la stringa accumulata dal visitatore e la scrivo nel file
                writer.write(codeGen.getCode());
            }

            System.out.println("Compilazione terminata. Output in out.dc");

        } catch (LexicalException | SyntacticException e) {
            // Catturo e stampo le eccezioni relative alla sintassi o al lessico del programma sorgente
            System.err.println("Errore di Compilazione: " + e.getMessage());
        } catch (IOException e) {
            // Catturo i problemi di lettura/scrittura dei file fisici
            System.err.println("Errore I/O: " + e.getMessage());
        }
    }
}