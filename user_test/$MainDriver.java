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
			TestableKarel k= new ABCBot(5,5,North,29);
			k.task();
		}catch(Exception e){}
		try{
			TestableKarel k= new ABCBot(8,8,North,0);
			k.task();
		}catch(Exception e){}
		try{
			TestableKarel k= new ABCBot(8,3,North,0);
			k.task();
		}catch(Exception e){}
		try{
			TestableKarel k= new ABCBot(4,3,North,0);
			k.task();
		}catch(Exception e){}
	}
}
