package kareltester;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created on 3/26/16.
 */
public class KarelCornerComponent extends JComponent implements MouseListener, Kwld2Listener {

    //==============================STATICS FOR IMAGES SO LOADING ISNT BAD===================//
    private static HashMap<Direction, Image> imageMap;
    static {
        imageMap = new HashMap<>();
    }




    //===============================NON STATIC================================//
    private KarelWorldViewComponent worldViewComponent;
    private int street, avenue;
    private JPopupMenu menu;

    //to making calculation not in drawing
    private int numBeepers;
    private boolean hasNSWall;
    private boolean hasEWWall;
    private boolean hasKarel;

    public KarelCornerComponent(KarelWorldViewComponent worldViewComponent, int street, int avenue) {
        this.worldViewComponent = worldViewComponent;
        this.street = street;
        this.avenue = avenue;

        addMouseListener(this);
        FileReaderWriter.addListener(this);

        //updateImage();
        updateNumBeepers();
        updateWalls();
        setUpPopupMenu();

    }




    //===============================JCOMPONENT STUFF=========================//
    /*
    That cyclomatic complexity tho
     */
    @Override
    protected void paintComponent(Graphics g) {

        int middleX = getWidth()/2;
        int middleY = getHeight()/2;
        String tooltip = "";

        Graphics2D g2 = (Graphics2D) g;
        //draw crosshair
        g2.setColor(new Color(161, 162, 255));
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
            tooltip += "|beepers on corner:" + numBeepers + "| ";
            int size = getWidth() / 3;

            int w = (getWidth() - size) / 2;
            int h = (getHeight() - size) / 2;
            g.fillOval(w, h, size, size);
            g.setColor(Color.WHITE);
            g.drawString("" + numBeepers, w + 5, h + getHeight()/4);
        }

        //karels
        Karel[] ks = FileReaderWriter.getKarel(street, avenue);
        hasKarel = ks.length > 0;
        for(Karel k:ks)
        {
            String name = k.getSource().getName();

            try {
                Image image = null;
                if(!imageMap.containsKey(k.getDir())) {
                    InputStream is = KarelCornerComponent.class.getClassLoader().getResourceAsStream(
                            "kareltester/resources/karel" + k.getDir() + ".png"
                    );
                    image = ImageIO.read(is);
                    is.close();
                    imageMap.put(k.getDir(), image);
                } else
                {
                    image = imageMap.get(k.getDir());
                }
                g2.setColor(new Color(
                        name.substring(0, name.length()/3).hashCode()%255,
                        name.substring(name.length()/3, 2*name.length()/3).hashCode()%255,
                        name.substring(2*name.length()/3).hashCode()%255
                ));
                g.drawRect(
                        getWidth()/5 + getWidth()/4,
                        getHeight()/5 + getHeight()/4,
                        getWidth()/10,
                        getHeight()/10
                );
                g.drawImage(image.getScaledInstance(getWidth()/2, getHeight()/2, 0), getWidth()/4, getHeight()/4, null);

            } catch (IOException e) {
                e.printStackTrace();
            }
            g2.drawString(
                    k.getBeepers() + "",
                    0,
                    middleY + 12
            );
            tooltip+= name.substring(0, name.length() -5) +":" + k.getDir()+ ":" + k.getBeepers() + " beepers| ";
        }
        setToolTipText(tooltip);


    }


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

    public void setUpPopupMenu()
    {
        menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Rotate karel");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worldViewComponent.rotateKarelsOnCorner(street,avenue);
            }
        });
        menu.add(item);

        item = new JMenuItem("Add Beeper to Karel");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worldViewComponent.addBeeperToKarel(street, avenue, 1);
            }
        });
        menu.add(item);

        item = new JMenuItem("Add 5 Beepers to Karel");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worldViewComponent.addBeeperToKarel(street, avenue, 5);
            }
        });
        menu.add(item);

        item = new JMenuItem("Remove Beeper from Karel");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worldViewComponent.addBeeperToKarel(street, avenue, -1);
            }
        });
        menu.add(item);

        item = new JMenuItem("Remove 5 Beepers from Karel");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worldViewComponent.addBeeperToKarel(street, avenue, -5);
            }
        });
        menu.add(item);

        item = new JMenuItem("Remove all Karels");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileReaderWriter.removeKarels(street, avenue);
            }
        });
        menu.add(item);
        add(menu);
    }



    //==================================MOUSE LISTENER STUFF======================//
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger() && hasKarel) {
            menu.show(e.getComponent(), e.getX(), e.getY());
        } else
        {
            worldViewComponent.onCornerClick(street, avenue);
        }
    }
    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger() && hasKarel) {
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }


    //--unused
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    //=====================================KWLD2 LISTENER STUFF====================//
    @Override
    public void onChange(int st, int av) {
        if(st == street && av == avenue)
        {
            updateNumBeepers();
            updateWalls();
            repaint();
        }
    }

    @Override
    public void onWorldSizeChange() {}
}