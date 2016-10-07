/*
 * Created on 2004/10/5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.vt100;

import gds.net.telnet.OPTIONS;
import gds.net.telnet.OptionHandler;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VT100_TerminalTypeOptionHandler extends OptionHandler {

	
	public VT100_TerminalTypeOptionHandler(){
		super(OPTIONS.TERMINAL_TYPE,true,false);
	}
	public boolean shouldSubnegotiateLocal(){
		return true;
	}
}
