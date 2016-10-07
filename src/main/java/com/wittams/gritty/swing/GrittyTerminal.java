package com.wittams.gritty.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wittams.gritty.Emulator;
import com.wittams.gritty.ResizePanelDelegate;
import com.wittams.gritty.ScrollBuffer;
import com.wittams.gritty.StyleState;
import com.wittams.gritty.TerminalBackBuffer;
import com.wittams.gritty.TerminalWriter;
import com.wittams.gritty.Tty;
import com.wittams.gritty.TtyChannel;

import gds.console.IControlConsole;
import gds.console.ITerminal;
import gds.console.ResponseBuffer;
import gds.console.SessionLogWriter;

public class GrittyTerminal extends JPanel implements ITerminal{
	private static final Logger logger = LoggerFactory.getLogger(GrittyTerminal.class.getName());

	private static final long serialVersionUID = -8213232075937432833L;

	private final StyleState styleState;
	private final TerminalBackBuffer backBuffer;
	private final ScrollBuffer scrollBuffer;
	private final TermPanel termPanel;
	private final JScrollBar scrollBar;

	private Tty tty;
	private TtyChannel ttyChannel;
	private TerminalWriter terminalWriter;
	private Emulator emulator;

	private Thread emuThread;

	private AtomicBoolean sessionRunning = new AtomicBoolean();
	private PreConnectHandler preconnectHandler;

	public static enum BufferType {
		Back() {
			@Override
			String getValue(GrittyTerminal term) {
				return term.getTermPanel().getTerminalBackBuffer().getLines();
			}
		},
		BackStyle() {
			@Override
			String getValue(GrittyTerminal term) {
				return term.getTermPanel().getTerminalBackBuffer().getStyleLines();
			}
		},
		Damage() {
			@Override
			String getValue(GrittyTerminal term) {
				return term.getTermPanel().getTerminalBackBuffer().getDamageLines();
			}
		},
		Scroll() {
			@Override
			String getValue(GrittyTerminal term) {
				return term.getTermPanel().getScrollBuffer().getLines();
			}
		};

		abstract String getValue(GrittyTerminal term);
	}

	public GrittyTerminal() {
		super(new BorderLayout());

		styleState = new StyleState();
		backBuffer = new TerminalBackBuffer(80, 24, styleState);
		scrollBuffer = new ScrollBuffer();

		termPanel = new TermPanel(backBuffer, scrollBuffer, styleState);
		terminalWriter = new TerminalWriter(termPanel, backBuffer, styleState);
		preconnectHandler = new PreConnectHandler(terminalWriter);
		termPanel.setKeyHandler(preconnectHandler);
		scrollBar = new JScrollBar();

		add(termPanel, BorderLayout.CENTER);
		add(scrollBar, BorderLayout.EAST);
		scrollBar.setModel(termPanel.getBoundedRangeModel());
		sessionRunning.set(false);
		termPanel.setScroller(scrollBar);
	}

	public TermPanel getTermPanel() {
		return termPanel;
	}

	public JScrollBar getScrollBar() {
		return scrollBar;
	}

	public void setTty(Tty tty) {
		this.tty = tty;
		ttyChannel = new TtyChannel(tty);

		emulator = new Emulator(terminalWriter, ttyChannel);
		this.termPanel.setEmulator(emulator);
	}

	@Override
	public void start() throws IOException {
		if (!sessionRunning.get()) {
			sessionRunning.set(true);

			try {
				tty.connect();
			}catch (IOException e) {
				tty.close();
				sessionRunning.set(false);

				if(e instanceof IOException){
					throw e;
				}
				throw new IOException(e);
			}


			emuThread = new Thread(new EmulatorTask());
			emuThread.start();
		} else {
			logger.error("Should not try to start session again at this point... ");
		}
	}

	@Override
	public void stop() {
		if (sessionRunning.get() && emuThread != null)
			emuThread.interrupt();
	}

	@Override
	public boolean isSessionRunning() {
		return sessionRunning.get();
	}

	class EmulatorTask implements Runnable {
		@Override
		public void run() {
			try {

				if(tty.init(preconnectHandler)){

					Runnable r=new Runnable() {
						@Override
						public void run() {
							termPanel.setKeyHandler(new ConnectedKeyHandler(emulator));
							termPanel.requestFocusInWindow();
						}
					};
					try{
						SwingUtilities.invokeAndWait(r);
					}catch(Exception ex){

					}
					emulator.start();
				}
			} finally {
				try {
					tty.close();
				} catch (Exception e) {
				}
				sessionRunning.set(false);
				termPanel.setKeyHandler(preconnectHandler);
			}
		}
	}

	public String getBufferText(BufferType type) {
		return type.getValue(this);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(termPanel.getPixelWidth() + scrollBar.getPreferredSize().width,
				termPanel.getPixelHeight());
	}

	public void sendCommand(String string) throws IOException {
		if (sessionRunning.get()) {
			ttyChannel.sendBytes(string.getBytes());
		}
	}

	@Override
	public boolean requestFocusInWindow() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				termPanel.requestFocusInWindow();
			}
		});
		return super.requestFocusInWindow();
	}

	public void setResizePanelDelegate(final ResizePanelDelegate resizeDelegate) {
		this.termPanel.setResizePanelDelegate(resizeDelegate);
	}


	@Override
	public void type(char ch){
		if(this.termPanel!=null){
	    	int kv=KeyEvent.KEY_TYPED;
	    	int kr=ch;
			KeyEvent ke=new KeyEvent(this.termPanel,KeyEvent.KEY_PRESSED,0,0,kv,(char)kr);
	        this.termPanel.processKeyEvent(ke);
		}

	}

	@Override
	public void setSessionLogWriter(SessionLogWriter slog){
		this.terminalWriter.setSessionLogWriter(slog);
	}

	@Override
	public SessionLogWriter getSessionLogWriter(){
		return this.terminalWriter.getSessionLogWriter();
	}
	@Override
	public void addResponseBuffer(ResponseBuffer bfr){
		this.terminalWriter.addResponseBuffer(bfr);
	}


	@Override
	public void removeResponseBuffer(ResponseBuffer bfr){
		this.terminalWriter.removeResponseBuffer(bfr);
	}


	@Override
	public void clear() {
		this.terminalWriter.clearScreen();
	}


	@Override
	public void setConsole(IControlConsole console) {
		if(termPanel!=null){
			termPanel.setConsole(console);
		}

	}
}
