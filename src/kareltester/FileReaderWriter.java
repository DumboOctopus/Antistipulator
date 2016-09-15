package kareltester;

import kareltherobot.Directions;
import kareltherobot.World;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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
 * TODO: KTerminals.printErr()
 * TODO: finish documentation
 * TODO: Maybe have a right click tip that uses reflection to call any method of the user's choice :D
 * TODO: replace Direction class with Karel Directions to be more consistant.
 * TODO: use classloaders to see if .class file implements TestableKarel
 * TODO: change System.in stream
 * TODO: reset world after each karel option :D
 *
 * TODO: if necessary, change MVC diagram{
 *     currently, the file itself counts as the model,
 *     but, it will be more efficient if their was an object ,not a file, which has these
 *     constant read and write operations from the GUI.
 *
 *     [KarelCorner, KarelCorner, ...]
 *          ^
 *          |
 *   [KarelWorldView Componenent]
 *        ^   |
 *        |   v
 *      [database]     {KarelWorldEditorComponent}
 *       ^  |                   |
 *       [] []  <---------------0
 *       |  v
 *    [KWLD2 FILE]
 *
 *
 *
 *    WorldDatabase:
 *      Sparse3DArray worldItems;
 *
 *      ctor(File kwld2, File kwld)
 *
 *      flushKwld2();
 *      flushKwld();
 *      getCorner(int st, int av): WorldComponents[]
 *      addComponenet(int st, int av, WorldComponent): void
 *
 *      addListener(CacheListener l)
 *      removeListener(CacheListener l)
 *      -fireCornerChanged(int st, int av)
 *
 *   Sparse3DArray<T>:
 *
 *      ArrayList<Sparse3DArrayItem<T>> worldItems
 *
 *      set(int row, int column, T obj):
 *          either finds (by binary search) and adds object to existing (r,c) one
 *          or inserts new Sparse3DArrayItem<T> in natural ordering
 *      get(int row, int column): T
 *      remove(int row, int column, T obj)
 *      remove(int row, int column): T
 *
 *   Sparse3DArrayItem<T>:
 *
 *      int row, column
 *      ArrayList<T> obj;
 *
 *      ctor:(T obj)
 *
 *      +ArrayList<T> getObjects()
 *
 *
 *      compareTo(): by row
 *
 *   CacheListener
 *      void onCornerChange(int st, int av)
 *
 *
 *
 * }
 */


public class FileReaderWriter
{
    //===================================ATRRIBUTES===============================/

    private static final String NEW_LINE = System.getProperty("line.separator");;
    private static File kwld2File; //Writer will be created when necessary
    private static File kwldFile; //^ same thing as above

    private static ArrayList<Kwld2Listener> listeners; //the corners which listen to the kwld2 file updates

    private static Stack<JFrame> executions;
    

    //==================================Static-Constructor=================================//
    static {
        ////FOR DEBUGGING PATH ERRORS IN Z DRIVE
        //KTerminalUtils.println(FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String tmp = FileReaderWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToOuterFolder = tmp.substring(0, tmp.lastIndexOf(tmp.charAt(0)));
        kwld2File = new File(pathToOuterFolder + System.getProperty("file.separator") + "$KarelsHome.kwld2");

        executions = new Stack<>();

        //creates file if not created already else does nothing
        try{
            kwld2File.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }

        kwldFile = new File(pathToOuterFolder + System.getProperty("file.separator") + "$KarelsHome.kwld");
        try {
            kwldFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //copyToPlusLibs();

        listeners = new ArrayList<Kwld2Listener>();
    }

    public static void copyFrom(File f)
    {
        int streets = getStreets();
        int avenues = getAvenues();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder builder = new StringBuilder();

            //the reason why we use string builder rather than direct copy
            //is so we minimize file writing which is quite expensive. Heh.
            String line = null;
            while((line = reader.readLine()) != null)
            {
                builder.append(line).append(NEW_LINE);
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
            for (int st = 1; st <= getStreets() ; st++) {
                for(Kwld2Listener listener: listeners)
                    listener.onChange(st, av);
            }
        }
        if(streets != getStreets() || avenues != getAvenues())
        {
            KTerminalUtils.println("The size of the world has changed. Please restart the app.");
            KTerminalUtils.println("Yeah...u should seriously listen to me though, after all J' suis la application");
            KTerminalUtils.println("Yeah... please excuse my intentionally bad french.\n\n Sincerely, \nAntiStipulator");
        }

    }


     public static void clearWorld()
    {
        int totalAves = getAvenues();
        int totalStres = getStreets();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
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
    /**
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
                //later use reflection to find if it is instance of UrRobot
                return true;
                /*
                try{
                File parentDir = new File(
                    pathname.getAbsolutePath().substring(
                        0,
                        pathname.getAbsolutePath().lastIndexOf(System.getProperty("file.separator"))
                    ) + System.getProperty("file.separator") + "+libs" //bc classloader refer to highre ones when they can't find it
                );
                URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{parentDir.toURI().toURL()}
                );
                System.out.println(pathname.getName().replace(".java", ""));
                Class<?> karelClass = classLoader.loadClass(pathname.getName().replace(".java", ""));
                Class<?> superClass =  karelClass.getSuperclass();
                while(superClass != null && !superClass.getName().equals("UrRobot"))
                {
                    superClass =  karelClass.getSuperclass();
                }
                return superClass != null;
                } catch(Exception e)
                {
                    KTerminalUtils.println("plz report this error: \n\n"+e);
                    return false;
                }
                */
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
                    builder.append(signature).append(newNumBeepers).append(NEW_LINE);
                    foundSignature = true;
                } else {
                    builder.append(line).append(NEW_LINE);//CHECK IF NEW_LINE is okies
                }

            }
            reader.close();

            //adds if foundSig not true
            if(!foundSignature) builder.append(signature).append("1").append(NEW_LINE);

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
            
            while((line = reader.readLine()) != null)
            {
                //if its not the signature... so that it never includes any signature ones
                if (!line.contains(signature)) {
                    builder.append(line).append(NEW_LINE);//CHECK IF NEW_LINE is okies
                }

            }
            reader.close();

            //since all signatures were removed now we just add this
            builder.append(signature).append(ending).append(NEW_LINE);

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
                    builder.append(line).append(NEW_LINE);//TODO CHECK IF NEW_LINE is okies
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
                    builder.append(line).append(NEW_LINE);//TODO CHECK IF NEW_LINE is okies
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
        KTerminalUtils.println("Starting KarelTest");
        while(!executions.empty())
        {
            executions.pop().dispose();
        }
        KTerminalUtils.clear();
        //copyToPlusLibs();
        createKWLD();
        runNoDriverKarelDriver();
    }

    private static void runNoDriverKarelDriver() {
        //TODO: improve speed control
        JFrame f = new JFrame("Karel Test");
        executions.add(f);
        f.add(World.worldCanvas());
        f.setVisible(true);

        World.reset();
        System.out.println(kwldFile.getName());
        World.readWorld(kwldFile.getName());
//        World.setBeeperColor(Color.red);
//        World.setStreetColor(Color.blue);
//        World.setNeutroniumColor(Color.green.darker().darker());
        World.setDelay(50);
        f.setSize(600, 600);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        World.showSpeedControl(true);

        Karel[] ks = getAllKarels();
        if(ks.length ==0)  return;
        PrintStream defualt = System.out;
        PrintStream stream = new PrintStream(KTerminalUtils.getOutputStream());
        System.setOut(stream);

        try {
        
            File parentDir = new File(
                    ks[0].getSource().getAbsolutePath().substring(
                        0,
                        ks[0].getSource().getAbsolutePath().lastIndexOf(System.getProperty("file.separator"))
                    )
            );
            URLClassLoader classLoader = new URLClassLoader(
                new URL[]{parentDir.toURI().toURL()}
            );
            
            for(Karel k: ks)
            {
                Class<?> karelClass = classLoader.loadClass(k.getSource().getName().replace(".java", ""));
                Constructor<?> ctor = karelClass.getDeclaredConstructor(int.class, int.class, Directions.Direction.class, int.class);
                //System.out.println(ctor);
                Object karelInstance = ctor.newInstance(
                        k.getStreet(),
                        k.getAvenue(),
                        Direction.getKarelDirection( k.getDir()),
                        k.getBeepers()
                );
                Method taskMethod = karelInstance.getClass().getDeclaredMethod("task", new Class[0]);
                taskMethod.invoke(karelInstance, new Object[0]);

            }

        } catch (ClassNotFoundException e) {
            System.out.println("You forgot to compile. Go back in BlueJ and hit the compile button");
            KTerminalUtils.printlnErr(e);
        }catch(NoSuchMethodException e) {
            if(e.getMessage().contains("."))
                System.out.println("You either forgot to write the method: " + e.getMessage());
            else
                System.out.println("You either forgot to write the constructor of " + e.getMessage());
             KTerminalUtils.printlnErr(e);
        } catch(IllegalAccessException e)
        {
            System.out.println("Be sure that your constructor and the task method is public.");
             KTerminalUtils.printlnErr(e);
        }catch (Exception e) {
            System.out.println("Please report this error: ");
            e.printStackTrace();
             KTerminalUtils.printlnErr(e);
        } finally {
            System.setOut(defualt);
        }
    }

    /*
    For BlueJ to find the kareltester package, we must have the AntiStipulator jar file inside the +libs folder
    However, the AntiStipulator jar file cannot find the .java to create karels if it is not located in the same
    folder as itself. The java files are not located in +libs so.. So the solution is to keep the
    AntiStipulator.jar outside the +libs folder and have it put a copy of itself inside +libs. +libs
    is assummed to exists because u can't compile BlueJ karel files without it
     */
    /** since it is not using testableKarel, no need :D**/
    @Deprecated
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

            
        /*
        //NOTE CODE STOlEN FROM INTERNET FROM Ordiel
        String resourceName  = "KarelJRobot.jar";
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        stream = null;
        resStreamOut = null;
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
        */
    }

    private static void createKWLD() {


        //read from kwld2 and write anything necessary into kwld
        try {

            BufferedReader reader = new BufferedReader(new FileReader(kwld2File));
            StringBuilder builder = new StringBuilder();

            builder.append("KarelWorld").append(NEW_LINE);

            String line;
            while((line = reader.readLine()) != null)
            {
                //maybe remove regex if its too expensive
                if(!line.contains("_") && !line.matches("\\s*") && !line.contains("KarelWorld")) {
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


}
