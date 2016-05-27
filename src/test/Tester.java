package test; /**
 * Created on 2/19/16.
 */
import kareltester.*;


public class Tester {
    public static void main(String[] arg)
    {
        KTerminalUtils.print("HI World");
        KTerminalUtils.print("HOLA");
        KTerminalUtils.println("HI");

        KTerminalUtils.println("HI");
    }

    private class myKarel implements TestableKarel{
        @Override
        public void task() {
            //HI NEIL BE COOL AND STUFF LIKE TESTABLEKAREL WOOHOO

        }
    }
}
