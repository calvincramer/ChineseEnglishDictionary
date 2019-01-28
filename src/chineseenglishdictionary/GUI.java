package chineseenglishdictionary;

import java.awt.Dimension;
import java.awt.Graphics;
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

/**
 * Takes information from database an displays it
 * @author Calvin Cramer
 */
public class GUI extends JFrame {
    
    private Database db;
    
    private JMenuBar menuBar;
        private JMenu fileMenu;
            private JMenuItem createNewItem;
            private JMenuItem openItem;
            private JMenuItem openURLItem;
            private JMenuItem saveItem;
            private JMenuItem saveAsItem;
        private JMenu editMenu;
            //
        private JMenu printMenu;
            private JMenuItem printSelectorDialogItem;
            private JMenuItem printSelectionItem;
            private JMenuItem printAllItem;
        private JMenu selectMenu;
            //
        
        private JScrollPane tableScrollPane;
            private JTable table;

            
    public GUI() {
        this(null);
    }
    
    public GUI(Database db) {
        this.db = db;
        
        this.initGUI();
        this.centerFrame();
    }    
    
    private void initGUI() {
        //make menubar, table
        //save feature, 
        //open feature
        //open from file or URL
        //print function
            //print selection
            //print all
        //select by function
        //columns of table:
            //chinese character, english, pinyin, column for each flag
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("Dictionary");
            
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
        
        printMenu = new JMenu("Print");
        printMenu.setMnemonic(KeyEvent.VK_P);
        
            printSelectorDialogItem = new JMenuItem("Select in dialog then print");
            printSelectionItem = new JMenuItem("Print Selection");
            printAllItem = new JMenuItem("Print All");
        
        selectMenu = new JMenu("Select");
        //selectMenu.setMnemonic(KeyEvent.VK_S);
        
        
        tableScrollPane = new JScrollPane();
        
        table = new JTable(10,4);   // Number of rows, number of columns
        table.setFillsViewportHeight(true);
        
        
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
        menuBar.add(printMenu);
            printMenu.add(printSelectorDialogItem);
            printMenu.add(printSelectionItem);
            printMenu.add(printAllItem);
        menuBar.add(selectMenu);
            //
        this.setJMenuBar(menuBar);
        
        // Content pane
        tableScrollPane.add(table);
        this.getContentPane().add(tableScrollPane);
        
        // Set preferred dimensions
        //this.menuBar.setPreferredSize(new Dimension(1000,30));
        this.getContentPane().setPreferredSize(new Dimension(400,300));
        
        this.pack();
    }
    
    
    
    @Override 
    public void paint(Graphics g) {
        
    }
    
    /**
     * Centers the frame in the window
     */
    private void centerFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        int x = (width / 2) - (this.getWidth() / 2);
        int y = (height / 2) - (this.getHeight() / 2);
        this.setBounds(x, y, this.getWidth(), this.getHeight());
    }
    
    
    public static void main(String[] args) {
        new GUI().setVisible(true);
    }
}
