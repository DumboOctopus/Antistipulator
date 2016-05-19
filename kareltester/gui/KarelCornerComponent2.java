package kareltester.gui;

import kareltester.FileReaderWriter;
import kareltester.Karel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

/**
 * Created on 5/15/16.
 */
public class KarelCornerComponent2 extends JComponent {

    private int street, avenue;
    private Image img = null;

    //to making calculation not in drawing
    private int numBeepers;
    private boolean hasNSWall;
    private boolean hasEWWall;

    public KarelCornerComponent2(int street, int avenue) {
        this.street = street;
        this.avenue = avenue;

        //updateImage();
        updateNumBeepers();
        updateWalls();


    }




    //===============================JCOMPONENT STUFF=========================//
    @Override
    protected void paintComponent(Graphics g) {

        //image memory problem
        //g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        int middleX = getWidth()/2;
        int middleY = getHeight()/2;

        Graphics2D g2 = (Graphics2D) g;
        //draw crosshair
        g2.setColor(new Color(255, 143, 108));
        g2.setStroke(new BasicStroke(getWidth()/20));
        g2.draw(new Line2D.Float(0, middleY, getWidth(), middleY));
        g2.draw(new Line2D.Float(middleX, 0, middleX, getHeight()));

        //draw walls
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(getWidth()/6));

        if(hasNSWall)
        {
            g2.draw(new Line2D.Float(getWidth() - 3, 0, getWidth() - 3, getHeight()));
        }
        if(hasEWWall)
        {
            g2.draw(new Line2D.Float(0, 0, getWidth(), 0));
        }
        //beepers
        if(numBeepers != 0) {
            g.setColor(Color.BLACK);

            int size = getWidth() / 3;

            int w = (getWidth() - size) / 2;
            int h = (getHeight() - size) / 2;
            g.fillOval(w, h, size, size);
            g.setColor(Color.WHITE);
            g.drawString("" + numBeepers, w + 5, h + getHeight()/4);
        }

        //karels
        Karel[] ks = FileReaderWriter.getKarel(street, avenue);
        g2.setColor(new Color(0, 155, 0));
        for(Karel k:ks)
        {
            String name = k.getSource().getName();
            g2.drawString(
                    name.substring(0, name.length() -5),
                    0,
                    middleY
            );
            g2.drawString(
                    ("" + k.getDir()).substring(0, 1) + ":" + k.getBeepers(),
                    0,
                    middleY + 12
            );
        }


    }

    //----------------updating data and image------------//
//    public void updateImage()
//    {
//        System.out.println(street + ": " + avenue);
//        boolean nswall = FileReaderWriter.hasNSWall(street, avenue);
//        boolean ewwall = FileReaderWriter.hasEWWall(street,avenue);

    //image memory problem
//        String imageName;
//        if(street != 1 && avenue != 1)
//            imageName = "kareltester/resources/corner 0" + (ewwall?1:0) + (nswall?1:0) + "0.png";
//        else if(street == 1 && avenue == 1)
//            imageName = "kareltester/resources/corner 1" + (ewwall?1:0) + (nswall?1:0) + "1.png";
//        else if(street == 1)
//            imageName = "kareltester/resources/corner 0" + (ewwall?1:0) + (nswall?1:0) + "1.png";
//        else
//            imageName = "kareltester/resources/corner 1" + (ewwall?1:0) + (nswall?1:0) + "0.png";
//
//        try {
//            InputStream is = KarelCornerComponent.class.getClassLoader().getResourceAsStream(imageName);
//            img = ImageIO.read(is);
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//    }

    private void updateNumBeepers() {
        numBeepers = FileReaderWriter.getBeepers(
                this.street,
                this.avenue
        );
    }

    private void updateWalls()
    {
        hasNSWall = FileReaderWriter.hasNSWall(street, avenue);
        hasEWWall = FileReaderWriter.hasEWWall(street,avenue);
    }

    public void update()
    {
        updateWalls();
        updateNumBeepers();
        repaint();
    }
}
