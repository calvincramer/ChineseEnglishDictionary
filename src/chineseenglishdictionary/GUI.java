package chineseenglishdictionary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

/**
 * Takes information from database an displays it
 * TODO: Chinese character, English, pinyin, column for each flag for table
 * TODO: Press column header, which sorts by that column
 * @author Calvin Cramer
 */
public class GUI extends JFrame {
    
    private Database db;
    
    private static final String[][] DEFAULT_TABLE_DATA = {
        {"hello", "nǐ hǎo", "你很"},
        {"goodbye", "zài jiàn", "再见"},
        {"good", "hǎo", "好"},
        {"no", "bù", "不"},
        {"1", "2", "3"},    
        {"4", "5", "6"},
        {"2", "8", "9"},
        {"6", "7", "5"},
        {"3", "3", "3"},
        {"7", "7", "1"},
        {"3", "9", "2"},
        {"8", "6", "5"},
        {"3", "5", "6"},
        {"5", "6", "7"},
        {"7", "7", "8"},
        {"9", "8", "9"},
        {"3", "9", "5"},
    };
    private static final String[] DEFAULT_COLUMN_NAMES = {"English", "Pinyin", "Chinese Characters"};
    
    private JMenuBar menuBar;
        private JMenu fileMenu;
            private JMenuItem createNewItem;
            private JMenuItem openItem;
            private JMenuItem openURLItem;
            private JMenuItem saveItem;
            private JMenuItem saveAsItem;
        private JMenu editMenu;
            //
        private JMenu viewMenu;
            private JMenuItem sortByEnglishItem;
            private JMenuItem sortByPinyinItem;
            private JMenuItem sortByChineseItem;
        private JMenu printMenu;
            private JMenuItem printSelectorDialogItem;
            private JMenuItem printSelectionItem;
            private JMenuItem printAllItem;
        private JMenu selectMenu;
            private JMenuItem selectAllItem;
            private JMenuItem selectNoneItem;
            private JMenuItem selectOppositeItem;
            //
        
        private JScrollPane tableScrollPane;
            private JTable table;

    
    /**
     * Creates a new GUI frame with a default table of entries
     */    
    public GUI() 
    {
        this(null);
    }
    
    
    /**
     * Creates a new GUI frame with a specified database
     * @param db Database to use
     */
    public GUI(Database db) 
    {
        this.db = db;
        
        this.initGUI();
        this.centerFrame();
    }    
    
    
    /**
     * Creates all of the components, sizes and packs them into the frame
     */
    private void initGUI() 
    {
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        if (defaults.get("Table.alternateRowColor") == null)
            defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
        
        // This
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("Dictionary");
            
        // Menu bar
        menuBar = new JMenuBar();
        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_M);
        
            createNewItem = new JMenuItem("Create new database", KeyEvent.VK_N);
            createNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            
            openItem = new JMenuItem("Open local database");
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
            
            openURLItem = new JMenuItem("Open database from URL");
            //openURLItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); //ctrl shift O
        
            saveItem = new JMenuItem("Save database", KeyEvent.VK_S);
            saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        
            saveAsItem = new JMenuItem("Save database as");
            
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
            //
            
        viewMenu = new JMenu("View");
            sortByEnglishItem = new JMenuItem("Sort by English");
            sortByPinyinItem = new JMenuItem("Sort by Pinyin");
            sortByChineseItem = new JMenuItem("Sort by Chinese characters");
            
        printMenu = new JMenu("Print");
        printMenu.setMnemonic(KeyEvent.VK_P);
        
            printSelectorDialogItem = new JMenuItem("Select in dialog then print");
            printSelectionItem = new JMenuItem("Print Selection");
            printAllItem = new JMenuItem("Print All");
        
        selectMenu = new JMenu("Select");
        //selectMenu.setMnemonic(KeyEvent.VK_S);
            selectAllItem = new JMenuItem("Select all");
            selectNoneItem = new JMenuItem("Select none");
            selectOppositeItem = new JMenuItem("Select inverse");
        
        // Content pane
        //table = new JTable(10,4);   // Number of rows, number of columns
        table = new JTable(DEFAULT_TABLE_DATA, DEFAULT_COLUMN_NAMES);
        table.setFillsViewportHeight(true);
        table.setFont(table.getFont().deriveFont(24f));
        table.setRowHeight(30);
        
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(new LineBorder(Color.RED, 4));
        
        // Add everything to parent container
        // Menubar
        menuBar.add(fileMenu);
            fileMenu.add(createNewItem);
            fileMenu.add(openItem);
            fileMenu.add(openURLItem);
            fileMenu.add(saveItem);
            fileMenu.add(saveAsItem);
        menuBar.add(editMenu);
            //
        menuBar.add(viewMenu);
            viewMenu.add(sortByEnglishItem);
            viewMenu.add(sortByPinyinItem);
            viewMenu.add(sortByChineseItem);
        menuBar.add(printMenu);
            printMenu.add(printSelectorDialogItem);
            printMenu.add(printSelectionItem);
            printMenu.add(printAllItem);
        menuBar.add(selectMenu);
            selectMenu.add(selectAllItem);
            selectMenu.add(selectNoneItem);
            selectMenu.add(selectOppositeItem);
        this.setJMenuBar(menuBar);
        
        // Content pane
        //tableScrollPane.add(table);
        this.getContentPane().add(tableScrollPane);
        //this.getContentPane().add(this.table);
        
        // Set preferred dimensions
        //this.menuBar.setPreferredSize(new Dimension(1000,30));
        this.getContentPane().setPreferredSize(new Dimension(400,300));
        
        this.pack();
    }
    
    
    /**
     * Puts the frame in the center of the screen
     * Note that the frame needs to be packed first in order for the get height
     * and get width functions to give the correct numbers
     */
    private void centerFrame() 
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        int x = (width / 2) - (this.getWidth() / 2);
        int y = (height / 2) - (this.getHeight() / 2);
        this.setBounds(x, y, this.getWidth(), this.getHeight());
    }
    
    
    /**
     * Creates a default GUI window
     * @param args unused
     */
    public static void main(String[] args) 
    {
        new GUI().setVisible(true);
    }
}
