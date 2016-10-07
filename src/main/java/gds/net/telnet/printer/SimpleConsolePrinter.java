/*
 * Created on 2005/2/20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.printer;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleConsolePrinter implements Printer{
	/* (non-Javadoc)
	 * @see nionet.telnet.printer.Printer#getPrinterName()
	 */
	String name="";
	public String getPrinterName() {
		// TODO Auto-generated method stub
		return name;
	}

	/* (non-Javadoc)
	 * @see nionet.telnet.printer.Printer#setPrinterName(java.lang.String)
	 */
	public void setPrinterName(String name) {
		this.name=name;
	}

	/* (non-Javadoc)
	 * @see nionet.telnet.printer.Printer#print(java.lang.String)
	 */
	public void print(String incomingText){
		try{
			System.out.print(incomingText);
		}catch(Exception ee){
			ee.printStackTrace();
		}
	}
}
