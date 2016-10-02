package kareltester;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a Karel. It does not synchronize changes with the kwld2 file.
 * Thus, every field is final.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Karel
{
    private final File source;
    private final int street;
    private final int avenue;
    private final int beepers;
    private final Direction dir;
    
    public Karel(int st, int av, Direction dir, int beeps, File source)
    {
        this.street = st;
        this.avenue = av;
        this.beepers = beeps;
        this.dir = dir;
        this.source = source;
    }

    //===============================GETTERS/SETTERS===============================//

    public File getSource() {
        return source;
    }


    public int getStreet() {
        return street;
    }


    public int getAvenue() {
        return avenue;
    }


    public int getBeepers() {
        return beepers;
    }


    public Direction getDir() {
        return dir;
    }


    //================STRING CONVERTING STUFF========================//
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


        //split words by spaces
        String[] subTokens = s.split(" ");



        //get all the values we need
        int street = Integer.parseInt(subTokens[1]);
        int avenue = Integer.parseInt(subTokens[2]);
        Direction dir = Direction.parseDirection(subTokens[3]);
        int beepers = Integer.parseInt(subTokens[4]);
        String fileSource = subTokens[5];

        //create karel and return;
        return new Karel(street, avenue, dir, beepers, new File(fileSource));
    }
}
