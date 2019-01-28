/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseenglishdictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class of utility functions
 */
public class Util {
    
    /**
     * Read a file from url
     * @param url
     * @return 
     */
    protected static String[] readFile(String url) 
    {
        Scanner reader;
        List<String> lines = new ArrayList<>();

        try {
            reader = new Scanner(new File(url));
            while (reader.hasNextLine())
                lines.add(reader.nextLine());
        } 
        catch (FileNotFoundException e) { 
            writeFile(Main.defau1t, url);
            return readFile(url);
        }
        
        return lines.toArray(new String[lines.size()]);
    }
    
    
    /**
     * Write a string to a file from url
     * @param data
     * @param url
     * @return 
     */
    protected static boolean writeFile(String data, String url) 
    {
        
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(url), StandardCharsets.UTF_8);
            writer.write(data);
            writer.close();
            
        } catch (FileNotFoundException ex) { 
            ex.printStackTrace(); 
            return false;
        } catch (IOException ex) {
            ex.printStackTrace(); 
            return false;
        }
        return true;
    }
    
    
    /**
     * Write a preamble, header and data to a file from url
     * Data will be written on separate lines
     * @param data
     * @param url
     * @return 
     */
    protected static boolean writeFile(String[] preamble, String header, String data[], String url) 
    {
        
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(url), StandardCharsets.UTF_8);
            for (String line : preamble) {
                writer.write(line);
                writer.write("\n");
            }
            writer.write(header);
            for (String line : data) {
                writer.write(line);
                writer.write("\n");
            }
            writer.close();
            
        } catch (FileNotFoundException ex) { 
            ex.printStackTrace(); 
            return false;
        } catch (IOException ex) {
            ex.printStackTrace(); 
            return false;
        }
        return true;
    }
    
    
    /**
     * Write a header and data to a file from url
     * Data will be written on separate lines
     * @param data
     * @param url
     * @return 
     */
    protected static boolean writeFile(String header, String data[], String url) 
    {
        
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(url), StandardCharsets.UTF_8);
            writer.write(header);
            for (String line : data) {
                writer.write(line);
                writer.write("\n");
            }
            writer.close();
            
        } catch (FileNotFoundException ex) { 
            ex.printStackTrace(); 
            return false;
        } catch (IOException ex) {
            ex.printStackTrace(); 
            return false;
        }
        return true;
    }
    
    
    /**
     * Formats a string to be at minimum length characters long
     * @param string input string
     * @param length length of characters desired
     * @return a string to be at minimum length characters long
     */
    protected static String strWidth(String string, int length) 
    {
        return String.format("%-" + length + "s", string);
    }
    
}
