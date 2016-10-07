/*
 * Created on 2004/10/4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet;

import java.nio.ByteBuffer;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public  abstract class SubnegotiationManager {

	
	protected Terminal terminal;
	public abstract void handle(ByteBuffer bfr)throws Exception;
	
	public void init(Terminal terminal){
		this.terminal=terminal;
	}
	
	
	protected abstract void startNegotiation()throws Exception;
		
	protected abstract void finishNegotiation()throws Exception;
	
	
	/**
	 * When Client sent a WILL, remote side sent a DO
	 * this will be called 
	 */
	public void initalLocal(int optionCode)throws Exception{
		
	}
		
	/**
	 * When Client sent a DO, remote side sent a WILL 
	 * this will be called
	 */
	public  void initalRemote(int optionCode)throws Exception{
		
	}
	
	
}
