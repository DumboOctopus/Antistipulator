import kareltester.*;
import kareltherobot.*;
import java.util.Scanner;

public class TestR extends Robot implements TestableKarel{

    public TestR(int st, int av, Direction dir, int be)
    {
        super(st, av, dir, be);
    }

    public void task()
    {
        Scanner scan = new Scanner(System.in);
        System.out.println(scan.next());
    }

}