/*
 * Created on 2004/10/7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.printer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**gem
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleFilePrinter implements Printer {

	/**
	 *
	 */
	File f;
	BufferedWriter fwr;

	public SimpleFilePrinter(String fileName)throws IOException {
		this(fileName,false);
	}

	public SimpleFilePrinter(String fileName,boolean append)throws IOException {
		f=new File(fileName);
		fwr=new BufferedWriter(new FileWriter(f,append));
	}
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
	public void print(String incomingText) {
		try{
			fwr.write(incomingText);

		}catch(Exception ee){
			ee.printStackTrace();
		}
	}

	public void flush(){

		try {
			fwr.flush();
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace();
		}
	}
	public void finalize(){
		try {
			fwr.flush();
			fwr.close();
		} catch (Exception e) {
			// TODO: handle errorThrow
		}
	}

}
