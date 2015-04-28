package infil;

import java.util.ArrayList;

/**
 * Class for taking and reading notes from the user
 * @author Jeremy Bassi
 */
public class Notekeeper 
{
    private ArrayList<String> notes;
    
    public Notekeeper()
    {
        notes = new ArrayList<>();
    }
    
    public void write(String s)
    {
        notes.add(s);
    }
    
    public void deleteLine()
    {
        notes.remove(notes.size() - 1);
    }
    
    public void clear()
    {
        notes.clear();
    }
    
    public String[] read()
    {
        String[] arr = new String[notes.size()];
        
        for (int i = 0; i < notes.size(); i++)
        {
            arr[i] = notes.get(i);
        }
        
        return arr;
    }
   
    private void print()
    {
        String[] read = read();
        
        for (String s : read)
        {
            System.out.println(s);
        }
    }
}
