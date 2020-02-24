package Interpreter;

public class FatalCompileError extends Exception{
    public FatalCompileError(String message){
        super(message);
    }
}
