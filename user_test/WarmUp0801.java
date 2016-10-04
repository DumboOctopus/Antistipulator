import kareltherobot.*;
import kareltester.*;

public class WarmUp0801 implements TestableKarel
{
	public static void main(String[] args)
	{
		Robot r = new Robot(1, 1,North, 3);
		for(int i = 1; i <= 17; i++)
		{
			PrintRow(Math.abs(i-9));
		}
	}

	public void task()
	{
	    for(int i = 1; i <= 17; i++)
		{
			PrintRow(Math.abs(i-9));
		}
	}
	
	public static void PrintRow (int start)
	{
		String row = "";
		for(int i = 0; i < 10; i++)
		{
			if((start+i + 22)%10 != 0)
				row += (start+i + 22)%10 + "     ";
		}
		System.out.println(row);
	}
}
