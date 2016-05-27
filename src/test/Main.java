package test;
/**
 * Write a description of class Main here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import kareltester.gui.KarelWorldViewComponent;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new JFrame();
                f.add(new KarelWorldViewComponent());
                f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                f.setVisible(true);
                f.setSize(500, 500);
            }
        });
    }
    
    
    
}
