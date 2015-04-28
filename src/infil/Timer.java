package infil;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Class to handle the timing elements of the game
 * @author Jeremy Bassi
 */
public class Timer 
{
    /**
     * when the timer was started in nanoseconds.
     */
    private long startTime;
    
    /**
     * does nothing more than create the object.
     */
    public Timer()
    {
        
    }
    
    /**
     * starts the timer by setting the start time.
     */
    public void start()
    {
        startTime = System.nanoTime();
    }
    
    /**
     * Gives the time in seconds since the start of the timer
     * @return time in seconds
     */
    public int getSeconds()
    {
        return (int) ((System.nanoTime() - startTime) / 1000000000);
    }
    
    /**
     * Gives a formatted timestamp to be used in the game with
     * hours, minutes, and seconds.
     * @return timestamp String
     */
    public String timeStamp()
    {
        int seconds = getSeconds();
        NumberFormat formatter = new DecimalFormat("00");
        return "[" + (seconds / 3600) + ":" + formatter.format((seconds / 60) % 60) + ":" + formatter.format(seconds % 60) + "]"; 
    }
}
