package gds.net.telnet;

public abstract class OptionHandler {
	/***************************************************************************
	 * Option code
	 **************************************************************************/
	private int optionCode = -1;


	/***************************************************************************
	 * true if the option should be accepted on the local side
	 **************************************************************************/
	private boolean acceptLocal = false;

	/***************************************************************************
	 * true if the option should be accepted on the remote side
	 **************************************************************************/
	private boolean acceptRemote = false;

//	/***************************************************************************
//	 * true if the option is active on the local side
//	 **************************************************************************/
//	private boolean doFlag = false;
//
//	/***************************************************************************
//	 * true if the option is active on the remote side
//	 **************************************************************************/
//	private boolean willFlag = false;

	/***************************************************************************
	 * Constructor for the TelnetOptionHandler. Allows defining desired initial
	 * setting for local/remote activation of this option and behaviour in case
	 * a local/remote activation request for this option is received.
	 * <p>
	 * 
	 * @param optcode -
	 *            Option code.
	 * @param acceptlocal -
	 *            if set to true, any DO request is accepted.
	 * @param acceptremote -
	 *            if set to true, any WILL request is accepted.
	 **************************************************************************/
	public OptionHandler(int optcode, boolean acceptlocal, boolean acceptremote) {
		optionCode = optcode;
		
		acceptLocal = acceptlocal;
		acceptRemote = acceptremote;
	}

	/***************************************************************************
	 * Returns the option code for this option.
	 * <p>
	 * 
	 * @return Option code.
	 **************************************************************************/
	public int getOptionCode() {
		return (optionCode);
	}

	/***************************************************************************
	 * Returns a boolean indicating whether to accept a DO request coming from
	 * the other end.
	 * <p>
	 * 
	 * @return true if a DO request shall be accepted.
	 **************************************************************************/
	public boolean getAcceptLocal() {
		return (acceptLocal);
	}

	/***************************************************************************
	 * Returns a boolean indicating whether to accept a WILL request coming from
	 * the other end.
	 * <p>
	 * 
	 * @return true if a WILL request shall be accepted.
	 **************************************************************************/
	public boolean getAcceptRemote() {
		return (acceptRemote);
	}

	/***************************************************************************
	 * Set behaviour of the option for DO requests coming from the other end.
	 * <p>
	 * 
	 * @param accept -
	 *            if true, subsequent DO requests will be accepted.
	 **************************************************************************/
	public void setAcceptLocal(boolean accept) {
		acceptLocal = accept;
	}

	/***************************************************************************
	 * Set behaviour of the option for WILL requests coming from the other end.
	 * <p>
	 * 
	 * @param accept -
	 *            if true, subsequent WILL requests will be accepted.
	 **************************************************************************/
	public void setAcceptRemote(boolean accept) {
		acceptRemote = accept;
	}

	


	 /*
	 * 1) Client sent a WILL, remote side sent a DO
	 * 2) Need to be implemented
	 */
	public boolean shouldSubnegotiateLocalInitialize(){
		
		return false;
	}

	/*
	 * 1) Client sent a DO, remote side sent a WILL
	 * 2) Need to be implemented
	 */
	public boolean shouldSubnegotiateRemoteInitialize(){
		return false;
	}


//	/***************************************************************************
//	 * Returns a boolean indicating whether a WILL request sent to the other
//	 * side has been acknowledged.
//	 * <p>
//	 * 
//	 * @return true if a WILL sent to the other side has been acknowledged.
//	 **************************************************************************/
//	public boolean getWill() {
//		return willFlag;
//	}
//
//	/***************************************************************************
//	 * Tells this option whether a WILL request sent to the other side has been
//	 * acknowledged (invoked by TelnetClient).
//	 * <p>
//	 * 
//	 * @param state -
//	 *            if true, a WILL request has been acknowledged.
//	 **************************************************************************/
//	public void setWill(boolean state) {
//		willFlag = state;
//	}
//
//	/***************************************************************************
//	 * Returns a boolean indicating whether a DO request sent to the other side
//	 * has been acknowledged.
//	 * <p>
//	 * 
//	 * @return true if a DO sent to the other side has been acknowledged.
//	 **************************************************************************/
//	public boolean getDo() {
//		return doFlag;
//	}
//
//	/***************************************************************************
//	 * Tells this option whether a DO request sent to the other side has been
//	 * acknowledged (invoked by TelnetClient).
//	 * <p>
//	 * 
//	 * @param state -
//	 *            if true, a DO request has been acknowledged.
//	 **************************************************************************/
//	public void setDo(boolean state) {
//		doFlag = state;
//	}
	
	public void handleDORequest(Terminal terminal){
		
	}
	public void handleWILLRequest(Terminal terminal){
		
	}
	public void handleWONTRequest(Terminal terminal){
		
	}
	public void handleDONTRequest(Terminal terminal){
		
	}
	
}