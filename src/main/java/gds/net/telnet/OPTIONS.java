package gds.net.telnet;

public class OPTIONS {
	/*** The maximum value an option code can have.  This value is 255. ***/
	public static final int MAX_OPTION_VALUE = 255;

	public static int BINARY = 0;

	public static int ECHO = 1;

	public static int PREPARE_TO_RECONNECT = 2;

	public static int SUPPRESS_GO_AHEAD = 3;

	public static int APPROXIMATE_MESSAGE_SIZE = 4;

	public static int STATUS = 5;

	public static int TIMING_MARK = 6;

	public static int REMOTE_CONTROLLED_TRANSMISSION = 7;

	public static int NEGOTIATE_OUTPUT_LINE_WIDTH = 8;

	public static int NEGOTIATE_OUTPUT_PAGE_SIZE = 9;

	public static int NEGOTIATE_CARRIAGE_RETURN = 10;

	public static int NEGOTIATE_HORIZONTAL_TAB_STOP = 11;

	public static int NEGOTIATE_HORIZONTAL_TAB = 12;

	public static int NEGOTIATE_FORMFEED = 13;

	public static int NEGOTIATE_VERTICAL_TAB_STOP = 14;

	public static int NEGOTIATE_VERTICAL_TAB = 15;

	public static int NEGOTIATE_LINEFEED = 16;

	public static int EXTENDED_ASCII = 17;

	public static int FORCE_LOGOUT = 18;

	public static int BYTE_MACRO = 19;

	public static int DATA_ENTRY_TERMINAL = 20;

	public static int SUPDUP = 21;

	public static int SUPDUP_OUTPUT = 22;

	public static int SEND_LOCATION = 23;

	public static int TERMINAL_TYPE = 24;

	public static int END_OF_RECORD = 25;

	public static int TACACS_USER_IDENTIFICATION = 26;

	public static int OUTPUT_MARKING = 27;

	public static int TERMINAL_LOCATION_NUMBER = 28;

	public static int REGIME_3270 = 29;

	public static int X3_PAD = 30;

	public static int WINDOW_SIZE = 31;

	public static int TERMINAL_SPEED = 32;

	public static int REMOTE_FLOW_CONTROL = 33;

	public static int LINEMODE = 34;

	public static int X_DISPLAY_LOCATION = 35;

	public static int OLD_ENVIRONMENT_VARIABLES = 36;

	public static int AUTHENTICATION = 37;

	public static int ENCRYPTION = 38;

	public static int NEW_ENVIRONMENT_VARIABLES = 39;

	public static int EXTENDED_OPTIONS_LIST = 255;
	
	public static int TN3270E = 40;

	
	
	// Cannot be instantiated
	private OPTIONS() {
	}
}