package infil;

import java.util.ArrayList;

/**
 * Class which handles upgrade system and values
 * NOTE -- isUpgrade and menu methods must be changed with each new upgrade
 * @author Jeremy Bassi
 */
public class Upgrade 
{
    public static final int BRUTE_SPEED = 1;
    public static final int CREDITS = 2;
    public static final int INSTANT = 3;
    public static final int TIME = 4;

    public static final int SPEED_COST = 50;
    public static final int TIME_COST = 50;
    public static final int CREDITS_COST = 50;
    public static final int INSTANT_COST = 400;
    public static final int CREDIT_AMOUNT = 200;
    public static final int TIME_AMOUNT = 30;
    
    private int type;
    private int cost;
    private String message;
    
    public Upgrade(int type)
    {
        this.type = type;
        
        switch(type)
        {
            case BRUTE_SPEED:
            {
                cost = SPEED_COST;
                message = "Brute Force Speed Upgrade Applied";
            }
                break;
            case TIME:
            {
                cost = TIME_COST;
                message = "Firewall Security Upgrade Applied -- Time Added";
            }
                break;
            case CREDITS:
            {
                cost = CREDITS_COST;
                message = "Credits Added to Account";
            }
                break;
            case INSTANT:
            {
                cost = INSTANT_COST;
                message = "Instant Hacking Abilities Unlocked";
            }
                break;
        }
    }
    
    public static boolean isUpgrade(int i)
    {
        if (i >= 1 && i <= 4)
        {
            return true;
        }
        
        return false;
    }
    
    public static String[] menu()
    {
        ArrayList<String> arr = new ArrayList<>();
        
        arr.add("1 - " + SPEED_COST + " Increase Brute Force Speed");
        arr.add("2 - " + CREDITS_COST + " Credits");
        arr.add("3 - " + INSTANT_COST + " Instant Hacking");
        
        if (Controller.mode == Controller.CLASSIC)
        {
            arr.add("4 - " + TIME_COST + " Firewall Security (Extra Time)"); //copyright Kevin Butryn corp.
        }
        
        String[] str = new String[arr.size()];
        
        return arr.toArray(str); 
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
