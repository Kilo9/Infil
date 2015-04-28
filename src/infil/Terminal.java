package infil;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author Jeremy Bassi
 */
public class Terminal extends Canvas implements Runnable 
{

    public static final int TWIDTH = 800;
    public static final int THEIGHT = 600;
    public static final int LINE_HEIGHT = 20;
    public static final int EXCESS = LINE_HEIGHT / 2;
    public static final int NUM_LINES = THEIGHT / LINE_HEIGHT;
    
    public static final String TITLE = "Infil Beta 1.0";
    public static final Color DEFAULT_BG_COLOR = new Color(0x000000);
    public static final Color DEFAULT_TEXT_COLOR = new Color(0x00ff00);
    
    public static BufferedImage focusLostImage;
    public static BufferedImage gameOverImage;
    
    //public static final boolean 
    
    private static final String iconPath = "/main/icon.png";
    private static final String FocusLostImagePath = "/main/focusLostImage.png";
    private static final String gameOverImagePath = "/main/gameOverImage.png";
    
    private volatile boolean running;
    
    private static JFrame frame;
    
    private static BufferedImage icon;
    
    
    
    private Thread thread;
    private InputHandler input;
    private Controller control;
    
    public static Color bgColor;
    public static Color textColor;
    
    public Terminal()
    {
        input = new InputHandler();
        
        bgColor = DEFAULT_BG_COLOR;
        textColor = DEFAULT_TEXT_COLOR;
        control = new Controller(input);
        
        //images
        try 
        {
            icon = resizeImage(ImageIO.read(Terminal.class.getResource(iconPath)), 256, 256);
            focusLostImage = resizeImage(ImageIO.read(Terminal.class.getResource(FocusLostImagePath)), Terminal.TWIDTH, Terminal.THEIGHT);
            gameOverImage = resizeImage(ImageIO.read(Terminal.class.getResource(gameOverImagePath)), Terminal.TWIDTH, Terminal.THEIGHT);
        } catch (IOException ex) 
        {
            System.err.println(ex.getMessage());
        }
        
        addMouseListener(input);
        addMouseMotionListener(input);
        addKeyListener(input);
        addFocusListener(input);
    }
    
    public synchronized void start()
    {
        if (running)
        {
            return;
        }
        
        running = true;
        thread = new Thread(this);
        thread.start();
    }
    
    public synchronized void stop()
    {
        if (!running)
        {
            return;
        }
        
        running = false;
        try 
        {
            thread.join();
        } catch (InterruptedException ex) 
        {
            System.err.println(ex.getMessage());
        }
        
    }
    
    private static BufferedImage resizeImage(BufferedImage image, int width, int height) 
    {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    @Override
    public void run() 
    {
        
        int frames = 0;
        
        double unprocessedSeconds = 0;
        long lastTime = System.nanoTime();
        double secondsPerTick = 1.0 / 60.0;
        int tickCount = 0;
        
        requestFocus();
        
        while (running)
        {
            long now = System.nanoTime();
            long passedTime = now - lastTime;
            lastTime = now;
            
            if (passedTime < 0)
            {
                passedTime = 0;
            }
            if (passedTime > 100000000)
            {
                passedTime = 100000000;
            }
            
            unprocessedSeconds += passedTime / 1000000000.0;
            
            boolean ticked = false;
            
            while (unprocessedSeconds > secondsPerTick)
            {
                tick();
                unprocessedSeconds -= secondsPerTick;
                ticked = true;
                
                tickCount++;
                if (tickCount % 60 == 0)
                {
                    System.out.println(frames + " fps");
                    lastTime += 1000;
                    frames = 0;
                }
            }
            
            if (ticked)
            {
                render();
                frames++;
            }
            else
            {
                try
                {
                    Thread.sleep(1);
                } catch (InterruptedException e)
                {
                    System.err.println(e.getMessage());
                }
            }
        }
        
    }
    
    public void tick()
    {
        control.tick();
    }

    public void render()
    {
        BufferStrategy bs = getBufferStrategy();
            
        if (bs == null)
        {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        
        //draw
        g.setColor(bgColor);
        g.fillRect(0, 0, TWIDTH, THEIGHT);
        g.setColor(textColor);
        control.draw(g);
        g.dispose();
        
        bs.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Terminal infil = new Terminal();
        
        frame = new JFrame(TITLE);
        frame.add(infil);
        
        
        frame.setResizable(false);
        frame.setSize(TWIDTH, THEIGHT);
        frame.setVisible(true);
        Insets insets = frame.getInsets();
        int insetWidth = insets.left + insets.right;
        int insetHeight = insets.bottom + insets.top;
        Dimension real = new Dimension(TWIDTH + insetWidth, THEIGHT + insetHeight);
        frame.setSize(real);
        frame.setPreferredSize(real);
        frame.setMinimumSize(real);
        frame.setMaximumSize(real);
        
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        
        infil.start();
        infil.requestFocus();
    }
}
