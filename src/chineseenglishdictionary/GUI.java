package chineseenglishdictionary;

import java.awt.Graphics;
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
    
    private JMenuBar menuBar;
        private JMenu fileMenu;
            private JMenuItem createNewItem;
            private JMenuItem openItem;
            private JMenuItem openURLItem;
            private JMenuItem saveItem;
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

    
    /*
    public GUI(Databse db) {
        
    }
    */
    
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
            
        menuBar = new JMenuBar();
        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_M);
        
            createNewItem = new JMenuItem("Create new database", KeyEvent.VK_N);
            createNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            
            openItem = new JMenuItem("Open local database");
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
            
            openURLItem = new JMenuItem("Open database from URL");
            //openURLItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); //ctrl shift O
        
            saveItem = new JMenuItem("A text-only menu item", KeyEvent.VK_S);
            saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        
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
        
        table = new JTable();
        table.setFillsViewportHeight(true);
        
        
        //add everything to parent container
        menuBar.add(fileMenu);
            fileMenu.add(createNewItem);
            fileMenu.add(openItem);
            fileMenu.add(openURLItem);
            fileMenu.add(saveItem);
        menuBar.add(editMenu);
            //
        menuBar.add(printMenu);
            printMenu.add(printSelectorDialogItem);
            printMenu.add(printSelectionItem);
            printMenu.add(printAllItem);
        menuBar.add(selectMenu);
            //
        this.add(menuBar);
        
        tableScrollPane.add(table);
        this.add(tableScrollPane);
        
        
        this.pack();
    }
    
    
    
    @Override 
    public void paint(Graphics g) {
        
    }
}
