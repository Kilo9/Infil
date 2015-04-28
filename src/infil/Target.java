package infil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Jeremy Bassi
 */
public class Target 
{   
    //Code Randomizer Values
    public static final int MIN_DIGITS = 3;
    public static final int MAX_DIGITS = 5;
    
    private static ArrayList<String> names = new ArrayList<>();
    private static DataReader dr = new DataReader();
    private static final String namesPath = "/main/names.txt";
    
    static
    {
        names.addAll(Arrays.asList(dr.read(namesPath)));
    }
    
    private String name;
    private int code;
    private String ip;
    private int credits;
    private int xp;
    
    private int temp;
    private int end;
    private int interceptCount;
    
    private int interceptRate; //from 1 to 10
    
    private boolean bruteForce;
    private boolean scanned;
    
    public Target(String name, String ip, int code, int credits, int xp, int interceptRate)
    {
        this.name = name;
        this.ip = ip;
        this.code = code;
        this.credits = credits;
        this.xp = xp;
        this.interceptRate = interceptRate;
        
        interceptCount = 0;
        
        scanned = false;
    }
    
    public Target()
    {
        Random r = new Random();

        //name
        if (names.isEmpty())
        {
            name = "XYZ";
        }
        else
        {
            int nm = r.nextInt(names.size());
            name = names.remove(nm);
        }
            
        credits = 100 + r.nextInt(8) * 50;
        xp = 20 + ((r.nextInt(8) + 1) * 10);
        ip = randIP();
        code = randCode();
        interceptRate = r.nextInt(10) + 1;
    }
    
    private String randIP()
    {
        Random r = new Random();
        return "" + r.nextInt(9) + r.nextInt(9) + r.nextInt(9) + "." + r.nextInt(9) + "." + r.nextInt(9) + "." + r.nextInt(9) + r.nextInt(9) + r.nextInt(9);
    }
    
    /**
     * Advances the bruteforce code num, and if the code matches, returns flag (-1)
     * @return the message to display
     */
    public String bruteForce()
    {       
        
        if (temp == end + 1)
        {
            bruteForce = false;
            return "BruteForce stopped: end value reached";
        }
        
        if (temp == code) 
        {
            bruteForce = false;
            Sound.finish.play();
            return "the code is: " + getCode();
        }

        bruteForce = true;
        
        return "attempting code: " + temp++;
    }
    
    public void setBrute(int start)
    {
        temp = start;
        int digits = String.valueOf(code).length();
        end = (int) Math.pow(10, ++digits); //assign to max number of code
    }
    
    public void setBrute(int start, int end)
    {
        temp = start;
        this.end = end;
    }
    
    private int randCode()
    {
        Random r = new Random();
        //generates the code from the random
        int digits = MIN_DIGITS + r.nextInt(MAX_DIGITS - MIN_DIGITS);
        int c = 0;
        
        for (int i = 0; i < digits; i++)
        {
            c += r.nextInt(9) * ((int)Math.pow(10, i));
        }
        
        return  c;
    }
    
    public String intercept()
    {
        interceptCount++;
        
        String strCode = String.valueOf(code);
        String inter = "";
        
        for (char c : strCode.toCharArray())
        {
            if (interceptRate >= ((int)(Math.random() * 10 + 1)))
            {
                inter += c;
            }
            else
            {
                inter += "-";
            }
        }
        
        return inter;
    }
    
    
    @Override
    public String toString()
    {
        return name;
    }
    
    public String scan()
    {
        scanned = true;
        return name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return the credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * @return the xp
     */
    public int getXp() {
        return xp;
    }

    /**
     * @return the bruteForce
     */
    public boolean isBruteForce() {
        return bruteForce;
    }

    public void activateBrute()
    {
        bruteForce = true;
    }

    /**
     * @return the interceptCount
     */
    public int getInterceptCount() {
        return interceptCount;
    }

    /**
     * @return the scanned
     */
    public boolean isScanned() {
        return scanned;
    }

}
