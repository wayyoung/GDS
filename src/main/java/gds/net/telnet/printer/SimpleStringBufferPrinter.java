/*
 * Created on 2005/2/21
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
public class SimpleStringBufferPrinter implements Printer {
	/* (non-Javadoc)
	 * @see com.glimsoft.net.telnet.printer.Printer#print(java.lang.String)
	 */
	String name="";
	StringBuffer buffer=new StringBuffer();
	public void print(String text) {
		try{
			buffer.append(text);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see com.glimsoft.net.telnet.printer.Printer#setPrinterName(java.lang.String)
	 */
	public void setPrinterName(String name) {
		this.name=name;

	}

	/* (non-Javadoc)
	 * @see com.glimsoft.net.telnet.printer.Printer#getPrinterName()
	 */
	public String getPrinterName() {
		// TODO Auto-generated method stub
		return name;
	}

	/**
	 * @return Returns the buffer.
	 */
	public StringBuffer getBuffer() {
		return buffer;
	}
	public void clear(){
		buffer.delete(0,buffer.length());
	}
}
