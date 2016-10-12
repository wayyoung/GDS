package gds.net;

import java.io.IOException;

public interface ITelnet{
//	public boolean isLocal();
//	public int getProxyPort();
//	public void setProxyPort(int port);
//
	public int getPort();
	public void setPort(int port);

	public String getHost();
	public void setHost(String host);

	public String getUsername();
	public void setUsername(String host);

	public String getPassword();
	public void setPassword(String host);

	public void login(String usernameString,String passwordString,String loginSuccessString)throws IOException;
	public void login(String usernameString,String passwordString)throws IOException;
//
//	public String getName();
//	public void setName(String name);

	public void setConnectionString(String str);
	public String getConnectionString();

	public boolean connect() throws IOException;
	public void disconnect();
	public boolean isRunning();


	public boolean cmd(String cmdString)throws IOException;
	public boolean cmd(String cmdString, String exceptedString)throws IOException;
	public boolean cmd(String cmdString, int timeoutSeconds)throws IOException;
	public boolean cmd(String cmdString, String exceptedString, int timeoutSeconds)throws IOException;

	public boolean validatePrompt()throws IOException;
	public void setDefaultTimeout(int seconds);
	public void send(String cmdString)throws IOException;
	public boolean expect(String match)throws IOException;
	public boolean expect(String match, int timeoutSeconds)throws IOException;
	public boolean match(String match)throws IOException;
	public boolean match(String match, int timeoutSeconds)throws IOException;
	public boolean checkResponse(String match, int timeoutSeconds,boolean regex)throws IOException;

	public String getPrompt();
	public void setPrompt(String prompt);
	public void PS1(String pattern)throws IOException;
	public String getResponse();
	public void clearResponse();

	public int getResponseCheckTimeInMillis();
	public void setResponseCheckTimeInMillis(int milliSeconds);


	public void setFileLogPath(String config);
	public String getFileLogPath();
	public void setFileLogAppend(boolean append);
	public boolean isFileLogAppend();
	public void saveFileLog()throws IOException;
	public void closeFileLog();


//	public boolean ready();
//	public void close();



//	public UserInfo getUserInfoInstace();

//	public void type(char ch);
}
