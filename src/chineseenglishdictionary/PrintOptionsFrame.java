package chineseenglishdictionary;

import chineseenglishdictionary.Main.Entry;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class PrintOptionsFrame 
    extends JFrame {
    

    private JPanel topPanel;
        private JPanel printerPanel;
            private JButton selectDefaultPrinterButton;
            private JComboBox printerComboBox;
        private JPanel typePanel;
            private JRadioButton hideEnglishButton;
            private JRadioButton hidePinyinButton;
            private JRadioButton hideChineseCharactersButton;
            private JRadioButton hideEnglishPinyinButton;
            private JRadioButton hideEnglishCharactersButton;
            private JRadioButton hidePinyinCharactersButton;
            private JRadioButton randomHideEnglishPinyinButton;
            private JRadioButton randomHideEnglishCharactersButton;
            private JRadioButton randomHidePinyinCharactersButton;
            private JRadioButton randomHideOneButton;
            private JRadioButton randomHideTwoButton;
            private JRadioButton showAllButton;
    private JPanel centerPanel;
        private JPanel centerTopPanel;
        private JRadioButton selectByFlagsButton;
        private JRadioButton selectRandomlyButton;
        private JRadioButton includeFlagsButton;
        private JRadioButton excludeFlagsButton;
        private JPanel flagsPanel;
            private List<JCheckBox> flagsCheckBoxes;
    
    private JPanel bottomPanel;
        private JCheckBox printAnswersCheckBox;
        private JCheckBox randomizeOrderCheckBox;
        private JLabel totalEntriesLabel;
        private JLabel selectedEntriesLabel;
        private JCheckBox limitEntries;
        private JTextField limitEntriesTextField;
        private JButton printButton;    
    
        
    private final List<Entry> entries;
    private boolean[] selectedEntries;
    private Map<Character, String> flags;
    private Map<JCheckBox, String> cbMap;
    private Map<JRadioButton, Type>    typeMap;
    private Type currentlySelectedType = Type.HIDE_ENGLISH;
    
    private PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    private String[] printServicesNames;
    private PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
    
    private static Random rng = new Random(System.currentTimeMillis());

    
    public PrintOptionsFrame(List<Entry> entries, Map<Character, String> flags) {
        super("Printing Options");
        this.entries = entries;
        this.selectedEntries = new boolean[entries.size()];
        this.flags = flags;
        init();
        this.selectButtonChanged();
        this.limitEntriesButtonChanged();
        
        this.setAlwaysOnTop(true);
        this.setVisible(true);
        this.toFront();
        
        //map
        this.typeMap = new HashMap<>();
        this.typeMap.put(this.hideEnglishButton,                Type.HIDE_ENGLISH);
        this.typeMap.put(this.hidePinyinButton,                 Type.HIDE_PINYIN);
        this.typeMap.put(this.hideChineseCharactersButton,      Type.HIDE_CHARACTERS);
        this.typeMap.put(this.hideEnglishPinyinButton,          Type.HIDE_ENGLISH_PINYIN);
        this.typeMap.put(this.hideEnglishCharactersButton,      Type.HIDE_ENGLISH_CHARACTERS);
        this.typeMap.put(this.hidePinyinCharactersButton,       Type.HIDE_PINYIN_CHARACTERS);
        this.typeMap.put(this.randomHideEnglishPinyinButton,    Type.HIDE_RANDOM_ENGLISH_PINYIN);
        this.typeMap.put(this.randomHideEnglishCharactersButton,Type.HIDE_RANDOM_ENGLISH_CHARACTERS);
        this.typeMap.put(this.randomHidePinyinCharactersButton, Type.HIDE_RANDOM_PINYIN_CHARACTERS);
        this.typeMap.put(this.randomHideOneButton,              Type.HIDE_RANDOM_ONE);
        this.typeMap.put(this.randomHideTwoButton,              Type.HIDE_RANDOM_TWO);
        this.typeMap.put(this.showAllButton,                    Type.SHOW_ALL);
        
        //centeering
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2 - this.getSize().width/2, dim.height/2 - this.getSize().height/2);
    }
    
    
    private void init() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setTitle("Printing Options");
        this.setResizable(false);
        
        this.topPanel = new JPanel(new BorderLayout());
        
        BorderLayout bl = new BorderLayout();
        bl.setHgap(5);
        this.printerPanel = new JPanel(bl);
        this.printerPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1), "Choose Printer", TitledBorder.LEFT, TitledBorder.CENTER));
        
        this.selectDefaultPrinterButton = new JButton("Select default printer");
        this.printServicesNames = new String[this.printServices.length];
        for (int i = 0; i < this.printServicesNames.length; i++)
            this.printServicesNames[i] = this.printServices[i].getName();
        this.printerComboBox = new JComboBox(this.printServicesNames);
        this.printerComboBox.setSelectedItem(this.defaultPrintService.getName());
        
        this.typePanel = new JPanel(new GridLayout(0, 3, 10, 10));
        this.typePanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1), "Choose What To Hide", TitledBorder.LEFT, TitledBorder.CENTER));
        
        this.hideEnglishButton = new JRadioButton("English", true);
        this.hidePinyinButton = new JRadioButton("Pinyin");
        this.hideChineseCharactersButton = new JRadioButton("Characters");
        this.hideEnglishPinyinButton = new JRadioButton("English and Pinyin");
        this.hideEnglishCharactersButton = new JRadioButton("English and Characters");
        this.hidePinyinCharactersButton = new JRadioButton("Pinyin and Characters");
        this.randomHideEnglishPinyinButton = new JRadioButton("Randomly English or Pinyin");
        this.randomHideEnglishCharactersButton = new JRadioButton("Randomly English or Characters");
        this.randomHidePinyinCharactersButton = new JRadioButton("Randomly Pinyin or Characters");
        this.randomHideOneButton = new JRadioButton("Randomly one field");
        this.randomHideTwoButton = new JRadioButton("Randomly two fields");
        this.showAllButton = new JRadioButton("Show all");
        
        this.hideEnglishButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.hidePinyinButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.hideChineseCharactersButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.hideEnglishPinyinButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.hideEnglishCharactersButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.hidePinyinCharactersButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.randomHideEnglishPinyinButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.randomHideEnglishCharactersButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.randomHidePinyinCharactersButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.randomHideOneButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.randomHideTwoButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.showAllButton.setHorizontalAlignment(SwingConstants.CENTER);
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(this.hideEnglishButton);
        typeGroup.add(this.hidePinyinButton);
        typeGroup.add(this.hideChineseCharactersButton);
        typeGroup.add(this.hideEnglishPinyinButton);
        typeGroup.add(this.hideEnglishCharactersButton);
        typeGroup.add(this.hidePinyinCharactersButton);
        typeGroup.add(this.randomHideEnglishPinyinButton);
        typeGroup.add(this.randomHideEnglishCharactersButton);
        typeGroup.add(this.randomHidePinyinCharactersButton);
        typeGroup.add(this.randomHideOneButton);
        typeGroup.add(this.randomHideTwoButton);
        typeGroup.add(this.showAllButton);
        
        this.centerPanel = new JPanel(new BorderLayout());
        this.centerPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1), "Select By", TitledBorder.LEFT, TitledBorder.CENTER));
        
        this.centerTopPanel = new JPanel();
        //this.centerTopPanel.setBorder(new LineBorder(Color.BLACK, 1));
        
        this.selectByFlagsButton = new JRadioButton("Select by flags", false);
        this.selectRandomlyButton = new JRadioButton("Select randomly", true);
        this.includeFlagsButton = new JRadioButton("Include flags:", true);
        this.excludeFlagsButton = new JRadioButton("Exclude flags:", false);
        
        ButtonGroup selectionGroup = new ButtonGroup();
        selectionGroup.add(this.selectByFlagsButton);
        selectionGroup.add(this.selectRandomlyButton);
        
        ButtonGroup inOutGroup = new ButtonGroup();
        inOutGroup.add(this.includeFlagsButton);
        inOutGroup.add(this.excludeFlagsButton);
        
        this.flagsPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        //this.flagsPanel.setLayout(new BoxLayout(this.flagsPanel, BoxLayout.Y_AXIS));
        //this.flagsPanel.setBorder(new LineBorder(Color.YELLOW, 1));
        
        this.flagsCheckBoxes = new ArrayList<>();
        this.cbMap = new HashMap<>();
        for (Character c : this.flags.keySet()) {
            JCheckBox temp = new JCheckBox(c + " : " + this.flags.get(c));
            temp.setHorizontalAlignment(SwingConstants.CENTER);
            this.flagsCheckBoxes.add(temp);
            this.cbMap.put(temp, flags.get(c));
        }
        
        
        
        this.bottomPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        this.bottomPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1), "Options", TitledBorder.LEFT, TitledBorder.CENTER));
        
        this.printAnswersCheckBox = new JCheckBox("Print answer sheet", true);
        
        this.randomizeOrderCheckBox = new JCheckBox("Randomize order", true);
        
        this.totalEntriesLabel = new JLabel("Total entries: " + this.entries.size());
        this.totalEntriesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.selectedEntriesLabel = new JLabel("Selected entries: " + this.entries.size());
        this.selectedEntriesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.limitEntries = new JCheckBox("Limit entries?", true);
        this.limitEntries.setHorizontalAlignment(SwingConstants.RIGHT);
        
        this.limitEntriesTextField = new JTextField("" + '\u221e');
        this.printButton = new JButton("PRINT");
        
        
        //actions
        this.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                exitButtonPressed();
            }
        });
        
        this.selectDefaultPrinterButton.addActionListener((ActionEvent e) -> {
            setToDefaultPrinterButtonPressed();
        });
        
        this.hideEnglishButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.hidePinyinButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.hideChineseCharactersButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.hideEnglishPinyinButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.hideEnglishCharactersButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.hidePinyinCharactersButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.randomHideEnglishPinyinButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.randomHideEnglishCharactersButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.randomHidePinyinCharactersButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.randomHideOneButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.randomHideTwoButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        this.showAllButton.addActionListener((ActionEvent e) -> {
            typeButtonChanged();
        });
        
        this.selectByFlagsButton.addActionListener((ActionEvent e) -> {
            selectButtonChanged();
        });
        this.selectRandomlyButton.addActionListener((ActionEvent e) -> {
            selectButtonChanged();
        });
        this.limitEntries.addActionListener((ActionEvent e) -> {
            limitEntriesButtonChanged();
        });
        
        for (JCheckBox cb : this.flagsCheckBoxes) {
            cb.addActionListener((ActionEvent e) -> {
                filterChanged();
            });
        }
        
        this.printButton.addActionListener((ActionEvent e) -> {
            printPressed();
        });
        
        
        //layout
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.getContentPane().add(this.topPanel);
            this.topPanel.add(this.printerPanel, BorderLayout.NORTH);
                this.printerPanel.add(this.selectDefaultPrinterButton, BorderLayout.WEST);
                this.printerPanel.add(this.printerComboBox, BorderLayout.CENTER);
            this.topPanel.add(this.typePanel, BorderLayout.CENTER);
                this.typePanel.add(this.hideEnglishButton);
                this.typePanel.add(this.hidePinyinButton);
                this.typePanel.add(this.hideChineseCharactersButton);
                this.typePanel.add(this.hideEnglishPinyinButton);
                this.typePanel.add(this.hideEnglishCharactersButton);
                this.typePanel.add(this.hidePinyinCharactersButton);
                this.typePanel.add(this.randomHideEnglishPinyinButton);
                this.typePanel.add(this.randomHideEnglishCharactersButton);
                this.typePanel.add(this.randomHidePinyinCharactersButton);
                this.typePanel.add(this.randomHideOneButton);
                this.typePanel.add(this.randomHideTwoButton);
                this.typePanel.add(this.showAllButton);
        this.getContentPane().add(this.centerPanel);
            this.centerPanel.add(this.centerTopPanel, BorderLayout.NORTH);
                this.centerTopPanel.add(this.selectByFlagsButton);
                this.centerTopPanel.add(this.selectRandomlyButton);
                this.centerTopPanel.add(new JLabel("        "));
                this.centerTopPanel.add(this.includeFlagsButton);
                this.centerTopPanel.add(this.excludeFlagsButton);
            this.centerPanel.add(this.flagsPanel, BorderLayout.CENTER);
                for (JCheckBox cb : this.flagsCheckBoxes)
                    this.flagsPanel.add(cb);
        this.getContentPane().add(this.bottomPanel);
            this.bottomPanel.add(this.printAnswersCheckBox);
            this.bottomPanel.add(this.randomizeOrderCheckBox);
            this.bottomPanel.add(this.totalEntriesLabel);
            this.bottomPanel.add(this.selectedEntriesLabel);
            this.bottomPanel.add(this.limitEntries);
            this.bottomPanel.add(this.limitEntriesTextField);
            this.bottomPanel.add(this.printButton);
            
        this.pack();
    }
    
    private void setToDefaultPrinterButtonPressed() {
        this.printerComboBox.setSelectedItem(this.defaultPrintService.getName());
    }
    
    private void typeButtonChanged() {
        if (this.showAllButton.isSelected())
            this.printAnswersCheckBox.setSelected(false);
        else
            this.printAnswersCheckBox.setSelected(true);
        
        //update currently selected type button
        if (this.hideEnglishButton.isSelected())    this.currentlySelectedType = this.typeMap.get(this.hideEnglishButton);
        if (this.hidePinyinButton.isSelected())     this.currentlySelectedType = this.typeMap.get(this.hidePinyinButton);
        if (this.hideChineseCharactersButton.isSelected())  this.currentlySelectedType = this.typeMap.get(this.hideChineseCharactersButton);
        if (this.hideEnglishPinyinButton.isSelected())      this.currentlySelectedType = this.typeMap.get(this.hideEnglishPinyinButton);
        if (this.hideEnglishCharactersButton.isSelected())  this.currentlySelectedType = this.typeMap.get(this.hideEnglishCharactersButton);
        if (this.hidePinyinCharactersButton.isSelected())   this.currentlySelectedType = this.typeMap.get(this.hidePinyinCharactersButton);
        if (this.randomHideEnglishPinyinButton.isSelected())    this.currentlySelectedType = this.typeMap.get(this.randomHideEnglishPinyinButton);
        if (this.randomHideEnglishCharactersButton.isSelected())    this.currentlySelectedType = this.typeMap.get(this.randomHideEnglishCharactersButton);
        if (this.randomHidePinyinCharactersButton.isSelected()) this.currentlySelectedType = this.typeMap.get(this.randomHidePinyinCharactersButton);
        if (this.randomHideOneButton.isSelected())  this.currentlySelectedType = this.typeMap.get(this.randomHideOneButton);
        if (this.randomHideTwoButton.isSelected())  this.currentlySelectedType = this.typeMap.get(this.randomHideTwoButton);
        if (this.showAllButton.isSelected())    this.currentlySelectedType = this.typeMap.get(this.showAllButton);
        
        //System.out.println("currently selected type: " + this.currentlySelectedType.getText());
    }
    
    private void selectButtonChanged() {
        filterChanged();
        if (this.selectByFlagsButton.isSelected()) {
            this.includeFlagsButton.setEnabled(true);
            this.excludeFlagsButton.setEnabled(true);
            for (JCheckBox cb : this.flagsCheckBoxes)
                cb.setEnabled(true);
        }
        else if (this.selectRandomlyButton.isSelected()) {
            this.includeFlagsButton.setEnabled(false);
            this.excludeFlagsButton.setEnabled(false);
            for (JCheckBox cb : this.flagsCheckBoxes)
                cb.setEnabled(false);
        }
    }
    
    private void limitEntriesButtonChanged() {
        if (this.limitEntries.isSelected()) {
            this.limitEntriesTextField.setText("80");
        }
        else {
            this.limitEntriesTextField.setText("" + '\u221e');
        }
    }
    
    private void filterChanged() {
        if (this.selectRandomlyButton.isSelected()) {
            for (int i = 0; i < this.selectedEntries.length; i++)
                selectedEntries[i] = true;
            this.selectedEntriesLabel.setText("Selected entries: " + this.entries.size());
            return;
        }
        else if (this.selectByFlagsButton.isSelected()) {
            int total = 0;
            if (this.includeFlagsButton.isSelected()) {
                
                for (int i = 0; i < this.selectedEntries.length; i++)   //clear selection
                    selectedEntries[i] = false;
                
                for (int i = 0; i < this.entries.size(); i++) {
                    boolean containsAllFlags = true;
                    for (JCheckBox cb : this.flagsCheckBoxes) {
                        if (cb.isSelected() && entries.get(i).flags.get(cbMap.get(cb)) == false) {
                            containsAllFlags = false;
                            break;
                        }
                    }
                    if (containsAllFlags) {
                        total++;
                        selectedEntries[i] = true;
                    }
                }
            }
            else if (this.excludeFlagsButton.isSelected()) {
                
                total = this.entries.size();
                for (int i = 0; i < this.selectedEntries.length; i++)   //clear selection
                    selectedEntries[i] = true;
                
                for (int i = 0; i < this.entries.size(); i++) {
                    boolean containsAnyFlag = false;
                    for (JCheckBox cb : this.flagsCheckBoxes) {
                        if (cb.isSelected() && entries.get(i).flags.get(cbMap.get(cb)) == true) {
                            containsAnyFlag = true;
                            break;
                        }
                    }
                    if (containsAnyFlag) {
                        total--;
                        selectedEntries[i] = false;
                    }
                }
            }
            
            this.selectedEntriesLabel.setText("Selected entries: " + total);
            
            //testing
            int testingTotal = 0;
            for (boolean b : this.selectedEntries)
                if (b)
                    testingTotal++;
            if (total != testingTotal) {
                System.out.println("DIFFERENCE: " + total + "\t" + testingTotal);
            }
            
            return;
        }
    }
    
    private void printPressed() {
        this.setVisible(false);
        
        //create list of entries to be printed
        List<Entry> toPrint = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            if (this.selectedEntries[i] == true)
                toPrint.add(entries.get(i));
        }
        if (this.randomizeOrderCheckBox.isSelected()) {
            toPrint = shuffle(toPrint);
        }
        
        try {
            PrintService printService = this.printServices[this.printerComboBox.getSelectedIndex()];
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                attributes.add(OrientationRequested.PORTRAIT);
                attributes.add(new MediaPrintableArea(0.5f, 0.5f, 7.5f, 10f, MediaPrintableArea.INCH));
                attributes.add(new Copies(1));
                attributes.add(MediaSizeName.NA_LETTER);
            
            System.out.println("Selected printer: " + printService.getName());

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Dictionary");
            PageFormat format = job.defaultPage();          //setting able to print on whole paper
            Paper paper = format.getPaper();                //copied, not reference
            double inset = 36;                              //72nths of an inch
            paper.setImageableArea(inset, inset, paper.getWidth() - inset * 2, paper.getHeight() - inset * 2);
            format.setPaper(paper);
            job.setPrintService(printService);
            job.setPrintable(new EntryPagePrinter(toPrint, this.currentlySelectedType));
            System.out.print("Sending print job... ");
            long start = System.currentTimeMillis();
            /**/ job.print(attributes);   /**/    //the holy print line      
            System.out.println("sent (" + (System.currentTimeMillis() - start) + "ms)");
            
            //answer sheet
            if (this.printAnswersCheckBox.isSelected()) {                
                PrinterJob answerJob = PrinterJob.getPrinterJob();
                answerJob.setJobName("Answer Sheet");
                PageFormat answerFormat = answerJob.defaultPage();          //setting able to print on whole paper
                Paper answerPaper = answerFormat.getPaper();                //copied, not reference
                answerPaper.setImageableArea(inset, inset, answerPaper.getWidth() - inset * 2, answerPaper.getHeight() - inset * 2);
                answerFormat.setPaper(answerPaper);
                answerJob.setPrintService(printService);
                answerJob.setPrintable(new EntryPagePrinter(toPrint, Type.SHOW_ALL));
                System.out.print("Sending answer print job... ");
                start = System.currentTimeMillis();
                /**/ answerJob.print(attributes);   /**/    //the holy print line      
                System.out.println("sent (" + (System.currentTimeMillis() - start) + "ms)");
            }
            
            //printing stuff for testing
            /*
            System.out.println("Default page format: " + format.toString());
            System.out.println("\tHeight: " + format.getHeight() + " (" + (format.getHeight() / 72) + ")"+ " width: " + format.getWidth() + " (" + (format.getWidth()/ 72) + ")"
                    + " imag height: " + format.getImageableHeight() + " (" + (format.getImageableHeight()/ 72) + ")" + " imag width: " + format.getImageableWidth() + " (" + (format.getImageableWidth()/ 72) + ")"
                    + " imag x: " + format.getImageableX() + " (" + (format.getImageableX()/ 72) + ")" + " imag y: " + format.getImageableY() + " (" + (format.getImageableY()/ 72) + ")");
            
            System.out.println("PrintRequestAttributeSet: " + attributes.toString());
            for (Attribute attr : attributes.toArray()) {
                System.out.println("\t" + attr.toString() + " -> " + attr.getName() + "    " + attr.getCategory());
            }   
            */
        } catch (PrinterException ex) {
            Logger.getLogger(PrintOptionsFrame.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        
        this.exitButtonPressed();
    }

    private void exitButtonPressed() {
        this.setVisible(false);
        this.dispose();
    }
    
    public static <E> List<E> shuffle(List<E> list) {
        List<E> shuffled = new ArrayList<>();
        
        for (E el : list)
            shuffled.add((shuffled.size() == 0) ? 0 : rng.nextInt(shuffled.size() + 1), el);
        return shuffled;
        
    }
    
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        //list.add(4);
        
        Map<List<Integer>, Integer> occur = new HashMap<>();
        int max = 10000000;
        
        for (int i = 0; i < max; i++) {
            List<Integer> shuf = PrintOptionsFrame.shuffle(list);
            Integer numOccur = occur.get(shuf);
            if (numOccur == null)
                occur.put(shuf, 1);
            else
                occur.put(shuf, numOccur + 1);
        }
        
        for (List<Integer> l : occur.keySet()) {
            System.out.println(l + "\t" + occur.get(l) + "\t" + (occur.get(l) * 1.0 / max) + "%");
        }
        
        
        
    }
    
    /**
     * Creates the image for the paper
     */
    private class EntryPagePrinter implements Printable {
        List<Entry> entriesToPrint;
        Type type;
        
        public EntryPagePrinter(List<Entry> e, Type t) {
            this.entriesToPrint = e;
            this.type = t;
        }
        
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0)  return Printable.NO_SUCH_PAGE;
            
            /*
            System.out.println("Page format in print: " + pageFormat.toString());
            System.out.println("\tHeight: " + pageFormat.getHeight() + " (" + (pageFormat.getHeight() / 72) + ")"+ " width: " + pageFormat.getWidth() + " (" + (pageFormat.getWidth()/ 72) + ")"
                    + " imag height: " + pageFormat.getImageableHeight() + " (" + (pageFormat.getImageableHeight()/ 72) + ")" + " imag width: " + pageFormat.getImageableWidth() + " (" + (pageFormat.getImageableWidth()/ 72) + ")"
                    + " imag x: " + pageFormat.getImageableX() + " (" + (pageFormat.getImageableX()/ 72) + ")" + " imag y: " + pageFormat.getImageableY() + " (" + (pageFormat.getImageableY()/ 72) + ")");
            */

            Paper paper = pageFormat.getPaper();
            //int width = (int) paper.getWidth();     //in 72nths of an inch
            //int height = (int) paper.getHeight();   //^ 
            int width = (int) paper.getImageableWidth();
            int height = (int) paper.getImageableHeight();
            double widthInches = width / 72.0;
            double heightInches = height / 72.0;
            
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            

            //draw whole dictionary
            final boolean drawLines = true;
            final boolean drawText = true;
            final boolean drawTextAlignmentLines = false;
            final boolean drawBorderLine = false;
            final boolean drawCornerLines = false;
            
            final boolean drawX = false;
            final int entryHeight = 18;     //72ths of an inch
            final int engWidth = 108;   //1 1/2
            final int pinWidth = 82;    //1 + a little
            final int charWidth = 62;   //1 - a little
            //the above three should add up to 3 1/2 inches (252)
            final int gapWidth = 36;    //1/2
            final int textStartGap = 2;
            final int engTextWidth = engWidth - textStartGap * 2;
            final int pinTextWidth = pinWidth - textStartGap * 2;
            final int charTextWidth = charWidth - textStartGap * 2;
            
            final int columnWidth = engWidth + pinWidth + charWidth;
            final int maxEntriesPerColumn = height / entryHeight;
            //final int fontHeight = g2.getFontMetrics().getAscent() + g2.getFontMetrics().getDescent();  //what unit?
            final int fontHeight = g2.getFont().getSize();  //a point size is a 72ths of an inch
            final int fontHeight2 = g2.getFontMetrics().getHeight();
            int numEntries = entriesToPrint.size();
            int rowsFirstCol;
            int rowsSecondCol;
            int tx;
            int ty;
            
            rowsFirstCol = Math.min(numEntries, maxEntriesPerColumn);
            numEntries -= rowsFirstCol;
            
            rowsSecondCol = Math.min(numEntries, maxEntriesPerColumn);
            numEntries -= rowsSecondCol;
            
            if (numEntries > 0)
                System.out.println("Wanted to print more entries, but ran out of room on page");
            
            //x accros whole page
            if (drawX) {
                g2.drawLine(0, 0, width, height);
                g2.drawLine(0, height, width, 0);
            }
            
            //dashed border
            if (drawBorderLine) {
                g2.setColor(Color.LIGHT_GRAY);
                Stroke defaultStroke = g2.getStroke();
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10f,5f}, 0));   //dashed
                g2.drawLine(0, 0, width, 0);
                g2.drawLine(0, 0, 0, height);
                g2.drawLine(width, height, 0, height);
                g2.drawLine(width, height, width, 0);
                g2.setStroke(defaultStroke);
            }
            
            //arrows in corner of drawing area
            if (drawCornerLines) {
                int lineLength = 72 / 2;
                //top left corner
                g2.setColor(Color.GRAY);
                g2.drawLine(0, 0, 0 + lineLength, 0);
                g2.drawLine(0, 0, 0, 0 + lineLength);
                //top right corner
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(width, 0, width - lineLength, 0);
                g2.drawLine(width, 0, width, 0 + lineLength);
                //bottom left corner
                g2.setColor(Color.DARK_GRAY);
                g2.drawLine(0, height, 0 + lineLength, height);
                g2.drawLine(0, height, 0, height - lineLength);
                //bottom right corner
                g2.setColor(Color.BLUE);
                g2.drawLine(width, height, width - lineLength, height);
                g2.drawLine(width, height, width, height - lineLength);
            }
            
            
            //draw table lines
            if (drawLines) {
                int firstColHeight = entryHeight * rowsFirstCol;
                int secondColHeight = entryHeight * rowsSecondCol;

                //verticle lines
                g2.setColor(Color.BLACK);
                tx = 0;
                g2.drawLine(tx, 0, tx, firstColHeight);
                tx += engWidth;
                g2.drawLine(tx, 0, tx, firstColHeight);
                tx += pinWidth;
                g2.drawLine(tx, 0, tx, firstColHeight);
                tx += charWidth;
                g2.drawLine(tx, 0, tx, firstColHeight);
                if (rowsSecondCol > 0) {
                    tx += gapWidth;
                    g2.drawLine(tx, 0, tx, secondColHeight);
                    tx += engWidth;
                    g2.drawLine(tx, 0, tx, secondColHeight);
                    tx += pinWidth;
                    g2.drawLine(tx, 0, tx, secondColHeight);
                    tx += charWidth;
                    g2.drawLine(tx, 0, tx, secondColHeight);
                }
                //horizontal lines
                tx = 0;
                ty = 0;
                for (int i = 0; i <= rowsFirstCol; i++) {
                    g2.drawLine(tx, ty, tx + columnWidth, ty);
                    ty += entryHeight;
                }
                if (rowsSecondCol > 0) {
                    tx = columnWidth + gapWidth;
                    ty = 0;
                    for (int i = 0; i <= rowsSecondCol; i++) {
                        g2.drawLine(tx, ty, tx + columnWidth, ty);
                        ty += entryHeight;
                    }
                }
            }
            
            
            //draw text
            if (drawText) {
                g2.setColor(Color.BLACK);
                int entryIndex = 0;
                //first column
                ty = entryHeight - (entryHeight - fontHeight) / 2;
                tx = textStartGap;
                for (int i = 0; i < rowsFirstCol; i++) {
                    String[] fields = new String[] {entriesToPrint.get(entryIndex).english, entriesToPrint.get(entryIndex).pinyin, entriesToPrint.get(entryIndex).characters};
                    fields = conformToType(fields);
                    drawTextIn(g2, fields[0], tx,                       ty, engTextWidth);
                    drawTextIn(g2, fields[1], tx + engWidth,            ty, pinTextWidth);
                    drawTextIn(g2, fields[2], tx + engWidth + pinWidth, ty, charTextWidth);

                    //testing font alignment
                    if (drawTextAlignmentLines) {
                        g2.setColor(Color.LIGHT_GRAY);
                        g2.drawLine(tx, ty, tx + engWidth + pinWidth + charWidth, ty);
                        g2.setColor(Color.RED);
                        g2.drawLine(tx, ty - g2.getFontMetrics().getAscent(), tx + engWidth + pinWidth + charWidth, ty - g2.getFontMetrics().getAscent());
                        g2.setColor(Color.GREEN);
                        g2.drawLine(tx, ty + g2.getFontMetrics().getDescent(), tx + engWidth + pinWidth + charWidth, ty + g2.getFontMetrics().getDescent());
                        g2.setColor(Color.BLACK);
                    }

                    ty += entryHeight;
                    entryIndex++;
                }

                //second column
                ty = entryHeight - (entryHeight - fontHeight) / 2;
                tx = engWidth + pinWidth + charWidth + gapWidth + textStartGap;
                for (int i = 0; i < rowsSecondCol; i++) {
                    String[] fields = new String[] {entriesToPrint.get(entryIndex).english, entriesToPrint.get(entryIndex).pinyin, entriesToPrint.get(entryIndex).characters};
                    fields = conformToType(fields);
                    drawTextIn(g2, fields[0], tx,                       ty, engTextWidth);
                    drawTextIn(g2, fields[1], tx + engWidth,            ty, pinTextWidth);
                    drawTextIn(g2, fields[2], tx + engWidth + pinWidth, ty, charTextWidth);

                    ty += entryHeight;
                    entryIndex++;
                }
            }

            return Printable.PAGE_EXISTS;
        }
        
        private String[] conformToType(String[] inp) {
            if (this.type == null) {
                this.type = Type.SHOW_ALL;
                System.out.println("PRINT TYPE IS NULL FOR SOME REASON, PRINTING EVERYTHING");
            }
            
            switch (this.type) {
                case HIDE_ENGLISH:  inp[0] = "";       break;
                case HIDE_PINYIN:   inp[1] = "";     break;
                case HIDE_CHARACTERS:   inp[2] = ""; break;
                case HIDE_ENGLISH_PINYIN: inp[0] = ""; inp[1] = "";       break;
                case HIDE_ENGLISH_CHARACTERS: inp[0] = ""; inp[2] = "";   break;
                case HIDE_PINYIN_CHARACTERS:  inp[1] = ""; inp[2] = "";   break;
                case HIDE_RANDOM_ENGLISH_PINYIN:  
                    boolean b = rng.nextBoolean();
                    if (b)  inp[0] = "";
                    else    inp[1] = "";
                    break;
                case HIDE_RANDOM_ENGLISH_CHARACTERS:
                    b = rng.nextBoolean();
                    if (b)  inp[0] = "";
                    else    inp[2] = "";
                    break;
                case HIDE_RANDOM_PINYIN_CHARACTERS:
                    b = rng.nextBoolean();
                    if (b)  inp[1] = "";
                    else    inp[2] = "";
                    break;
                case HIDE_RANDOM_ONE:  
                    int n = rng.nextInt(3);
                    inp[n] = "";
                    break;
                case HIDE_RANDOM_TWO:  
                    n = rng.nextInt(3);
                    for (int i = 0; i < 3; i++)
                        if (i != n)
                            inp[i] = "";
                    break;
                case SHOW_ALL:  break;
                default: System.out.println("weird type: " + this.type);;
            }
            return inp;
        }
        
        private void drawTextIn(Graphics2D g2, String text, int x, int y, int width) {
            //maybe we could do some fancy math, maybe this is the easiest way?4
            FontMetrics m = g2.getFontMetrics();
            
            Font orig = g2.getFont();
            Font smaller = orig.deriveFont(orig.getSize2D());
            while (m.stringWidth(text) > width) {
                smaller = smaller.deriveFont(smaller.getSize2D() - 0.01f);
                g2.setFont(smaller);
                m = g2.getFontMetrics();
            }
            g2.drawString(text, x, y);
            g2.setFont(orig);
        }
    }
    
    private enum EntryType {
        ENGLISH, PINYIN, CHARACTERS
    }
    
    private enum Type {
        HIDE_ENGLISH, HIDE_PINYIN, HIDE_CHARACTERS,
        HIDE_ENGLISH_PINYIN, HIDE_ENGLISH_CHARACTERS, HIDE_PINYIN_CHARACTERS,
        HIDE_RANDOM_ENGLISH_PINYIN, HIDE_RANDOM_ENGLISH_CHARACTERS, HIDE_RANDOM_PINYIN_CHARACTERS,
        HIDE_RANDOM_ONE, HIDE_RANDOM_TWO, SHOW_ALL
    }
}
