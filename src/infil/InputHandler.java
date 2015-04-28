package infil;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author Jeremy Bassi
 */
public class InputHandler implements MouseListener, MouseMotionListener, KeyListener, FocusListener
{
    
    public StringBuffer text;
    public boolean enter;
    public boolean backspace;
    public boolean shift;
    public boolean scrollUp;
    public boolean scrollDown;
    public boolean goToBottom;
    public boolean hasFocus;
    
    public InputHandler()
    {
        text = new StringBuffer();
    }

    @Override
    public void mouseClicked(MouseEvent e) 
    {

    }

    @Override
    public void mousePressed(MouseEvent e) 
    {

    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {

    }

    @Override
    public void mouseEntered(MouseEvent e) 
    {
        
    }

    @Override
    public void mouseExited(MouseEvent e) 
    {

    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {

    }

    @Override
    public void mouseMoved(MouseEvent e) 
    {

    }

    @Override
    public void keyTyped(KeyEvent e) 
    {
        int keyCode = e.getKeyCode();
        char c = e.getKeyChar();
        
        if (!Controller.busy && !Controller.bruteforce)
        {
            if (c == '\u0008') //must use unicode due to problem that e.getKeyCode does not recognize backspace
            {
                if (text.length() != 0)
                {
                    text.deleteCharAt(text.length() - 1);
                }
                else
                {
                    backspace = true;
                }
            }
            else if (c == '\n') //also not recognized as space
            {
                enter = true;
            }
            else
            {
                text.append(c);
            }
        }
        else
        {
            Sound.error.play();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_SHIFT)
        {
            shift = true;
        }
        
        if (keyCode == KeyEvent.VK_UP)
        {
            scrollUp = true;
            scrollDown = false;
        }
        else if (keyCode == KeyEvent.VK_DOWN)
        {
            scrollDown = true;
            scrollUp = false;
        }
        else if (!shift)
        {
            goToBottom = true;
        }
        
        if (scrollUp && scrollDown)
        {
            scrollUp = scrollDown = false;
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_SHIFT)
        {
            shift = false;
        }
        
        if (keyCode == KeyEvent.VK_UP)
        {
            scrollUp = false;
        }
        
        if (keyCode == KeyEvent.VK_DOWN)
        {
            scrollDown = false;
        }
    }

    @Override
    public void focusGained(FocusEvent e) 
    {
        hasFocus = true;
    }

    @Override
    public void focusLost(FocusEvent e) 
    {
        hasFocus = false;
    }

}
