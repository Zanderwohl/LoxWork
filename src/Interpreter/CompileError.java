package Interpreter;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class CompileError {

    private static ResourceBundle em = ResourceBundle.getBundle("Interpreter.errors", Main.locale);

    /**
     * Enum of all types of errors that can happen; each should have a unique value which can be accessed.
     */
    public enum Error{
        FileNotFound(1),            //A file specified could not be found.
        UnexpectedCharacter(2),     //A character that should not appear in that position has been found.
        UnterminatedComment(3),     //A block commend does not have a closer.
        UnterminatedString(4),      //A string does not have a closing quote.
        CompilerFlagNoArg(5),        //A compiler flag that requires an argument is missing that argument.
        UnrecognizedFlag(6),        //A flag that isn't a real flag was included.
        OpenLeftParen(7),            //A parenthesis was left open.
        ExpectedExpression(8),
        UnterminatedTernary(9)      //A malformed ternary operator.
        ;

        private final int value;
        private Error(int value){
            this.value = value;
        }
        public int value(){
            return value;
        }
    }

    private static ArrayList<CompileError> errors = new ArrayList<CompileError>();

    private Error errorType;
    private int line, pos;
    private String[] details;

    /**
     * An error that gets generated during the compilation.
     * @param e The type of error it is.
     * @param line The line on which the error occurred.
     * @param pos The position of the character on which the error starts.
     * @param details A String array of details about the error and how it occurred.
     * @param fatal true if the error should stop compilation, false otherwise.
     */
    public CompileError(Error e, int line, int pos, String[] details, boolean fatal){
        this.errorType = e;
        this.line = line;
        this.pos = pos;
        this.details = details;
        if(fatal){
            //throw new FatalCompileError(toString());
            System.out.println(toString());
            System.exit(e.value());
        }
    }

    /**
     * Print out all errors that have accumulated.
     */
    public static void dump(){
        for(CompileError e: errors){
            System.out.println(e.toString());
        }
    }

    /**
     * TODO: This should probably go away.
     * @return
     */
    public String toString(){
        return getMessage();
    }

    /**
     * Enqueues an error
     * @param e The error type.
     * @param line The line on which the error occurs.
     * @param pos The character position within the line the error occurs.
     * @param details A String array of details, which can be substituted in the compiler message.
     * @param fatal true if this error should halt compliation, false otherwise.
     */
    public static void enqueue(Error e, int line, int pos, String[] details, boolean fatal){
        errors.add(new CompileError(e, line, pos, details, fatal));
    }

    /**
     * Generates a message for this CompileError, substituting line and character numbers for %location% in the text,
     * an indicator squiggle for %squiggle%, and each item in the details array for each %details%, in order given by
     * the array.
     * @return The completed message.
     */
    private String getMessage(){
        String message = em.getString(errorType.name());
        for(int i = 0; i < details.length; i++){
            message = message.replaceAll("%detail" + i + "%", details[i]);
        }

        if(line != -1 && pos != -1) {
            message = message.replace("%location%", "line " + line + ", character " + pos + "");
            message = message.replace("%squiggle%",generateSquiggle(pos));
        }

        return message;
    }

    /**
     * Outputs a squiggle like "~~~~~~^" to indicate where a problem in compilation is.
     * @param length How many characters in the erroneous symbol is.
     * @return A String of the correct length.
     */
    private String generateSquiggle(int length){
        String squiggle = "";
        for(int i = 0; i < length - 1; i++){
            squiggle += "~";
        }
        squiggle += "^";
        return squiggle;
    }
}
