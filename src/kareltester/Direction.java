package kareltester;

import kareltherobot.Directions;

import java.util.InputMismatchException;

/**
 * Enum for representing Directions of Karels
 *
 */
public enum Direction {
    NORTH, SOUTH, EAST, WEST;



    //just like Integer.parseInt(), it takes in a string and returns a Direction data type.
    public static Direction parseDirection(String s)
    {
        switch(s)
        {
            case "NORTH":
                return NORTH;
            case "SOUTH":
                return SOUTH;
            case "EAST":
                return EAST;
            case "WEST":
                return WEST;
        }
        throw new InputMismatchException(s+ " is not a valid direction");
    }

    public static String getDirectionsInterface(Direction d)
    {
        switch(d)
        {
            case NORTH: return "North";
            case SOUTH: return "South";
            case EAST: return "East";
            case WEST: return "West";
        }
        throw new InputMismatchException(d + " is not a karel direction");
    }

    public static Directions.Direction getKarelDirection(Direction d)
    {
        switch(d)
        {
            case NORTH: return Directions.North;
            case SOUTH: return Directions.South;
            case EAST: return Directions.East;
            case WEST: return Directions.West;
        }
        throw new InputMismatchException("is idk, no direction to convert too...");
    }

    public static Direction rotateRight(Direction d)
    {
        switch(d)
        {
            case NORTH:
                return (Direction.EAST);
            case EAST:
                return (Direction.SOUTH);
            case SOUTH:
                return (Direction.WEST);
            case WEST:
                return (Direction.NORTH);
        }
        throw new InputMismatchException("is idk, no direction to convert too...");
    }
}
