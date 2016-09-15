package kareltester;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.OutputStream;

/**
 * Class that makes a terminal. Only one terminal allowed; U can't construct one
 * unless u are a KTerminalUtils
 */
public class KTerminalUtils {
    private static KTerminal currTerminal;
    private static boolean showErrors = false;


    //=====================GETTER=================//
    public static KTerminal getCurrTerminal() {
        return currTerminal;
    }


    //====================METHODS================//
    public static void print(final Object s)
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
    public static void println(final Object s)
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

    public static void clear()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currTerminal.clear();

            }
        });
    }
    
    //=========================ERROR======================//
    public static void printErr(final Object s)
    {

        if(showErrors){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    checkAndInitKTerminal();

                    currTerminal.print(s);

                    currTerminal.requestFocus();

                }
            });
        }

    }
    
    
    public static void setShowErr(boolean b)
    {
        showErrors = b;
    }
    public static void printlnErr(final Object s)
    {
        if(showErrors){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    checkAndInitKTerminal();

                    currTerminal.println(s);

                    currTerminal.requestFocus();

                }
            });
        }
    }
    

    public static TextAreaOutputStream getOutputStream()
    {
        return KTerminal.stream;
    }
    //--helpers----
    private static void checkAndInitKTerminal() {
        if(currTerminal == null) currTerminal = new KTerminal(400, 300);

    }


    private static class KTerminal extends JFrame {

        private JScrollPane scrollPane;
        private JTextArea textArea;
        StringBuilder text;
        public static TextAreaOutputStream stream;


        public KTerminal(int width, int height) {
            super("Karel Terminal :D ");

            textArea = new JTextArea();
            textArea.setEditable(false);
            scrollPane = new JScrollPane(textArea);
            text = new StringBuilder();
            add(scrollPane);

            stream = new TextAreaOutputStream(textArea);

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

        public void clear()
        {
            text = new StringBuilder();
            textArea.setText("");
        }

    }
}
