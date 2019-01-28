package chineseenglishdictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class Main {
    private static final List<Entry> entries = new ArrayList<>();   //maybe need to arrayify this if the dictionary is too big?
                                                                    //probably not since the dictionary won't be more than 100,000 entries
        
    private static final String fileNameEnd = "dictionary.txt";
    private static final String projDir = System.getProperty("user.dir");
    private static final String fileName = projDir + "\\" + fileNameEnd;
    private static Entry mostRecentlyDeletedEntry = null;
    private static Entry mostRecentlyAddedEntry = null;
    private static int mostRecentlyAddedIndex = -1;
    private static final Charset utf8 = Charset.forName("UTF-8");
    private static final Scanner in = new Scanner(new InputStreamReader(System.in, utf8));
    
    private static final String middleLine = "#######################################\n";
    protected static final String defau1t = "S#skip\n" 
                                        + middleLine
                                        + "hello#nǐ hǎo#你很#\n"
                                        + "goodbye#zài jiàn#再见#XO\n"
                                        + "good#hǎo#好#\n" 
                                        + "no#bù#不#X\n";
    private static final String userPrompt = "> ";
    private static final String helpMessage = "COMMANDS:\n"
                                        + "(1,A) - add entries to the dictionary\n"
                                        + "(2,S) - print to screen\n"
                                        + "(3,D) - delete an entry from the dictionary\n"
                                        + "(4,P) - print the dictionary to paper\n"
                                        + "(5,H) - shows this message\n"
                                        + "(6,F) - finds an entry in the file\n"
                                        + "(7,C) - change an existing entry\n"
                                        + "(0,9,Q) - quits program";
    
    //flags are false if not present, true if present
    private static final Map<Character, String> flagHash = new HashMap<Character, String>();
    private static final Map<String, Character> reverseFlagHash = new HashMap<>();
    
    public static void main(String[] args) {
        //read data from file
        String[] fileData = Util.readFile(fileName);
        
        //look for split between flags and dictionary
        int split = 0;
        while (split < fileData.length && (fileData[split].isEmpty() || fileData[split].charAt(0) != '#'))
            split++;
        
        //read flags
        for (int i = 0; i < split; i++) {
            String[] flagLine = fileData[i].split("#");
            if (flagLine.length < 2) {
                System.out.println("badly formed flag line: " + fileData[i]);
                continue;
            }
            else 
                flagHash.put(Character.toUpperCase(flagLine[0].charAt(0)), flagLine[1].trim());
        }
        for (Character key : flagHash.keySet()) {
            String value = flagHash.get(key);
            reverseFlagHash.put(value, key);
        }
        
        //store data in entry form 
        try {
            for (int i = split+1; i < fileData.length; i++) {
                String[] comps = fileData[i].split("#");
                if (comps.length < 3) {
                    System.out.println("badly formed line --> [" + fileData[i] + "]");
                    continue;
                }
                Entry temp = new Entry(comps[0].trim(), comps[1].trim(), comps[2].trim());
                
                String flags;
                if (comps.length >= 4)  flags = comps[3].toUpperCase();
                else    flags = "";
                
                for (Character c : flagHash.keySet()) { //this will delete any flags that aren't in the flagHash
                    boolean b = flags.contains(""+c);
                    temp.flags.put(flagHash.get(c), b);
                }
                entries.add(temp);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        
        //print stuff
        System.out.println(fileName);
        //sort entries
        System.out.print("Sorting dictionary... ");
        long start = System.currentTimeMillis();
        Collections.sort(entries);
        long end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");
        //print flags
        System.out.println("Flags:");
        printFlags();
        System.out.println();
        System.out.println("Dictionary:");
        //print entries
        printEntries(entries);
        System.out.println();
        
        //testing before main loop
        //List<Entry> e = selectByFlags(false);
        //System.out.println(e);
        //System.exit(0);
        
        //main loop
        System.out.println("\n" + helpMessage);
        
        while (true) {
            //input character
            System.out.print("\n" + userPrompt);
            String inp = in.nextLine().trim();
            if (inp.isEmpty()) continue;
            char charInp = Character.toUpperCase(inp.charAt(0));

            //parse command
            if      (charInp == '1' || charInp == 'A')  addEntryStart();  //add entry
            else if (charInp == '2' || charInp == 'S')  printToScreen();    //print dictionary
            else if (charInp == '3' || charInp == 'D')  deleteEntry();      //delete an entry
            else if (charInp == '4' || charInp == 'P')  printStart();     //print to paper
            else if (charInp == '5' || charInp == 'H')  System.out.println(helpMessage);     //show help message
            else if (charInp == '6' || charInp == 'F')  findEntryStart();   //find 
            else if (charInp == '7' || charInp == 'C')  changeEntryStart();      //change
            else if (charInp == '0' || charInp == '9' || charInp == 'Q') {    //quit
                System.out.println("quitting..."); 
                break; 
            }
            else    System.out.println("'" + charInp + "' is an unrecognized command. Type '5' or 'H' to see commands");
        }
        
        //end
        saveEntriesToFile(fileName, true);
        System.out.println("Exiting.");
    }
    
    
    /**
     * Creates new print screen dialog
     */
    private static void printStart() {
        System.out.println("Opening print dialog...");
        PrintOptionsFrame pof = new PrintOptionsFrame(entries, flagHash);
        pof.showOnScreen();
    }
    
    
    private static void changeEntry(int index) {
        //actually change entry 
        System.out.println("Change " + entries.get(index).toStringCompact() + " by which field? (0 to exit)");
        System.out.println("(1) - English");
        System.out.println("(2) - pinyin");
        System.out.println("(3) - Chinese characters");
        System.out.println("(4) - flags");
        
        int res = getNumberFromUser(0,4, "Change: ");
        if (res == 0) return;
        
        Entry originalEntry = new Entry(entries.get(index));    //deep copy
        
        if (res != 4) {
            String oldField = entries.get(index).english;
            if      (res == 1)  oldField = entries.get(index).english;
            else if (res == 2)  oldField = entries.get(index).pinyin;
            else if (res == 3)  oldField = entries.get(index).characters;
            System.out.println("Original: " + oldField);
            System.out.print  ("New     : ");
            String newField = in.nextLine().trim();
            if      (res == 1)  entries.get(index).english = newField;
            else if (res == 2)  entries.get(index).pinyin = newField;
            else if (res == 3)  entries.get(index).characters = newField;   
        }
        else if (res == 4) {
            System.out.println("Type the character of each flag you would like to set to be true:");
            System.out.println(getFlagString());
            
            System.out.print("Old flags: ");
            for (char c : flagHash.keySet())
                if (originalEntry.flags.get(flagHash.get(c)) == true)
                    System.out.print(c);
            System.out.println();
            
            System.out.print  ("New flags: " + userPrompt);
            String inp = getFlagsFromUser();
            //clear flags
            entries.get(index).flags.clear();
            //add new flags
            for (char flag : inp.toCharArray())
                entries.get(index).flags.put(flagHash.get(flag), true);
        }
        else {
            System.out.println("BAD SELECTION NUMBER");
            return;
        }
        
        System.out.println("Changed " + originalEntry.toStringCompact() + " to " + entries.get(index).toStringCompact());
        saveEntriesToFile(fileName, true);
    }
    
    
    private static void changeEntryStart() {
        System.out.println("Select entry to change by? (0 to exit)");
        System.out.println("(1) - id (index)");
        System.out.println("(2) - english");
        System.out.println("(3) - pinyin");
        System.out.println("(4) - chinese characters");
        
        int choice = getNumberFromUser(0,4, "Change: ");
        if (choice == 0 || choice > 4) return;
        
        List<Integer> matchingIndices = new ArrayList<>();
        
        if (choice == 1) {  //index choose
            System.out.println("Search by index:");
            int inp = getNumberFromUser(0, entries.size() - 1, "Change: ");
            matchingIndices.add(inp);
        }
        else if (choice == 2) { //english
            System.out.println("Search by English:");
            System.out.print("Change: " + userPrompt);
            String inp = in.nextLine().trim().toLowerCase();
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).english.toLowerCase().contains(inp))
                    matchingIndices.add(i);
            }
        }
        else if (choice == 3) { //pinyin
            System.out.println("Search by pinyin:");
            System.out.print("Change: " + userPrompt);
            String inp = in.nextLine().trim().toLowerCase();
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).pinyin.toLowerCase().contains(inp))
                    matchingIndices.add(i);
            }
        }
        else if (choice == 4) { //chinese characters
            System.out.println("Search by Chinese characters:");
            System.out.print("Change: " + userPrompt);
            String inp = in.nextLine().trim().toLowerCase();
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).characters.toLowerCase().contains(inp))
                    matchingIndices.add(i);
            }
        }
        else {
            System.out.println("bad selection number");
            return;
        }
        
        //deal with matching indices
        if (matchingIndices.size() == 0)
            return;
        else if (matchingIndices.size() == 1) {
            changeEntry(matchingIndices.get(0));
        }
        else {
            System.out.println("Multiple entries match, select index of desired one: ");
            
            for (int ind : matchingIndices)
                System.out.println(Util.strWidth(""+ind, 2) + ": " + entries.get(ind));
            System.out.print("Change: " + userPrompt);
            String inp = in.nextLine().trim();
            try {
                int num = Integer.parseInt(inp);
                if (num < 0 || num >= entries.size()) {
                    System.out.println("Not a valid number, not changing anything.");
                    return;
                }
                for (int ind : matchingIndices) {
                    if (num == ind) {
                        changeEntry(ind);
                        return;
                    }
                }
            }
            catch (NumberFormatException ex) {}
            System.out.println("Not a valid number, not changing anything."); 
        }
    }
    
    
    /**
     * Gets and returns a 'Y' or 'N' from the user
     * @return 
     */
    private static char getYesNo() {
        char c = '\0';
        do {
            c = Character.toUpperCase(in.nextLine().trim().charAt(0));
            if (c != 'Y' && c != 'N')   System.out.println("only 'Y' and 'N' accepted");
        } while (c != 'Y' && c != 'N');
        return c;
    }
    
    
    /**
     * Deletes the entry that the user selects
     * Go over again
     * @param entries 
     */
    private static void deleteEntry() {
        System.out.println("DELETE BY? (0 to exit):");
        System.out.println("(1) - id (index)");
        System.out.println("(2) - ENGLISH");
        System.out.println("(3) - PINYIN");
        System.out.println("(4) - CHINESE CHARACTERS");
        System.out.println("(5) - FLAGS");
        System.out.println("(6) - MOST RECENTLY ADDED ENTRY");
        
        int n = getNumberFromUser(0,6, "DELETE: ");
        if (n == 0) return;
        else if (n == 1) {
            System.out.println("ENTER INDEX TO DELETE");
            int ind = getNumberFromUser(0, entries.size() - 1, "DELETE: ");
            System.out.println("ARE YOU SURE YOU WANT TO DELETE " + entries.get(ind).toStringCompact() + " ??? (Y/N)");
            char c = getYesNo();
            if (c == 'Y')   delete(ind);
            else
                System.out.println("Cancelled deletion");
        }
        else if (n >= 2 && n <= 4) {
            System.out.print("WHAT DO YOU WANT TO DELETE?\nDELETE: " + userPrompt);
            String inp = in.nextLine().trim();
            if (inp.isEmpty()) return;

            List<Integer> matchedIndices = new ArrayList<>();
            if (n == 1) {
                for (int i = 0; i < entries.size(); i++)
                    if (entries.get(i).english.equals(inp)) matchedIndices.add(i); 
            } 
            else if (n == 2) {
                for (int i = 0; i < entries.size(); i++)
                    if (entries.get(i).pinyin.equals(inp)) matchedIndices.add(i); 
            }
            else if (n == 3) {
                for (int i = 0; i < entries.size(); i++)
                    if (entries.get(i).characters.equals(inp)) matchedIndices.add(i); 
            }
            
            //deal with matches
            if (matchedIndices.size() == 1) {
                delete(matchedIndices.get(0));
            }
            else if (matchedIndices.size() > 0) {
                System.out.println("FOUND MULTIPLE MATCHES: SELECT THE INDEX TO DELETE OR ANYTHING ELSE TO QUIT");
                for (int ind : matchedIndices)
                    System.out.println(Util.strWidth(""+ind, 2) + ": " + entries.get(ind));
                System.out.print("DELETE: " + userPrompt);
                inp = in.nextLine().trim();
                try {
                    int num = Integer.parseInt(inp);
                    if (num < 0 || num >= entries.size()) {
                        System.out.println("Not a valid number, not deleting anything.");
                        return;
                    }
                    for (int ind : matchedIndices) {
                        if (num == ind) {
                            delete(num);
                            return;
                        }
                    }
                }
                catch (NumberFormatException ex) {}
                System.out.println("Not a valid number, not deleting anything.");
            }
            else 
                System.out.println("Could not match '" + inp + "' anywhere in the selected category");
        }
        else if (n == 5) {  //flags
            System.out.println("NOT IMPLEMENTED YET");
        }
        else if (n == 6) {  //most recently added
            delete(mostRecentlyAddedIndex);
        }
        else {
            System.out.println("BAD CHOICE OF CHOICE");
        }
    }
    
    
    /**
     * Should be the only method that deletes an entry from the dictionary
     * @param index 
     */
    public static void delete(int index) {
        if (index < 0 || index >= entries.size()) {
            System.out.println("ERROR TRYING TO DELETE OUT OF BOUNDS OF DICTIONARY");
            return;
        }
        mostRecentlyDeletedEntry = entries.remove(index);
        System.out.println("Deleted " + mostRecentlyDeletedEntry.toStringCompact() + " from the dictionary");
        
    }
    
    /**
     * Gets a single number from the user, low high inclusive
     * @param low
     * @param high
     * @param prefixMessage
     * @return 
     */
    private static int getNumberFromUser(int low, int high, String prefixMessage) {
        int choice = Integer.MIN_VALUE;
        do {
            System.out.print(prefixMessage + userPrompt);
            String line = in.nextLine().trim();
            try { choice = Integer.parseInt(line); }
            catch (NumberFormatException e) { choice = Integer.MIN_VALUE; }
            if (choice < low || choice > high)     System.out.println("can only input numbers " + low + " though " + high);
        } while (choice < low || choice > high);
        return choice;
    }
    
    
    private static List<Entry> selectByFlags(boolean allowMultipleSelection) {
        if (allowMultipleSelection) System.out.println("MULTIPLE ENTRY SELECTION IS NOT FULLY SUPPORTED");
        System.out.print("Select by flags by typing the character for each flag: ");
        if (allowMultipleSelection) System.out.println("(can select more than one)");
        else    System.out.println("(only allowed to select one)");
        
        List<Entry> selectedEntries = new ArrayList<>();
        boolean exitCondition = false;
        boolean selectOneStage = false;
        
        do {
            if (!selectOneStage) {
                System.out.println(getFlagString());
                System.out.print("Input flags: " + userPrompt);
                String res = getFlagsFromUser();
                for (char flag : res.toCharArray()) {
                    for (Entry e : entries)
                        if (e.flags.get(flagHash.get(flag)) == true)
                            selectedEntries.add(e);
                }
            }
            else {
                System.out.println("Choose the entry you want by number: ");
                printEntries(selectedEntries);
                int n = getNumberFromUser(0, selectedEntries.size() - 1, "");
                Entry userWants = selectedEntries.remove(n);
                selectedEntries.clear();
                selectedEntries.add(userWants);
            }
            
            
            //update exit condition
            if (allowMultipleSelection)
                exitCondition = true;       //allow user to further refine selected entries
            else
                exitCondition = selectedEntries.size() == 1;
            
            if (!allowMultipleSelection)
                selectOneStage = true;
            
        } while (exitCondition == false);
        
        return selectedEntries;
    }
    
    /**
     * Prints stuff from the dictionary according to how user wants
     * @param entries 
     */
    private static void printToScreen() {
        System.out.println("How would you like to print? (0 to exit):");
        System.out.println("(1) - entire dictionary");
        System.out.println("(2) - most recently added entry");
        System.out.println("(3) - most recently deleted entry");
        System.out.println("(4) - index range (inclusive)");
        System.out.println("(5) - first letter range English (inclusive)");
        System.out.println("(6) - first letter range pinyin (inclusive)");
        System.out.println("(7) - entries with certain flags");
        
        int choice = getNumberFromUser(0,7, "print: ");
        if (choice == 0)    return;
        
        if (choice == 1) {
            System.out.println("PRINTING WHOLE DICTIONARY (" + entries.size() + " entries)");
            printEntries(entries);
        }
        else if (choice == 2) {
            if (mostRecentlyAddedEntry == null) System.out.println("There is no recently added entry");
            else    System.out.println("Most recently added entry: " + mostRecentlyAddedEntry.toStringCompact());
        }
        else if (choice == 3) {
            if (mostRecentlyDeletedEntry == null) System.out.println("There is not recently deleted entry");
            else System.out.println("Most recently deleted entry: " + mostRecentlyDeletedEntry.toStringCompact());
        }
        else if (choice == 4) {     //index range
            int lowIndex  = getNumberFromUser(0, entries.size() - 1, "low index: ");
            int highIndex = getNumberFromUser(0, entries.size() - 1, "high index: ");
            if (lowIndex > highIndex) {
                int temp = lowIndex;
                lowIndex = highIndex;
                highIndex = temp;
            }
            for (int i = lowIndex; i <= highIndex && i <= entries.size(); i++)
                System.out.println(Util.strWidth(""+i, 2) + ": " + entries.get(i));
        }
        else if (choice == 5) {     //english first character range
            System.out.print("low character: " + userPrompt);
            char low = Character.toUpperCase(in.nextLine().trim().charAt(0));
            System.out.print("high character: " + userPrompt);
            char high = Character.toUpperCase(in.nextLine().trim().charAt(0));
            
            if (low > high) {
                char temp = low;
                low = high;
                high = temp;
            }
            
            for (int i = 0; i < entries.size(); i++) {
                int firstChar = Character.toUpperCase(entries.get(i).english.charAt(0));
                if (firstChar < low)     continue;
                if (firstChar > high)    break;
                System.out.println(Util.strWidth(""+i, 2) + ": " + entries.get(i));
            }
        }
        else if (choice == 6) {     //english first character range
            System.out.print("low character: " + userPrompt);
            char low = Character.toUpperCase(in.nextLine().trim().charAt(0));
            System.out.print("high character: " + userPrompt);
            char high = Character.toUpperCase(in.nextLine().trim().charAt(0));
            
            if (low > high) {
                char temp = low;
                low = high;
                high = temp;
            }
            
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).pinyin.charAt(0) < low)     continue;
                if (entries.get(i).pinyin.charAt(0) > high)    break;
                System.out.println(Util.strWidth(""+i, 2) + ": " + entries.get(i));
            }
        }
        else if (choice == 7) { //select by flags
            List<Entry> selected = Main.selectByFlags(true);
            Main.printEntries(selected);
        }
        else {
            System.out.println("bad choice number");
        }
    }
    
    
    /**
     * Does it in dumb O(n)
     * TODO: better insert algorithm
     * @param entries
     * @param toAdd 
     */
    private static int insertInOrder(Entry toAdd) {
        int index = 0;
        for (int i = 0; i < entries.size(); i++) {
            if (toAdd.compareTo(entries.get(i)) <= 0)
                break;
            index++;
        }
        entries.add(index, toAdd);
        return index;
    }
    
    
    private static void findEntryStart() {
        List<Integer> result = findEntry();
        if (result == null) {}
        else if (result.size() == 0)    System.out.println("could not find any matching entries");
        else {
            System.out.println("Found " + (result.size() > 1 ? "these " + result.size() + " entries:" : "this entry:"));
            for (int n : result)
                System.out.println(Util.strWidth(""+n, 2) + ": " + entries.get(n));
        }
    }
    
    /**
     * Accepts user input to search through the dictionary
     * Will return a list of matching indices, null if the user quits early, and a list of zero size if nothing matches
     * @param entries
     * @return 
     */
    private static List<Integer> findEntry() {
        //find by exact english, exact pinyin, exact chinese, or starting with english, pinyin, chinese
        System.out.println("How you would like to find? (0 to exit):");
        System.out.println("(1) - english: search term can come up anywhere");
        System.out.println("(2) - pinyin: ^");
        System.out.println("(3) - chinese characters: ^");
        System.out.println("(4) - exact english: must match verbatim");
        System.out.println("(5) - exact pinyin: ^");
        System.out.println("(6) - exact chinese characters: ^");
        System.out.println("\t(make sure to use the accent marks for pinyin)");
        
        int choice = -1;
        do {
            System.out.print("find " + userPrompt);
            String line = in.nextLine().trim();
            try { choice = Integer.parseInt(line); }
            catch (NumberFormatException e) { choice = -1; }
            if (choice == 0) return null;
            if (choice < 1 || choice > 6)     System.out.println("can only input numbers 1 though 6");
        } while (choice < 1 || choice > 6);
        
        List<Integer> finds = new ArrayList<>();
        System.out.print("Enter search " + userPrompt);
        String inp = in.nextLine().trim().toLowerCase();
        
        if (choice == 1) {   //match anywhere english
            for (int i = 0; i < entries.size(); i++)
                if (entries.get(i).english.toLowerCase().contains(inp))   finds.add(i);
        }
        else if (choice == 2) {  //match anywhere pinyin
            for (int i = 0; i < entries.size(); i++)
                if (entries.get(i).pinyin.toLowerCase().contains(inp))   finds.add(i);
        }
        else if (choice == 3) {  //match anywhere chinese characters
            for (int i = 0; i < entries.size(); i++)
                if (entries.get(i).characters.toLowerCase().contains(inp))   finds.add(i);
        }
        else if (choice == 4) {   //exact english
            for (int i = 0; i < entries.size(); i++)
                if (inp.equals(entries.get(i).english))     finds.add(i);
        }
        else if (choice == 5) {   //exact pinyin
            for (int i = 0; i < entries.size(); i++)
                if (inp.equals(entries.get(i).pinyin))     finds.add(i);
        }
        else if (choice == 6) {   //exact chinese characters
            for (int i = 0; i < entries.size(); i++)
                if (inp.equals(entries.get(i).characters))     finds.add(i);
        }
        else {
            System.out.println("something really bad happened in findEntry()\nnot really really bad but a bad choice number");
        }
        
        return finds;
    }
    
    
    private static String getFlagsFromUser() {
        String inp = in.nextLine().trim().toUpperCase();
        String flags = "";
        for (char c : inp.toCharArray()) {
            if (flagHash.containsKey(c))
                flags += c;
        }
        return flags;
    }
    
    private static String getFlagString() {
        String s = "";
        for (char c : flagHash.keySet()) {
            s += "[" + c + " = " + flagHash.get(c) + "]";
        }
        if (s.isEmpty())    return "(no flags)";
        return s;
    }
    
    
    /**
     * Should be the only method that adds an entry to the dictionary
     * @param e 
     */
    private static void add(Entry e) {
        int index = insertInOrder(e);       //add in order
        mostRecentlyAddedEntry = e;
        mostRecentlyAddedIndex = index;
        System.out.println("Added: " + e.toStringCompact() + "\tto the dictionary");
        saveEntriesToFile(fileName, true);
    }
    
    
    private static void addMostRecentEntryBack() {
        if (mostRecentlyDeletedEntry == null) {
            System.out.println("There is not recently deleted entry!");
        }
        else {
            System.out.println("Do you want to add " + mostRecentlyDeletedEntry.toStringCompact() + " to the dictionary (Y/N)?");
            char inp = getYesNo();
            if (inp == 'Y') {
                add(mostRecentlyDeletedEntry);
                mostRecentlyDeletedEntry = null;
            }
        }
    }
    
    
    /**
     * Guides the user adding a new entry
     */
    private static void addEntryStart() {
        System.out.println("Add entry by: (0 to exit)");
        System.out.println("(1) - English, pinyin, Chinese characters without flags");
        System.out.println("(2) - same as above with flags");
        System.out.println("(3) - add the last entry deleted back");
        int inp = getNumberFromUser(0,3, "Add: ");
        if (inp == 0)   return;
        if (inp == 3) {
            addMostRecentEntryBack();
            return;
        }
        
        Entry toAdd = inputEntry();
        if (toAdd == null) {
            System.out.println("You must fill out all the fields to add another entry.");
            return;
        }
        for (Entry e : entries) {
            if (toAdd.english.equals(e.english) && toAdd.pinyin.equals(e.pinyin) && toAdd.characters.equals(e.characters)) {
                System.out.println("Entry already present, not adding to dictionary");
                return;
            }
        }
        
        if (inp == 2)   {   //set flags
            //System.out.print("Would you like to set flags? (Y/N): ");
            //char c = getYesNo();
            //if (c == 'Y') {
            System.out.println("Type the character of each flag you would like to set to be true:");
            System.out.println(getFlagString());
            System.out.print("Input flags: " + userPrompt);
            String res = getFlagsFromUser();
            for (char flag : res.toCharArray())
                toAdd.flags.put(flagHash.get(flag), true);
            //}
        }
        
        add(toAdd);
    }
    
    
    /**
     * Gets the information for a new entry from the user
     * @return 
     */
    private static Entry inputEntry() {
        System.out.print("ADDING ENTRY:\nEnglish: ");
        String eng = in.nextLine().trim();
        System.out.print("Pin-yin: ");
        String pinyin = in.nextLine().trim();
        System.out.print("Chinese characters: ");
        String chars = in.nextLine().trim();

        if (eng.isEmpty() || pinyin.isEmpty() || chars.isEmpty()) 
            return null;
        else
            return new Entry(eng,pinyin,chars); 
    }
    
    
    /**
     * Saves the list of entries to a file
     * @param entries
     * @param fileName
     * @param print 
     */
    private static void saveEntriesToFile(String fileName, boolean print) {
        //convert flags back to file string form
        String[] flagsData = new String[flagHash.size()];
        int index = 0;
        for (char c : flagHash.keySet()) {
            flagsData[index] = c + "#" + flagHash.get(c);
            index++;
        }
        Arrays.sort(flagsData);     //alphabetize the flags
        
        //convert Entries back to file string form
        String[] fileOutputData = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++)
            fileOutputData[i] = entries.get(i).toFileEntryString();
        
        //write back header and things from above
        if (print)  System.out.print("Saving file... ");
        long start = System.currentTimeMillis();
        Util.writeFile(flagsData, middleLine, fileOutputData, fileName);
        long end = System.currentTimeMillis();
        if (print)  System.out.println("done (" + (end - start) + " ms)");
    }
    
    
    /**
     * Print the list of entries to standard out
     * @param entries 
     */
    private static void printEntries(List<Entry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            System.out.println(Util.strWidth(""+i, 2) + ": " + entries.get(i));
        }
    }
    
    
    private static void printFlags() {
        //print alphabetically
        Map<Character, String> tempFlags = new TreeMap<Character, String>(flagHash);
        //Collections.sort(flagsTemp);
        
        for (char c : tempFlags.keySet())
            System.out.println(c + " -> " + flagHash.get(c));
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Represents a single entry in the dictionary
     */
    public static class Entry 
        implements Comparable {
        
        String english;
        String pinyin;
        String characters;
        Map<String, Boolean> flags;
        

        public Entry(String english, String pinyin, String characters) {
            this.english = english;
            this.pinyin = pinyin;
            this.characters = characters;
            this.flags = new HashMap<>();
        }
        
        //for deep copy
        public Entry(Entry other) {
            this.english = other.english;
            this.pinyin = other.pinyin;
            this.characters = other.characters;
            this.flags = new HashMap<>();
            for (String key : other.flags.keySet())
                this.flags.put(key, other.flags.get(key));
        }

        
        public void addFlag(String key, boolean value) {
            this.flags.put(key, value);
        }
        
        @Override
        public String toString() {
            int lengthEng = (((english.length()     - 1) / 8) + 1) * 8;
            int lengthPin = (((pinyin.length()      - 1) / 8) + 1) * 8;
            int lengthCh  = (((characters.length()  - 1) / 4) + 1) * 4;
            String s = "[" + Util.strWidth(english, lengthEng) 
                    + " - " + Util.strWidth(pinyin, lengthPin)
                    + " - " + Util.strWidth(characters, lengthCh);
            
            int setFields = 0;
            for (String key : flags.keySet())
                if (flags.get(key) == true)
                    setFields++;
            
            if (setFields > 0)
                s += " || ";
            
            int n = 0;
            for (String key : flags.keySet()) {
                if (flags.get(key) == true) {
                    s += key;
                    if (n < setFields)
                        s += " ";
                }
            }
            
            return s.trim() + "]";
        }
        
        public String toStringCompact() {
            String s = "[" + english + " - " + pinyin + " - " + characters;
            
            int setFields = 0;
            for (String key : flags.keySet())
                if (flags.get(key) == true)
                    setFields++;
            
            if (setFields > 0)
                s += " || ";
            
            int n = 0;
            for (String key : flags.keySet()) {
                if (flags.get(key) == true) {
                    s += key;
                    if (n < setFields)
                        s += " ";
                }
            }
            
            return s.trim() + "]";
        }
        
        public String toFileEntryString() {
            String s = this.english + "#" + this.pinyin + "#" + this.characters;
            boolean atLeastOneTrueFlag = false;
            for (String flag : this.flags.keySet()) {
                if (flags.get(flag) == true) {
                    atLeastOneTrueFlag = true;
                    break;
                }
            }
            if (atLeastOneTrueFlag) s += "#";
            
            for (String flag : this.flags.keySet())
                if (flags.get(flag) == true)
                    s += reverseFlagHash.get(flag);
            return s;
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof Entry) {
                Entry other = (Entry) o;
                int res = this.english.compareToIgnoreCase(other.english);
                if (res != 0)   return res;
                res = this.pinyin.compareToIgnoreCase(other.pinyin);
                if (res != 0)   return res;
                res = this.characters.compareToIgnoreCase(other.characters);
                return res;
            }
            return -1;
        }
        
    }
}
//TO DO:
//delete function delete (select to delete) by flags
//add fuction to add/delete flags entirely from dictionary
//new method : select entry -- can use for change function, delete function, find function


//printing frame:
    //dont exit program if user hits close button X
    //do something on print button press or close
    //see if printing works

//convert flag character to string for longer flags

//select by flag method functionality:
    //remove entries with these flags
    //add entries with these flags
    //remove entry by id
    //add all items
    //remove all items

//more general select by method?
    //select by flags included
    //select by first letter
    //...


//LATER ON
//instead of taking input from stdin, take it directly from keyboard, so don't have to hit enter each time
//uniform way to tell which level the user is in, like in console  (base level = '>') (first level = '>levelName>') maybe
//uniform way to input menu item numbers or characters, always a way to go back up the menu
//stay in command until uer exits (right now it just exits after you do one thing)
//alternate dictionary for sentances, one column per page
//multi page printing