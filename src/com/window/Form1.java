package com.window;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import com.exceptions.ContainerSizeException;
import com.libs.Stego;
import com.utils.FileUtils;
import com.utils.ImageFilter;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Main form class
 * @author Serob.Balyan
 */
public class Form1 extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JFileChooser fcContainer;
	private JFileChooser fcSecret;
	
	private Label containerLable;
	private Label secretLable;
	private Label fullContainerLable;
	
	private File container;
	private File fullContainer;
	private File secret;
	
	public Form1(){
		super(new GridLayout(1,0));
		fcContainer = new JFileChooser();
		fcContainer.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//Add a custom file filter and disable the default
	    //(Accept All) file filter.
		fcContainer.addChoosableFileFilter(new ImageFilter());
		fcContainer.setAcceptAllFileFilterUsed(false);
		fcSecret = new JFileChooser();
		fcSecret.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		Border paneEdge = BorderFactory.createEmptyBorder(0,10,10,10);
		
		//Hide panel
		JPanel hidePane = new JPanel();
		hidePane.setBorder(paneEdge);
		hidePane.setLayout(null);
		
		//buttons for hide panel
		JButton b1 = new JButton("Choose container file ...", createImageIcon("/com/resources/img/Open16.gif"));
		b1.setBounds(35, 70, 180, 30);
		b1.addActionListener(new ContainerOpenListener());
		containerLable = new Label();
		containerLable.setBounds(40, 100, 170, 30);
		
		JButton b2 = new JButton("Choose secret file ...", createImageIcon("/com/resources/img/Open16.gif"));
		b2.setBounds(280, 70, 180, 30);
		b2.addActionListener(new SecretOpenListener());
		secretLable = new Label();
		secretLable.setBounds(285, 100, 170, 30);
		
		JButton b3 = new JButton("Hide data", createImageIcon("/com/resources/img/Save16.gif"));
		b3.setBounds(190, 230, 120, 50);
		b3.addActionListener(new HideListener());
		
		hidePane.add(b1);
		hidePane.add(b2);
		hidePane.add(b3);
		hidePane.add(containerLable);
		hidePane.add(secretLable);
		
		JPanel extractPane = new JPanel();
		extractPane.setBorder(paneEdge);
		extractPane.setLayout(null);
		
		JButton b4 = new JButton("Choose container file ...", createImageIcon("/com/resources/img/Open16.gif"));
		b4.setBounds(160, 70, 180, 30);
		b4.addActionListener(new FullContainerOpenListener());
		fullContainerLable = new Label();
		fullContainerLable.setBounds(165, 100, 170, 30);
		
		JButton b5 = new JButton("Extract data", createImageIcon("/com/resources/img/Save16.gif"));
		b5.setBounds(180, 230, 140, 50);
		b5.addActionListener(new ExtractListener());

		
		extractPane.add(b4);
		extractPane.add(b5);
		extractPane.add(fullContainerLable);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Hiding", null, hidePane, null);
		tabbedPane.addTab("Extracting", null, extractPane, null);
		add(tabbedPane);
		
	}
	
	private class ContainerOpenListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fcContainer.showOpenDialog(Form1.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				container = fcContainer.getSelectedFile(); 
                containerLable.setText(container.getName());
	        } 
		}
		
	}
	
	private class FullContainerOpenListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fcContainer.showOpenDialog(Form1.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				fullContainer = fcContainer.getSelectedFile();
                fullContainerLable.setText(fullContainer.getName());
	        }
		}
		
	}
	
	private class SecretOpenListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fcSecret.showOpenDialog(Form1.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				secret = fcSecret.getSelectedFile();
                secretLable.setText(secret.getName());
	        } 
		}
	}
	
	private class HideListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (container == null || secret == null){
				JOptionPane.showMessageDialog(Form1.this, "You must choose container AND secret files!", "ERROR!", JOptionPane.OK_OPTION);
			} else {
				try {
					byte[] fullContainerBytes = Stego.hide(container, secret);
					String newName = FileUtils.getFilePathOnly(container) + "\\FULL_"  + container.getName();
					FileUtils.byteArrayToFile(newName, fullContainerBytes);
					JOptionPane.showMessageDialog(Form1.this, "Full container now is \'FULL_"  + container.getName() + "\'");
				} catch (ContainerSizeException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(Form1.this, e1.getMessage(),  "ERROR!", JOptionPane.OK_OPTION);
				}
			}
		}
		
	}
	
	private class ExtractListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(fullContainer == null){
				JOptionPane.showMessageDialog(Form1.this, "You must choose full container!", "ERROR!", JOptionPane.OK_OPTION);
			} else {
				try{
					StringBuilder secretsName = new StringBuilder();
					String newPath;
					byte[] secretBytes =Stego.extract(fullContainer, secretsName);
					newPath = FileUtils.getFilePathOnly(fullContainer) + "\\" + secretsName.toString();
					FileUtils.byteArrayToFile(newPath, secretBytes);
					JOptionPane.showMessageDialog(Form1.this, "The extracted file is \'"  + secretsName.toString() + "\'");
				
				} catch (OutOfMemoryError e1){
					JOptionPane.showMessageDialog(Form1.this, "Choose the correct container!", "ERROR!", JOptionPane.OK_OPTION);
				}
			}
			
			
		}
		
	}
	
	 /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Form1.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	
	 /**
     * Create the GUI and show it.  For thread safety, 
     * this method should be invoked from the 
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
    	JFrame frame = new JFrame("Hiding");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        Form1 form1 = new Form1();
        //newContentPane.setLayout(null);
        form1.setOpaque(true); //content panes must be opaque
        frame.setContentPane(form1);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(500, 400);
    }
    
	public static void main(String[] args) {
	        //Schedule a job for the event-dispatching thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
	    }

}
