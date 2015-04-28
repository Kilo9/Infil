package infil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeremy Bassi
 */
public class DataReader 
{
    private BufferedReader br;
    
    public DataReader()
    {
        
    }
    
    public String[] read(String path)
    {
        initReader(path);
        
        ArrayList<String> data = new ArrayList<>();
        
        String line;
        
        try 
        {
            while ((line = br.readLine()) != null)
            {
                data.add(line);
            }
        } catch (IOException ex) 
        {
            System.err.println(ex.getMessage());
        }
        
        String[] arr = new String[data.size()];
        
        for (int i = 0; i < data.size(); i++)
        {
            arr[i] = data.get(i);
        }
        
        return arr;
    }
    
    private void initReader(String path)
    {
        try
        {
            InputStream is = DataReader.class.getResourceAsStream(path);
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr); 
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
}
