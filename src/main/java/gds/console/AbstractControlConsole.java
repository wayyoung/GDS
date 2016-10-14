package gds.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.script.ScriptEngineManager;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wittams.gritty.swing.GrittyTerminal;

import gds.GDS;
import gds.GDSError;
import gds.serial.ISerialPortConnection;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public abstract class AbstractControlConsole implements IControlConsole{
	private static final Logger logger = LoggerFactory.getLogger(AbstractControlConsole.class.getName());
	private static final Logger gdsLogger = LoggerFactory.getLogger(GDS.GDS_LOGGER);


	protected GrittyTerminal term;
	protected String name="GDSCC";
	protected String terminalConnectionString;


	protected AbstractControlConsole() throws RemoteException {
		super();
	}

	protected boolean terminalInterrupted=false;


	protected SessionLogWriter sessionLogWriter=new SessionLogWriter();


	@Override
	public void logCCEvent(String msg){
		logEvent(">>>> #GDSCC: "+msg);
	}

	@Override
	public void logEvent(String msg){
		this.sessionLogWriter.write("\n"+msg+"\n");
		gdsLogger.info(msg);
	}
	/**
	 *
	 */
	public static final int GEM_BTN_SIZE=4;
//	private static final int ARDUINO_OFFSET=5;


	protected int defaultTimeout=30;
	protected int checkTimeInMillis=5;
	protected int loopSendSleepTimeInMillis=2;
	protected int clearResponseWaitTimeInMillis=50;

	@Override
	public int getClearResponseWaitTimeInMillis() {
		return clearResponseWaitTimeInMillis;
	}

	@Override
	public void setClearResponseWaitTimeInMillis(int clearResponseWaitTimeInMillis) {
		this.clearResponseWaitTimeInMillis = clearResponseWaitTimeInMillis;
	}

	protected String arduinoConnectionString;

	protected ISerialPortConnection arduinoConnection=null;


	protected int btnStatus[]=new int[GEM_BTN_SIZE];

	@Override
	public boolean isErrorThrown() {
		return errorThrown;
	}

	@Override
	public void setErrorThrown(boolean errorThrown) {
		this.errorThrown = errorThrown;
	}

	protected boolean errorThrown =false;

	@Override
	public GDSError getLastError() {
		return lastError;
	}
//
	protected GDSError lastError;


	protected int[] clickPressingTimeInMillis={300,300,300,300,300,300,300,300};

	protected String prompt="]##";


	protected ResponseBuffer resp=new ResponseBuffer();

//	public String[]  fsScripts=new String[8];
	public int[] fsFlags=new int[8];
	public GroovyShell[] scriptEngines=new GroovyShell[8];
	@SuppressWarnings("unchecked")
	public SwingWorker<String,Void>[] scriptWorkers=new SwingWorker[8];

	protected  ScriptEngineManager manager = new ScriptEngineManager();

	//flag to enable the cmd echo check when using cmd() API
	boolean cmdEchoCheck=false;

	abstract public void updateFunctionScriptDialog();

	public boolean checkInterrupted(){
		if(terminalInterrupted){
			terminalInterrupted=false;
//			throw new RuntimeConsoleInterruptedException();
			return true;
		}
		return false;
	}

	/*  Function Script
	 */
	public final Properties fsScripts=new Properties();
	public void loadFunctionScriptProperties(){
		File propF=new File(System.getProperty("user.home")+"/.gds.fscript.prop");
		if(propF.exists()){
			try {
				fsScripts.load(new FileReader(propF));
			} catch (Exception e) {
				logger.error("ERROR!!", e);
			}
		}
	}

	public void saveFunctionScriptProperties(){
		File propF=new File(System.getProperty("user.home")+"/.gds.fscript.prop");

		try {
			fsScripts.store(new FileWriter(propF),"");
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}

	}

	@Override
	public void executeFScript(final int idx){

		if((fsFlags[idx]==0) && fsScripts.getProperty("F"+(idx+1))!=null){
			fsFlags[idx] = 1;
			 scriptWorkers[idx]=new SwingWorker<String, Void>() {
				 @Override
			       protected String doInBackground() throws Exception {
					 	BufferedReader bfr=null;
					 	int lineCount=0;
						File f=new File(fsScripts.getProperty("F"+(idx+1)));
						try {

							if (scriptEngines[idx] == null) {
								Binding binding = new Binding();
								binding.setVariable("cc", AbstractControlConsole.this);
								binding.setVariable("worker", this);
								scriptEngines[idx]= new GroovyShell(binding);
							}
//							scriptEngines[idx].put("worker", this);
//							scriptEngines[idx].put(ScriptEngine.FILENAME,fsScripts.getProperty("F"+(idx+1)));
							String codeLine=null;
							bfr=new BufferedReader(new FileReader(f));
							while((codeLine=bfr.readLine())!=null){
								scriptEngines[idx].evaluate(codeLine);
								lineCount++;

								if(this.isCancelled())break;
							}
						} catch (Exception e) {
							logger.error("ERROR!! FScript["+idx+"] eval failed @"+lineCount+" of "+f.getCanonicalPath()+". ",e);

						}
						if(fsFlags[idx]==2){
							logger.info("FScript["+idx+"] cancelled, stopping execution");
							try {
								AbstractControlConsole.this.ready();
							} catch (RemoteException e1) {

							}
						}
						fsFlags[idx] = 0;
						scriptWorkers[idx]=null;
						scriptEngines[idx] = null;
						AbstractControlConsole.this.updateFunctionScriptDialog();
						return null;

			       }

			};
			scriptWorkers[idx].execute();
		}
	}



	@Override
	public String getTerminalConnectionString() {
		return this.terminalConnectionString;
	}

	@Override
	public void setTerminalConnectionString(String terminalConnectionString) {
		this.terminalConnectionString = terminalConnectionString;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public boolean isErrorThrow() {
		return errorThrown;
	}

	public void setErrorThrow(boolean errorThrow) {
		this.errorThrown = errorThrow;
	}



	public ResponseBuffer getCommandResponse() {
		return resp;
	}



	private String _getLineEnd(){
		return GDS.LF;
	}

	private void _send(String cmd){
		int length = cmd.length();
	    for (int i = 0; i < length; i++) {
	    	char character = cmd.charAt(i);
	    	try{
	    		type(character);
	    	}catch(Exception ex){
	    		logger.error("ERROR!!",ex);
	    	}
	    }
	}

	@Override
	public int getResponseCheckTimeInMillis() {
		return checkTimeInMillis;
	}


	@Override
	public void setResponseCheckTimeInMillis(int checkTimeInMillis) {
		this.checkTimeInMillis = checkTimeInMillis;
	}

	@Override
	public synchronized boolean cmd(String cmd) throws RemoteException{
		return cmd(cmd,prompt,defaultTimeout);
	}

	@Override
	public synchronized boolean cmd(String cmd, String expected) throws RemoteException{
		return cmd(cmd,expected,this.defaultTimeout);
	}

	@Override
	public synchronized boolean cmd(String cmd, int timeout) throws RemoteException{
		return cmd(cmd,prompt,timeout);
	}

	@Override
	public synchronized boolean cmd(String cmd,String expected, int timeout)throws RemoteException {
		String check=(expected==null)?this.prompt:expected;
//		this.resp.setEnabled(true);
		this.ready();
		this.resp.clear();

		cmd=cmd.replace("\n","").replace("\r","");


		this._send(cmd+_getLineEnd());
		if(cmdEchoCheck){
			checkResponse(cmd, timeout,false);
			this.resp.delete(0, this.resp.indexOf(cmd));
		}
		boolean res=checkResponse(check, timeout,false);
//		this.resp.setEnabled(false);
		return res;

	}

	@Override
	public synchronized boolean validatePrompt()throws RemoteException {

		send(_getLineEnd());
		send(_getLineEnd());
		resp.clear();
		send(_getLineEnd());
		resp.clear();
		send(_getLineEnd());
		return expect(prompt);
	}

	@Override
	public void setDefaultTimeout(int seconds) {
		this.defaultTimeout=seconds;
	}

	@Override
	public synchronized void send(String cmd) throws RemoteException {

//			resp.setEnabled(true);
			this.ready();
			_send(cmd);
//			int length = cmd.length();
//		    for (int i = 0; i < length; i++) {
//		    	char character = cmd.charAt(i);
//		        type(character);
//		    }
	}

	boolean busy=false;
	boolean sendLoopingAsyncRet=false;
	boolean sendLoopingAsyncThreadRunning=false;

	Thread sendLoopingAsyncThread=null;

	@Override
	public synchronized boolean asyncSendLooping(String cmdString, String exceptedString, int timeoutSeconds)throws RemoteException {
		logCCEvent("asyncSendLooping(exceptedString="+exceptedString+") START");
		if(sendLoopingAsyncThread!=null&&sendLoopingAsyncThreadRunning){
			return false;
		}
		final String fcmdString=cmdString;
		final String fexceptedString=exceptedString;
		final int ftimeoutSeconds=timeoutSeconds;
		lastError =null;
		sendLoopingAsyncRet=false;
		sendLoopingAsyncThread=new Thread(){
			@Override
			public void run(){

				logCCEvent("asyncSendLooping(exceptedString="+exceptedString+") Thread START");


//				sendLoopingAsyncThreadRunning=true;
				try {
					sendLoopingAsyncRet=AbstractControlConsole.this.sync_sendLooping(fcmdString,fexceptedString,ftimeoutSeconds);
				} catch (GDSError e){

				}
				logCCEvent("asyncSendLooping(exceptedString="+exceptedString+") Thread END");
				sendLoopingAsyncThreadRunning=false;
			}
		};
		sendLoopingAsyncThreadRunning=true;
		sendLoopingAsyncThread.start();

		return true;
	}

	@Override
	public boolean asyncSendLoopingWait()throws RemoteException {
		if(sendLoopingAsyncThread==null){
			return false;
		}

		try {
			sendLoopingAsyncThread.join();
		} catch (InterruptedException e) {
		}
		if(!sendLoopingAsyncRet && lastError!=null &&errorThrown){
			throw lastError;
		}

		return sendLoopingAsyncRet;
	}

	@Override
	public synchronized boolean sendLooping(String cmdString, String exceptedString, int timeoutSeconds)throws RemoteException {
		boolean ret=false;

		logCCEvent("sendLooping(exceptedString="+exceptedString+") START");
		ret=sync_sendLooping(cmdString, exceptedString, timeoutSeconds);
		logCCEvent("sendLooping(exceptedString="+exceptedString+") END");
		return ret;
	}


	private synchronized boolean sync_sendLooping(String cmdString, String exceptedString, int timeoutSeconds) {

//		if(busy){
//			lastError =new GDSError("sendLooping is BUSY!!");
//			logCCEvent("sendLooping(exceptedString="+exceptedString+") BUSY!!");
//			return false;
//		}
		lastError =null;
//		this.resp.setEnabled(true);
		long st=System.currentTimeMillis();
		boolean ret=false;
		busy=true;
		while((System.currentTimeMillis()-st)<(timeoutSeconds*1000)){
			_send(cmdString);
			try{
				wait(loopSendSleepTimeInMillis);
			}catch(Exception ee){

			}
			if(resp.contains(exceptedString)){
				ret=true;
				break;
			}
			if(checkInterrupted()){
				break;
			}
		}
		busy=false;
//		this.resp.setEnabled(false);

		if(!ret){
		   lastError =new GDSError("Console failed to except string:\""+exceptedString+"\",  Buffered Response:\n"+this.resp.toString()+"\n>>\n");
			if(errorThrown) {
				throw lastError;
			}
		}

		return ret;

	}

	@Override
	public synchronized boolean match(String match,int timeout) {
		boolean res=checkResponse(match, timeout,true);
//		resp.setEnabled(false);
		return res;
	}

	@Override
	public synchronized boolean match(String match) {
		return match(match, this.defaultTimeout);
	}



	@Override
	public synchronized boolean expect(String match,int timeout) {
		boolean res=checkResponse(match, timeout,false);
//		this.resp.setEnabled(false);
		return res;
	}

	@Override
	public synchronized boolean expect(String match) {
		return expect(match, this.defaultTimeout);
	}


//	@Override
	private synchronized boolean checkResponse(String match, int timeout,boolean regex) {

		boolean ret=false;
		lastError =null;
//			resp.setEnabled(true);
		long st=System.currentTimeMillis();
		busy=true;
		Pattern p=(regex)?Pattern.compile(match):null;
		while((System.currentTimeMillis()-st)<(timeout*1000)){

			if((regex)?(p.matcher(resp.toString()).find()):(resp.contains(match))){
				ret=true;
//					if(action!=null)
//					{
//						action.run();
//					}
				break;
			}


			if(checkTimeInMillis>0){
				try {
					Thread.sleep(checkTimeInMillis);
				} catch (InterruptedException e) {
//					logger.error("ERROR!!", e);
				}
			}
			if(checkInterrupted()){
				break;
			}

		}
		busy=false;
		if(!ret){
			lastError =new GDSError("Console failed to except string:\""+match+"\", timeout="+timeout+". Buffered Response:\n"+resp.toString()+"\n>>\n");
			if(errorThrown) {
				throw lastError;
			}
		}

		return ret;
}

	@Override
	public String getPrompt() throws RemoteException{
		return prompt;
	}

	@Override
	public synchronized void setPrompt(String prompt) throws RemoteException{
		this.prompt=prompt;
	}


	@Override
	public synchronized void PS1(String pattern) throws RemoteException{
		send("export PS1="+pattern+"\n");
		send("\n");
	}

	@Override
	public String getResponse(){
		return this.resp.toString();
	}

	@Override
	public void clearResponse(){
		logCCEvent("clearResponse()");
		if(this.resp!=null){
			if(clearResponseWaitTimeInMillis>0){
				try {
					Thread.sleep(clearResponseWaitTimeInMillis);
				} catch (InterruptedException e) {

				}
			}
			this.resp.clear();
		}
	}


	private void sendFirmataDataForButton() throws IOException{
		int val1=0;
		for(int i=0;i<GEM_BTN_SIZE;i++){
			val1 += (btnStatus[i]<<(2+i));
		}

//		byte[] resp=new byte[128];
//		while(this.arduinoConnection.getInputStream().read(resp)>0);
		this.arduinoConnection.getOutputStream().write(new byte[]{(byte)0x90,(byte)val1,(byte)0x00});
		this.arduinoConnection.getOutputStream().flush();
//		this.arduinoConnection.getInputStream().read(resp);
//		while(this.arduinoConnection.getInputStream().read(resp)>0);
//		try {
//			Thread.sleep(10);
//		} catch (InterruptedException e) {
//
//		}
//		int len=this.arduinoConnection.getInputStream().read(resp);


	}


	@Override
	public void buttonPress(int btnIndex) {
		logCCEvent("buttonPress("+btnIndex+")");
		try {
			if(btnIndex>GEM_BTN_SIZE)return;
			if(null==this.arduinoConnection && !this.arduinoConnection.isOpened()) return;
			btnStatus[btnIndex]=1;
			sendFirmataDataForButton();
//			this.firmata.getPin(btnIndex+2).setValue(1);

//			this.firmata.sendMessage(new SetDigitalPinValueMessage(btnIndex+2,DigitalPinValue.HIGH ));
//			this.arduinoConnection.getOutputStream().write((byte)(btnIndex+1+0x30));
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}

	}

	@Override
	public void buttonRelease(int btnIndex) {
		logCCEvent("buttonRelease("+btnIndex+")");
		try {
			if(btnIndex>GEM_BTN_SIZE)return;
			if(null==this.arduinoConnection) return;
			btnStatus[btnIndex]=0;
			sendFirmataDataForButton();
//			this.firmata.getPin(btnIndex+2).setValue(0);
//			this.firmata.sendMessage(new SetDigitalPinValueMessage(btnIndex+2,DigitalPinValue.LOW ));
//			this.arduinoConnection.getOutputStream().write((byte)(btnIndex+1+ARDUINO_OFFSET+0x30));
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}

	}

	@Override
	public void buttonClick(int btnIndex) {
		logCCEvent("buttonClick("+btnIndex+")");

		try{

			if(btnIndex>GEM_BTN_SIZE)return;
			if(null==this.arduinoConnection || !this.arduinoConnection.isOpened()) return;
//			this.arduinoConnection.getOutputStream().write((byte)(btnIndex+1+0x30));

			btnStatus[btnIndex]=1;
			sendFirmataDataForButton();
//			this.firmata.getPin(btnIndex+2).setValue(1);
//			this.firmata.sendMessage(new SetDigitalPinValueMessage(btnIndex+2,DigitalPinValue.HIGH ));
			Thread.sleep(clickPressingTimeInMillis[btnIndex]);

//			this.arduinoConnection.getOutputStream().write((byte)(btnIndex+1+ARDUINO_OFFSET+0x30));
			btnStatus[btnIndex]=0;
			sendFirmataDataForButton();
//			this.firmata.getPin(btnIndex+2).setValue(0);
//			this.firmata.sendMessage(new SetDigitalPinValueMessage(btnIndex+2,DigitalPinValue.LOW ));

		}catch(Exception ee){
			logger.warn("WARNING!!",ee);
		}
	}

	@Override
	public void setButtonClickPressingTimeInMillis(char btnIndex,int milliseconds) {
		this.clickPressingTimeInMillis[btnIndex]=milliseconds;

	}
	@Override
	public void buttonReleaseAll() {
//		logCCEvent("buttonReleaseAll()");
		try {
			for(int i=0;i<GEM_BTN_SIZE;i++){
				btnStatus[i]=0;
			}

			for(int i=0;i<GEM_BTN_SIZE;i++){
				this.buttonRelease(i);
			}
//			this.arduinoConnection.getOutputStream().write((byte)(0x30));
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}
	}



	@Override
	public void setArduinoConnectionString(String str) {
		this.arduinoConnectionString=str;

	}

	@Override
	public String getArduinoConnectionString() {
		return this.arduinoConnectionString;
	}





//	public ITerminalConnection getConnection() {
//		return terminalConnection;
//	}
//	public void setConnection(ITerminalConnection connection) {
//		this.terminalConnection = connection;
//	}
//
//	protected ITerminalConnection createSerialConnection(String constr){
//
//		String str=constr.trim();
//        ITerminalConnection siocon=null;
//		if(str.startsWith("SSH:")){
//			return null;
//		}else{
//			int idx=-1;
//			if((idx=str.indexOf(":"))>=0){
//				str=str.substring(idx+1);
//			}
//
//			siocon=SerialConnection.newInstance();
//			siocon.setupConnectionString(constr);
////			siocon.setProperty(ITerminalConnection.KEY_INPUT_RECV_TIMEOUT_MS, "1");
//			return siocon;
//		}
//	}

	@Override
	public boolean ready()throws RemoteException{
		long st=System.currentTimeMillis();
		while(busy){
			this.terminalInterrupted=true;
			if((System.currentTimeMillis()-st) > 3000){
				return false;
			}
			try {
				Thread.sleep(checkTimeInMillis);
			} catch (InterruptedException e) {
			}

		}
		return true;
	}

	@Override
	public void setResponseEnabled(boolean enable) throws RemoteException {
		this.resp.setEnabled(enable);

	}

	@Override
	public boolean isResponseEnabled() throws RemoteException {
		return this.resp.isEnabled();
	}

	@Override
	public void addSessionFileLog(String filePath) throws RemoteException {
		try{


			if(!this.sessionLogWriter.contains(filePath)){
				File seLogFile=new File(filePath);
				FileUtils.forceMkdir(seLogFile.getParentFile());
				logCCEvent("addSessionFileLog('"+filePath+"')");
				this.sessionLogWriter.put(filePath, new FileWriter(seLogFile));
			}

		}catch(IOException ex){
			throw new RemoteException("ERROR!!",ex);
		}

	}

	@Override
	public void removeSessionFileLog(String filePath) throws RemoteException {
		try{
			Writer wr=null;
			if((wr=this.sessionLogWriter.get(filePath))!=null){
				this.sessionLogWriter.remove(filePath);
				wr.flush();
				wr.close();
				wr=null;
				logCCEvent("removeSessionFileLog('"+filePath+"')");
			}

		}catch(IOException ex){
			throw new RemoteException("ERROR!!",ex);
		}

	}



}
