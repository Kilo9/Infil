package infil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

/**
 * Class which handles creation of playable Sound objects
 * @author Jeremy Bassi
 */
public class Sound 
{
    public static Sound finish = loadSound("/sound/finish.wav");
    public static Sound enter = loadSound("/sound/enter.wav");
    public static Sound brute = loadSound("/sound/brute.wav");
    public static Sound error = loadSound("/sound/error.wav");
    public static Sound beep = loadSound("/sound/beep.wav");
    //public static Sound loneStar = loadSound("/sound/loneStar.wav"); 
    
    private Clip clip;
    
    public static Sound loadSound(String path)
    {
        Sound sound = new Sound();
        
        try
        {
            InputStream is = Sound.class.getResourceAsStream(path);
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            sound.clip = clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
        return sound;
    }
    
    public void play()
    {
        try
        {
            if (clip != null)
            {
                new Thread() 
                {
                    @Override
                    public void run()
                    {
                        synchronized (clip)
                        {
                            clip.stop();
                            clip.setFramePosition(0);
                            clip.start();
                        }
                    }
                }.start();
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    public void loop()
    {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    public void stop()
    {
        clip.stop();
    }
}
