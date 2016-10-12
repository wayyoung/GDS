/**
 * 	Gao Da Shung
 *  WTF...
 */
package gds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gds.console.IControlConsole;
import gds.swing.SwingControlConsole;

//import gem.console.swing.GemConsole;

/**
 * @author Administrator
 *
 */
public class GDSCC {

	private static Logger logger = LoggerFactory.getLogger(GDS.GDS_LOGGER);
	public static final int DEFAULT_PROXY_PORT = 5566;
	public static final String CONFIGURATION_FILE = "gdscc.configurationFile";
	public static final String PROXY_PORT = "gdscc.proxyPort";
	public static final String TERMINAL_CONFIG = "gdscc.terminalConfig";
	public static final String ARDUINO_CONFIG = "gdscc.arduinoConfig";

	static String GDSCC_ID;
	public String GDSCC_PROMPT = "'GDS## '";

	public static final String CONSOLE_CTRL_C = "\u0003";
	public static final String CONSOLE_CTRL_Z = "\u0026";

	Properties ccConfiguration = new Properties();

	IControlConsole gdsConsole;
	int proxyPort = DEFAULT_PROXY_PORT;

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	boolean remote = true;

	FileLock fLock;
	java.rmi.registry.Registry proxyRegistry;
	String configFile;

	private void ERROR(Throwable ee) {
		logger.error("ERROR!!", ee);
	}

	public String getConfigurationFilePath() {
		return configFile;
	}

	public void setConfigurationFilePath(String f) {
		configFile = f;
	}

	public int getConsoleProxyPort() {
		return proxyPort;
	}

	public void setConfig(String key, String val) {
		ccConfiguration.setProperty(key, val);
	}

	public void setTerminalConfig(String config) {
		setConfig(TERMINAL_CONFIG, config);
	}

	public void setArduinoConfig(String config) {
		setConfig(ARDUINO_CONFIG, config);
	}

	public boolean loadConfigurationFromFile() throws IOException {
		File f = new File(configFile);
		// if(configFile==null){
		// configFile= System.getenv(KEY_GEM_CONFIG_FILE);
		// if(configFile==null){
		// File f=new File("gdscc.config");
		// if(f.exists()){
		// configFile=f.getCanonicalPath();
		// }else{
		// String cf="gdscc.config";
		// if(!new File(cf).exists()){
		// logger.info("No GDS ControlCenter configuration file found.");
		// return false;
		// }
		// configFile=cf;
		//
		// }
		// }
		// }

		logger.info("Using '" + f.getCanonicalPath() + "' as GDSCC configuration file");

		ccConfiguration.clear();

		try {
			ccConfiguration.load(new FileInputStream(f));
			if (ccConfiguration.containsKey(PROXY_PORT)) {
				proxyPort = Integer.parseInt(ccConfiguration.getProperty(PROXY_PORT));
			}
		} catch (Exception ee) {
			throw new RuntimeException(ee);
		}

		return true;

	}

	static Thread mainThread;// = Thread.currentThread();
	// static Lookup nameLookup;// = Simon.createNameLookup("127.0.0.1",
	// proxyPort);

	/*
	 * (non-Javadoc)
	 *
	 * @see gds.A#GDSCCID()
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see gds.IGDSCC#GDSCCID()
	 */

	public String GDSCCID() {
		return "GDSCC_" + proxyPort;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gds.A#initRemoteClient()
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see gds.IGDSCC#initRemoteClient()
	 */

	public boolean initRemoteClient() {
		logger.info("GDS_VERSION: " + GDS.GDS_VERSION + " @ " + System.getProperty("user.dir"));
		boolean res = true;
		gdsConsole = null;
		remote = true;

		try {
			proxyRegistry = LocateRegistry.getRegistry(proxyPort);
			gdsConsole = (IControlConsole) proxyRegistry.lookup(GDSCCID());
			if (!gdsConsole.getVersion().equals(GDS.GDS_VERSION)) {
				throw new RuntimeException("GDS version is mismatched!! Remote GDSCC:" + gdsConsole.getVersion()
						+ ". Local GDSCC:" + GDS.GDS_VERSION + " !!");
			}
			logger.info("GDSCC is bind to remote ID: " + GDSCCID());
			logger.info("GDSCC init successfully.");
		} catch (Exception ee) {
			logger.error("GDSCC init failed.", ee);
			res = false;
		}

		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gds.A#getVersion()
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see gds.IGDSCC#getVersion()
	 */

	public String getVersion() throws RemoteException {

		return this.getConsole().getVersion();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gds.A#init()
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see gds.IGDSCC#init()
	 */

	public boolean init() {
		logger.info("GDSCC_VERSION: " + GDS.GDS_VERSION);
		gdsConsole = null;

		boolean res = true;
		File f = new File(FileUtils.getUserDirectory(), GDSCCID() + ".lck");
		try {

			fLock = FileChannel
					.open(f.toPath(), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)
					.tryLock();
			remote = (fLock == null);
		} catch (IOException ex) {
			logger.error("ERROR!!", ex);
			remote = true;
		}
		// if(f.exists())
		// {
		// try{notLocked=f.delete();}catch(Exception ed){}
		// }else{
		// notLocked=true;
		// }

		try {
			if (!remote) {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						try {
							if (proxyRegistry != null) {

								proxyRegistry = null;
							}

							proxyRegistry = LocateRegistry.createRegistry(proxyPort);
							gdsConsole = new SwingControlConsole();
							gdsConsole.setName(GDSCCID());
							IControlConsole stub = (IControlConsole) UnicastRemoteObject.exportObject(gdsConsole, 0);

							// Bind the remote object's stub in the registry
							// proxyRegistry =
							// LocateRegistry.getRegistry(proxyPort);
							proxyRegistry.bind(GDSCCID(), stub);
							logger.info("GDSCC is initialized locally with ID:" + GDSCCID());
						} catch (Exception e) {

						}

					}
				});

			} else {
				mainThread = Thread.currentThread();
				// nameLookup = Simon.createNameLookup("127.0.0.1", proxyPort);
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						// keepRunning = false;
						try {
							mainThread.join();
							bye();
							// while(true);
						} catch (InterruptedException ex) {
							//
							logger.error("ERROR!!", ex);
						}
					}
				});

				proxyRegistry = LocateRegistry.getRegistry(proxyPort);
				gdsConsole = (IControlConsole) proxyRegistry.lookup(GDSCCID());
				logger.info("GDSCC is bind to remote ID: " + GDSCCID());
			}
			logger.info("GDSCC init successfully.");
		} catch (Exception ee) {
			logger.error("GDSCC init failed.", ee);
			res = false;
		}

		return res;

	}

	public IControlConsole getConsole() {
		return gdsConsole;
	}

	public boolean terminalConnect() {
		String conf = ccConfiguration.getProperty(TERMINAL_CONFIG);

		boolean res = false;
		try {
			if (remote) {
				if (conf == null)
					conf = gdsConsole.getTerminalConnectionString();
				if (conf != null)
					ccConfiguration.setProperty(TERMINAL_CONFIG, conf);
			}

			// if( gdsConsole.getSerialConnectionString()==null
			// || (!conf.equals(gemConsole.getSerialConnectionString())
			// || (!gemConsole.isSerialConnectionRunning()))
			// )
			// {
			//

			try {
				gdsConsole.setTerminalConnectionString(conf);
				res = gdsConsole.startTerminal();
			} catch (Exception ee) {
				ERROR(ee);
			}
			// }
			// else
			// {
			// logger.info("Console already opened @ '"+conf+"'");
			// res=true;
			//
			// }
		} catch (Exception ee) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(ee.getMessage());
		}

		return res;

	}

	static SimpleDateFormat smd = new SimpleDateFormat("yyyyMMMdd");
	static SimpleDateFormat smdt = new SimpleDateFormat("yyyyMMMddhhmmss");

	public static int random() {
		return new Random().nextInt();
	}

	public static String getDate() {
		return smd.format(GregorianCalendar.getInstance().getTime());

	}

	public static String getDateTime() {
		return smdt.format(GregorianCalendar.getInstance().getTime());
	}

	public void setConsoleVisible(final boolean visible) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						getConsole().setVisible(visible);
					} catch (RemoteException e) {
						logger.error("ERROR!!", e);
					}
				}
			});
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}
	}

	public boolean cmd(String cmdString) {

		try {
			return getConsole().cmd(cmdString);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}

	public boolean cmd(String cmdString, String exceptedString) {
		try {
			return getConsole().cmd(cmdString, exceptedString);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;
	}

	public boolean cmd(String cmdString, int timeoutSeconds) {
		try {
			return getConsole().cmd(cmdString, timeoutSeconds);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;
	}

	public boolean cmd(String cmdString, String exceptedString, int timeoutSeconds) {
		try {
			return getConsole().cmd(cmdString, exceptedString, timeoutSeconds);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}

	public boolean validatePrompt() {
		try {
			return getConsole().validatePrompt();
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;
	}

	public void setDefaultTimeout(int seconds) {
		try {
			getConsole().setDefaultTimeout(seconds);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}

	public void send(String cmdString) {
		try {
			getConsole().send(cmdString);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}

	public boolean sendLooping(String cmdString, String exceptedString, int timeoutSeconds) {
		try {
			return getConsole().sendLooping(cmdString, exceptedString, timeoutSeconds);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;

	}

	public boolean asyncSendLooping(String cmdString, String exceptedString, int timeoutSeconds) {
		try {
			return getConsole().asyncSendLooping(cmdString, exceptedString, timeoutSeconds);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;

	}

	public boolean asyncSendLoopingWait() {
		try {
			return getConsole().asyncSendLoopingWait();
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;

	}

	public GDSError getLastError() {
		try {
			return getConsole().getLastError();
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;

	}

	public void setErrorThrown(boolean thrown) {
		try {
			getConsole().setErrorThrown(thrown);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public boolean isErrorThrown() {
		try {
			return getConsole().isErrorThrown();
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public boolean expect(String match) {
		try {
			return getConsole().expect(match);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;
	}

	public boolean expect(String match, int timeoutSeconds) {
		try {
			return getConsole().expect(match, timeoutSeconds);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;
	}

	public boolean match(String match) {
		try {
			return getConsole().match(match);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;
	}

	public boolean match(String match, int timeoutSeconds) {
		try {
			return getConsole().match(match, timeoutSeconds);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return false;
	}

	public String getPrompt() {
		try {
			return getConsole().getPrompt();
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return null;
	}

	public void setPrompt(String prompt) {
		try {
			getConsole().setPrompt(prompt);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void PS1(String pattern) {
		try {
			getConsole().PS1(pattern);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public String getResponse() {
		try {
			return getConsole().getResponse();
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return null;
	}

	public void clearResponse() {
		try {
			getConsole().clearResponse();
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public int getResponseCheckTimeInMillis() {
		try {
			return getConsole().getResponseCheckTimeInMillis();
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
		// return -1;
	}

	public void setResponseCheckTimeInMillis(int milliSeconds) {
		try {
			getConsole().setResponseCheckTimeInMillis(milliSeconds);
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}

	public void msleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			// TODO: handle errorThrow
		}
	}

	public static long currentMillis() {
		return System.currentTimeMillis();
	}

	public void info() {
		try {
			StringBuilder stb = new StringBuilder();

			stb.append("\n\n");

			Iterator<Object> itr = ccConfiguration.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				stb.append(key.toString() + " = " + ccConfiguration.getProperty(key.toString()) + "\n");
			}

			stb.append("\n");

			if (getConsole() == null)
				stb.append("[Console] NA\n");
			{
				stb.append("[Serial] \n");
				stb.append("    connectionConfig: " + ((getConsole().getTerminalConnectionString() == null) ? ("NA")
						: gdsConsole.getTerminalConnectionString()) + "\n");
				stb.append("    running: " + getConsole().isTerminalRunning() + "\n");
				stb.append("\n");
			}

			logger.info(stb.toString());
		} catch (Exception e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}

	public static long checksumCRC32(String file) throws IOException {
		return FileUtils.checksumCRC32(new File(file));
	};

	public static void cleanDirectory(String file) throws IOException {
		FileUtils.cleanDirectory(new File(file));
	}

	public static boolean contentEquals(String file1, String file2) throws IOException {
		return FileUtils.contentEquals(new File(file1), new File(file2));
	};

	public static void copyDirectory(String dir1, String dir2) throws IOException {
		FileUtils.copyDirectory(new File(dir1), new File(dir2));
	};

	public static void copyIntoDirectory(String src, String dir) throws IOException {
		File fsrc = new File(src);
		if (fsrc.isDirectory()) {
			FileUtils.copyDirectoryToDirectory(fsrc, new File(dir));
		} else {
			FileUtils.copyFileToDirectory(fsrc, new File(dir));
		}
	}

	public static void delete(String src) throws IOException {
		FileUtils.deleteQuietly(new File(src));
	}

	public static boolean directoryContains(String dir, String file) throws IOException {
		return FileUtils.directoryContains(new File(dir), new File(file));
	}

	public static boolean compare(String f1, String f2) throws IOException {
		return FileUtils.contentEquals(new File(f1), new File(f2));
	}

	public static byte[] readFile(String file) throws IOException {
		return FileUtils.readFileToByteArray(new File(file));
	}

	public static void writeFile(String file, byte[] content) throws IOException {
		FileUtils.writeByteArrayToFile(new File(file), content);
	}

	public static void moveFile(String f1, String f2) throws IOException {
		FileUtils.moveFile(new File(f1), new File(f2));
	}

	public static void moveDirectory(String dir1, String dir2) throws IOException {
		FileUtils.moveDirectory(new File(dir1), new File(dir2));
	}

	public static String getTempDirectory() {
		return FileUtils.getTempDirectoryPath();
	}

	public static String getUserdirectory() {
		return FileUtils.getUserDirectoryPath();
	}

	public boolean arduinoConnect() {

		String conf = ccConfiguration.getProperty(ARDUINO_CONFIG);
		boolean res = false;
		try {

			if (remote) {
				if (conf == null)
					conf = gdsConsole.getArduinoConnectionString();
				if (conf != null)
					ccConfiguration.setProperty(ARDUINO_CONFIG, conf);
			}

			// if( gdsConsole.getArduinoConnectionConfig()==null
			// || (!conf.equals(gdsConsole.getArduinoConnectionConfig()))
			// )
			// {

			try {
				gdsConsole.setArduinoConnectionString(conf);
				res = gdsConsole.connectArduino();
			} catch (Exception ee) {
				ERROR(ee);
			}
			// }
			// else
			// {
			// logger.info("Buttons already opened @ '"+conf+"'");
			// res=true;
			//
			// }
		} catch (Exception ee) {
			ERROR(ee);
		}

		return res;
	}

	public void buttonPress(int btnIndex) {
		try {
			getConsole().buttonPress(btnIndex);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void buttonRelease(int btnIndex) {
		try {
			getConsole().buttonRelease(btnIndex);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void buttonClick(int btnIndex) {
		try {
			getConsole().buttonClick(btnIndex);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void setButtonClickPressingTimeInMillis(char btnIndex, int milliseconds) {
		try {
			getConsole().setButtonClickPressingTimeInMillis(btnIndex, milliseconds);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void releaseButtons() {
		try {
			getConsole().buttonReleaseAll();
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public boolean isRemote() {
		return remote;
	}

	public static String whereFrom(Class<?> c) {

		ClassLoader loader = c.getClassLoader();
		if (loader == null) {
			// Try the bootstrap classloader - obtained from the ultimate parent
			// of the System Class Loader.
			loader = ClassLoader.getSystemClassLoader();
			while (loader != null && loader.getParent() != null) {
				loader = loader.getParent();
			}
		}
		if (loader != null) {
			String name = c.getCanonicalName();
			URL resource = loader.getResource(name.replace(".", "/") + ".class");
			if (resource == null) {
				resource = loader.getResource(name.replace(".", "/") + ".groovy");
			}
			if (resource != null) {
				return resource.toString();
			}
		}
		return null;
	}

	public void bye() {
		try {
			if (gdsConsole != null) {
				if (remote) {
					// proxyRegistry.unbind(KEY_GEM_CONSOLE);

				} else {
					gdsConsole.close();
				}
			}
			gdsConsole = null;
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}
	}

	public void setFileLogPath(String filePath) {
		try {
			getConsole().setFileLogPath(filePath);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public String getFileLogPath() {
		try {
			return getConsole().getFileLogPath();
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void logCCEvent(String msg) {
		try {
			getConsole().logCCEvent(msg);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void logEvent(String msg) {
		try {
			getConsole().logEvent(msg);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void saveFileLog() {
		try {
			getConsole().saveFileLog();
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void closeFileLog() {
		try {
			getConsole().closeFileLog();
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void setFileLogAppend(boolean append) {
		try {
			getConsole().setFileLogAppend(append);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void ready() {
		try {
			getConsole().ready();
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public boolean isFileLogAppend() {
		try {
			return getConsole().isFileLogAppend();
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}

	public void setReponseEnabled(boolean enable) {
		try {
			getConsole().setResponseEnabled(enable);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public boolean isReponseEnabled() {
		try {
			return getConsole().isResponseEnabled();
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}

	public void forceExit() throws Exception {
		getConsole().forceExit();
	}


	public void addSessionFileLog(String filePath){
		try {
			getConsole().addSessionFileLog(filePath);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}

	}


	public void removeSessionFileLog(String filePath) throws RemoteException {
		try {
			getConsole().removeSessionFileLog(filePath);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}


	public int getClearResponseWaitTimeInMillis() {
		try {
			return getConsole().getClearResponseWaitTimeInMillis();
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}

	public void setClearResponseWaitTimeInMillis(int clearResponseWaitTimeInMillis) {
		try {
			getConsole().setClearResponseWaitTimeInMillis(clearResponseWaitTimeInMillis);
		} catch (RemoteException e) {
			// logger.log(Level.FATAL, "ERROR!!", e);
			throw new RuntimeException(e);
		}
	}


	public static void main(String[] args) throws Exception {

		String cf = null;
		GDSCC cc = new GDSCC();
		if ((cf = System.getProperty(CONFIGURATION_FILE)) != null) {
			cc.setConfigurationFilePath(cf);
			cc.loadConfigurationFromFile();

		}

		cc.init();
		// cc.connectSerial();
		// cc.connectArduino();

		cc.setConsoleVisible(true);
		if (args.length > 0 && args[0].equals("EXIT")) {
			cc.forceExit();
			System.exit(0);
		}

	}
}
