package ua.com.didux.dorrent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Dorrent extends JFrame implements ActionListener
{
   // JButton okButton;
   // JButton openButton;
   // JTextField textField;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem openFileMenuItem;
    JMenuItem exitFileMenuItem;
    JFileChooser fileChooser;
    
    Dorrent()
    {
        this.setTitle("Dorrent");
        this.setSize(600, 600);
        this.setLayout(null);
        
//        okButton = new JButton("OK");
//        okButton.setBounds(50, 150, 100, 30);
//        okButton.addActionListener(this);
//        this.add(okButton);
//        
//        openButton = new JButton("Open");
//        openButton.setBounds(450, 50, 100, 30);
//        openButton.addActionListener(this);
//        this.add(openButton);
//        
//        textField = new JTextField();
//        textField.setBounds(50, 50, 300, 30);
//        this.add(textField);


        menuBar = new JMenuBar();
        fileMenu = new  JMenu("File");

        openFileMenuItem = new JMenuItem("Open");
        openFileMenuItem.addActionListener(this);
        
        exitFileMenuItem = new JMenuItem("Exit");
        exitFileMenuItem.addActionListener(this);        
        
        fileMenu.add(openFileMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitFileMenuItem);
        
        menuBar.add(fileMenu);
        
        this.setJMenuBar(menuBar);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    
    public static void main(String[] args)
    {        
        
        Dorrent dorrent = new Dorrent();
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        
        try
        {
            if(e.getSource() == openFileMenuItem)
            {
                
                if(fileChooser == null)
                    fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("BitTorrent files", "torrent");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(this);
                if (returnVal != JFileChooser.APPROVE_OPTION) 
                    return;
                
                BenObject benObj = BenObject.getBenObject(new FileInputStream(fileChooser.getSelectedFile().getPath()));
                benObj.print(0);
                MetaInfo metaInfo = new MetaInfo((BenDictionary)benObj);
                
                JOptionPane.showMessageDialog(this, metaInfo.toString());  
            }
            else if(e.getSource() == exitFileMenuItem)
            {
                setVisible(false);
                dispose();
            }
            
        }
        catch(Exception ex)
        {
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
//            JOptionPane.showMessageDialog(rootPane, menuBar, null, WIDTH, null);
            JOptionPane.showMessageDialog(this, sw.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
