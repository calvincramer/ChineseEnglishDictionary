/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseenglishdictionary;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author CalvinLaptop
 */
/**
* Represents a single entry in the dictionary
*/
public  class Entry 
    implements Comparable {

    String english;
    String pinyin;
    String characters;
    Map<String, Boolean> flags;


    public Entry(String english, String pinyin, String characters) 
    {
        this.english = english;
        this.pinyin = pinyin;
        this.characters = characters;
        this.flags = new HashMap<>();
    }

    //for deep copy
    public Entry(Entry other) 
    {
        this.english = other.english;
        this.pinyin = other.pinyin;
        this.characters = other.characters;
        this.flags = new HashMap<>();
        for (String key : other.flags.keySet())
            this.flags.put(key, other.flags.get(key));
    }


    public void addFlag(String key, boolean value) 
    {
        this.flags.put(key, value);
    }

    @Override
    public String toString() 
    {
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

    public String toStringCompact() 
    {
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

    public String toFileEntryString() 
    {
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
                s += Main.reverseFlagHash.get(flag);
        return s;
    }

    @Override
    public int compareTo(Object o) 
    {
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
