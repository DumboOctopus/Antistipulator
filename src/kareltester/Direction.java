package kareltester;

/**
 * Hi this is the document speaking
 *
 * So when your reading this code, you probably noticed the strange line
 *  NORTH, SOUTH, EAST, WEST, IDK;
 * which seems total incorrect and shouldn't compile right? However, this is
 * correct and the reason why is because this is an enum
 *
 * enums are like classes but they just define constants. the
 * NORTH, SOUTH, EAST, WEST, IDK;
 * is defining NORTH, SOUTH, EAST WEST and IDK as public constants so in code i could be like
 *
 * Direction karelsDirection = Direction.NORTH;
 *
 * we could also use it like this:
 *
 * if(karelsDirection == Direction.NORTH)
 * {
 *     //to coding magic
 * }
 *
 */
public enum Direction {
    NORTH, SOUTH, EAST, WEST, IDK;

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
        return IDK;
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
        return "ERROR";
    }
}
