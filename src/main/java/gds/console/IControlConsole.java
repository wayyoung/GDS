package gds.console;

import java.io.IOException;
import java.rmi.RemoteException;

import gds.GDSError;

public interface IControlConsole extends java.rmi.Remote {

	public String getVersion() throws RemoteException;

	public void setVisible(boolean visible) throws RemoteException;

	public String getName() throws RemoteException;

	public void setName(String name) throws RemoteException;

	public boolean ready() throws RemoteException;

	public void close() throws RemoteException;

	public void setTerminalConnectionString(String str) throws RemoteException;

	public String getTerminalConnectionString() throws RemoteException;

	public boolean startTerminal() throws IOException, RemoteException;

	public void stopTerminal() throws RemoteException;

//	public void resetTerminal() throws RemoteException;

	public boolean isTerminalRunning() throws RemoteException;

	public boolean cmd(String cmdString) throws RemoteException;

	public boolean cmd(String cmdString, String exceptedString) throws RemoteException;

	public boolean cmd(String cmdString, int timeoutSeconds) throws RemoteException;

	public boolean cmd(String cmdString, String exceptedString, int timeoutSeconds) throws RemoteException;

	public boolean validatePrompt() throws RemoteException;

	public void setDefaultTimeout(int seconds) throws RemoteException;

	public void send(String cmdString) throws RemoteException;

	public boolean sendLooping(String cmdString, String exceptedString, int timeoutSeconds) throws RemoteException;

	public boolean expect(String match) throws RemoteException;

	public boolean expect(String match, int timeoutSeconds) throws RemoteException;

	public boolean match(String match) throws RemoteException;

	public boolean match(String match, int timeoutSeconds) throws RemoteException;

	public boolean asyncSendLooping(String cmdString, String exceptedString, int timeoutSeconds) throws RemoteException;

	public boolean asyncSendLoopingWait() throws RemoteException;

	public String getPrompt() throws RemoteException;

	public void setPrompt(String prompt) throws RemoteException;

	public void PS1(String pattern) throws RemoteException;

	public String getResponse() throws RemoteException;

	public void clearResponse() throws RemoteException;

	public void setResponseEnabled(boolean enable) throws RemoteException;

	public boolean isResponseEnabled() throws RemoteException;

	public int getResponseCheckTimeInMillis() throws RemoteException;

	public void setResponseCheckTimeInMillis(int milliSeconds) throws RemoteException;

	public void buttonPress(int btnIndex) throws RemoteException;

	public void buttonRelease(int btnIndex) throws RemoteException;

	public void buttonClick(int btnIndex) throws RemoteException;

	public void setButtonClickPressingTimeInMillis(char btnIndex, int milliseconds) throws RemoteException;

	public void buttonReleaseAll() throws RemoteException;

	public boolean connectArduino() throws IOException, RemoteException;

	public void disconnectArduino() throws RemoteException;

	public void setArduinoConnectionString(String str) throws RemoteException;

	public String getArduinoConnectionString() throws RemoteException;


	public void type(char ch) throws RemoteException;

	public void setFileLogPath(String config) throws RemoteException;
	public String getFileLogPath() throws RemoteException;
	public void setFileLogAppend(boolean append) throws RemoteException;
	public boolean isFileLogAppend() throws RemoteException;
	public void saveFileLog() throws RemoteException;
	public void closeFileLog() throws RemoteException;

	public void addSessionFileLog(String filePath)throws RemoteException;
	public void removeSessionFileLog(String filePath)throws RemoteException;

	public boolean isErrorThrown() throws RemoteException;
	public void setErrorThrown(boolean thrown) throws RemoteException;
	public GDSError getLastError() throws RemoteException;
	public void logCCEvent(String msg) throws RemoteException;
	public void logEvent(String msg) throws RemoteException;
	public void forceExit() throws RemoteException;


	public void executeFScript(int idx)throws RemoteException;

	public int getClearResponseWaitTimeInMillis()throws RemoteException;
	public void setClearResponseWaitTimeInMillis(int clearResponseWaitTimeInMillis)throws RemoteException;
}
