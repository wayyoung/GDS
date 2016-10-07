/*
 * Created on 2004/10/4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ken
 *
 * The stateManager, subnegotiationManager, optionsManager must be set for the termianl before calling the connect() method
 * 
 */
public class Terminal{
	
	private Logger logger=LoggerFactory.getLogger(getClass().getName());
	/*
	 * channel connected to server
	 */
	SocketChannel sc;
	
	
	ByteBuffer userBuffer=ByteBuffer.allocate(512);
	
	
	
	
	/*ReadChannl for client*/
	TerminalReadChannel readChannel;
	/*WriteChannel for client*/
	TerminalWriteChannel writeChannel;

	NonBlockingPipe  terminalToClientPipe=new NonBlockingPipe();
	NonBlockingPipe  clientToTerminalPipe=new NonBlockingPipe();
	
	
	/*ToServerPipe (for internal module like OptionsHandler, SubnegotiationManager)*/
	NonBlockingPipe toServerPipe=new NonBlockingPipe();
	/**
	 * Server channel ReadBuffer*
	 */
	ByteBuffer readBuffer=ByteBuffer.allocate(512);
	
	SubnegotiationManager subnegotiationManager;
	OptionsManager optionsManager;
	
	int state=STATES.NON;
	
	/*
	 * temporary ByteBuffer used to hold the data to send to the server
	 */
	private ByteBuffer _clientToServerBuffer=ByteBuffer.allocate(512);
	private ByteBuffer _internalToServerBuffer=ByteBuffer.allocate(512);
//	private ByteBuffer _ToServerBuffer=ByteBuffer.allocate(512);
	
	public String type="NONE";
	boolean echo=true;
	
	public void connect(String server,int port)throws IOException{
		
		this.openServerChannel(server,port);
		writeChannel=new TerminalWriteChannel(this);
		readChannel=new TerminalReadChannel(this);
	}
	public boolean isConnected(){
		if(sc!=null&&sc.isConnected()){
			return true;
		}
		return false;
	}
	public TerminalReadChannel  getReadChannel(){
		return readChannel;
	}
	public TerminalWriteChannel  getWriteChannel(){
		return writeChannel;
	}
	
	
	public void disconnect(){
		try{
			sc.socket().close();
		}catch(Exception ee){
			
		}
	}
	
	public void setOptionsManager(OptionsManager optionsManager){
		this.optionsManager=optionsManager;
		optionsManager.init(this);
	}
	public void setSubnegotiationManager(SubnegotiationManager subnegotiationManager){
		this.subnegotiationManager=subnegotiationManager;
		subnegotiationManager.init(this);
	}
	
	public OptionsManager getOptionsManager(){
		return this.optionsManager;
	}
	public SubnegotiationManager getSubnegotiationManager(){
		return this.subnegotiationManager;
	}
	public NonBlockingPipe getToServerPipe(){
		return this.toServerPipe;
	}
	
	public int getState(){
		return this.state;
	}
	public void setState(int state){
		this.state=state;
	}
	public void progress() throws Exception {
		processInternalToServerData();
		processClientToServerData();
		readBuffer.compact();
		if(sc.read(readBuffer)>0){
			readBuffer.flip();
			processReadBuffer(readBuffer);
		}
	}
	
	private void processReadBuffer(ByteBuffer bfr)throws Exception{
		while(bfr.hasRemaining()){
			
				/*CRAP CODE*/
				if(state==STATES.NON){
					
					byte b=(byte)bfr.get();
					logger.debug("receive data:"+b);
					if(b==(byte)COMMANDS.IAC){
						state=STATES.IAC;
					}else{
						terminalToClientPipe.write(ByteBuffer.wrap(new byte[]{b}));
					}
				}else if(state==STATES.IAC){	
					byte b=(byte)bfr.get();
					logger.debug("receive COMMAND:"+b);
					if(b==(byte)COMMANDS.WILL){
						state=STATES.WILL;
					}else if(b==(byte)COMMANDS.WONT){
						state=STATES.WONT;
					}else if(b==(byte)COMMANDS.DO){
						state=STATES.DO;
					}else if(b==(byte)COMMANDS.DONT){
						state=STATES.DONT;
					}else if(b==(byte)COMMANDS.SB){
						state=STATES.SB;
					}else if(b==(byte)COMMANDS.DM){
						state=STATES.NON;
					}else if(b==(byte)COMMANDS.NOP){
						state=STATES.NON;
					}else{
						throw new Exception("unable to handle Command:"+(byte)b);
					}
				}else if(state==STATES.DO||state==STATES.DONT||state==STATES.WILL||state==STATES.WONT){
					byte b=(byte)bfr.get();
					logger.debug("receive OPTION:"+b);
					optionsManager.processInboundOption(b);
					state=STATES.NON;
				}else if(state==STATES.IN_SB){
					subnegotiationManager.handle(bfr);
				}else if(state==STATES.SB){
					logger.debug("start SUBNEGOTIATION");
					subnegotiationManager.handle(bfr);
				}else if(state==STATES.SE){
					logger.debug("end SUBNEGOTIATION");
					state=STATES.NON;
				}else{
					throw new Exception("unable to handle state:"+state);
			}
		}
	}
	
	/**
	 * When this method is called, terminal will send the data from internal to the server.
	 * The internal data is build from the OptionsManager and the SubnegotiationManager.
	 * @throws Exception
	 */
	private void processInternalToServerData()throws Exception{
		_internalToServerBuffer.compact();
		while(toServerPipe.read(_internalToServerBuffer)>0){
			_internalToServerBuffer.flip();
			if(_internalToServerBuffer.hasRemaining()){
				sc.write(_internalToServerBuffer);
			}
		}
	}
	
	/**
	 * When this method is called, terminal will send the data from client to the server.
	 * The default implementaion is to send the data received from client directly. And if 
	 * it is needed to modified the data before it is sent, please implement in this method. 
	 * @throws Exception
	 */
	private  void processClientToServerData()throws Exception{
		_clientToServerBuffer.compact();
		while(clientToTerminalPipe.read(_clientToServerBuffer)>0){
			_clientToServerBuffer.flip();
			
			/*debug code*/
			if(logger.isDebugEnabled()){
				byte[] temp=new byte[(_clientToServerBuffer.remaining())];
				_clientToServerBuffer.get(temp);
				logger.debug("sending user input:"+new String(temp));
				_clientToServerBuffer.flip();
			}
			
			if(_clientToServerBuffer.hasRemaining()){
				sc.write(_clientToServerBuffer);
			}
		}
	}
	
//	private void checkRequiredManager()throws Exception{
//		if(this.optionsManager==null){
//			throw new Exception("please set optionsManager before connect()!!");
//		}
//		if(this.subnegotiationManager==null){
//			throw new Exception("please set subnegotiationManager before connect()!!");
//		}
//	}
	private void openServerChannel(String server,int port)throws IOException{
		sc=SocketChannel.open();
		sc.configureBlocking(false);
		sc.connect(new InetSocketAddress(server,port));
		while(!sc.finishConnect()){
		}
	}
	public void finalize(){
		disconnect();
	}
	
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}	
	/**
	 * @return Returns the echo.
	 */
	public boolean isEcho() {
		return echo;
	}
	/**
	 * @param echo The echo to set.
	 */
	public void setEcho(boolean echo) {
		this.echo = echo;
	}	
}
