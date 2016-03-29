package kareltester;

import java.io.*;
import java.util.ArrayList;

/**
 * Description:
 *  This class manages all files. It does this by creating kwld2 or reusing an old kwld2 (so that 
 *  brings up the option of saving worlds for later testing). Whenever called on by the KarelWorldEditor
 *  it will add beepers, walls, and karels.
 *
 *  This class is a whole bunch of utility methods. To use these utility methods one must first call
 *  the static method setUp() which preforms all necessary preparations. Note that calling this multiple times
 *  will not cause any issues so always call this before using any FileReaderWriter methods.
 *  The other utility methods are as follows
 *
 *      File[] getAllKarelsJavaFilesInFolder():
 *          this is for robbie since he has to list the karels we can use. This will retrive the Files
 *          which are .java and implement TestableKarel.
 *
 *      addListener(Kwld2Listener l)
 *          This method allows u to add an event listener. This will register Kwld2Listener l inside
 *          its list of objects that want to be told when the kwld2 file is modified. When the kwld2
 *          file is modified, FileREaderWriter will call the onChange() method of all of Kwld2Listeners
 *          registered. In other words, it makes it possible to be notified when the kwld2 file is changed
 *      removeListener(Kwld2Listener l)
 *          to unsuscribe from notifications from kwld2 file.
 *
 *      Karel[] getKarel(int st, int av)
 *          gets all karels on street, avenue
 *      int getBeepers(int st, int av)
 *          returns number of beepers on street, avenue
 *      boolean hasNSWall(int st, int av)
 *          return true iff there is north south wall on st, av
 *      boolean hasEWwall(int st, int av)
 *          returns true iff there is East west wall on st, av
 *      Direction getWall(int st, int av)
 *          return Direction.NORTH if theres a North south wall on st, av
 *          return Direction.EAST if theres an East west wall on st, av
 *          returns null if there is no walls on st, av
 *      setStreets(int st)
 *      setAvenues(int av)
 *
 *      addKarel(Karel k)
 *      addBeeper(int st, int av)
 *      addBeepers(int st, int av, int amount)
 *      addNSWall(int st, int av)
 *      addEWWall(int st, int av)
 *      setStreets(int number)
 *      setAvenues(int number)
 *
 *      boolean removeAllBeepers(int st, int av)
 *          removes all beepers from corner st, av
 *          returns true if it found the beeper and removed it
 *          return false if no beepers on that corner to begin with
 *      boolean subtractOneBeeper(int st, int av)
 *          self explanatory
 *      boolean removeNSWall(int st, int av)
 *      boolean removeEWWall(int st, int av)
 *      boolean removeKarels(Karel k)
 *
 *
 *
 *TODO: finish documentation
 * TODO: finish setStreets, setAvenues
 * TODO: finish find all karels in folder
 * TODO: make KTerminal work off of input, output and error streams
 */

//TODO: replace KTerminal with TerminalFactory stuff...maybe some time in the future it looks really complicated T.T
//

public class FileReaderWriter
{
    //===================================ATRRIBUTES===============================/


    private static File kwld2File; //Writer will be created when necessary
    private static File kwldFile;

    private static ArrayList<Karel> uncompiledKarels;
    private static File mainDriver; //Writer will be created when necessary
    private static ArrayList<Kwld2Listener> listeners;
    private static boolean inited = false;





    //==================================psuedo COnstrutor=================================//
    public static void setUp()
    {
        if(inited) return;
        String tmp = FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToOuterFolder = tmp.substring(0, tmp.lastIndexOf(tmp.charAt(0)));
        kwld2File = new File(pathToOuterFolder + "/$KarelsHome.kwld2");

        //creates file if not created already else does nothing
        try{
            kwld2File.createNewFile();
        }catch(Exception e){}

        kwldFile = new File(pathToOuterFolder + "/$KarelsHome.kwld");
        try {
            kwldFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mainDriver = new File(pathToOuterFolder + "/$MainDriver.java");
        
        //creates file if not created already else does nothing
        try{
            mainDriver.createNewFile();
        }catch(Exception e){}

        copyToPlusLibs();

        uncompiledKarels = new ArrayList<Karel>();
        listeners = new ArrayList<Kwld2Listener>();
        inited = true;
    }

    public static void copyFrom(File f)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder builder = new StringBuilder();

            String line = null;
            while((line = reader.readLine()) != null)
            {
                builder.append(line).append("\n");
            }
            reader.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(kwld2File));
            bw.write(builder.toString());
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //fire kwld2 changed for like everything :D
        for (int av = 1; av <= getAvenues(); av++) {
            for (int st = 1; st <= getSteets() ; st++) {
                for(Kwld2Listener listener: listeners)
                    listener.onChange(st, av);
            }
        }
    }




    //=======================================LISTENERS====================================//
    private static void fireKwld2Changed(int[] streets, int[] avenue)
    {
        for (int i = 0; i < streets.length; i++) {
            for(Kwld2Listener listener: listeners)
                listener.onChange(streets[i], avenue[i]);
        }


    }
    public static void addListener(Kwld2Listener l)
    {
        listeners.add(l);
    }
    public static boolean removeListener(Kwld2Listener l)
    {
        return listeners.remove(l);
    }


    //=======================================FINDING ALL IN FOLDER==================//
    /*
    @return all the ? implements TestableKarel inside the person's BlueJ folder

     */
    public static File[] getAllKarelsJavaFilesInFolder()
    {
        String tmp = FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToOuterFolder = tmp.substring(0, tmp.lastIndexOf(tmp.charAt(0)));
        File outerFolder = new File(pathToOuterFolder);
        File[] testableFiles = outerFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //check if its .java
                if(!pathname.getName().contains(".java")) return false;
                //check inside file if its contains public class [FileName] implements TestableKarel
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(pathname));

                    String line = reader.readLine();
                    while(line != null)
                    {
                        String className = pathname.getName();
                        className = className.substring(0, className.indexOf('.'));
                        String prcLine = line.replaceAll(" ","").replaceAll("\n", "");

                        //checking if [className] implements TestableKarel
                        //example: publicclassABCBotextendsRobotimplementsTestableKarel

                        //we are looking at public class {blah}

                        if(prcLine.contains("publicclass" + className))
                        {

                            reader.close(); //we wont bother checking rest of file, theres no point
                            return prcLine.contains("implementsTestableKarel");
                        }

                        line = reader.readLine();
                    }

                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }
        });
        return testableFiles;
    }

    public static File[] getAllKwldFilesInFolder()
    {
        String tmp = FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToOuterFolder = tmp.substring(0, tmp.lastIndexOf(tmp.charAt(0)));
        File outerFolder = new File(pathToOuterFolder);
        return outerFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //check if its .kwld
                return pathname.getName().contains(".kwld") && !pathname.getName().contains("$KarelsHome");

            }
        });
    }


    //=======================================GETTING FROM WORLD===========================//
    public static Karel[] getKarel(int st, int av)
    {
        //tokens = array of all lines that start with _karel [st] [av]
        String[] tokens =  findAllInKwld2("_karel " + st + " " + av + " ");

        //holds karels found as a Karel object.
        Karel[] karelsOnDaSquare = new Karel[tokens.length];


        for (int i = 0; i < tokens.length; i++) {
            //create karel and add to array
            karelsOnDaSquare[i] = Karel.parseKarel(tokens[i]);
        }

        //thats all
        return karelsOnDaSquare;
    }
    public static Karel[] getAllKarels()
    {
        //tokens = array of all lines that start with _karel [st] [av]
        String[] tokens =  findAllInKwld2("_karel ");

        //holds karels found as a Karel object.
        Karel[] karelsOnDaSquare = new Karel[tokens.length];


        for (int i = 0; i < tokens.length; i++) {

            //create karel and add to array
            karelsOnDaSquare[i] = Karel.parseKarel(tokens[i]);
        }

        //thats all
        return karelsOnDaSquare;
    }
    public static int getBeepers(int st, int av)
    {
        //beepers for st, av are only in one line so only need to find the first (and only) beeper [st] [av]
        String token = findFirstInKwld2("beepers " + st + " " + av + " ");
        //System.out.println("Checking "+st+ ","+av+" t:" + token);
        if(token == null) return 0;
        else return Integer.parseInt(token.substring(token.lastIndexOf(" ") + 1, token.length()));

    }
    public static boolean hasNSWall(int st, int av)
    {
        return (findFirstInKwld2("northsouthwalls " + av + " " + st + " ") != null);
    }
    public static boolean hasEWWall(int st, int av)
    {
        return (findFirstInKwld2("eastwestwalls " + st + " " + av + " ") != null);
    }

    public static Direction getWall(int st, int av)
    {
        if(findFirstInKwld2("northsouthwalls " + st + " " + av + " ") != null) return Direction.NORTH;   //north south wall
        else if(findFirstInKwld2("eastwestwalls " + st + " " + av + " ") != null) return Direction.EAST; //eastwest wall
        else return Direction.IDK;
    }
    public static int getSteets()
    {
        String s = findFirstInKwld2("streets ");
        if(s == null)
        {
            setStreets(10);
            return 10;
        }else
        {
            return Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.length()));
        }
    }
    public static int getAvenues()
    {
        String s = findFirstInKwld2("avenues ");
        if(s == null)
        {
            setAvenues(10);
            return 10;
        }else
        {
            return Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.length()));
        }
    }

    //-----------helpers
    private static String[] findAllInKwld2(String keyWord) {
        ArrayList<String> out = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
            String line;
            while((line = reader.readLine()) != null)
            {
                if(line.contains(keyWord)) out.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be read.. T.T");
            return new String[]{"ERROR -1 -1 ERROR -1"};//so getKarel doesn't explode
        } catch (IOException e) {
            e.printStackTrace();
        }

        //copies the arrayList into String[] then return
        String[] stockArr = new String[out.size()];
        stockArr = out.toArray(stockArr);
        return stockArr;
    }

    private static String findFirstInKwld2(String keyword)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
            String line;
            while((line = reader.readLine()) != null)
            {
                if(line.contains(keyword)) return line;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be found for some reason.. T.T");
            return "ERROR";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }







    //========================================ADDING TO WORLD=============================//

    public static void addKarel(Karel karel)
    {
        //format:  _karel [st] [av] [dir] [#beepers] [? extends UrRobot]
        appendToKwld2(karel.toString());
        fireKwld2Changed(new int[]{karel.getStreet()}, new int[]{karel.getAvenue()});
    }
    public static void addBeepers(int st, int av, int num)
    {
        //beepers [st] [av] [#]
        smartAppendToKwld2("beepers " + st + " " + av + " ", num + "");
        fireKwld2Changed(
                new int[]{st},
                new int[]{av}
        );
    }
    public static void addBeeper(int st, int av)
    {
        //beepers [st] [av] [#]
        String signature = "beepers " + st + " " + av + " " ;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
            String line;

            StringBuilder builder = new StringBuilder();
            boolean foundSignature = false;
            while((line = reader.readLine()) != null)
            {
                //if its not the signature...
                if (line.contains(signature)) {
                    int newNumBeepers;
                    newNumBeepers = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1, line.length()));
                    newNumBeepers ++;
                    builder.append(signature).append(" " + newNumBeepers).append("\n");
                    foundSignature = true;
                } else {
                    builder.append(line).append("\n");//CHECK IF \n is okies
                }

            }
            reader.close();

            //adds if foundSig not true
            if(!foundSignature) builder.append(signature + " 1").append("\n");

            //writes to file now :)
            BufferedWriter bw = new BufferedWriter(new FileWriter(kwld2File, false));
            bw.write(builder.toString());
            bw.close();


        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be found for some reason.. T.T");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fireKwld2Changed(
                new int[]{st},
                new int[]{av}
        );
    }

    public static void addNSWall (int st, int av)
    {
        //format: northsouthwalls [av] [st] [st]
        smartAppendToKwld2("northsouthwalls " + av+" "+st +" ", st + "");
        fireKwld2Changed(
                new int[]{st},
                new int[]{av}
        );
    }

    public static void addEWWall (int st, int av)
    {
        smartAppendToKwld2("eastwestwalls " + st+" "+av +" ", av + "");
        fireKwld2Changed(
                new int[]{st},
                new int[]{av}
        );
    }
    public static void setStreets (int in_streets) {
        smartAppendToKwld2("streets ", "" + in_streets);

    }
    public static void setAvenues(int in_avenue) {
        smartAppendToKwld2("avenues ", "" + in_avenue);
    }

    //------helpers
    /*
    this method simply adds the line, if it already exists then does nothing
     */
    private static void appendToKwld2(String toAppend) {
        if(findFirstInKwld2(toAppend) != null) return; //already exists in file
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(kwld2File, true));
            bw.newLine();
            bw.write(toAppend);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: kwld2 cannot be written to ");
        }
    }

    /*
    this method is an interesting concept.
    first parameter is the "signature" of the text. The method will first search for this signature. If it finds it
    it will simply replace the back end of the signature with the "ending".
    If it can't find signature it will simply append "signature" + "ending"
     */
    private static void smartAppendToKwld2(String signature, String ending)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
            String line;

            StringBuilder builder = new StringBuilder();
            boolean foundSignature = false;
            while((line = reader.readLine()) != null)
            {
                //if its not the signature...
                if (line.contains(signature)) {
                    builder.append(signature).append(ending).append("\n");
                    foundSignature = true;
                } else {
                    builder.append(line).append("\n");//CHECK IF \n is okies
                }

            }
            reader.close();

            //adds if foundSig not true
            if(!foundSignature) builder.append(signature + ending).append("\n");

            //writes to file now :)
            BufferedWriter bw = new BufferedWriter(new FileWriter(kwld2File, false));
            bw.write(builder.toString());
            bw.close();


        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be found for some reason.. T.T");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //===========================REMOVING FROM WORLD==================================//

    public static boolean removeAllBeepers(int st, int av)
    {
        if (deleteLineContaining("beepers " + st + " " + av + " " )) {
            fireKwld2Changed(
                    new int[]{st},
                    new int[]{av}
            );
            return true;
        }
        return false;

    }

    public static boolean subtractOneBeeper(int st, int av)
    {
        String signature = "beepers " + st + " " + av + " " ;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
            String line;

            StringBuilder builder = new StringBuilder();
            boolean foundLine = false;
            boolean couldSubtract = true;

            while((line = reader.readLine()) != null)
            {
                //if its not the signature...then add it otherwise don't cuz we delete that
                if (!line.contains(signature)) {
                    builder.append(line).append("\n");//TODO CHECK IF \n is okies
                } else
                {
                    //  0       1   2    3
                    //beepers [st] [av] [#]
                    String[] segments = line.split(" ");

                    int numBeepers = Integer.parseInt(segments[3]) - 1;
                    if(numBeepers < 0){
                        couldSubtract = false;
                        numBeepers = 0;
                    }
                    builder.append(signature).append(" ").append(numBeepers).append("\n");

                    foundLine = true;
                }

            }
            reader.close();


            //writes to file now :)
            BufferedWriter bw = new BufferedWriter(new FileWriter(kwld2File, false));
            bw.write(builder.toString());
            bw.close();
            if (foundLine && couldSubtract) {
                fireKwld2Changed(
                        new int[]{st},
                        new int[]{av}
                );
                return true;
            }
            return false;
        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be found for some reason.. T.T, make sure its inside the blueJ folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("SOmething went horribly wrong, pls find the nearest nerd...");
        return false;
    }

    public static boolean removeNSWall(int st, int av)
    {
        if (deleteLineContaining("northsouthwalls " + av + " " + st + " " )) {
            fireKwld2Changed(
                    new int[]{st},
                    new int[]{av}
            );
            return true;
        }
        else return false;
    }

    public static boolean removeEWWall(int st, int av)
    {
        if (deleteLineContaining("eastwestwalls " + st + " " + av + " " )) {
            fireKwld2Changed(
                    new int[]{st},
                    new int[]{av}
            );
            return true;
        }
        else return false;
    }

    public static boolean removeKarel(Karel k)
    {
        //format:  _karel [st] [av] [dir] [#beepers] [? implements TestableKarel]
        if (deleteLineContaining(k.toString())) {
            fireKwld2Changed(
                    new int[]{k.getStreet()},
                    new int[]{k.getAvenue()}
            );
            return true;
        }
        else return false;
    }

    public static boolean removeKarels(int st, int av)
    {
        //format:  _karel [st] [av] [dir] [#beepers] [? implements TestableKarel]
        if (deleteLineContaining("_karel " + st + " " + av + " " )) {
            fireKwld2Changed(
                    new int[]{st},
                    new int[]{av}
            );
            return true;
        }
        else return false;
    }

    //helpers
    private static boolean deleteLineContaining(String signature) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
            String line;

            StringBuilder builder = new StringBuilder();
            boolean foundLine = false;
            while((line = reader.readLine()) != null)
            {
                //if its not the signature...then add it otherwise don't cuz we delete that
                if (!line.contains(signature)) {
                    builder.append(line).append("\n");//TODO CHECK IF \n is okies
                } else
                {
                    foundLine = true;
                }

            }
            reader.close();


            //writes to file now :)
            BufferedWriter bw = new BufferedWriter(new FileWriter(kwld2File, false));
            bw.write(builder.toString());
            bw.close();
            return foundLine;
        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be found for some reason.. T.T, make sure its inside the blueJ folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("SOmething went horribly wrong, pls find the nearest nerd...");
        return false;
    }



    //=================================SETTING/GETTING WORLD CONSTANTS============================//






    //=================================FINALLLY RUNNING KAREL WORLD :))==================//
    public static void runKarelTest()
    {
        KTerminalUtils.println("Starting KarelTest");
        createKWLD();
        createMainDriver();
        copyToPlusLibs();
        compileMainDriverAndRun();
    }

    /*
    For BlueJ to find the kareltester package, we must have the AntiStipulator jar file inside the +libs folder
    However, the AntiStipulator jar file cannot find the .java to create karels if it is not located in the same
    folder as itself. The java files are not located in +libs so.. So the solution is to keep the
    AntiStipulator.jar outside the +libs folder and have it put a copy of itself inside +libs. +libs
    is assummed to exists because u can't compile BlueJ karel files without it
     */
    private static void copyToPlusLibs()
    {
        String pathToJar = FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToOutside = pathToJar.substring(0, pathToJar.lastIndexOf(pathToJar.charAt(0)));
        File plusLibs = new File(pathToOutside+"/+libs/AntiStipulator.jar");
        if(!plusLibs.exists())
        {
            try {
                File jar = new File(pathToJar);
                InputStream in = new FileInputStream(jar);
                OutputStream out = new FileOutputStream(plusLibs);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void createKWLD() {


        //read from kwld2 and write anything necessary into kwld
        try {

            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
            StringBuilder builder = new StringBuilder();

            builder.append("KarelWorld\n");

            String line;
            while((line = reader.readLine()) != null)
            {
                if(!line.contains("_") && !line.equals("\n") && !line.contains("KarelWorld")) {
                    builder.append(line).append("\n");
                }
            }
            reader.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(kwldFile, false));
            bw.write(builder.toString());
            bw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be found for some reason.. T.T");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createMainDriver() {
        //creates file if not created already else does nothing
        try{
            mainDriver.createNewFile();
        }catch(Exception e){}


        //read from kwld2 and write anything necessary into kwld
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("import kareltester.*;\n");
            builder.append("import kareltherobot.*;\n\n");


            builder.append("public class $MainDriver implements Directions{\n");


            //static initation block
            builder.append("\tstatic {\n");
            builder.append("        World.reset(); \n");
            builder.append("        World.readWorld(\"$KarelsHome.kwld\"); \n");
            builder.append("//      World.setBeeperColor(Color.red);\n");
            builder.append("//      World.setStreetColor(Color.blue);\n");
            builder.append("//      World.setNeutroniumColor(Color.green.darker().darker());\n");
            builder.append("        World.setDelay(50);  \n");
            builder.append("        World.setVisible(true);\n");
            builder.append("        World.showSpeedControl(true);\n ");
            builder.append("    }\n");


            builder.append("\tprivate static long startTime;");

            builder.append("\tpublic static void main(String[] args){\n");

            //to make sure world inited first
            builder.append("\t\tstartTime = System.currentTimeMillis();\n");
            builder.append("\t\twhile(System.currentTimeMillis() - startTime < 3000){}\n");

            Karel[] ks = getAllKarels();
            uncompiledKarels = new ArrayList<>(ks.length);
            for(Karel k: ks)
            {
                uncompiledKarels.add(k);
                builder.append("\t\ttry{\n");
                String nameOfFile = k.getSource().getName();
                String nameOfClass = nameOfFile.substring(0, nameOfFile.length() - 5);
                String constructor = "new " + nameOfClass + "(" + k.getStreet() + "," + k.getAvenue() + "," + Direction.getDirectionsInterface(k.getDir()) + "," + k.getBeepers() + ");";

                builder.append("\t\t\tTestableKarel k" + "= "+constructor + "\n");
                builder.append("\t\t\tk.task();\n");
                builder.append("\t\t}").append("catch(Exception e){}\n");
            }

            builder.append("\t}\n");
            builder.append("}\n");



            BufferedWriter bw = new BufferedWriter(new FileWriter(mainDriver, false));
            bw.write(builder.toString());
            bw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be found for some reason.. T.T");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compileMainDriverAndRun() {
        //javac.tools method; problem: ToolProvider.getSystemJavaCompiler returns null when double click jar file
//
//
//        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
//        KTerminalUtils.println(javac);
//        StandardJavaFileManager fileManager = javac.getStandardFileManager(null, null, null);
//        Iterable<? extends JavaFileObject> toCompile;
//        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
//
//        KTerminalUtils.println("Hows it going guys :D");
//
//        ArrayList<File> fileToCompile = new ArrayList<File>();
//        fileToCompile.add(mainDriver);
//
//        toCompile =fileManager.getJavaFileObjectsFromFiles(fileToCompile);
//
//        //options
//        ArrayList<String> options = new ArrayList<>(2);
//        options.add("-cp");
//        String s = ".:+libs/KarelJRobot.jar:+libs/AntiStipulator.jar";
//        for(Karel k: uncompiledKarels)
//        {
//            s += ":" + k.getSource().getName();
//            KTerminalUtils.println(k);
//        }
//        s+= "";
//        options.add(s);
//        KTerminalUtils.println("Hows it going guys :D");
//        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, diagnostics, options, null, toCompile);
//        boolean success = task.call();
//
//        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
//            KTerminalUtils.println("Error " + diagnostic.getCode() + ":" + diagnostic.getLineNumber());
//            KTerminalUtils.println("\t" + diagnostic.getMessage(Locale.getDefault()));
//
//        }
//        if(!success) return;

        try {

            //javac -cp .:+libs/KarelJRobot.jar:+libs/AntiStipulator.jar \$MainDriver.java
            //java -cp .:+libs/KarelJRobot.jar:+libs/AntiStipulator.jar \$MainDriver

            //i assume we are calling outside +libs folder
            Process pro1 = Runtime.getRuntime().exec(
                    "javac -cp .:+libs/KarelJRobot.jar:AntiStipulator.jar "
                            + mainDriver.getName().substring(0,mainDriver.getName().length())
            );
            printKarelOutput(pro1.getInputStream());
            printKarelOutput(pro1.getErrorStream());

            pro1.waitFor();
            if(pro1.exitValue() != 0) {
                KTerminalUtils.println("Your karel did not compile. Please check your code");
                return;
            }

            Process pro2 = Runtime.getRuntime().exec(
                    "java -cp .:+libs/KarelJRobot.jar:AntiStipulator.jar "
                    + mainDriver.getName().substring(0,mainDriver.getName().length() -5)
            );
            printKarelOutput(pro2.getInputStream());
            printKarelOutput(pro2.getErrorStream());
            pro2.waitFor();
            KTerminalUtils.println("\n\nThats all folks");
        } catch (Exception e) {
            KTerminalUtils.println("BIIGGGG Error occuered sorry freshy or sophy using this program");
            KTerminalUtils.println("I can't access main driver because security... ur computer is dumb. jkjk");
            KTerminalUtils.println("But seriously... check where u made this BlueJ project. There something really wrong");

        }
    }
    //--helper
    private static void printKarelOutput(InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            KTerminalUtils.println(line);
        }
    }


    //===============TESTING================//

}
