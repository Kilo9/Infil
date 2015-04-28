package infil;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Timer used to count in reverse.
 * @author Jeremy Bassi
 */
public class CountDownTimer extends Timer
{
    private int time;
    
    public CountDownTimer(int t)
    {
        time = t; 
    }
    
    @Override
    public void start()
    {
        super.start();
    }
    
    public int getSecondsRemaining()
    {
        return time - super.getSeconds();
    }
    
    public boolean timeUp()
    {
        if (getSecondsRemaining() <= 0)
        {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String timeStamp()
    {
        int seconds = getSecondsRemaining();
        NumberFormat formatter = new DecimalFormat("00");
        return "[" + (seconds / 3600) + ":" + formatter.format((seconds / 60) % 60) + ":" + formatter.format(seconds % 60) + "]"; 
    }
    
    public void add(int s)
    {
        time += s;
    }
}
