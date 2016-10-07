/*
 * Created on 2004/10/6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TerminalWriteChannel {
	Terminal tm;
	public TerminalWriteChannel(Terminal tm){
		this.tm=tm;
	}
	public void write(ByteBuffer bfr)throws IOException{
		try{
			tm.progress();
		}catch(Exception e){
			IOException ee=new IOException("error while doing Terminal.progress()");
			ee.setStackTrace(e.getStackTrace());
			throw ee;
			 
		}
		tm.clientToTerminalPipe.write(bfr);
	}
}
