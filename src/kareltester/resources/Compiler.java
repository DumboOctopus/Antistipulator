

import javax.tools.*;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This may seem like a terrible organization... like theres litterally a class inside the resoureces
 * Its as if theres a package called resources...however, its actually a fairly good one because, in fact,
 * this file is a resource. This java file is intended to be thrown outside into the AntiStipulator files folder.
 * Then when the Karel files need compiling, it runs.
 *
 * It is outside the jar because classes inside the jar cannot access the javax.tools.JavaCompiler
 */

public class Compiler {

    /**
     *
     * @param args a list of the file names of the classes to compile
     */
    public static void main(String[] args) {
        //javac.tools method; problem: ToolProvider.getSystemJavaCompiler returns null when double click jar file

        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        if(javac == null){
            String javaFiles = "";
            for(String arg: args) javaFiles += " " +arg;
            try{
                Process pro1 = Runtime.getRuntime().exec(
                        "javac -cp .:+libs/KarelJRobot.jar:AntiStipulator.jar"
                                + javaFiles
                );

                pro1.waitFor();
                if(pro1.exitValue() != 0) {
                    System.out.println("Your karel did not compile. Please check your code");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Cannot Compile sources." + e.getMessage());

            }
        } else {
            StandardJavaFileManager fileManager = javac.getStandardFileManager(null, null, null);
            Iterable<? extends JavaFileObject> toCompile;
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();


            //adding all them karels specified in the arguments
            ArrayList<File> fileToCompile = new ArrayList<File>();
            for (String fileName : args) {
                fileToCompile.add(new File(fileName));
            }
            toCompile = fileManager.getJavaFileObjectsFromFiles(fileToCompile);

            //configuring classpath
            ArrayList<String> options = new ArrayList<>(2);
            options.add("-cp");
            options.add(".:+libs/KarelJRobot.jar");

            JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, diagnostics, options, null, toCompile);
            boolean success = task.call();

            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                System.out.println("Error " + diagnostic.getCode() + ":" + diagnostic.getLineNumber());
                System.out.println("\t" + diagnostic.getMessage(Locale.getDefault()));

            }
            System.out.println("Compilation " + (success ? "Successful" : "Unsuccessful"));
        }
    }
}
