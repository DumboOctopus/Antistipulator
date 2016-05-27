package kareltester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created on 3/27/16.
 */
public class Main {
    public static void main(String[] args) {
        Process p = null;
        try {
            System.out.println("enteed");
            p = Runtime.getRuntime().exec("java -mx512M -cp AntiStipulator.jar kareltester.gui.KarelWorldEditor");
            BufferedReader is = new BufferedReader(new InputStreamReader( p.getInputStream()));

            String line = null;
            while((line = is.readLine()) != null)
            {
                System.out.println(line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
