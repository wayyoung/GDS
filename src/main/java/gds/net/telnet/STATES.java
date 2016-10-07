/*
 * Created on 2004/10/4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class STATES {
	private static Logger logger=LoggerFactory.getLogger(STATES.class);
	public static final int 
	  NON = 0,
	  IAC = 1, 
	  WILL = 2,
	  WONT = 3,
	  DO = 4, 
	  DONT = 5,
	  SB = 6, 
	  SE = 7, 
//	  CR = 8, 
//	  IAC_SB = 9,
	  IN_SB=10;
	public static int getStateCode(String stateName){
		try{
			return STATES.class.getDeclaredField(stateName).getInt(null);
		}catch(Exception ee){
			logger.warn("ERROR!!",ee);
			return -1;
		}
	}

}
