package infil;

import java.util.ArrayList;

/**
 *
 * @author Jeremy Bassi
 */
public class Player 
{
    public static final int BASE_BRUTE = 5;
    public static final int BASE_XP = 100;
    public static final int BASE_CREDITS = 0;
    
    private int xp;
    private int credits;
    private int bruteWait;
    
    private CountDownTimer cdTimer;
    
    public Player(CountDownTimer cdt)
    {
        xp = BASE_XP;
        credits = BASE_CREDITS;
        bruteWait = BASE_BRUTE;
        cdTimer = cdt;
    }
    
    public void applyUpGrade(Upgrade u)
    {
        System.out.println("check 2");
        
        if (u == null)
        {
            return;
        }
        
        if (u.getType() == Upgrade.BRUTE_SPEED)
        {
            if (bruteWait > 1)
            {
                bruteWait--;
            }
        }
        else if (u.getType() == Upgrade.TIME && Controller.mode == Controller.CLASSIC)
        {
            cdTimer.add(Upgrade.TIME_AMOUNT);
        }
        else if (u.getType() == Upgrade.CREDITS)
        {
            System.out.println("check 3");
            credits += Upgrade.CREDIT_AMOUNT;
        }
        else if (u.getType() == Upgrade.INSTANT)
        {
            //work done in Controller
            return;
        }
        else
        {
            return;
        }
    }
    
    public void addCredits(int c)
    {
        credits += c;
    }
    
    public boolean charge(int c)
    {
        if (c > xp)
        {
            return false;
        }
        
        xp -= c;
        return true;
    }
    
    public void addXP(int x)
    {
        xp += x;
    }
    
    /**
     * @return the xp
     */
    public int getXp() {
        return xp;
    }

    /**
     * @return the credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * @return the bruteWait
     */
    public int getBruteWait() {
        return bruteWait;
    }

    /**
     * @param bruteWait the bruteWait to set
     */
    public void setBruteWait(int bruteWait) {
        this.bruteWait = bruteWait;
    }
    
    public String[] status()
    {
        ArrayList<String> arr = new ArrayList<>();
        
        arr.add("Player Status Information");
        arr.add("XP : " + xp);
        arr.add("Credits : " + credits);
        arr.add("BruteForce Speed : " + (6 - bruteWait));
        
        String[] arr0 = new String[arr.size()];
        
        return arr.toArray(arr0);
    }
    
    public void godMode()
    {
        bruteWait = 1; //TODO replace
        xp = 1000000;
        credits = 1000000;
    }
}
