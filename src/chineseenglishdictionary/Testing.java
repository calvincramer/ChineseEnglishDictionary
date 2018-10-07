package chineseenglishdictionary;

import chineseenglishdictionary.Main.Entry;
import java.nio.charset.Charset;

public class Testing {

    public static void main(String[] args) {
        Charset utf8 = Charset.forName("UTF-8");
        Charset utf16 = Charset.forName("UTF-16");
        
        //SortedMap<String, Charset> charsets = Charset.availableCharsets();
        //for (String key : charsets.keySet())
        //    System.out.println(key + " : " + charsets.get(key));
        //
        /*
   
        System.out.println("testing system out");
        String nihao = "你很";
        System.out.println("nihao : " + nihao);
        System.out.println("");
        
        Scanner in = new Scanner(new InputStreamReader(System.in, utf8));
        in.useDelimiter("[\r\n]+");
        
        System.out.println("testing system in");
        System.out.print("enter chinese characters: ");
        String inp = in.nextLine().trim();
        System.out.println("you entered: " + inp);
        byte[] inpBytes = inp.getBytes(utf8);
        for (byte b : inpBytes)
            System.out.printf("0x%02X ", b);
        System.out.println("");
        System.out.println("");
        */
        
        /*
        Entry e1 = new Entry("eng", "pin", "char");
        e1.addFlag("testkey", true);
        Entry e1copy = new Entry(e1);
        
        System.out.println("e1: " + e1);
        System.out.println("e1 copy: " + e1copy);
        
        
        e1.addFlag("testkey2", true);
        e1.setEnglish("engchanged");
        
        System.out.println("changed....");
        
        System.out.println("e1: " + e1);
        System.out.println("e1 copy: " + e1copy);
        */
        
    }
}
