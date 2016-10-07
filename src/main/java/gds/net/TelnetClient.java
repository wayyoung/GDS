/*
 * Created on 2004/10/7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gds.net.telnet.HoldTimeoutException;
import gds.net.telnet.OPTIONS;
import gds.net.telnet.OptionHandler;
import gds.net.telnet.OptionsManager;
import gds.net.telnet.SubnegotiationManager;
import gds.net.telnet.Terminal;
import gds.net.telnet.TerminalReadChannel;
import gds.net.telnet.printer.ASCIICharacterFilter;
import gds.net.telnet.printer.Printer;
import gds.net.telnet.printer.SimpleFilePrinter;
import gds.net.telnet.vt100.VT100_EchoOptionHandler;
import gds.net.telnet.vt100.VT100_SubnegotiationManager;
import gds.net.telnet.vt100.VT100_TerminalTypeOptionHandler;

/**
 * This class is a basic telnet client implementation. Please see the 
 * description for each method and determine how to operate the client 
 * instance. Please see the com.glimsoft.net.telnet.example.VT100ClientSample 
 * for more detail. Remember, the printer you added will not be closed even if 
 * the client is disconnected. You need to handle the printer yourself.
 */
public class TelnetClient implements ITelnet{
	protected Logger logger=LoggerFactory.getLogger(getClass());
	protected ASCIICharacterFilter af=new ASCIICharacterFilter();
	protected boolean ignoreASCIIControls=true;
	
//	Hashtable printers=new Hashtable();
	protected ArrayList<Printer> printers=new ArrayList<Printer>();
	
	protected Terminal terminal;
	protected StringBuffer sbfr=new StringBuffer();

	protected ByteBuffer bbfr=ByteBuffer.allocate(512);
	
	protected TerminalReadChannel rc;
	
	protected String prompt=">";
	protected String username;
	protected String password;
	protected String server;
	protected int port=23;
	
	int responseCheckTimeInMillis=10;
	int defaultTimeout=30;
	int loopSendSleepTimeInMillis=10;
	
	public int getDefaultTimeout() {
		return defaultTimeout;
	}
	
	
	@Override
	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}
	
	@Override
	public int getResponseCheckTimeInMillis() {
		return responseCheckTimeInMillis;
	}
	
	@Override
	public void setResponseCheckTimeInMillis(int responseCheckTimeInMillis) {
		this.responseCheckTimeInMillis = responseCheckTimeInMillis;
	}
	protected boolean looping=false;

	protected Thread holdingThread;
//	HashMap patterns=new HashMap();
	
	
	protected  String lastSend;
	
	public TelnetClient(String server,int port)throws Exception{
		this();
		this.server=server;
		this.port=port;
	}
	public TelnetClient()throws Exception{
		terminal=new Terminal();
		OptionsManager om=new OptionsManager();
		
		om.setOptionHandler(OPTIONS.TERMINAL_TYPE,(OptionHandler)new VT100_TerminalTypeOptionHandler());
		om.setOptionHandler(OPTIONS.ECHO,(OptionHandler)new VT100_EchoOptionHandler());
		SubnegotiationManager sh=new VT100_SubnegotiationManager();
		terminal.setType("VT100");
		terminal.setOptionsManager(om);
		terminal.setSubnegotiationManager(sh);
		
	}
	
	
	@Override
	public void login(String usernameString,String passwordString,String loginSuccessString)throws IOException{
		
		connect();
		expect(usernameString,30);
		send(username);
		if(passwordString!=null){
			expect(passwordString,30);
			if(terminal.isEcho()){
				terminal.setEcho(false);
				send(password);
				terminal.setEcho(true);
			}else{
				send(password);
			}
		}
//		
		expect(loginSuccessString,30);
		
	}
	
	@Override
	public void login(String usernameString,String passwordString)throws IOException{
		this.login(usernameString,passwordString,this.prompt);
	}
	
	
	@Override
	public synchronized boolean cmd(String cmd) throws IOException{
		return cmd(cmd,prompt,defaultTimeout);
	}

	@Override
	public synchronized boolean cmd(String cmd, String expected) throws IOException{
		return cmd(cmd,expected,this.defaultTimeout);
	}
	
	@Override
	public synchronized boolean cmd(String cmd, int timeout) throws IOException{
		return cmd(cmd,prompt,timeout);
	}
	
	@Override
	public synchronized boolean cmd(String cmd,String expected, int timeout)throws IOException {
		String check=(expected==null)?this.prompt:expected;
		if(cmd.endsWith("\n")){
			this.send(cmd);
		}else{
			this.send(cmd+"\n");
		}
		return expect(check,timeout);
		
	}
	
	
	@Override
	public boolean connect()throws IOException{
		if(!terminal.isConnected()){
			terminal.connect(server,port);
			rc=terminal.getReadChannel();
		}
		return true;
	}
	
	@Override
	public void disconnect(){
		try{
			/*stop looping*/
			looping=false;
		
			int _count=0;
			while(rc.read(bbfr)>0&&_count<3){
				_count++;
			}
			try{
				send("exit");
				expect("logout",3);
			}catch(Exception ee){
			}
			terminal.disconnect();
			printers.clear();
			terminal=null;
		}catch(Exception e){
		}
	}

	@Override
	public synchronized boolean match(String match)throws IOException{
		return this.match(match, this.defaultTimeout);
	}
	
	@Override
	public synchronized boolean match(String match,int timeout)throws IOException{
		return this.checkResponse(match, timeout,true);
	}
	
	@Override
	public synchronized boolean expect(String match)throws IOException{
		return this.expect(match, this.defaultTimeout);
	}
	
	@Override
	public synchronized boolean expect(String match,int timeout)throws IOException{
		return this.checkResponse(match, timeout,false);
	}
	
	
	
	@Override
	public synchronized boolean checkResponse(String match,int timeoutSeconds,boolean regex)throws IOException{
		long startTime=System.currentTimeMillis();
		Pattern p=(regex)?Pattern.compile(match):null;
		
		while((System.currentTimeMillis()-startTime)<(timeoutSeconds*1000)){
			retrive();

			if((regex)?(p.matcher(sbfr.toString()).find()):(sbfr.indexOf(match)>=0)){
				return true;
			}
			try{
				Thread.sleep(50);
			}catch(Exception ee){
				
			}
		}
		return false;
		//throw new RuntimeException("Can't receive desired String:"+exitRegPattern+" before timeout.\n Last String:"+sbfr.toString());
	}
	
	@Override
	public synchronized void send(String command)throws IOException{
		lastSend=command;
		sbfr.delete(0,sbfr.length());
		this.printout(command);
		terminal.getWriteChannel().write(ByteBuffer.wrap((command).getBytes()));
	}
	
	private synchronized void hold()throws IOException{
		looping=true;
		while(looping){
			try{
				retrive();
			}catch(IOException ee){
				looping=false;
				throw ee;
			}
			try{
				Thread.sleep(this.responseCheckTimeInMillis);
			}catch(Exception ee){
			}
		}
		looping=false;
	}
//	public synchronized void hold(int timeoutSeconds)throws IOException{
//		long startTime=System.currentTimeMillis();
//		while((System.currentTimeMillis()-startTime)<(timeoutSeconds*1000)){
//			retrive();
//		}
//	}
	public void asynchold(){
		if(holdingThread==null||(!holdingThread.isAlive())){
			holdingThread=new Thread(){
				public void run(){
					try{
						hold();
					}catch(IOException e){
						logger.warn("warning!!",e);
					}
				}};
			holdingThread.start();
		}
	}
	
	public void stopHolding(){
		looping=false;
		try{
			if(holdingThread!=null&&holdingThread.isAlive()){
				holdingThread.join();
			}
		}catch(Exception ee){
			logger.warn("warning!!",ee);
		}
	}
	
	@Override
	public synchronized boolean validatePrompt()throws IOException {
		
		send("\n");
		send("\n");
		return expect(prompt);
	}

	
	@Override
	public synchronized void PS1(String pattern)throws IOException{
		send("export PS1="+pattern+"\n");
		send("\n");
	}
	
	private void retrive()throws IOException{
		if(rc.read(bbfr)>0){
			bbfr.flip();
			byte[] data=new byte[bbfr.remaining()];
			bbfr.get(data);
			bbfr.clear();
			String received=new String(data);
			if(ignoreASCIIControls){
				try{
				received=af.filter(received);
				}catch(Exception ee){
					logger.error("error!!",ee);
				}
			}
			this.printout(received);
			sbfr.append(received);
		}
	}
	
	private void printout(String str){
		Iterator itr=printers.iterator();
		while(itr.hasNext()){
			((Printer)itr.next()).print(str);
		}
	}
	
	public void removePrinter(int index){
		try {
			((PrintStream)printers.remove(index)).close();
		}catch (Exception e) {
		}
	}
	public int addPrinter(Printer printer){
		printers.add(printer);
		return (printers.size()-1);
	}
	
	
	
	public ArrayList getPrinters(){
		return this.printers;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String getPrompt() {
		return prompt;
	}
	
	@Override
	public void setPrompt(String promptString) {
		this.prompt = promptString;
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public int getPort() {
		return port;
	}
	
	/**
	 * @param port The port to set.
	 */
	@Override
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String getHost() {
		return server;
	}
	/**
	 * @param server The server to set.
	 */
	@Override
	public void setHost(String server) {
		this.server = server;
	}
	
	public void prompt() throws IOException, HoldTimeoutException{
		cmd("");
	}
	
	@Override
	public String getResponse(){
		return sbfr.append(getLastReply()).toString();
	}
	
	public String getLastReply(){
		StringBuffer _bf=new StringBuffer();
		try{
			BufferedReader reader=new BufferedReader(new StringReader(sbfr.toString()));
			String line=reader.readLine();
			if(!line.endsWith(lastSend)){
				throw new Exception("the first line of reply is not equals to excepted String("+lastSend+"), line="+line);
			}
			String _content=null;
			while((line=reader.readLine())!=null){
				if(_content!=null){
					_bf.append(_content+"\n");
				}
				_content=line;
			}
		}catch(Exception ee){
			logger.warn("error!!",ee);
		}
		return _bf.toString();
		
		
	}
	/**
	 * @return Returns the ignoreASCIIControls.
	 */
	public boolean isIgnoreASCIIControls() {
		return ignoreASCIIControls;
	}
	/**
	 * @param ignoreASCIIControls The ignoreASCIIControls to set.
	 */
	public void setIgnoreASCIIControls(boolean ignoreASCIIControls) {
		this.ignoreASCIIControls = ignoreASCIIControls;
	}



	String connectionString;
	boolean logFileAppend=false;
	String logFile;
	SimpleFilePrinter logFilePrinter;
	
	@Override
	public void setConnectionString(String str) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getConnectionString() {
		return this.connectionString;
	}


	@Override
	public boolean isRunning() {
		return terminal.isConnected();
	}


	

	@Override
	public void setLogFile(String config) {
		this.logFile=config;
	}


	@Override
	public String getLogFile() {
		return logFile;
	}


	@Override
	public void setLogFileAppend(boolean append) {
		this.logFileAppend=append;
		
	}

	@Override
	public void clearResponse(){
		this.sbfr.delete(0, this.sbfr.length());
	}
	
	
	@Override
	public boolean isLogFileAppend() {
		return this.logFileAppend;
	}


	@Override
	public void saveLog() throws IOException{
		logFilePrinter=new SimpleFilePrinter(this.logFile,this.logFileAppend);
		this.addPrinter(logFilePrinter);
		
	}


	@Override
	public void stopLog() {
		logFilePrinter.flush();
		logFilePrinter.finalize();
		this.getPrinters().remove(logFilePrinter);
		logFilePrinter=null;
		
	}
	
	
}
