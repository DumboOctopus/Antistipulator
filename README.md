# Antistipulator
To test the karels...without hassel. Why do more work when u can do less...Worked on with Lydia Chan, Max Nyguyen and Robbie Robertson
Note, this project is design for AP Computer Science Children using BlueJ. Thus, these instructions will focus on utilizing this app in a BlueJ context. If you are another fellow Kareler who is not using BlueJ, please see the Notes at the bottom.

##Instructions:

1. Double-click the AntiStipulator.jar file to start the program. DO SO BEFORE OPENING BLUEJ<sup>*</sup>. The AntiStipulator.jar file will create certain files that BlueJ will need to use.

2. If you want to copy the contents of a Karel world inside your folder, choose the world it in the drop down menu labeled "Copy Kwld." 

3. Select the function of the mouse by clicking on a choice in the drop down menu to the right. After selecting a certain function, click on the desired corner to place or remove a component. For example, if you click on “Add Beeper,” a beeper will appear when you press on a corner.

4. If you wish to delete a World Item, simply press the World Item’s corresponding remove button and then press on the corner on which you want to remove the World Item. For example, if you wanted to remove a beeper from the (1,1) corner, you would simply click on the “Remove Beeper” button and then click on the (1,1) corner.
5. Open BlueJ.
6. Now go into the file of a Karel you wish to test. At the top of the file write:


    `import kareltester.*;`


 Then make your class implement TestableKarel:
   
    `public class MyRobot extends Robot implements TestableKarel`


 Finally, write the task method inside. The task method will be called when the AntiStipulator runs your Karel program.
    ```
    public void task(){
           //your code goes here
    }
    ```
7. Add your Karel into the world: choose the Karel you wish to place using the drop down menu on the right, and then clicking on the corner where the Karel should go. If you do not see your Karel in the drop down menu, “Refresh Karels.”
8. After you have placed your Karels, run the World with the “Run” button. A new window should pop up, and Karels will be instantiated and their task methods will run.
9. If your Karels are not working, you can easily fix your code in BlueJ, compile, and run your code. If you wish to test your Karel in a different World, simply change your world and run again.


##Tips: 

1. To place the desired number of beepers on the corner, press the “Add Beeper” button the desired number of times. 
2. The program removes beepers from the corner one at a time. 
3. Have fun and Karel on. 


The following is an example of a Karel that the AntiStipulator can test:


    import kareltherobot.*;
    import kareltester.*;
    public class MyBot extends Robot implements TestableKarel
    {
       
        public MyBot(int st, int av, Direction dir, int b)
        {
            super(st, av,dir,b);
        }
        
        public void carpetRooms()
        {
            checkWall();
            System.out.println("hi");
            while(frontIsClear())
            {
                move();
                checkWall();
            }
        }
        
        public void checkWall()
        {
            turnLeft();
            move();
            turnRight();
            boolean okay = true;
            for(int i = 0; i < 3; i++)
            {
                if(frontIsClear())okay = false;
                turnLeft();
            }
            if(okay) putBeeper();
            move();
            turnLeft();
        }
        
        public void task()
        {
            carpetRooms();
        }
        
        private void turnRight()
        {
            turnLeft(); turnLeft(); turnLeft();
        }
    }

##Notes:
<sup>*</sup>If u are an IDE other than BlueJ, be sure to add AntiStipulator.jar as an external library. If you are using a terminal to compile and run, be sure to include the jar in the classpath:

    javac -classpath path/to/Antistipulator.jar:path/to/KarelJRobot.jar MyAwesomeKarelProgram.java
    java -classpath path/to/Antistipulator.jar:path/to/KarelJRobot.jar MyAwesomeKarelProgram
