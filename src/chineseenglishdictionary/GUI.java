package chineseenglishdictionary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * TODO: when editing cell of table, have editing font be similar or same to normal viewing font
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
            private JMenuItem cutItem;
            private JMenuItem copyItem;
            private JMenuItem pasteItem;
            private JMenuItem insertNewEntryItem;
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
        
        // Window setup
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("Dictionary");
            
        // Instantiate Menu bar
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
            createNewItem = new JMenuItem("Create new database");
            openItem = new JMenuItem("Open local database");
            openURLItem = new JMenuItem("Open database from URL");
            saveItem = new JMenuItem("Save database");
            saveAsItem = new JMenuItem("Save database as");
        editMenu = new JMenu("Edit");
            cutItem = new JMenuItem("Cut selection");
            copyItem = new JMenuItem("Copy selection");
            pasteItem = new JMenuItem("Pase selection");
            insertNewEntryItem = new JMenuItem("Insert new entry");
        viewMenu = new JMenu("View");
            sortByEnglishItem = new JMenuItem("Sort by English");
            sortByPinyinItem = new JMenuItem("Sort by Pinyin");
            sortByChineseItem = new JMenuItem("Sort by Chinese characters");
        printMenu = new JMenu("Print");
            printSelectorDialogItem = new JMenuItem("Select in dialog then print");
            printSelectionItem = new JMenuItem("Print Selection");
            printAllItem = new JMenuItem("Print All");
        selectMenu = new JMenu("Select");
            selectAllItem = new JMenuItem("Select All");
            selectNoneItem = new JMenuItem("Select None");
            selectOppositeItem = new JMenuItem("Select Inverse");
        
        // Content pane
        table = new JTable(DEFAULT_TABLE_DATA, DEFAULT_COLUMN_NAMES);
        table.setFillsViewportHeight(true);
        table.setFont(table.getFont().deriveFont(24f));
        table.setRowHeight(30);
        
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(new LineBorder(Color.RED, 4));
        
        // Add action listeners hooks
        // File
        createNewItem.addActionListener((ActionEvent e) -> { createNewItemAction(e); });
        openItem.addActionListener((ActionEvent e) -> { openItemAction(e); });
        openURLItem.addActionListener((ActionEvent e) -> { openURLItemAction(e); });
        saveItem.addActionListener((ActionEvent e) -> { saveItemAction(e); });
        saveAsItem.addActionListener((ActionEvent e) -> { saveAsItemAction(e); });
        // Edit
        cutItem.addActionListener((ActionEvent e) -> { cutItemAction(e); });
        copyItem.addActionListener((ActionEvent e) -> { copyItemAction(e); });
        pasteItem.addActionListener((ActionEvent e) -> { pasteItemAction(e); });
        insertNewEntryItem.addActionListener((ActionEvent e) -> { insertNewEntryItemAction(e); });
        // View
        sortByEnglishItem.addActionListener((ActionEvent e) -> { sortByEnglishItemAction(e); });
        sortByPinyinItem.addActionListener((ActionEvent e) -> { sortByPinyinItemAction(e); });
        sortByChineseItem.addActionListener((ActionEvent e) -> { sortByChineseItemAction(e); });
        // Print
        printSelectorDialogItem.addActionListener((ActionEvent e) -> { printSelectorDialogItemAction(e); });
        printSelectionItem.addActionListener((ActionEvent e) -> { printSelectionItemAction(e); });
        printAllItem.addActionListener((ActionEvent e) -> { printAllItemAction(e); });
        // Select
        selectAllItem.addActionListener((ActionEvent e) -> { selectAllItemAction(e); });
        selectNoneItem.addActionListener((ActionEvent e) -> { selectNoneItemAction(e); });
        selectOppositeItem.addActionListener((ActionEvent e) -> { selectOppositeItem(e); });

        // Set mnemonics (alt + key) and accelerators (ctrl + key)
        fileMenu.setMnemonic(KeyEvent.VK_F);
            createNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            createNewItem.setMnemonic(KeyEvent.VK_N);
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
            openItem.setMnemonic(KeyEvent.VK_O);
            openURLItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
            openURLItem.setMnemonic(KeyEvent.VK_U);
            //openURLItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); //ctrl shift O
            saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
            saveItem.setMnemonic(KeyEvent.VK_S);
        editMenu.setMnemonic(KeyEvent.VK_E);
        printMenu.setMnemonic(KeyEvent.VK_P);
        selectMenu.setMnemonic(KeyEvent.VK_S);
            selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
            selectAllItem.setMnemonic(KeyEvent.VK_A);
            selectNoneItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            selectOppositeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        
        // Add everything to parent container
        // Menubar
        menuBar.add(fileMenu);
            fileMenu.add(createNewItem);
            fileMenu.add(openItem);
            fileMenu.add(openURLItem);
            fileMenu.add(saveItem);
            fileMenu.add(saveAsItem);
        menuBar.add(editMenu);
            editMenu.add(cutItem);
            editMenu.add(copyItem);
            editMenu.add(pasteItem);
            editMenu.add(insertNewEntryItem);
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
    
    //<editor-fold defaultstate="collapsed" desc="Window action functions">
    // File
    private void createNewItemAction(ActionEvent e) {
        System.err.println("createNewItemAction not implemented yet");
    }
    private void openItemAction(ActionEvent e) {
        System.err.println("openItemAction not implemented yet");
    }
    private void openURLItemAction(ActionEvent e) {
        System.err.println("openURLItemAction not implemented yet");
    }
    private void saveItemAction(ActionEvent e) {
        System.err.println("saveItemAction not implemented yet");
    }
    private void saveAsItemAction(ActionEvent e) {
        System.err.println("saveAsItemAction not implemented yet");
    }
    // Edit
    private void cutItemAction(ActionEvent e) {
        System.err.println("cutItemAction not implemented yet");
    }
    private void copyItemAction(ActionEvent e) {
        System.err.println("copyItemAction not implemented yet");
    }
    private void pasteItemAction(ActionEvent e) {
        System.err.println("pasteItemAction not implemented yet");
    }
    private void insertNewEntryItemAction(ActionEvent e) {
        System.err.println("insertNewEntryItemAction not implemented yet");
    }
    // View
    private void sortByEnglishItemAction(ActionEvent e) {
        System.err.println("sortByEnglishItemAction not implemented yet");
    }
    private void sortByPinyinItemAction(ActionEvent e) {
        System.err.println("sortByPinyinItemAction not implemented yet");
    }
    private void sortByChineseItemAction(ActionEvent e) {
        System.err.println("sortByChineseItemAction not implemented yet");
    }
    // Print
    private void printSelectorDialogItemAction(ActionEvent e) {
        System.err.println("printSelectorDialogItemAction not implemented yet");
    }
    private void printSelectionItemAction(ActionEvent e) {
        System.err.println("printSelectionItemAction not implemented yet");
    }
    private void printAllItemAction(ActionEvent e) {
        System.err.println("printAllItemAction not implemented yet");
    }
    // Select
    private void selectAllItemAction(ActionEvent e) {
        System.err.println("selectAllItemAction not implemented yet");
    }
    private void selectNoneItemAction(ActionEvent e) {
        System.err.println("selectNoneItemAction not implemented yet");
    }
    private void selectOppositeItem(ActionEvent e) {
        System.err.println("selectOppositeItem not implemented yet");
    }
    //</editor-fold>
    
    
    /**
     * Creates a default GUI window
     * @param args unused
     */
    public static void main(String[] args) 
    {
        new GUI().setVisible(true);
    }
}
