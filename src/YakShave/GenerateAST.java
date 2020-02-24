package YakShave;

import com.sun.source.tree.LiteralTree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        String outputDir = "src/Interpreter";

        defineAst(outputDir, "Expression", Arrays.asList(
                "Binary : Expression left, Token operator, Expression right",
                "Grouping   :   Expression expression",
                "Literal :   Object value",
                "Unary  :   Token operator, Expression right"
        ));
    }

    private static void defineAst(
            String outputDir, String baseName, List<String> types
    ) throws IOException {
        String path = outputDir + "/" + baseName + ".java";

        //File file = new File(path);
        //file.mkdirs();
        //file.createNewFile();
        PrintWriter writer = new PrintWriter(path, "UTF-8");



        writer.println("package Interpreter;");
        writer.println();
        writer.println("import java.util.List;");

        writer.println();
        writer.println("abstract class " + baseName + " {");

        for(String type: types){
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println();
        defineVisitor(writer, baseName, types);
        writer.println("}");


        writer.close();
    }

    private static void defineType(
            PrintWriter writer, String baseName,
            String className, String fieldList
    ){
        writer.println();
        writer.println("    static class " + className + " extends " + baseName + " {");

        writer.println("        " + className + "(" + fieldList + ") {");

        String[] fields = fieldList.split(", ");
        for(String field: fields){
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        writer.println();
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor){");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");

        writer.println();
        for(String field: fields){
            writer.println("        final " + field + ";");
        }

        writer.println("    }");
        //TODO: parameters.
    }

    private static void defineVisitor(
            PrintWriter writer, String baseName, List<String> types
    ){
        writer.println("    interface Visitor<R> {");

        for(String type: types){
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(Expression." + typeName + " " +
            baseName.toLowerCase() + ");");
        }

        writer.println("    }");
    }
}
