package infil;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Jeremy Bassi
 */
public class Controller 
{
    public static final int SCROLL_WAIT = 5; 
    public static final int SHIFT_SCROLL_WAIT = 1;
    public static final int GAME_TIME = 5 * 60; //minutes
    public static final int CLASSIC = 1;
    public static final int FREE = 2;
    
    //Randomizer values
    public static final int MIN_SCAN_TIME = 3;
    public static final int MAX_SCAN_TIME = 6;
    public static final int MIN_PROBE_TIME = 3;
    public static final int MAX_PROBE_TIME = 6;
    public static final int MIN_INTERCEPT_TIME = 3;
    public static final int MAX_INTERCEPT_TIME = 6;
    public static final int MIN_NUM_TARGETS = 4;
    public static final int MAX_NUM_TARGETS = 8;
    public static final double TARGET_ADD_PERCENT = .5;
    
    
    private static final String scan = "/scan";
    private static final String brute = "/bruteforce";
    private static final String cls = "/cls";
    private static final String notes = "/notes";
    private static final String clearNotes = "/clrnotes";
    private static final String deleteNote = "/delnote";
    private static final String rem = "#";
    private static final String exit = "/exit";
    private static final String colorChange = "/colorchange";
    private static final String help = "/help";
    private static final String callsign = ">> ";
    private static final String target = "/target";
    private static final String status = "/status";
    private static final String time = "/time";
    private static final String trace = "/trace";
    private static final String classic = "/classic";
    private static final String free = "/free";
    private static final String extract = "/extract";
    private static final String upgrade = "/upgrade";
    private static final String probe = "/probe";
    private static final String intercept = "/intercept";
    private static final String beep = "/beep";
    private static final String music = "/music";
    private static final String musicStop = "/musicstop";
    private static final String bobby = "/posi";
    private static final String about = "/about";
    private static final String godCommand = "showmethemoney!";
    
    private static final String helpPath = "/main/help.txt";
    private static final String openPath = "/main/open.txt";
    private static final String aboutPath = "/main/about.txt";
    
    private Font font;
    private FontMetrics metrics;
    
    private TextList list;
    private InputHandler input;
    private Notekeeper notekeeper;
    private ArrayList<Target> targs;
    private Target selected;
    private Timer timer;
    private CountDownTimer cdTimer;
    private DataReader dr;
    private ArrayList<String> postMessage;
    private Player player;

    public static boolean busy;
    public static boolean bruteforce;
    
    private boolean started;
    private boolean gameOver;
    private boolean instant;
    private boolean godMode;
    
    private int scrollTimer;
    private int bruteTimer;
    private int waitTimer;
    private int bruteWait;
    private long startTime;
    
    public static int mode;
    
    /**
     * Creates a controller for the game
     * @param input - the input handler to sort input from
     */
    public Controller(InputHandler input)
    {
        this.input = input;
        notekeeper = new Notekeeper();
        
        targs = new ArrayList<>();
        
        int num = random(MIN_NUM_TARGETS, MAX_NUM_TARGETS);
        
        for (int i = 0; i < num; i++)
        {
            targs.add(new Target());
        }
        
        font = new Font("Monospace", Font.PLAIN, Terminal.LINE_HEIGHT);
        busy = false;
        timer = new Timer();
        cdTimer = new CountDownTimer(GAME_TIME);
        list = new TextList(Terminal.NUM_LINES - 1, metrics, timer, cdTimer);
        dr = new DataReader();
        list.add(dr.read(openPath));
        list.space();
        
        player = new Player(cdTimer);
        
        scrollTimer = 0;
        bruteTimer = 0;
        startTime = -1;
        bruteWait = player.getBruteWait();
        busy = false;
        started = false;
        gameOver = false;
        bruteforce = false;
        postMessage = new ArrayList<>();
        godMode = false;
    }
    
    /**
     * decides what should be done by checking key input or program status
     *  ***happens every 1/60 of a second
     */
    public void tick()
    {  
        bruteWait = player.getBruteWait();
        
        if (mode == CLASSIC && cdTimer.timeUp())
        {
            gameOver = true;
            return;
        }
        
        if (selected == null || !selected.isBruteForce())
        {
            bruteforce = false;
        }
        
        
        scrollTimer++;
        
        if (input.goToBottom)
        {
            list.goToBottom();
            input.goToBottom = false;
        }
        
        if (busy)
        {
            if (startTime == -1)
            {
                startTime = System.nanoTime();
            }
            
            if (instant)
            {
                waitTimer = 0;
            }

            if (waitTimer == 0)
            {
                Sound.finish.play();
                long time = System.nanoTime() - startTime;
                startTime = -1;
                
                busy = false;
                
                if (postMessage != null && !postMessage.isEmpty())
                {
                    boolean first = true;
                    //Purge the postmessage array
                    while (!postMessage.isEmpty())
                    {
                        if (first)
                        {
                            list.timeAdd(postMessage.remove(0));
                            first = false;
                            continue;
                        }
                        
                        list.indentAdd(postMessage.remove(0));
                    }
                    
                    list.space();
                    list.indentAdd("time taken : " + time / 1000000000);
                    list.space();
                }
                
                return;
            }
            
            waitTimer--;
        }
        else if (selected != null && selected.isBruteForce()) //Handles the bruteforce code attempting
        {
            //TODO handle input when bruteforcing
            if (bruteTimer == bruteWait)
            {
                bruteTimer = 0;
                Sound.brute.play();
                list.timeAdd(selected.bruteForce());
            }
            else
            {
                bruteTimer++;
            }
        }
        else
        {
            if (input.enter)
            {
                Sound.enter.play();
                execute(input.text.toString());
                
                input.text = new StringBuffer();
                input.enter = false;
            }
            else if (input.backspace)
            {
                list.delete();
                input.backspace = false;
            }
            else if (input.shift)
            {
                if (input.scrollUp && scrollTimer >= SHIFT_SCROLL_WAIT)
                {
                    list.scrollUp();
                    scrollTimer = 0;
                }
                else if (input.scrollDown && scrollTimer >= SHIFT_SCROLL_WAIT)
                {
                    list.scrollDown();
                    scrollTimer = 0;
                }
            }
            else if (input.scrollUp && scrollTimer >= SCROLL_WAIT)
            {
                list.scrollUp();
                scrollTimer = 0;
            }
            else if (input.scrollDown && scrollTimer >= SCROLL_WAIT)
            {
                list.scrollDown();
                scrollTimer = 0;
            }
            
        }
        
    }
    
    /**
     * Draws the list of text to the Graphics context
     *  ***used by Terminal class Canvas
     * @param g - Graphics Context
     */
    public void draw(Graphics g)
    {
        if (gameOver)
        {
            Font f = new Font("Monospace", Font.BOLD, 60);
            metrics = g.getFontMetrics(f);
            g.setFont(f);
            g.setColor(Color.white);
            g.drawImage(Terminal.gameOverImage, 0, 0, null);
            String s = "Credits : " + player.getCredits();
            g.drawString(s, (Terminal.TWIDTH - metrics.stringWidth(s)) / 2, (Terminal.THEIGHT / 2) + metrics.getHeight());
            return;
        }
        
        if (input.hasFocus)
        {
            metrics = g.getFontMetrics(font);
            g.setFont(font);
            for (int i = 0; i < list.visible.size(); i++)
            {
                g.drawString(list.visible.get(i), Terminal.EXCESS, (i + 1) * Terminal.LINE_HEIGHT);
            }

            if (list.atBottom)
            {
                if (busy || bruteforce)
                {
                    return;
                }
                g.drawString(callsign + input.text.toString(), Terminal.EXCESS, list.current); 
            }

            if (!list.atTop)
            {
                g.drawString("^", Terminal.TWIDTH - metrics.stringWidth("^") - Terminal.EXCESS, Terminal.LINE_HEIGHT + Terminal.EXCESS);
            }
            
            if (mode == CLASSIC && cdTimer.getSecondsRemaining() < 60)
            {
                g.drawString(cdTimer.timeStamp(), Terminal.TWIDTH - metrics.stringWidth(cdTimer.timeStamp()) - Terminal.EXCESS, Terminal.THEIGHT - metrics.getHeight());
            }
        }
        else
        {
            g.drawImage(Terminal.focusLostImage, 0, 0, null);
            Font f = new Font("Monospace", Font.BOLD, 60);
            g.setFont(f);
            metrics = g.getFontMetrics(f);
            
            
            if (mode == FREE)
            {
                g.setColor(Color.WHITE);
                g.drawString(timer.timeStamp(), (Terminal.TWIDTH - metrics.stringWidth(timer.timeStamp())) / 2, (Terminal.THEIGHT - metrics.getHeight()) / 2);
            }
            else if (mode == CLASSIC)
            {
                g.setColor(Color.red);
                g.drawString(cdTimer.timeStamp(), (Terminal.TWIDTH - metrics.stringWidth(timer.timeStamp())) / 2, (Terminal.THEIGHT - metrics.getHeight()) / 2);
            }
            
        }
        
    }
    
    /**
     * Takes the input and executes the corresponding function(s)
     * @param s - the input
     */
    private void execute(String s)
    {
        
        if (!started)
        {
            if (s.trim().equalsIgnoreCase(free))
            {
                started = true;
                mode = FREE;
                list.clear();
                timer.start();
                list.add("Welcome to Free Play Mode...");
            }
            else if (s.trim().equalsIgnoreCase(classic))
            {
                started = true;
                mode = CLASSIC;
                list.clear();
                cdTimer.start();
                list.add("Welcome to Classic Mode...");
            }
            else
            {
                list.add("Please begin a new Game!");
            }
            
            return;
        }
        
        
        //if the line has no text, simply ignore enter press
        if (processCommand(s)[0].compareTo("") == 0)
        {
            return;
        }
        
        //if the line is a comment
            //note: done separately from switch statement
            //due to the fact that the comment may begin 
            //attached to the #
        if (s.startsWith(rem))
        {
            if (mode == CLASSIC)
            {
                notekeeper.write(cdTimer.timeStamp() + "  " + s.substring(1));
            }
            else if (mode == FREE)
            {
                notekeeper.write(timer.timeStamp() + "  " + s.substring(1));
            }
            
            list.timeAdd("note added");
            list.space();
            return;
        }
        
        //Main input handling of text entered
        switch (processCommand(s)[0])
        {
            case status:
            {
                list.timeAdd("Status Report:");
                list.add("");
                list.add(player.status());
                list.space();
            }
                break;
            case scan:
            {
                ArrayList<String> arr = new ArrayList<>();
                arr.add("Scan");
                arr.add("");
                
                for (int i = 0; i < targs.size(); i++)
                {
                    arr.add(i + ".  " + targs.get(i).scan());
                }
                
                String[] str = arr.toArray(new String[arr.size()]);
                
                wait("scanning...", randomTime(MIN_SCAN_TIME, MAX_SCAN_TIME), str);        
            }
                break;
            case intercept:
            {
                if (selected == null)
                {
                    list.timeAdd("intercept failed : no target selected");
                    list.space();
                    return;
                }

                wait("Running Intercept:", randomTime(MIN_INTERCEPT_TIME * (selected.getInterceptCount() + 1), MAX_INTERCEPT_TIME * (selected.getInterceptCount() + 1)),  "Intercept successful : " + selected.intercept());
            }
                break;
            case extract:
            {
                if (processCommand(s).length < 2)
                {
                    list.timeAdd("Please Enter Code as Argument");
                    list.space();
                    return;
                }
                
                if (selected == null)
                {
                    list.timeAdd("No Target Selected");
                    list.space();
                    return;
                }
                
                int i;
                try
                {
                    i = Integer.parseInt(processCommand(s)[1]);
                }
                catch (NumberFormatException e)
                {
                    list.timeAdd("Invalid Argument");
                    list.space();
                    return;
                }
                
                if (i == selected.getCode())
                {
                    player.addCredits(selected.getCredits());
                    player.addXP(selected.getXp());
                    list.add("Extracted : " + selected.getCredits() + " credits");
                    list.add("Gained : " + selected.getXp() + " XP");
                    list.space();
                    targs.remove(selected);
                    
                    //add a new target if there are none
                    //makes sure there is always a target
                    if (targs.isEmpty())
                    {
                        targs.add(new Target());
                    }
                    
                    //adds a target if it is meant to be!
                    if (TARGET_ADD_PERCENT >= Math.random())
                    {
                        targs.add(new Target());
                    }
                    
                    selected = null;
                }
            }
                break;
            case upgrade:
            {
                if (processCommand(s).length == 1)
                {
                    list.timeAdd("Player Upgrades Menu");
                    list.indentAdd(Upgrade.menu());
                    list.space();
                    return;
                }
                
                if (processCommand(s).length != 2)
                {
                    list.timeAdd("Invalid Argument(s)");
                    list.space();
                    return;
                }
                
                int a = 0;
                
                try
                {
                    a = Integer.parseInt(processCommand(s)[1]);
                }
                catch (NumberFormatException e)
                {
                    list.timeAdd("Invalid Argument");
                    list.space();
                    return;
                }

                if (Upgrade.isUpgrade(a))
                {
                    Upgrade u = new Upgrade(a);
                    if (player.charge(u.getCost()))
                    {
                        player.applyUpGrade(u);
                        list.timeAdd(u.getMessage());
                        list.space();
                        if (u.getType() == Upgrade.INSTANT)
                        {
                            instant = true;
                        }
                    }
                    else
                    {
                        list.timeAdd("Not Enough XP");
                        list.space();
                    }
                }
            } 
                break;
            case trace:
            {
                if (selected == null)
                {
                    list.timeAdd("trace failed : no target selected");
                    list.space();
                    return;
                }
                
                wait("Tracing IP: ", 5 * 60, "IP address is: " + selected.getIp());
            }
                break;
            case probe:
            {
                if (selected == null)
                {
                    list.timeAdd("probe failed : no target selected");
                    list.space();
                    return;
                }
                
                wait("Probing: ", randomTime(MIN_PROBE_TIME, MAX_PROBE_TIME), "Code contains: " + String.valueOf(selected.getCode()).length() + " digits");
            }
                break;
            case brute:
            {
                if (selected == null)
                {
                    list.timeAdd("No Target Selected");
                    list.space();
                    return;
                }
                
                if (processCommand(s).length < 2)
                {
                    list.add("Please specify IP Address");
                    return;
                }
                
                
                if (processCommand(s)[1].equalsIgnoreCase(selected.getIp()))
                {
                    selected.activateBrute();
                    bruteforce = true;
                    
                    if (processCommand(s).length == 3)
                    {
                        int arg = 0;
                        
                        try
                        {
                            arg = Integer.parseInt(processCommand(s)[2]);
                        } catch (NumberFormatException e)
                        {
                            list.timeAdd("Invalid Argument");
                            return;
                        }
                        
                        selected.setBrute(arg);
                        
                    }
                    else if (processCommand(s).length == 4)
                    {
                        int arg0 = 0;
                        int arg1 = 0;
                        
                        try
                        {
                            arg0 = Integer.parseInt(processCommand(s)[2]);
                            arg1 = Integer.parseInt(processCommand(s)[3]);
                        } catch (NumberFormatException e)
                        {
                            list.timeAdd("Invalid Argument(s)");
                            return;
                        }
                        
                        selected.setBrute(arg0, arg1);
                    }
                    else
                    {
                        selected.setBrute(0);
                    }
                }
                else
                {
                    list.timeAdd("Incorrect IP Address");
                    list.space();
                }
            }
                break;
            case deleteNote:
            {
                notekeeper.deleteLine();
                list.timeAdd("Note deleted");
                list.space();
            }
                break;
            case clearNotes:
            {
                notekeeper.clear();
                list.timeAdd("Notes cleared");
                list.space();
            }
                break;
            case time:
            {
                if (mode == FREE)
                {
                    list.add("time is: " + timer.timeStamp());
                    list.space();
                }
                else if (mode == CLASSIC)
                {
                    list.add("time is: " + cdTimer.timeStamp());
                    list.space();
                }
                
            }
                break;
            case beep:
            {
                Sound.beep.play();
            }
                break;
            case target:
            {
                int i = -1;
                
                if (processCommand(s).length < 2)
                {
                    list.add("Please Enter Target Number");
                    list.space();
                    return;
                }
                
                try
                {
                    i = Integer.parseInt(processCommand(s)[1]);
                } catch (NumberFormatException e)
                {
                    list.add("Invalid Argument");
                    list.space();
                    return;
                }
                
                if (i >= targs.size() || i < 0 || processCommand(s).length > 2)
                {
                    list.add("Invalid Argument");
                    list.space();
                    return;
                }
                
                if (targs.get(i).isScanned())
                {
                    selected = targs.get(i);
                    list.timeAdd("Target Selected: " + selected.getName());
                    list.space();
                    return;
                }
                
                list.timeAdd("Target not yet Scanned");
                list.space(); 
            }
                break;
            case notes:
            {
                list.indentAdd("notes:");
                list.indentAdd(notekeeper.read());
                list.space();
            }
                break;
            case bobby:
            {
                list.indentAdd("Bobby says Hello there!!! :)");
                list.indentAdd("Apple is awesome! "); //MACTURBATION4LIFE
                list.space();
            }
                break;
            case help:
            {
                list.timeAdd(Terminal.TITLE + " help");
                list.indentAdd(dr.read(helpPath));
            }
                break;
            case cls:
            {
                list.clear();
            }
                break;
                /*
            case music:
            {
                Sound.loneStar.loop();
                Sound.loneStar.play();
                list.timeAdd("Music Started:");
                list.indentAdd("Lone Star");
                list.indentAdd("Jim Guthrie");
                list.indentAdd("The Ballad of the Space Babies");
                list.indentAdd("Sword & Sworcery LP");
                list.space();
            }
                break;
            case musicStop:
            {
                Sound.loneStar.stop();
                list.timeAdd("Music Stopped");
                list.space();
            }
                break;
                */
            case exit:
            {
                System.exit(0);
            }
                break;
            case colorChange:
            {
                if (processCommand(s).length == 3) 
                {
                    changeColor(processCommand(s)[1], processCommand(s)[2]);
                    list.timeAdd("Color changed");
                    list.space();
                }
                else
                {
                    list.add("Error: actual and formal argument lists differ in length");
                    list.space();
                }
            }
                break;
            case about:
            {
                list.add(Terminal.TITLE);
                list.space();
                list.add(dr.read(aboutPath));
                list.space();
            }
                break;
            case godCommand:
            {
                godMode = true;
                player.godMode();
                instant = true;
                list.timeAdd("Here it is, Boss!");
            }
                break;
            default:
            {
                list.add("Invalid command");
                list.space();
            }
                break;
        }
            
    }
    
    private void wait(String s, int time, String f)
    {
        busy = true;
        waitTimer = time;
        list.timeAdd(s);
        postMessage.add(f);
    }
    
    private void wait(String s, int time, String[] f)
    {
        busy = true;
        waitTimer = time;
        list.timeAdd(s);
        
        for (String str : f)
        {
            postMessage.add(str);
        }
    }
    
    /**
     * changes the color of the screen
     * @param bg - bg color
     * @param text - text color
     */
    private void changeColor(String bg, String text)
    {
        try
        {
            Terminal.bgColor = new Color(Integer.parseInt(bg, 16));
            Terminal.textColor = new Color(Integer.parseInt(text, 16));
        } catch (NumberFormatException e)
        {
            list.add("Invalid Argument(s)");
        }
    }
    
    /**
     * splits the command up into command and arguments
     * @param s - the input
     * @return separated command & argument(s)
     */
    private String[] processCommand(String s)
    {
        return s.split("\\s");
    }
    
    /**
     * Prevents text from going off the screen by wrapping it
     * @param s
     * @param lim
     * @return 
     */
    private String[] wrap(String s, int lim)
    {
        //TODO finish
        String[] str = new String[2];
        String sub = "";
                
        if (metrics.stringWidth(s) > lim)
        {
            int i = s.length() - 1;
            sub = s.substring(i);
            
            while (metrics.stringWidth(sub) > lim)
            {
                i--;
                sub = s.substring(i);
            }
        }
        
        str[0] = s;
        str[1] = sub;
        
        return str;
    }
    
    /**
     * creates a line the width of the screen
     * @return the line (String)
     */
    private String line()
    {
        String s = "_";
        String c = "_";
        
        while (metrics.stringWidth(s) < (Terminal.TWIDTH - Terminal.EXCESS * 2))
        {
            s += c;
        }
        
        return s;
    }
    
    private int random(int min, int max)
    {
        return (((int) (Math.random() * (++max - min))) + min);
    }
    
    private int randomTime(int min, int max)
    {
        return random(min, max) * 60;
    }
}
