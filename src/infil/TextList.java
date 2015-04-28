package infil;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Jeremy Bassi
 */
public class TextList 
{
    private static final String indent = "     ";
    
    public Stack<String> up;
    public Stack<String> down;
    public ArrayList<String> visible;
    
    private int visibleLines;
    public int current;
    
    private FontMetrics metrics;
    private Timer timer;
    private CountDownTimer cdTimer;
    
    public boolean atBottom;
    public boolean atTop;
    
    public TextList(int v, FontMetrics fm, Timer t, CountDownTimer cdT)
    {
        visibleLines = v;
        
        up = new Stack<>();
        down = new Stack<>();
        visible = new ArrayList<>();
        
        current = Terminal.LINE_HEIGHT;
        timer = t;
        cdTimer = cdT;
        atBottom = true;
        atTop = true;
    }
    
    public void delete()
    {
        if (visible.size() < 1) //if visible is empty
        {
            if (up.size() < 1) //if there is nothing above
            {
                atTop = true;
                return;
            }
            
            up.pop();
            scrollDown();
            
            if (up.empty())
            {
                atTop = true;
            }
            
            return;
        }
        
        visible.remove(visible.size() - 1);
        
        current -= Terminal.LINE_HEIGHT;
    }
    
    public void clear()
    {
        up = new Stack<>();
        down = new Stack<>();
        visible = new ArrayList<>();
        current = Terminal.LINE_HEIGHT;
        atBottom = true;
        atTop = true;
    }
    
    public void add(String s)
    {
        if (visible.size() >= Terminal.NUM_LINES - 2) //if theres no more room on visible
        {
            up.push(visible.remove(0)); //shift the list up
            atTop = false;
        }
        else
        {
            current += Terminal.LINE_HEIGHT; //advance the current spot
        }
        
        visible.add(s);    
    }
    
    public void timeAdd(String s)
    {
        if (Controller.mode == Controller.CLASSIC)
        {
            add(cdTimer.timeStamp() + " " + s);
        }
        else if (Controller.mode == Controller.FREE)
        {
            add(timer.timeStamp() + " " + s);
        }
    }
    
    public void timeAdd(String[] arr)
    {
        for (String s : arr)
        {
            timeAdd(s);
        }
    }
    
    public void indentAdd(String s)
    {
        add(indent + s);
    }
    
    public void indentAdd(String[] arr)
    {
        for (String s : arr)
        {
            indentAdd(s);
        }
    }
    
    public void space()
    {
        add(" ");
    }
    
    public void scrollUp()
    {    
        if (up.empty()) //if there is nothing above
        {
            atTop = true;
            return;
        }
        
        if (visible.size() < Terminal.NUM_LINES - 2) //if visible is not full, like when deleted
        {
            atBottom = true;
            visible.add(0, up.pop());
            current += Terminal.LINE_HEIGHT;
            if (up.empty())
            {
                atTop = true;
            }
            return;
        }
         
        if (visible.size() == Terminal.NUM_LINES - 1) //if visible is full
        {
            down.push(visible.remove(visible.size() - 1)); //scroll up
        }
        
        atBottom = false;
        
        visible.add(0, up.pop());
        
        if (up.empty())
        {
            atTop = true;
        }
        
    }
    
    public void scrollDown()
    {      
        if (down.empty() && !atBottom) //if there is nothing below and it is not at the bottom
        {
            atBottom = true;
            visibleLines = Terminal.NUM_LINES - 2;
            up.push(visible.remove(0));
            atTop = false;
            return;
        }
        
        if (down.empty())
        {
            atBottom = true;
            return;
        }
        
        atTop = false;
                
        up.push(visible.remove(0));

        visible.add(down.pop());
    }
    
    public void goToBottom()
    {
        while (atBottom == false)
        {
            scrollDown();
        }
    }
    
    public void add(String[] arr)
    {
        for (String s : arr)
        {
            add(s);
        }
    }
}
