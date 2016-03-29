package kareltester;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Write a description of class Karel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Karel
{
    private File source;
    private int street;
    private int avenue;
    private int beepers;
    private Direction dir;
    
    public Karel(int st, int av, Direction dir, int beeps, File source)
    {
        this.street = st;
        this.avenue = av;
        this.beepers = beeps;
        this.dir = dir;
        this.source = source;
    }
    public Karel(int st, int av, Direction dir, int beeps, String source)
    {
        this(st,av,dir,beeps, new File(source));
    }
    //===============================GETTERS/SETTERS===============================//

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public int getStreet() {
        return street;
    }

    public void setStreet(int street) {
        this.street = street;
    }

    public int getAvenue() {
        return avenue;
    }

    public void setAvenue(int avenue) {
        this.avenue = avenue;
    }

    public int getBeepers() {
        return beepers;
    }

    public void setBeepers(int beepers) {
        this.beepers = beepers;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    //=============================FROM JCOMPONENT=================//
    /*
    * This method tell the computer how to draw a karel. By calling g.someDrawingMethod()
    * u can draw lines, rectangles, circle and images. For karels all this does is draws the
    * image of the karel in all the space it is provided.
    *
    *
    */
    //================toString and parseKarel
    @Override
    public String toString() {
        //format:  _karel [st] [av] [dir] [#beepers] [? implements TestableKarel]
        return "_karel "
                +street + " "
                +avenue + " "
                +dir + " "
                +beepers + " "
                +source.getName() + " ";
    }

    public static Karel parseKarel(String s){
        //format:  _karel [st] [av] [dir] [#beepers] [? extends TestableKarel]


        //creating scanner and splitting words by spaces
        String[] subTokens = s.split(" ");



        //get all the values we need
        int street = Integer.parseInt(subTokens[1]);
        int avenue = Integer.parseInt(subTokens[2]);
        Direction dir = Direction.parseDirection(subTokens[3]);
        int beepers = Integer.parseInt(subTokens[4]);
        String fileSource = subTokens[5];

        //create karel and return;
        return new Karel(street, avenue, dir, beepers, fileSource);
    }
}
