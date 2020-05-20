package Interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static Locale locale = new Locale("en","US");
    private static ResourceBundle mess = PropertyResourceBundle.getBundle("Interpreter.compilerUI", locale);

    /**
     * Main method, called from the command line with a
     * @param args
     */
    public static void main(String[] args){

        String fileName;
        String[] rawFlags;

        if(args.length > 0){
            //TODO: write command line parser
            fileName = args[0];
            rawFlags = Arrays.copyOfRange(args, 1, args.length);
        } else {
            System.out.println(mess.getString("inputPrompt"));

            Scanner cmd = new Scanner(System.in);
            String inputLine = cmd.nextLine();
            String[] inputs = inputLine.split("\\s");
            fileName = inputs[0];
            rawFlags = Arrays.copyOfRange(inputs, 1, inputs.length);

            cmd.close();
        }

        Flags flags = new Flags();
        flags.parse(rawFlags);

        Phase.changePhase(mess.getString("scanning"));

        String fileContents = "";
        try {
            fileContents = readFile(fileName);
        } catch (FileNotFoundException e){
            CompileError.enqueue(CompileError.Error.FileNotFound, -1, -1, new String[]{fileName}, true);
        }

        Phase.changePhase(mess.getString("parsing"));

        Lexer lexer = new Lexer(fileContents);
        List<Token> tokens = lexer.lexTokens();

        for(Token t: tokens){
            System.out.println(t.toString());
        }

        Parser parser = new Parser(tokens);
        Expression expression = parser.parse();

        System.out.println(new AstPrinter().print(expression));

        CompileError.dump();
    }

    public static String readFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner fileScanner = null;
        fileScanner = new Scanner(file);

        String fileContents = "";
        while(fileScanner.hasNextLine()){
            fileContents += fileScanner.nextLine() + "\n";
        }

        return fileContents;
    }
}
