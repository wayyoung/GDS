package gds.swing;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wittams.gritty.RequestOrigin;
import com.wittams.gritty.ResizePanelDelegate;
import com.wittams.gritty.Tty;
import com.wittams.gritty.jsch.JSchTty;
import com.wittams.gritty.swing.GrittyTerminal;

import gds.GDS;
import gds.console.AbstractControlConsole;
import gds.console.ITerminal;
import gds.console.NetSetting;
import gds.serial.SerialPortManager;
import gds.serial.SerialPortSetting;
import gds.serial.SerialPortSetting.ExceptionSettingsParse;
import gds.serial.SerialPortTty;

public class SwingControlConsole extends AbstractControlConsole implements ActionListener {

	boolean reponseWithUI = true;

	@Override
	public void setName(String name) {
		super.name = name;
		runOnEDT(new Runnable() {
			@Override
			public void run() {
				SwingControlConsole.this.UI_setTitle();
			}
		});

	}


	private JFrame frame;
	private static final Logger logger = LoggerFactory.getLogger(GDS.GDS_LOGGER);
	// ButtonDialog bdialog;//=new ButtonDialog();
	FunctionScriptDialog fsdlg;

	boolean fileLogAppend = false;

	JMenuItem mntmTerminalName;
	JMenuItem mntmArduinoName;
	JMenuItem mntmOpen;
	JMenuItem mntmClose;

	JMenuItem mntmStartLog;
	JMenuItem mntmOpen_1;
	JMenuItem mntmClose_1;
	JCheckBoxMenuItem chckbxmntmButton_1;
	JCheckBoxMenuItem chckbxmntmButton_2;
	JCheckBoxMenuItem chckbxmntmButton_3;
	JCheckBoxMenuItem chckbxmntmButton_4;
	JMenu mnTerminal;
	int widthGap;
	int heightGap;
	private JMenuItem mntmStopLog;

	private JMenuItem mntmAbout;

	private String fileLog;


	LogFileChooser logFileChooser;//=new JFileChooser();
	private JMenuItem mntmShowButtonPage;
	private JMenuItem mntmClear;
	private JSeparator separator;
	private JSeparator separator_1;
	private JMenu mnAbout;
	private JMenuItem mntmReleaseAllButtons;
	private JSeparator separator_2;


	@Override
	public void forceExit() {
		System.exit(-1);
	}

	public static void runOnEDT(final Runnable r) {
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (Exception e) {
				logger.error("ERROR!!", e);
			}
	}

	private void sizeFrameForTerm(final JFrame frame) {
		Dimension d = term.getPreferredSize();

		d.width += frame.getWidth() - frame.getContentPane().getWidth();
		d.height += frame.getHeight() - frame.getContentPane().getHeight();
		frame.setSize(d);
	}

	public SwingControlConsole() throws RemoteException {
		try {
			UIManager.setLookAndFeel(com.jtattoo.plaf.fast.FastLookAndFeel.class.getName());
			Locale.setDefault(Locale.ENGLISH);
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}

		initialize();

		try {
			frame.setIconImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("gds/icon/terminal.png")));
		} catch (Exception ex) {
			logger.error("ERROR!!", ex);
		}

		resp.setEnabled(true);

		term = new GrittyTerminal();
		this.term.addResponseBuffer(resp);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});

		sizeFrameForTerm(frame);
		frame.getContentPane().add("Center", (term));

		frame.pack();
		term.setVisible(true);

		frame.setResizable(true);

		term.setResizePanelDelegate(new ResizePanelDelegate(){
			@Override
			public void resizedPanel(final Dimension pixelDimension, final RequestOrigin origin) {
				if(origin == RequestOrigin.Remote)
					sizeFrameForTerm(frame);
			}
		 });

		 term.setSessionLogWriter(this.sessionLogWriter);
		 term.setConsole(this);

		loadFunctionScriptProperties();

		this.fsdlg=new FunctionScriptDialog(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// bdialog=new ButtonDialog();

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		mnTerminal = new JMenu("Terminal");
		menuBar.add(mnTerminal);

		mntmOpen = new JMenuItem("Connect");
		mnTerminal.add(mntmOpen);
		mntmOpen.addActionListener(this);
		mntmOpen.setActionCommand("openTerminal");

		mntmClear = new JMenuItem("Clear");
		mntmClear.setEnabled(false);
		mnTerminal.add(mntmClear);
		mntmClear.addActionListener(this);
		mntmClear.setActionCommand("clearTerminal");

		mntmClose = new JMenuItem("Disconnect");
		mntmClose.setEnabled(false);
		mnTerminal.add(mntmClose);
		mntmClose.addActionListener(this);
		mntmClose.setActionCommand("closeTerminal");

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnTerminal.add(mntmExit);
		mntmExit.addActionListener(this);
		mntmExit.setActionCommand("exit");

		separator = new JSeparator();
		mnTerminal.add(separator);

		mntmStartLog = new JMenuItem("Save Log");
		mnTerminal.add(mntmStartLog);
		mntmStartLog.addActionListener(this);
		mntmStartLog.setActionCommand("saveLogFile");

		mntmStopLog = new JMenuItem("Close Log");
		mntmStopLog.setEnabled(false);
		mntmStopLog.setActionCommand("closeLogFile");
		mntmStopLog.addActionListener(this);
		mnTerminal.add(mntmStopLog);

		separator_1 = new JSeparator();
		mnTerminal.add(separator_1);

		mntmTerminalName = new JMenuItem("[NA]");
		mntmTerminalName.setEnabled(false);
		mnTerminal.add(mntmTerminalName);

		JMenu mnArduino = new JMenu("Arduino");
		menuBar.add(mnArduino);

		mntmOpen_1 = new JMenuItem("Connect");
		mnArduino.add(mntmOpen_1);
		mntmOpen_1.addActionListener(this);
		mntmOpen_1.setActionCommand("openArduino");

		mntmClose_1 = new JMenuItem("Disconnect");
		mntmClose_1.setEnabled(false);
		mnArduino.add(mntmClose_1);
		mntmClose_1.addActionListener(this);
		mntmClose_1.setActionCommand("closeArduino");

		mntmShowButtonPage = new JMenuItem("Show Button Page");
		mntmShowButtonPage.setActionCommand("showButtonPage");
		mntmShowButtonPage.addActionListener(this);

		mnArduino.addSeparator();
		// new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// mntmShowButtonPage.setEnabled(false);
		// bdialog.setVisible(true);
		// }
		// });
		mnArduino.add(mntmShowButtonPage);

		mnArduino.addSeparator();
		chckbxmntmButton_1 = new JCheckBoxMenuItem("Button 1");
		mnArduino.add(chckbxmntmButton_1);

		chckbxmntmButton_2 = new JCheckBoxMenuItem("Button 2");
		mnArduino.add(chckbxmntmButton_2);

		chckbxmntmButton_3 = new JCheckBoxMenuItem("Button 3");
		mnArduino.add(chckbxmntmButton_3);

		chckbxmntmButton_4 = new JCheckBoxMenuItem("Button 4");
		mnArduino.add(chckbxmntmButton_4);

		mntmReleaseAllButtons = new JMenuItem("Reset All Buttons");
		mntmReleaseAllButtons.setActionCommand("releaseAllButtons");
		mntmReleaseAllButtons.addActionListener(this);
		mnArduino.add(mntmReleaseAllButtons);

		separator_2 = new JSeparator();
		mnArduino.add(separator_2);

		mntmArduinoName = new JMenuItem("[NA]");
		mntmArduinoName.setEnabled(false);
		mnArduino.add(mntmArduinoName);

		JMenu mnSettings = new JMenu("Function Scripts");
		menuBar.add(mnSettings);

		JMenuItem mntmFunctionScripts = new JMenuItem("Settings");
		mnSettings.add(mntmFunctionScripts);
		mntmFunctionScripts.addActionListener(this);
		mntmFunctionScripts.setActionCommand("settingFunctionScripts");

		logFileChooser=new LogFileChooser();
		logFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);


		// JCheckBoxMenuItem[] ba=new JCheckBoxMenuItem[4];
		// ba[0]=chckbxmntmButton_1;
		// ba[1]=chckbxmntmButton_2;
		// ba[2]=chckbxmntmButton_3;
		// ba[3]=chckbxmntmButton_4;
		//
		// bdialog.setParentCheckBoxList(ba);
		//
		// bdialog.pack();
		// chckbxmntmButton_1.addActionListener(bdialog);

		chckbxmntmButton_1.addActionListener(this);chckbxmntmButton_1.setActionCommand("B1");
		chckbxmntmButton_2.addActionListener(this);chckbxmntmButton_2.setActionCommand("B2");
		chckbxmntmButton_3.addActionListener(this);chckbxmntmButton_3.setActionCommand("B3");
		chckbxmntmButton_4.addActionListener(this);chckbxmntmButton_4.setActionCommand("B4");

		mnAbout = new JMenu("Help");
		menuBar.add(mnAbout);
		mntmAbout = new JMenuItem("About");
		mnAbout.add(mntmAbout);
		mntmAbout.addActionListener(this);
		mntmAbout.setActionCommand("about");

		UI_setTitle();
	}

	private boolean _startTerminal() throws IOException {

		if (!term.isSessionRunning()) {
			Tty tty = null;
			if (this.terminalConnectionString.startsWith(ITerminal.PREFIX_SSH)) {
				NetSetting set = new NetSetting();
				set.parse(this.terminalConnectionString.substring(4));
				tty = new JSchTty(set.getHost() + ":" + set.getPort(), null, null);

			} else {
				SerialPortSetting setting;
				try {
					setting = new SerialPortSetting(this.terminalConnectionString);
					tty = new SerialPortTty(setting);
				} catch (ExceptionSettingsParse e) {
					throw new IOException(e);
				}

			}
			this.term.setTty(tty);
			this.term.start();
		}

		return true;
	}

	private void _closeFileLog() {

		BufferedWriter wr=this.sessionLogWriter.getBackendWriter();
		if (wr != null) {
			try {
				wr.flush();
			} catch (IOException e) {

			}
			try {
				wr.close();
			} catch (IOException e) {
				logger.error("ERROR!!", e);
			}
			this.sessionLogWriter.setBackendWriter(null);
		}

		try {
			if(fileLog!=null)logger.info("closeFileLog: " + new File(this.fileLog).getCanonicalPath());
		} catch (IOException e) {

		}
		fileLog = null;
	}

	public boolean UI_startTerminal() throws IOException {
		boolean result = this._startTerminal();
		if (result) {
			this.mntmOpen.setEnabled(false);
			this.mntmClose.setEnabled(true);
			this.mntmTerminalName.setEnabled(true);
			this.mntmTerminalName.setText(terminalConnectionString);
			this.mntmClear.setEnabled(true);
			this.UI_setTitle();
		}

		return result;
	}

	public void UI_stopTerminal() throws IOException {
		this.term.stop();
		this.mntmOpen.setEnabled(true);
		this.mntmClose.setEnabled(false);
		this.mntmClear.setEnabled(false);
//		this.mntmStartLog.setText("Save Log");
//		this.mntmStartLog.setEnabled(true);
//		this.mntmStopLog.setEnabled(false);
		this.mntmTerminalName.setEnabled(false);
		this.mntmTerminalName.setText("[NA]");

		UI_setTitle();
	}



	static final byte[] FIRMATA_HEADER0={(byte)0xF9,(byte)0x02,(byte)0x03,(byte)0xF0,(byte)0x79,(byte)0x02,(byte)0x03,(byte)0x53,(byte)0x00,(byte)0x74};
	static final byte[] FIRMATA_HEADER1={(byte)0xF0,(byte)0x79,(byte)0x02,(byte)0x03,(byte)0x53,(byte)0x00,(byte)0x74};
	private boolean _connectArduino() throws IOException {

		try {
			SerialPortSetting setting=new SerialPortSetting(this.arduinoConnectionString);
			setting.setDirectInputStream(true);

			if(arduinoConnection!=null){
				if(!arduinoConnection.getSerialPortSetting().equals(setting)) {
					arduinoConnection.close();
					arduinoConnection=null;
				}else{
					return true;
				}
			}
			this.arduinoConnection= SerialPortManager.newConnection();
			this.arduinoConnection.open(setting);
			this.arduinoConnection.getOutputStream().write(new byte[]{(byte)0xF0,(byte)0x79,(byte)0xF7});
			this.arduinoConnection.getOutputStream().flush();
			byte[] resp=new byte[1024];
			int len=0;
			boolean firmataConnected=false;
			for(int i=0;i<1000;i++){
				len=this.arduinoConnection.getInputStream().read(resp);
				if(len>0){
					if(Arrays.equals(Arrays.copyOf(resp, FIRMATA_HEADER0.length),FIRMATA_HEADER0) || Arrays.equals(Arrays.copyOf(resp, FIRMATA_HEADER1.length),FIRMATA_HEADER1)){
						firmataConnected=true;
						Thread.sleep(50);
					}else{
						this.arduinoConnection.getOutputStream().write(new byte[]{(byte)0xF0,(byte)0x79,(byte)0xF7});
						this.arduinoConnection.getOutputStream().flush();
						Thread.sleep(50);
						continue;
					}
					break;
				}

				Thread.sleep(100);
			}
			if(firmataConnected){
				logger.info("Firmata Arduino connected");
			}else{
				throw new RuntimeException("Firmata Arduino connected failed!! len="+len);
			}
		} catch (Exception e) {
			throw new IOException(e);
		}

		return true;
	}

	private void _disconnectArduino() {
		try{
			this.arduinoConnection.close();
			this.arduinoConnection=null;
		}catch(Exception ee){

		}

	}

	private void UI_setTitle() {
		String tit=this.name+"@"+((this.terminalConnectionString!=null)?(this.terminalConnectionString):"");
		this.frame.setTitle(tit);

	}

	public boolean UI_connectArduino() throws IOException {
		boolean result =this._connectArduino();
		if(result)
		{
			this.mntmOpen_1.setEnabled(false);
			this.mntmClose_1.setEnabled(true);
			this.mntmArduinoName.setEnabled(true);
			this.mntmArduinoName.setText(arduinoConnectionString);
		}
		return result;

	}

	public void UI_disconnectArduino() {
		this._disconnectArduino();
		this.mntmOpen_1.setEnabled(true);
		this.mntmClose_1.setEnabled(false);

		this.mntmArduinoName.setEnabled(false);
		this.mntmArduinoName.setText("[NA]");
	}

	public void UI_saveFileLog() throws IOException {
		if (!this.mntmStartLog.isEnabled())
			return;

		logger.info("saveFileLog: " + new File(this.fileLog).getCanonicalPath());
		this.sessionLogWriter.setBackendWriter(new BufferedWriter(new FileWriter(this.fileLog,this.fileLogAppend)));

		this.mntmStartLog.setText("Log: " + this.fileLog);
		this.mntmStartLog.setEnabled(false);
		this.mntmStopLog.setEnabled(true);
	}

	public void UI_closeFileLog() {
		if (!this.mntmStopLog.isEnabled())
			return;
		_closeFileLog();
		try {

			this.mntmStartLog.setText("Save Log");
		} catch (Exception ex) {
		}

		this.mntmStartLog.setEnabled(true);
		this.mntmStopLog.setEnabled(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("openTerminal")) {
			TerminalConnectionDialog dlg = new TerminalConnectionDialog(frame);
			dlg.setModal(true);
			dlg.setLocationRelativeTo(frame);
			dlg.setVisible(true);
			if (dlg.isConfirmed()) {
				terminalConnectionString = dlg.getConnectionString();
				try {
					UI_startTerminal();
				} catch (Exception ee) {
					logger.error("ERROR!!", ee);
					if(reponseWithUI){
						javax.swing.JOptionPane.showMessageDialog(this.frame, ee.getMessage(), "ERROR!!",JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		} else if (e.getActionCommand().equals("closeTerminal")) {

			try {
				UI_stopTerminal();
			} catch (Exception ee) {
				logger.error("ERROR!!", ee);
			}

		 }
		 else if (e.getActionCommand().equals("clearTerminal")) {

			 try {
				// clear terminal
				this.term.clear();
			} catch (Exception ee) {
				logger.error("ERROR!!", ee);
			}
		} else if (e.getActionCommand().equals("openArduino")) {
			ArduinoConnectionDialog dlg = new ArduinoConnectionDialog(frame);
			dlg.setModal(true);
			dlg.setLocationRelativeTo(frame);
			dlg.setVisible(true);
			if (dlg.isConfirmed()) {
				arduinoConnectionString = dlg.getConnectionString();
				try {
					UI_connectArduino();

				} catch (Exception ee) {
					logger.error("ERROR!!", ee);
					if(reponseWithUI){
						javax.swing.JOptionPane.showMessageDialog(this.frame, ee.getMessage(), "ERROR!!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} else if (e.getActionCommand().equals("closeArduino")) {

			try {

				UI_disconnectArduino();
			} catch (Exception ee) {
				logger.error("ERROR!!", ee);
			}
		}
		else if(e.getActionCommand().equals("saveLogFile")){

			int result = logFileChooser.showSaveDialog(this.frame);

			if (result == JFileChooser.APPROVE_OPTION) {

				try {
					this.fileLog = logFileChooser.getSelectedFile().getCanonicalPath();
					this.fileLogAppend = logFileChooser.isAppend();
					UI_saveFileLog();

				} catch (Exception e1) {
					logger.error("ERROR!!", e1);
				}
			}
		}
		else if(e.getActionCommand().equals("closeLogFile")){
			UI_closeFileLog();
		}else if(e.getActionCommand().equals("settingFunctionScripts")){

			fsdlg.setVisible(true);
			fsdlg.setLocationRelativeTo(frame);
		}else if(e.getActionCommand().equals("showButtonPage")){
//			mntmShowButtonPage.setEnabled(false);
//			bdialog.setLocation(frame.getX()+frame.getWidth(),frame.getY());
//			bdialog.setVisible(true);
		}else if(e.getActionCommand().equals("releaseAllButtons")){
			this.buttonReleaseAll();
			chckbxmntmButton_1.setSelected(false);
			chckbxmntmButton_2.setSelected(false);
			chckbxmntmButton_3.setSelected(false);
			chckbxmntmButton_4.setSelected(false);
		}else if(e.getActionCommand().equals("B1")){if(chckbxmntmButton_1.isSelected()){this.buttonPress(0);}else{this.buttonRelease(0);
//		 	this.bdialog.getCheckBoxes()[0].setSelected(chckbxmntmButton_1.isSelected());
		 	}
		}else if(e.getActionCommand().equals("B2")){if(chckbxmntmButton_2.isSelected()){this.buttonPress(1);}else{this.buttonRelease(1);
//		 	this.bdialog.getCheckBoxes()[1].setSelected(chckbxmntmButton_2.isSelected());
		 	}
		}else if(e.getActionCommand().equals("B3")){if(chckbxmntmButton_3.isSelected()){this.buttonPress(2);}else{this.buttonRelease(2);
//			this.bdialog.getCheckBoxes()[2].setSelected(chckbxmntmButton_3.isSelected());
			}
		}else if(e.getActionCommand().equals("B4")){if(chckbxmntmButton_4.isSelected()){this.buttonPress(3);}else{this.buttonRelease(3);
//		  	this.bdialog.getCheckBoxes()[3].setSelected(chckbxmntmButton_4.isSelected());
		  	}
		}else if(e.getActionCommand().equals("about")){
			JOptionPane.showMessageDialog(this.frame, "Version: "+version);
		}else{
			logger.warn("unknown console command:"+e.getActionCommand());
		}

	}

	public final String version = GDS.GDS_VERSION;



	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVisible(boolean visible) {
		this.frame.setVisible(visible);
	}

	@Override
	public void type(char ch) {
		if (this.term != null && this.term.isSessionRunning()) {
			this.term.type(ch);
		}

	}

	@Override
	public void close() {
		frame.dispose();
	}

	@Override
	public boolean startTerminal() throws IOException, RemoteException {
		runOnEDT(new Runnable() {
			@Override
			public void run() {
				try {

					SwingControlConsole.this.UI_startTerminal();
				} catch (IOException e) {
					logger.error("ERROR!!", e);
				}
			}
		});
		return this.term.isSessionRunning();
	}

	@Override
	public void stopTerminal() throws RemoteException {
		runOnEDT(new Runnable() {
			@Override
			public void run() {
				try {
					SwingControlConsole.this.UI_stopTerminal();
				} catch (IOException e) {
					logger.error("ERROR!!", e);
				}
			}
		});
	}

	@Override
	public boolean isTerminalRunning() throws RemoteException {
		return this.term.isSessionRunning();
	}

	@Override
	public void setFileLogPath(String path) throws RemoteException {
		this.fileLog=path;
	}

	@Override
	public String getFileLogPath() throws RemoteException {
		return this.fileLog;
	}

	@Override
	public void setFileLogAppend(boolean append) throws RemoteException {
		this.fileLogAppend=append;

	}

	@Override
	public boolean isFileLogAppend() throws RemoteException {
		return fileLogAppend;
	}

	@Override
	public void saveFileLog() throws RemoteException {
		runOnEDT(new Runnable() {
			@Override
			public void run() {
				try {

					SwingControlConsole.this.UI_saveFileLog();
				} catch (IOException e) {
					logger.error("ERROR!!", e);
				}
			}
		});

	}


	@Override
	public void closeFileLog() throws RemoteException {
		runOnEDT(new Runnable() {
			@Override
			public void run() {
				SwingControlConsole.this.UI_closeFileLog();
			}
		});

	}

	@Override
	public boolean connectArduino() throws IOException, RemoteException {
		runOnEDT(new Runnable() {
			@Override
			public void run() {
				try {

					SwingControlConsole.this.UI_connectArduino();
				} catch (IOException e) {
					logger.error("ERROR!!", e);
				}
			}
		});

		return this.arduinoConnection.isOpened();
	}

	@Override
	public void disconnectArduino() throws RemoteException {
		runOnEDT(new Runnable() {
			@Override
			public void run() {
				SwingControlConsole.this.UI_disconnectArduino();
			}
		});
	}


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					SwingControlConsole window = new SwingControlConsole();
					window.frame.setVisible(true);
				} catch (Exception e) {
					logger.error("ERROR!!", e);
				}
			}
		});
	}


	@Override
	public void updateFunctionScriptDialog() {
		this.fsdlg.syncExecutingStatus();
	}




}
