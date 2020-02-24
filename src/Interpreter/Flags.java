package Interpreter;

import java.util.HashMap;
import java.util.Map;

public class Flags {

    public enum flagType{
        Optimize,
        OutputType
    }

    private Map<flagType, String> flags;

    public Flags(){
        flags = new HashMap<>();
    }

    public void parse(String[] flagList){
        int index = 0;
        while(index < flagList.length){
            index = consume(flagList, index);
        }
        System.out.println("Compiler flags:\n" + flags.toString());
    }

    private int consume(String[] flagList, int index){
        String flag = flagList[index];
        String nextFlag = null;

        if(index + 1 < flagList.length){
            nextFlag = flagList[index + 1];
        }
        index++;
        if(flag.equals("-o")){
            flags.put(flagType.Optimize,"true");
            return index;
        }
        if(flag.equals("-p")){
            if(nextTokenArgument(flag, nextFlag)) {
                flags.put(flagType.OutputType, nextFlag);
                index++;
            }
            return index;
        }
        String[] details = {flag};
        CompileError.enqueue(CompileError.Error.UnrecognizedFlag, 0, 0, details, false);
        return index;
    }

    private boolean nextTokenArgument(String flag, String nextFlag){
        if(nextFlag == null || nextFlag.charAt(0) == '-'){ //If not a flag, or no more args
            if(nextFlag == null){   //if no more args, indicate EOL
                nextFlag = "(EOL)";
            }
            String[] details = {flag, nextFlag};
            CompileError.enqueue(CompileError.Error.CompilerFlagNoArg,0,0,details,false);
            return false;
        }
        return true;
    }
}
