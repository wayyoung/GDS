/*
 * Created on 2004/10/5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.vt100;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gds.net.telnet.COMMANDS;
import gds.net.telnet.STATES;
import gds.net.telnet.SubnegotiationManager;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VT100_SubnegotiationManager extends SubnegotiationManager {
	private Logger logger=LoggerFactory.getLogger(getClass());
	ByteArrayOutputStream bous;
	boolean receivedIAC=false;

	public void handle(ByteBuffer bfr) throws Exception {
		
		if(terminal.getState()==STATES.SB){
			startNegotiation();
			terminal.setState(STATES.IN_SB);
		}else{
			if(receivedIAC){
				bfr.mark();
				byte b=(byte)bfr.get();
				if(b==(byte)COMMANDS.SE){
					finishNegotiation();
					terminal.setState(STATES.SE);
					return;	
				}
				bfr.reset();
				receivedIAC=false;
			}else{
				byte b=(byte)bfr.get();
				if(b==(byte)COMMANDS.IAC){
					receivedIAC=true;
					return;		
				}
				bous.write(b);
			}
		}
	}

	/* (non-Javadoc)
	 * @see nionet.telnet.SubnegotiationManager#finishNegotiation()
	 */
	protected void finishNegotiation()throws Exception{
		bous.flush();
		byte[] b=bous.toByteArray();
		bous.close();
		ByteArrayOutputStream temp=new ByteArrayOutputStream();
		
		if(b[0]==(byte)24&&b[1]==(byte)1){
			
			temp.write(COMMANDS.IAC);
			temp.write(COMMANDS.SB);
			temp.write(24);
			temp.write(0);
			temp.write("VT100".getBytes());
			temp.write(COMMANDS.IAC);
			temp.write(COMMANDS.SE);
			temp.flush();
			
//			terminal.getToServerChannel().write(ByteBuffer.wrap(temp.toByteArray()));
			terminal.getToServerPipe().write(ByteBuffer.wrap(temp.toByteArray()));
			temp.close();
		}else{
			
			StringBuffer _stb=new StringBuffer("");
			if(b.length>0){
				_stb.append(b[0]);
			}
			for(int i=1;i<b.length;i++){
				_stb.append(",");
				_stb.append(b[i]);
			}
			logger.error("unknow subnegotiation data:"+_stb.toString());
			throw new Exception();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see nionet.telnet.SubnegotiationManager#startNegotiation()
	 */
	protected void startNegotiation()throws Exception {
		bous=new ByteArrayOutputStream();
		receivedIAC=false;
	}
}
