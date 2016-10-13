package kareltester;

import kareltherobot.Directions;
import kareltherobot.World;

import javax.swing.*;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Stack;
import java.awt.event.*;

/**
 * Created by neilprajapati on 10/1/16.
 * neilprajapati, dont forget to javaDoc this file.
 */
public class KarelTestFrame extends JFrame {

    private static Stack<KarelTestFrame> executions = new Stack<>();


    public KarelTestFrame(File kwldFile, Karel[] ks)
    {
        //TODO: improve speed control
        super("Karel Test");
        
        System.out.println("Initializing Frame..");
        executions.add(this);
        add(World.worldCanvas());
        setVisible(true);
        /*
        addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e)
                {
                    System.out.println("i was here");
                    dispose();
                }
                public void  windowClosed(WindowEvent e)
                {
                    System.out.println("i was here a");
                    World.stop();
                    dispose();
                }
                public void windowStateChanged(WindowEvent e){
                    System.out.println("i was here  b ");
                    dispose();
                }
        });*/

        World.reset();
        System.out.println(kwldFile.getName());
        World.readWorld(kwldFile.getName());
        World.setDelay(50);
        setSize(600, 600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        World.showSpeedControl(true);
        
        KTerminalUtils.println("Starting Test...");
        
        startTest(ks);
    }

    protected void startTest(Karel[] ks) {
        Class<?>[] karelClasses = null;
        try {
            karelClasses = FileReaderWriter.getKarelClasses(ks);
        } catch  (ClassNotFoundException e) {
            System.out.println("You forgot to compile. Go back in BlueJ and hit the compile button");
            KTerminalUtils.printlnErr(e);
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            KTerminalUtils.println("Error 1");
            return;
        }
         KTerminalUtils.println("Found classes..\n\n-----");
        PrintStream defualt = System.out;
        PrintStream stream = new PrintStream(KTerminalUtils.getOutputStream());
        //System.setOut(stream);
        try{
            for (int i = 0; i < karelClasses.length; i++) {
                Class<?> karelClass = karelClasses[i];
                KTerminalUtils.println(karelClass);
                Constructor<?> ctor = karelClass.getDeclaredConstructor(int.class, int.class, Directions.Direction.class, int.class);
                //System.out.println(ctor);
                Object karelInstance = ctor.newInstance(
                        ks[i].getStreet(),
                        ks[i].getAvenue(),
                        Direction.getKarelDirection(ks[i].getDir()),
                        ks[i].getBeepers()
                    );
                Method taskMethod = karelInstance.getClass().getDeclaredMethod("task", new Class[0]);
                taskMethod.invoke(karelInstance, new Object[0]);
            }

        } catch(NoSuchMethodException e) {
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


    protected static void clearExecutions(){
        while(!executions.empty())
        {
            executions.pop().dispose();
        }
    }
}