package gds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gds.net.ITelnet;
import gds.net.TFTPServer;
import gds.net.TFTPServer.ServerMode;
import gds.net.TelnetClient;

public class GDS extends java.util.Properties {
	/**
	 *
	 */
	private static final long serialVersionUID = 4730766568916460344L;
	public static final String CRLF = "\r\n";
	public static final String LF = "\n";
	public static final String CR = "\r";

	public static final String GDS_LOGGER = "GDS";

	private static Logger logger = LoggerFactory.getLogger(GDS_LOGGER);

	public static final String GDS_VERSION = "1.0.5";
	public static final String GDS_CONFIG = "GDS_CONFIG";
	public static final String GDS_TESTBED = "GDS_TESTBED";
//	public static final String GDS_WORKSPACE = "GDS_WORKSPACE";

	// public static final String RUNTIME_HOME="gds.runtimeHome";
	public static final String BUILD = "gds.build";
//	public static final String CONFIG="gds.config";
//	public static final String TESTBED = "gds.testbed";
//	public static final String WORKSPACE = "gds.workspace";
//	public static final String TESTBED="gds.hostTestbed";
//	public static final String TARGET_TESTBED="gds.targetTestbed";
	public static final String HOST_IP = "gds.hostIP";
	public static final String TARGET_IP = "gds.targetIP";
	public static final String GATEWAY_IP = "gds.gatewayIP";
	public static final String TELNET_PORT = "gds.telnetPort";
	public static final String TELNET_USER = "gds.telnetUser";
	public static final String TELNET_PASSWORD = "gds.telnetPassword";
	public static final String TFTP_PORT = "gds.tftpPort";
	public static final String TFTP_PATH = "gds.tftpPath";
	public static final String MOCK = "gds.mock";
	public static final String TELNET_PROMPT = "gds.telnetPrompt";

	public static final String GDSCC_PORT = "gds.cc.port";

	// private static final int TELNET_0=0;
	// private static final int TELNET_1=1;
	// private static final int TELNET_2=2;
	// private static final int TELNET_3=3;

	public static String DEFAULT_TELNET_PROMPT = "#";
	ITelnet telnet = null;

	boolean initialized = false;
	boolean postInitialized=false;
	boolean ccInitialized = false;

	TFTPServer tftpServer;
	boolean mock = false;
	/*
	 * boolean systemLogAppend=true; FileAppender executionLogAppender;
	 */

	public boolean isMock() {
		return mock;
	}

	public void setMock(boolean mock) {
		this.mock = mock;
	}

	GDSCC cc = null;

	public void setTestbed(String dir) {
		this.setProperty(GDS_TESTBED, dir);
	}

	//
	public String getTestbed() {
		return this.getProperty(GDS_TESTBED);
	}

	public void setConfig(String filePath) {
		this.setProperty(GDS_CONFIG, filePath);
	}

	//
	public String getConfig() {
		return this.getProperty(GDS_CONFIG);
	}




	public boolean postInit() throws FileNotFoundException, IOException {

		if (!postInitialized) {
			initCC();
			if (getTestbed() == null) {
				this.setTestbed(new File("TESTBED").getCanonicalPath());
				FileUtils.forceMkdir(new File(this.getTestbed()));
			}


			logger.info(GDS_CONFIG + ": " + getConfig());
			logger.info(GDS_TESTBED + ": " + getTestbed());

			if (this.isMock()) {
				logger.info(MOCK + " = " + this.isMock());
			}
			postInitialized=true;
			logger.info("postInit() done");

		}
		return true;
	}

	public boolean preInit() throws FileNotFoundException, IOException {

		if (!initialized) {
			logger.info("GDS_VERSION: " + GDS_VERSION);
			Properties sysConfig = System.getProperties();// new
															// SystemConfiguration();
			if (sysConfig.getProperty(GDS_CONFIG) != null) {
				this.load(new FileInputStream(sysConfig.getProperty(GDS_CONFIG)));
				this.setConfig(new File(sysConfig.getProperty(GDS_CONFIG)).getCanonicalPath());
//				logger.info(
//						"GDS_CONFIG: " + this.getConfig());
			}
			if (sysConfig.getProperty(GDS_TESTBED) != null) {
				this.setTestbed(new File(sysConfig.getProperty(GDS_TESTBED)).getCanonicalPath());
//				logger.info("GDS_TESTBED: " + this.getTestbed());
			}

			for(Object k:sysConfig.keySet()){
				if(k!=null && k.toString().startsWith("gds.") && sysConfig.get(k.toString())!=null){
					this.put(k.toString(),sysConfig.get(k.toString()));
				}
			}

			if (this.getProperty(MOCK) != null && Boolean.parseBoolean(this.getProperty(MOCK))) {
				this.setMock(true);
			}

			initialized = true;
			logger.info("preInit() done");

		}

		return true;

	}

	private void initCC() throws FileNotFoundException, IOException {

		if (cc == null) {

			cc = new GDSCC();
			cc.setProxyPort(Integer.parseInt(this.getProperty(GDSCC_PORT)));
			if (!cc.initRemoteClient()) {
				throw new RuntimeException(
						"Failed to init GDSCC with port:" + Integer.parseInt(this.getProperty(GDSCC_PORT)) + " !!");
			}

		}
	}

	// public void setRuntimeHome(String home){
	// this.setProperty(RUNTIME_HOME, home);
	// }
	//
	// public String getRuntimeHome(){
	// return this.getString(RUNTIME_HOME);
	// }
	//

	// public void setHostTestbed(String hostTestbed){
	// this.setProperty(HOST_TESTBED, hostTestbed);
	// }
	//
	// public String getHostTestbed(){
	// return this.getString(HOST_TESTBED);
	// }
	//
	// public void setTargetTestbed(String targetTestbed){
	// this.setProperty(TARGET_TESTBED, targetTestbed);
	// }
	//
	// public String getTargetTestbed(){
	// return this.getString(TARGET_TESTBED);
	// }

	public void setBuild(String build) {
		this.setProperty(BUILD, build);
	}

	//
	public String getBuild() {
		return this.getProperty(BUILD);
	}

	//
	public void setTFTPPort(int port) {
		this.setProperty(TFTP_PORT, String.valueOf(port));
	}

	public int getTFTPPort() {
		return Integer.parseInt(this.getProperty(TFTP_PORT));
	}

	public void setTFTPPath(String path) {
		this.setProperty(TFTP_PATH, path);
	}

	public String getTFTPPath() {
		return this.getProperty(TFTP_PATH);
	}

	public void setTelnetPort(int port) {
		this.setProperty(TELNET_PORT, String.valueOf(port));
	}

	public void setTelnetUser(String user) {
		this.setProperty(TELNET_USER, user);
	}

	public String getTelnetUser() {
		return this.getProperty(TELNET_USER);
	}

	public int getTelnetPort() {
		return Integer.parseInt(this.getProperty(TELNET_PORT));
	}

	public void setTelnetPassword(String pass) {
		this.setProperty(TELNET_PASSWORD, pass);
	}

	public String getTelnetPassword() {
		return this.getProperty(TELNET_PASSWORD);
	}

	// public void setImagePath(String dir){
	// this.setProperty(IMAGE_PATH, dir);
	// }
	//
	// public String getImagePath(){
	// return this.getString(IMAGE_PATH);
	// }
	//

	public String getTelnetPrompt() {
		return (null == this.getProperty(TELNET_PROMPT)) ? DEFAULT_TELNET_PROMPT : this.getProperty(TELNET_PROMPT);
	}

	public void setTelnetPrompt(String prompt) {
		this.setProperty(TELNET_PROMPT, prompt);
	}

	public String getGatewayIP(String ip) {
		return this.getProperty(GATEWAY_IP);
	}

	public void setGatewayIP(String ip) {
		this.setProperty(GATEWAY_IP, ip);
	}

	public String getHostIP(String ip) {
		return this.getProperty(HOST_IP);
	}

	public String getTargetIP() {
		return this.getProperty(TARGET_IP);
	}

	public void setHostIP(String ip) {
		this.setProperty(HOST_IP, ip);
	}

	public void setTargetIP(String ip) {
		this.setProperty(TARGET_IP, ip);
	}

	public void startTFTPServer() throws IOException {
		this.tftpServer = new TFTPServer(new File(this.getTFTPPath()), new File(this.getTFTPPath()), this.getTFTPPort(),
				ServerMode.GET_ONLY, null, null);
	}

	public void startTFTPServer(String path, int port) throws IOException {
		this.setTFTPPath(path);
		this.setTFTPPort(port);
		this.tftpServer = new TFTPServer(new File(this.getTFTPPath()), new File(this.getTFTPPath()), this.getTFTPPort(),
				ServerMode.GET_ONLY, null, null);
	}

	public GDSCC getCC() {
		return this.cc;
	}

	public void stopTFTPServer() {
		if (this.tftpServer != null) {
			this.tftpServer.shutdown();
		}
		if (this.telnet != null) {
			telnet.disconnect();
		}
		this.tftpServer = null;
		this.telnet = null;
	}

	@Override
	public void finalize() {
		initialized = false;
		stopTFTPServer();
		/*
		 * LoggerContext context= (LoggerContext) LogManager.getContext();
		 * context.getRootLogger().removeAppender(this.executionLogAppender);
		 * context.updateLoggers(); executionLogAppender.stop();
		 * executionLogAppender=null;
		 */
		try {
			super.finalize();
		} catch (Throwable ex) {
			logger.warn("WARN!!", ex);
		}
		logger.info("finalize");
	}

	private static GDS gds = new GDS();

	public static GDS getInstance() {
		return gds;
	}

	public String dumpToString() {
		StringWriter strw = new StringWriter();
		this.list(new PrintWriter(strw));
		return strw.toString();
	}

	private ITelnet newTelnet(String host, int port, String user, String password) throws Exception {

		TelnetClient tlc = new TelnetClient(host, port);
		tlc.setUsername(user);
		tlc.setPassword(password);
		return tlc;

	}

	private ITelnet newTelnet() throws Exception {
		return newTelnet(this.getTargetIP(), this.getTelnetPort(), this.getTelnetUser(), this.getTelnetPassword());
	}

	public ITelnet telnet() throws Exception {

		if (telnet == null) {
			telnet = newTelnet();
			telnet.setPrompt(this.getTelnetPrompt());
			telnet.setFileLogPath("telnet.log");
			telnet.saveFileLog();
			logger.info("telnet created");
		} else {
			logger.info("telnet existed");
		}

		return telnet;
	}

	public String getString(String key){
		return this.getProperty(key);
	}

	public void setString(String key,String value){
		this.setProperty(key, value);
	}

	public int getInt(String key){
		return Integer.parseInt(this.getProperty(key));
	}

	public void setInt(String key,int value){
		this.setProperty(key, String.valueOf(value));
	}

	public long getLong(String key){
		return Long.parseLong(this.getProperty(key));
	}

	public void setLong(String key,long value){
		this.setProperty(key, String.valueOf(value));
	}

	public boolean getBoolean(String key){
		return Boolean.parseBoolean(this.getProperty(key));
	}

	public void setBoolean(String key,boolean value){
		this.setProperty(key, String.valueOf(value));
	}


	public double getDouble(String key){
		return Double.parseDouble(this.getProperty(key));
	}

	public void setDouble(String key,double value){
		this.setProperty(key, String.valueOf(value));
	}


}
