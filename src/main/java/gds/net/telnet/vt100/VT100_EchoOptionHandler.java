/*
 * Created on 2004/10/5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.vt100;

import gds.net.telnet.OPTIONS;
import gds.net.telnet.OptionHandler;
import gds.net.telnet.Terminal;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VT100_EchoOptionHandler extends OptionHandler {

	
	public VT100_EchoOptionHandler(){
		super(OPTIONS.ECHO,false,true);
	}
	
	
	/* (non-Javadoc)
	 * @see com.glimsoft.net.telnet.OptionHandler#handleWILLRequest(com.glimsoft.net.telnet.Terminal)
	 */
	public void handleWILLRequest(Terminal terminal) {
		terminal.setEcho(false);
	}
}
