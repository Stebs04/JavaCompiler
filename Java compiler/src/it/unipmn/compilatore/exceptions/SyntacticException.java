package it.unipmn.compilatore.exceptions;

public class SyntacticException extends IllegalArgumentException{
    public SyntacticException(String message){
        super(message);
    }
}
