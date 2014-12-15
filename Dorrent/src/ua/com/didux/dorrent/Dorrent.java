package ua.com.didux.dorrent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Dorrent extends JFrame implements ActionListener
{
    JButton okButton;
    JTextField textField;
    
    Dorrent()
    {
        this.setTitle("Dorrent");
        this.setSize(600, 600);
        this.setLayout(null);
        
        okButton = new JButton("OK");
        okButton.setBounds(50, 50, 100, 30);
        okButton.addActionListener(this);
        this.add(okButton);
        
        textField = new JTextField();
        textField.setBounds(50, 150, 300, 30);
        this.add(textField);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    
    void run()
    {
        // byte[] bytes = args[0].getBytes();

        // BenObject ben = BenObject.getBenObject(new ByteArrayInputStream(bytes));

        // ben.print(0); 
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
            if(e.getSource() == okButton)
            {
                byte[] bytes = textField.getText().getBytes();
                BenObject bo = BenObject.getBenObject(new ByteArrayInputStream(bytes));
                bo.print(0);
            }
        
        }
        catch(Exception ex)
        {
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            JOptionPane.showMessageDialog(this, sw.toString()); // stack trace as a string
        }
    }
}
