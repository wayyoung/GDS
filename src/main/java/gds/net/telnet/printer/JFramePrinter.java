/*
 * Created on 2004/7/8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.printer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class JFramePrinter extends JFrame implements Printer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4059317275535436635L;

	String printerName="JFramePrinter";
	/* (non-Javadoc)
	 * @see nionet.telnet.printer.Printer#getPrinterName()
	 */
	public String getPrinterName() {
		// TODO Auto-generated method stub
		return this.printerName;
	}
	/* (non-Javadoc)
	 * @see nionet.telnet.printer.Printer#print(java.lang.String)
	 */
	public void print(String arg0) {
		// TODO Auto-generated method stub
		try{
			this.aTextArea.append(arg0);
		}catch(Exception ee){
			ee.printStackTrace();
		}
	}
	/* (non-Javadoc)
	 * @see nionet.telnet.printer.Printer#setPrinterName(java.lang.String)
	 */
	public void setPrinterName(String arg0) {
		// TODO Auto-generated method stub
		this.printerName=arg0;
	}
	/**
	 * 
	 * @uml.property name="aTextArea"
	 * @uml.associationEnd 
	 * @uml.property name="aTextArea" multiplicity="(1 1)"
	 */
	JTextArea aTextArea = new JTextArea();

	/**
	 * 
	 * @uml.property name="sp"
	 * @uml.associationEnd 
	 * @uml.property name="sp" multiplicity="(1 1)"
	 */
	JScrollPane sp = new JScrollPane();

	
	
	public JFramePrinter(String frameTitle) {
		super(frameTitle);
		setSize(500, 500);
		getContentPane().setLayout(new BorderLayout());
		sp.getViewport().add(aTextArea);
		getContentPane().add(sp, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		

	}
	public JFramePrinter() {
		this("");
	}
	
	public void displayIt() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		
		setLocation((dim.width - abounds.width), 0);
		setVisible(true);
		requestFocus();
	}
	
	public String getPrintedText(){
		return aTextArea.getText();
	}

}
