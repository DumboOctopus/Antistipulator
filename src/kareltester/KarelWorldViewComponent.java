package kareltester;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created on 3/26/16.
 */
public class KarelWorldViewComponent extends JComponent  {

    private CornerClickListener onClickAction;


    public KarelWorldViewComponent()
    {
        super();
        setBorder(BorderFactory.createMatteBorder(0,10,10,0,Color.BLACK));

        //set up Corners
        setUpCorners();
        setSize(555, 555);
    }

    //--helpers
    private void setUpCorners() {
        int maxStreet = FileReaderWriter.getStreets();
        int maxAvenue = FileReaderWriter.getAvenues();

        setLayout(new GridLayout(maxStreet, maxAvenue));
        for (int street = maxStreet - 1; street >= 0 ; street--) {
            for (int avenue = 0; avenue <= maxAvenue - 1; avenue++) {
                //System.out.println("I: "+(street +1) + " " + (avenue+1));
                add(new KarelCornerComponent(this, street + 1, avenue + 1));
            }
        }
    }


    //==================LISTENING TO CORNERS :D=================//
    public void onCornerClick(int street, int avenue) {
        if(onClickAction != null) onClickAction.onClick(street, avenue);
    }




    //====================CHANGE MODES OF BEHAVOR================//
    private interface CornerClickListener{
        void onClick(int st, int av);
    }

    public void addBeeperMode(){
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                FileReaderWriter.addBeeper(st, av);
            }
        };
    }

    public void removeBeeperMode(){
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                FileReaderWriter.subtractOneBeeper(st, av);
            }
        };
    }

    /*
    TODO; replace file with karel so its not depend on kwld system.
     */
    public void addKarelMode(final File karelFile) {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                FileReaderWriter.addKarel(new Karel(st, av, Direction.NORTH, 0, karelFile));
            }
        };
    }



    public void addNSWallMode() {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                FileReaderWriter.addNSWall(st, av);
            }
        };
    }

    public void removeNSWallMode()
    {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                FileReaderWriter.removeNSWall(st, av);
            }
        };
    }

    public void addEWWallMode()
    {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                FileReaderWriter.addEWWall(st, av);
            }
        };
    }

    public void removeEWWallMode()
    {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                FileReaderWriter.removeEWWall(st, av);
            }
        };
    }

    public void rotateKarelMode() {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                rotateKarelsOnCorner(st, av);
            }
        };
    }

    public void removeKarelMode() {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                FileReaderWriter.removeKarels(st, av);
            }
        };
    }

    public void addBeeperToKarelMode() {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                addBeeperToKarel(st, av, 1);

            }
        };
    }

    public void removeBeeperFromKarelMode() {
        onClickAction = new CornerClickListener() {
            @Override
            public void onClick(int st, int av) {
                addBeeperToKarel(st, av, -1);

            }
        };
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }

    protected void rotateKarelsOnCorner(int st, int av) {
        Karel[] karelsOnCorner = FileReaderWriter.getKarel(st, av);
        for(Karel k: karelsOnCorner)
            FileReaderWriter.removeKarel(k);
        for(Karel k: karelsOnCorner)
        {
            Karel newKarel = new Karel(k.getStreet(), k.getAvenue(), Direction.rotateRight(k.getDir()), k.getBeepers(), k.getSource());
            FileReaderWriter.addKarel(newKarel);
        }

    }


    protected void addBeeperToKarel(int st, int av, int amt) {
        Karel[] karels = FileReaderWriter.getKarel(st, av);
        FileReaderWriter.removeKarels(st, av);
        for(Karel k: karels) {
            Karel newKarel = new Karel(k.getStreet(), k.getAvenue(), k.getDir(), k.getBeepers() + amt, k.getSource());
            FileReaderWriter.addKarel(newKarel);
        }
    }
}