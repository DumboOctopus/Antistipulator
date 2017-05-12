package kareltester;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Neil Prajapati
 *
 *  This class manages all files. It does this by creating kwld2 or reusing an old kwld2.
 *  Whenever called on by the KarelWorldEditor it will add beepers, walls, and karels.
 *
 *  This class is a whole bunch of utility methods. To use these utility methods one must first call
 *  the static method setUp() which preforms all necessary preparations. Note that calling this multiple times
 *  will not cause any issues so always call this before using any FileReaderWriter methods.
 *  The other utility methods include but are not limited to:
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
 * TODO: make KTerminal work off of input, output and error streams
 */

//TODO: replace KTerminal with TerminalFactory stuff...maybe some time in the future it looks really complicated T.T
//

public class FileReaderWriter
{
    //===================================ATRRIBUTES===============================/


    private static File kwld2File; //Writer will be created when necessary
    private static File kwldFile;

    private static File mainDriver; //Writer will be created when necessary
    private static File mainDriverJ;
    private static ArrayList<Kwld2Listener> listeners;
    private static final String NEW_LINE = System.getProperty("line.separator");;

    private static Stack<Process> mainDriverProcesses;



    //==================================psuedo COnstrutor=================================//
    static
    {

        String tmp = FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToOuterFolder = tmp.substring(0, tmp.lastIndexOf(tmp.charAt(0)));
        kwld2File = new File(pathToOuterFolder + "/$KarelsHome.kwld2");

        mainDriverProcesses = new Stack<>();

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


        mainDriverJ = new File(pathToOuterFolder + "/$MainDriver.j");
        try{
            mainDriverJ.createNewFile();
        }catch(Exception e){}

        copyToPlusLibs();

        listeners = new ArrayList<Kwld2Listener>();

    }

    public static void copyFrom(File f)
    {
        int initialAvenues = getAvenues(), intialStreets = getStreets();
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

        if(initialAvenues != getAvenues() || intialStreets != getStreets()){

            //the thing is...lots of listeners get removed when this is called...so loop forwards.
            for(int i = 0; i < listeners.size(); i++)
            {
                listeners.get(i).onWorldSizeChange();
            }
            return;
        }

        //fire kwld2 changed for like everything :D
        for (int av = 1; av <= getAvenues(); av++) {
            for (int st = 1; st <= getStreets() ; st++) {
                for(Kwld2Listener listener: listeners)
                    listener.onChange(st, av);
            }
        }
    }

    public static void clearWorld()
    {
        int totalAves = getAvenues();
        int totalStres = getStreets();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(kwld2File)); //TODO: what is this for?
            BufferedWriter bw = new BufferedWriter(new FileWriter(kwld2File, false));
            bw.write("streets " + totalStres + NEW_LINE+"avenues " + totalAves);
            bw.close();
            for (int av = 1; av <= totalAves; av++) {
                for (int st = 1; st <= totalStres ; st++) {
                    for(Kwld2Listener listener: listeners)
                        listener.onChange(st, av);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Oh noes, the kwld2 can't be found for some reason.. T.T");
        } catch (IOException e) {
            e.printStackTrace();
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

//    public static Direction getWall(int st, int av)
//    {
//        if(findFirstInKwld2("northsouthwalls " + st + " " + av + " ") != null) return Direction.NORTH;   //north south wall
//        else if(findFirstInKwld2("eastwestwalls " + st + " " + av + " ") != null) return Direction.EAST; //eastwest wall
//        else return Direction.IDK;
//    }
    public static int getStreets()
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
                    builder.append(signature).append(newNumBeepers).append("\n");
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
        for(int i = 0; i < listeners.size(); i++)
        {
            listeners.get(i).onWorldSizeChange();
        }

    }
    public static void setAvenues(int in_avenue) {
        smartAppendToKwld2("avenues ", "" + in_avenue);
        for(int i = 0; i < listeners.size(); i++)
        {
            listeners.get(i).onWorldSizeChange();
        }
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
                    int newNumBeepers;
                    newNumBeepers = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1, line.length()));
                    newNumBeepers --;
                    if(newNumBeepers < 0) {
                        newNumBeepers = 0;
                        couldSubtract = false;
                    }
                    builder.append(signature).append(newNumBeepers).append(NEW_LINE);

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
                    builder.append(line).append(NEW_LINE);//TODO CHECK IF \n is okies
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





    //=================================FINALLLY RUNNING KAREL WORLD :))==================//
    public static void runKarelTest()
    {
        if( getAllKarels().length == 0) {
            KTerminalUtils.println("There must be at least 1 Karel");
            return;
        }
        KTerminalUtils.println("Starting KarelTest");
        while(!mainDriverProcesses.empty())
        {
            mainDriverProcesses.pop().destroy();
        }
        copyToPlusLibs();
        createKWLD();
        //createMainDriver();
        createJasminMainDriver();
        compileMainDriverAndRun();
        //it seems like the World class can't be loaded...T.T idk why tho T.T.T.T
        //outside jar file its fine but instide its like derp.

//        //----------------------RESET WORLD--------------------//
//        ClassLoader cl = FileReaderWriter.class.getClassLoader();
//        SwingWorker<Void, Void> s = new SwingWorker<Void, Void>() {
//            @Override
//            protected Void doInBackground() throws Exception {
//                System.out.println("asdf");
//
//                System.out.println(World.worldCanvas());
//                System.out.println("asdf");
//                World.reset();
//                World.readWorld("$KarelsHome.kwld");
//                World.setBeeperColor(Color.red);
//                World.setStreetColor(Color.blue);
//                World.setNeutroniumColor(Color.green.darker().darker());
//                World.setDelay(50);
//                World.setVisible(true);
//                World.showSpeedControl(true);
//                ABCBot c = new ABCBot(1, 1, Directions.North, 1);
//                return null;
//            }
//        };
//        s.execute();
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


        ///copying jasmin
        //NOTE CODE STOlEN FROM INTERNET FROM Ordiel
        String resourceName = "jasmin.jar";
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = FileReaderWriter.class.getClassLoader().getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            System.out.println(stream);
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder +"/+libs/"+ resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                stream.close();
                resStreamOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //copying karel
        resourceName = "KarelJRobot.jar";
        stream = null;
        resStreamOut = null;
        //String jarFolder;
        try {
            stream = FileReaderWriter.class.getClassLoader().getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            System.out.println(stream);
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder +"/+libs/"+ resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                stream.close();
                resStreamOut.close();
            } catch (Exception e) {
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
                if(!line.contains("_") && !line.equals(NEW_LINE) && !line.contains("KarelWorld")) {
                    builder.append(line).append(NEW_LINE);
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
            mainDriverJ.createNewFile();
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
            for(Karel k: ks)
            {
                builder.append("\t\ttry{\n");
                String nameOfFile = k.getSource().getName();
                String nameOfClass = nameOfFile.substring(0, nameOfFile.length() - 5);
                String constructor = "new " + nameOfClass + "(" + k.getStreet() + "," + k.getAvenue() + "," + Direction.getDirectionsInterface(k.getDir()) + "," + k.getBeepers() + ");";

                builder.append("\t\t\tTestableKarel k" + "= "+constructor + NEW_LINE);
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

    private static void createJasminMainDriver(){
        //creates file if not created already else does nothing
        try{
            mainDriverJ.createNewFile();
        }catch(Exception e){}


        //read from kwld2 and write anything necessary into kwld
        try {
            StringBuilder builder = new StringBuilder();


            Karel[] ks = getAllKarels();
            builder.append(
                ";; Produced with help from JasminVisitor program (BCEL)\n" +
                ";; http://bcel.sourceforge.net/\n" +
                ";; Sun May 15 08:19:15 PDT 2016\n" +
                "\n" +
                ".source $MainDriver.java\n" +
                ".class public $MainDriver\n" +
                ".super java/lang/Object\n" +
                ".implements kareltherobot/Directions\n" +
                "\n" +
                ".field private static startTime J\n" +
                "\n" +
                ".method public <init>()V\n" +
                ".limit stack 1\n" +
                ".limit locals 1\n" +
                ".var 0 is this L$MainDriver; from Label0 to Label1\n" +
                "\n" +
                "Label0:\n" +
                "\taload_0\n" +
                "\tinvokespecial java/lang/Object/<init>()V\n" +
                "Label1:\n" +
                "\treturn\n" +
                "\n" +
                ".end method\n" +
                "\n" +
                ".method public static main([Ljava/lang/String;)V\n" +
                ".limit stack 6\n" +
                ".limit locals 2\n" +
                ".var 0 is arg0 [Ljava/lang/String; from KLabel0 to KLabelNoExceptionPath"+(ks.length - 1)+"\n" +
                "\n" +
                "Label3:\n" +
                "\tinvokestatic java/lang/System/currentTimeMillis()J\n" +
                "\tputstatic $MainDriver/startTime J\n" +
                "Label1:\n" +
                "\tinvokestatic java/lang/System/currentTimeMillis()J\n" +
                "\tgetstatic $MainDriver/startTime J\n" +
                "\tlsub\n" +
                "\tldc2_w 3000\n" +
                "\tlcmp\n" +
                "\tifge KLabel0\n" +
                "\tgoto Label1\n"
            );


            ///all the karels and sstuff
            /*EXAMPLE:
            KLabel0:
                new HappyRobot
                dup
                bipush 6
                iconst_4
                getstatic $MainDriver/North Lkareltherobot/Directions$Direction;
                iconst_0
                invokespecial HappyRobot/<init>(IILkareltherobot/Directions$Direction;I)V
                astore_1
                aload_1
            KLabelInvokeTask0:
                invokeinterface kareltester/TestableKarel/task()V 1
                goto Label2
            KLabelCatch0:
                astore_1
            KLabelNoExceptionPath0:
                return

            .catch java/lang/Exception from KLabel0 to KLabelInvokeTask0 using KLabelCatch0
             */
            /*
            //all labels for karels are in the form KLabel[use]#:
            each label set differs 1
             */
            int lblNumber = 0;

            for (Karel k : ks) {
                builder.append("KLabel").append(lblNumber).append(":").append(NEW_LINE);

                // construction of karel
                String nameOfFile = k.getSource().getName();
                String nameOfClass = nameOfFile.substring(0, nameOfFile.length() - 5);
                builder.append("\tnew " + nameOfClass).append(NEW_LINE);
                builder.append("\tdup").append(NEW_LINE);
                builder.append("\tbipush ").append(k.getStreet()).append(NEW_LINE);
                builder.append("\tbipush ").append(k.getAvenue()).append(NEW_LINE);
                builder.append("\tgetstatic $MainDriver/" + Direction.getDirectionsInterface(k.getDir()) + " Lkareltherobot/Directions$Direction;\n");
                builder.append("\tbipush ").append(k.getBeepers()).append(NEW_LINE);

                builder.append("\tinvokespecial " + nameOfClass + "/<init>(IILkareltherobot/Directions$Direction;I)V\n");
                builder.append(
                        "\tastore_1\n" +
                                "\taload_1\n"
                );

                //invokeinterface karel
                builder.append("KLabelInvokeTask" + lblNumber + ":\n");
                builder.append("\tinvokeinterface kareltester/TestableKarel/task()V 1\n");
                builder.append("\tgoto KLabelNoExceptionPath" + lblNumber + NEW_LINE);

                //catch block
                builder.append(
                        "KLabelCatch" + lblNumber + ":\n" +
                                "\tastore_1\n"
                );

                //no exception path
                builder.append(
                        "KLabelNoExceptionPath" + lblNumber + ":\n" +
                                "\t\n"
                );

                //if its the last one, stick a return on it. :D
                if (lblNumber == ks.length - 1) builder.append("\treturn\n");

                builder.append(".catch java/lang/Exception from KLabel" + lblNumber + " to KLabelInvokeTask" + lblNumber + " using KLabelCatch" + lblNumber + NEW_LINE);

                lblNumber++;
            }


            builder.append(".end method").append("\n");
            //the static block
            builder.append(
                    ".method static <clinit>()V\n" +
                    ".limit stack 1\n" +
                    ".limit locals 0\n" +
                    "\n" +
                    "\tinvokestatic kareltherobot/World/reset()V\n" +
                    "\tldc \"$KarelsHome.kwld\"\n" +
                    "\tinvokestatic kareltherobot/World/readWorld(Ljava/lang/String;)V\n" +
                    "\tbipush 50\n" +
                    "\tinvokestatic kareltherobot/World/setDelay(I)V\n" +
                    "\ticonst_1\n" +
                    "\tinvokestatic kareltherobot/World/setVisible(Z)V\n" +
                    "\ticonst_1\n" +
                    "\tinvokestatic kareltherobot/World/showSpeedControl(Z)V\n" +
                    "\treturn\n" +
                    "\n" +
                    ".end method\n"
            );

            BufferedWriter bw = new BufferedWriter(new FileWriter(mainDriverJ, false));
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


            //this doesn't work bc we have no jdk
            //i assume we are calling outside +libs folder
//            Process pro1 = Runtime.getRuntime().exec(
//                    "javac -cp .:+libs/KarelJRobot.jar:AntiStipulator.jar "
//                            + mainDriver.getName().substring(0,mainDriver.getName().length())
//            );
//            printKarelOutput(pro1.getInputStream());
//            printKarelOutput(pro1.getErrorStream());
//
//            pro1.waitFor();
            Process pro1 = Runtime.getRuntime().exec(
                    "java -jar +libs/jasmin.jar  "
                            + mainDriverJ.getName()
            );
            printKarelOutput(pro1.getInputStream());
            printKarelOutput(pro1.getErrorStream());
            pro1.waitFor();

            if(pro1.exitValue() != 0) {
                KTerminalUtils.println("Your karel did not compile. Please check your code");
                return;
            }


            boolean isWindow =  System.getProperty("os.name").toLowerCase().contains("win");
            Process mainDriverProcess;


            if(isWindow)
            {
                mainDriverProcess = Runtime.getRuntime().exec(
                        "java -cp .;+libs/KarelJRobot.jar;AntiStipulator.jar "
                                + mainDriver.getName().substring(0, mainDriver.getName().length() - 5)
                );
            } else {
                //if its mac of linuex it should be this....i think....this might cause problems but heh
                mainDriverProcess = Runtime.getRuntime().exec(
                        "java -cp .:+libs/KarelJRobot.jar:AntiStipulator.jar "
                                + mainDriver.getName().substring(0, mainDriver.getName().length() - 5)
                );
            }
            mainDriverProcesses.push(mainDriverProcess);
            printKarelOutput(mainDriverProcess.getInputStream());
            printKarelOutput(mainDriverProcess.getErrorStream());
            mainDriverProcess.waitFor();
            KTerminalUtils.println("\n\nThats all folks");
        }
        catch (Exception e) {
            //none of these exceptions are much interest to the user.
            e.printStackTrace();
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



}
