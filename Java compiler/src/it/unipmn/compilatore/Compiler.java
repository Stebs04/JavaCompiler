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
 * Classe principale (Driver) del compilatore.
 * <p>
 * Orchesta l'intero processo di compilazione seguendo la pipeline:
 * Scanner -> Parser -> TypeChecker -> CodeGenerator -> Output su file.
 * </p>
 */
public class Compiler {

    /**
     * Metodo di ingresso dell'applicazione.
     *
     * @param args Argomenti da riga di comando (il primo argomento è il file sorgente).
     */
    public static void main(String[] args) {
        // Seleziono il file di input: uso l'argomento se presente, altrimenti un file di default
        String fileName = (args.length > 0) ? args[0] : "programma.txt";

        try {
            System.out.println("Inizio compilazione del file: " + fileName);

            // Inizializzo lo scanner passando il percorso del file
            Scanner scanner = new Scanner(fileName);

            // Inizializzo il parser collegandolo allo scanner
            Parser parser = new Parser(scanner);

            // Avvio l'analisi sintattica per costruire l'AST
            NodeProgram program = parser.parse();
            System.out.println("Analisi sintattica completata correttamente.");

            // Creo il visitatore per l'analisi semantica
            TypeCheckVisitor typeChecker = new TypeCheckVisitor();
            // Percorro l'AST per verificare la correttezza dei tipi e degli scope
            program.accept(typeChecker);
            System.out.println("Analisi semantica completata: nessun errore trovato.");

            // Creo il visitatore per la generazione del codice target (dc)
            CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
            // Percorro l'AST per tradurre i nodi in comandi dc
            program.accept(codeGen);

            // Recupero il codice generato sotto forma di stringa
            String dcCode = codeGen.getCode();

            // Scrivo il codice generato sul file di output "out.dc"
            try (FileWriter writer = new FileWriter("out.dc")) {
                writer.write(dcCode);
            }

            System.out.println("Codice dc generato con successo in out.dc");

        } catch (LexicalException | SyntacticException e) {
            // Intercetto gli errori specifici del compilatore
            System.err.println("Errore di Compilazione: " + e.getMessage());
        } catch (IOException e) {
            // Intercetto errori di sistema (es. file non trovato)
            System.err.println("Errore di I/O: " + e.getMessage());
        }
    }
}