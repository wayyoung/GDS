/*
 * Created on 2004/10/5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public  class OptionsManager {
	private Logger logger=LoggerFactory.getLogger(getClass());
	HashMap<Integer,OptionHandler> optionHandlersMap=new HashMap<Integer,OptionHandler>();
	protected Terminal terminal;
	public void init(Terminal terminal){
		this.terminal=terminal;
		
	}
	
	
	public void setOptionHandler(int optionCode,OptionHandler oh){
		optionHandlersMap.put(new Integer(optionCode),oh);
	}
	
	protected OptionHandler getOptionHandler(int optionCode){
		OptionHandler oh=(OptionHandler)optionHandlersMap.get(new Integer(optionCode));
		if(oh==null){
			oh=new DoNothingOptionHandler(optionCode);
			optionHandlersMap.put(new Integer(optionCode),oh);
		}
		return oh;
	}

	public void  processInboundOption(int optionCode) throws Exception{
		OptionHandler oh=this.getOptionHandler(optionCode);
//		WritableByteChannel wch=terminal.getToServerChannel();
		NonBlockingPipe wch=terminal.getToServerPipe();
		
		if(terminal.getState()==STATES.DO||terminal.getState()==STATES.DONT){
			
			if(oh.getAcceptLocal()){
				wch.write(ByteBuffer.wrap(new byte[]{(byte)COMMANDS.IAC,(byte)COMMANDS.WILL,(byte)optionCode}));
				logger.debug("send WILL OPTION:"+optionCode);
				
			}else{
				wch.write(ByteBuffer.wrap(new byte[]{(byte)COMMANDS.IAC,(byte)COMMANDS.WONT,(byte)optionCode}));
				logger.debug("send WONT OPTION:"+optionCode);
			}
			if(terminal.getState()==STATES.DO){
				oh.handleDORequest(terminal);
			}else{
				oh.handleDONTRequest(terminal);
			}
			
		}else if(terminal.getState()==STATES.WILL||terminal.getState()==STATES.WONT){
			
			if(oh.getAcceptRemote()){
				wch.write(ByteBuffer.wrap(new byte[]{(byte)COMMANDS.IAC,(byte)COMMANDS.DO,(byte)optionCode}));
				logger.debug("send DO OPTION:"+optionCode);
			}else{
				wch.write(ByteBuffer.wrap(new byte[]{(byte)COMMANDS.IAC,(byte)COMMANDS.DONT,(byte)optionCode}));
				logger.debug("send DONT OPTION:"+optionCode);
			}
			if(terminal.getState()==STATES.WILL){
				oh.handleWILLRequest(terminal);
			}else{
				oh.handleWONTRequest(terminal);
			}
		}else{
			logger.error("error!!should not call processOption here(OptionManager)");
		}
		
		if(oh.shouldSubnegotiateLocalInitialize()){
			terminal.getSubnegotiationManager().initalLocal(optionCode);
		}
		if(oh.shouldSubnegotiateRemoteInitialize()){
			terminal.getSubnegotiationManager().initalRemote(optionCode);
		}
		
	}
}
