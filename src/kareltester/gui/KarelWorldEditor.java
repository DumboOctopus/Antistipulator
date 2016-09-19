package kareltester.gui;

import kareltester.FileReaderWriter;
import kareltester.KTerminalUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 */
public class KarelWorldEditor extends JFrame{
    private KarelWorldViewComponent worldViewComponent;

    private JComboBox<Option> worldModifyingOptions;
    private JComboBox<Option> readFileOptions;

    private Option[] standardOptions;
    private JButton btnRefresh;
    private JButton btnRun;


    //TODO; refactor comboBox into special class

    public KarelWorldEditor()
    {
        worldViewComponent = new KarelWorldViewComponent();
        add(worldViewComponent, BorderLayout.CENTER);
        worldViewComponent.addBeeperMode();


        //-------------jPanel
        setUpJPanel();

        //--------------
        JPanel panel = new JPanel();

        //streets
        panel.add(new JLabel("Streets: "));
        final JTextField streetField = new JTextField(FileReaderWriter.getStreets() + "");
        streetField.setColumns(3);
        streetField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int i = Integer.parseInt(streetField.getText());
                    FileReaderWriter.setStreets(i);
                    KTerminalUtils.println("Streets set to " + FileReaderWriter.getStreets());
                    KTerminalUtils.println("Now please restart the program to view changes");
                } catch (Exception e1)
                {
                    streetField.setText(""+FileReaderWriter.getStreets());
                }
            }
        });
        panel.add(streetField);

        //avenues
        panel.add(new JLabel("Avenues: "));
        final JTextField avenuesField = new JTextField(FileReaderWriter.getAvenues() + "");
        avenuesField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int i = Integer.parseInt(avenuesField.getText());
                    FileReaderWriter.setAvenues(i);
                    KTerminalUtils.println("Avenues set to " + FileReaderWriter.getAvenues());
                    KTerminalUtils.println("Now please restart the program to view changes");
                }catch (Exception he)
                {
                    avenuesField.setText(""+FileReaderWriter.getAvenues());
                }
            }
        });
        panel.add(avenuesField);

        add(panel, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(770, 650);
    }

    private void setUpJPanel() {

        //panel.setLayout(new GridLayout(5,2));

        //refresh Button
        JToolBar toolBar = new JToolBar();
        btnRefresh = new JButton("Refresh Karels");
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] karelFiles = FileReaderWriter.getAllKarelsJavaFilesInFolder();
                for(int i = standardOptions.length; i < worldModifyingOptions.getItemCount(); i ++)
                {
                    Option option = worldModifyingOptions.getItemAt(i);
                    System.out.println("Option: " + option.toString());
                    boolean toRemove = true;
                    for (int j = 0; j < karelFiles.length; j++) {
                        System.out.println("\t:"+karelFiles[j].getName());
                        if(option.toString().equals( karelFiles[j].getName()))
                        {
                            //option is a usable karel
                            toRemove = false;
                            System.out.println("its alright");
                        }
                    }
                    if(toRemove) worldModifyingOptions.removeItemAt(i);

                }
                for(File f: karelFiles)
                {
                    boolean toAdd = true;
                    for (int j = 0; j < worldModifyingOptions.getItemCount(); j++) {
                        Option option = worldModifyingOptions.getItemAt(j);
                        if(option.toString().equals(f.getName()))
                        {
                            //that means file is a valid option and is  already included
                            toAdd = false;
                        }
                    }
                    if(toAdd) worldModifyingOptions.addItem(getOptionFromFile(f));
                }

            }
        });
        toolBar.add(btnRefresh);

        //setUp run button
        btnRun = new JButton("Run");
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {

                        FileReaderWriter.runKarelTest();
                        return null;
                    }
                };
                worker.execute();
            }
        });
        toolBar.add(btnRun);


        //setUp standardOptions
        setUpStandardOptions();

        //setUp comboBox
        JPanel panel = new JPanel();
        panel.add(new JLabel("Click Mode:"));
        setUpComboBox();
        panel.add(worldModifyingOptions);
        toolBar.add(panel);

        //set up read from file button
        panel = new JPanel();

        panel.add(new JLabel("Copy Kwld: "));
        File[] kwlds = FileReaderWriter.getAllKwldFilesInFolder();
        Option[] kwldOptions = new Option[kwlds.length];
        for (int i = 0; i < kwlds.length; i++) {
            final File curr = kwlds[i];
            kwldOptions[i] = new Option(
                    kwlds[i].getName(),
                    new ActionListener() {
                        private File f = curr;
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            FileReaderWriter.copyFrom(f);
                        }
                    }
            );
        }
        readFileOptions = new JComboBox<>(kwldOptions);
        readFileOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Option)readFileOptions.getSelectedItem()).doAction();
            }
        });
        panel.add(readFileOptions);
        toolBar.add(panel);


        JButton clrButton = new JButton("Clear World");
        clrButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                FileReaderWriter.clearWorld();
            }
        });
        toolBar.add(clrButton);


        add(toolBar, BorderLayout.NORTH);
        
    }

    private void setUpStandardOptions() {
        standardOptions = new Option[]{
                new Option(
                        "Add Beeper",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.addBeeperMode();
                            }
                        }
                ),
                new Option(
                        "Remove Beeper",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.removeBeeperMode();
                            }
                        }
                ),
                new Option(
                        "Add NS Wall",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.addNSWallMode();
                            }
                        }
                ),
                new Option(
                        "Remove NS Wall",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.removeNSWallMode();
                            }
                        }
                ),
                new Option(
                        "Add EW Wall",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.addEWWallMode();
                            }
                        }
                ),
                 new Option(
                        "Remove EW Wall",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.removeEWWallMode();
                            }
                        }
                ),
                new Option(
                        "Rotate Karel",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.rotateKarelMode();
                            }
                        }
                ),
                new Option(
                        "Remove Karel",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.removeKarelMode();
                            }
                        }
                ),
                new Option(
                        "Add Beeper To Karel",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.addBeeperToKarelMode();
                            }
                        }
                ),
                new Option(
                        "Remove Beeper From Karel",
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                worldViewComponent.removeBeeperFromKarelMode();
                            }
                        }
                )
        };
    }

    private void setUpComboBox() {
        File[] karelsFiles = FileReaderWriter.getAllKarelsJavaFilesInFolder();
        Option[] so = new Option[standardOptions.length + karelsFiles.length];
        for (int i = 0; i < standardOptions.length; i++) {
            so[i] = standardOptions[i];
        }
        for (int i = 0; i < karelsFiles.length; i++) {
            File f = karelsFiles[i];
            so[i + standardOptions.length] = getOptionFromFile(f);
        }
        worldModifyingOptions = new JComboBox<>(so);
        worldModifyingOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Option) worldModifyingOptions.getSelectedItem()).doAction();
            }
        });
    }

    private Option getOptionFromFile(final File f) {
        return new Option(
                f.getName(),
                new ActionListener() {
                    private File karelFile = f;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //method in worldView that set karel
                        worldViewComponent.addKarelMode(karelFile);
                    }
                }
        );
    }

    private class Option{
        private String title;
        private ActionListener listener;

        public Option(String title, ActionListener listener) {
            this.title = title;
            this.listener = listener;
        }

        public void doAction()
        {
            listener.actionPerformed(null);
        }

        public String toString()
        {
            return title;
        }

    }





    //==================================START THIS PROGRAM==================================//

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KarelWorldEditor();
            }
        });
    }
}
