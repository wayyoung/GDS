/*
 * Created on 2004/11/9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.printer;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Printer {
	public void print(String text);
	public void setPrinterName(String name);
	public String getPrinterName();
}
