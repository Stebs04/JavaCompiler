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
 * Classe Driver che coordina la pipeline di compilazione.
 */
public class Compiler {

    public static void main(String[] args) {
        String fileName = (args.length > 0) ? args[0] : "programma.txt";

        try {
            System.out.println("Inizio compilazione: " + fileName);

            Scanner scanner = new Scanner(fileName);
            Parser parser = new Parser(scanner);
            NodeProgram program = parser.parse();

            System.out.println("Parsing completato.");

            TypeCheckVisitor typeChecker = new TypeCheckVisitor();
            // Questa fase può modificare l'AST inserendo conversioni
            program.accept(typeChecker);

            System.out.println("Controllo tipi completato.");

            CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
            program.accept(codeGen);

            try (FileWriter writer = new FileWriter("out.dc")) {
                writer.write(codeGen.getCode());
            }

            System.out.println("Compilazione terminata. Output in out.dc");

        } catch (LexicalException | SyntacticException e) {
            System.err.println("Errore di Compilazione: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Errore I/O: " + e.getMessage());
        }
    }
}