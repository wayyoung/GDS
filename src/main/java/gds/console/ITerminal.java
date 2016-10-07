package gds.console;

import java.io.IOException;

public interface ITerminal {

	public static final String PREFIX_COM="COM:";
	public static final String PREFIX_SSH="SSH:";
	public static final String PREFIX_TELNET="TELNET:";

	void start()throws IOException;
	void stop();
	void clear();

	void type(char ch);

	boolean isSessionRunning();

	void setConsole(IControlConsole console);

	void setSessionLogWriter(SessionLogWriter slog);
	SessionLogWriter getSessionLogWriter();

	void addResponseBuffer(ResponseBuffer bfr);
	void removeResponseBuffer(ResponseBuffer bfr);

}
