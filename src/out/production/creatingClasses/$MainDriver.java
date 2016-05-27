import kareltester.*;
import kareltherobot.*;

public class $MainDriver implements Directions{
	static {
        World.reset(); 
        World.readWorld("$KarelsHome.kwld"); 
//      World.setBeeperColor(Color.red);
//      World.setStreetColor(Color.blue);
//      World.setNeutroniumColor(Color.green.darker().darker());
        World.setDelay(50);  
        World.setVisible(true);
        World.showSpeedControl(true);
     }
	private static long startTime;	public static void main(String[] args){
		startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < 3000){}
		try{
			TestableKarel k= new ABCBot(6,8,East,0);
			k.task();
		}catch(Exception e){}
		try{
			TestableKarel k= new ABCBot(6,8,South,0);
			k.task();
		}catch(Exception e){}
	}
}
