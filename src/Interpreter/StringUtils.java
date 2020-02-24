package Interpreter;

public class StringUtils {

    public static String unescape(String raw){
        String processed = raw.replaceAll("\\n","\n");
        processed = processed.replaceAll("\\t","\t");
        processed = processed.replaceAll("\\r","\r");

        return processed;
    }
}
