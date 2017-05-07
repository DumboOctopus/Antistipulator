package kareltester;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Class that makes a terminal. Only one terminal allowed; U can't construct one
 * unless u are a KTerminalUtils
 */
public class KTerminalUtils {
    private static KTerminal currTerminal;




    //=====================GETTER=================//
    public static KTerminal getCurrTerminal() {
        return currTerminal;
    }


    //====================METHODS================//
    public static void print(Object s)
    {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkAndInitKTerminal();
                currTerminal.print(s);
                currTerminal.requestFocus();
            }
        });

    }
    public static void println(Object s)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                checkAndInitKTerminal();

                currTerminal.println(s);

                currTerminal.requestFocus();

            }
        });
    }
    public static void show()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkAndInitKTerminal();
                currTerminal.requestFocus();
            }
        });
    }
    //--helpers----
    private static void checkAndInitKTerminal() {
        if(currTerminal == null) currTerminal = new KTerminal(400, 300);

    }

    private static class KTerminal extends JFrame {

        private JScrollPane scrollPane;
        private JTextArea textArea;
        StringBuilder text;

        public KTerminal(int width, int height) {
            super("Karel Terminal :D ");

            textArea = new JTextArea();
            textArea.setEditable(false);
            scrollPane = new JScrollPane(textArea);
            text = new StringBuilder();
            add(scrollPane);



            setBounds(0, 0, width, height);
            setVisible(true);
            setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    currTerminal = null;
                }
            });
        }

        public void print(Object s)
        {
            text.append(s);
            textArea.setText(text.toString());
        }

        public void println(Object s)
        {
            text.append(s).append("\n");
            textArea.setText(text.toString());
        }

    }
}
