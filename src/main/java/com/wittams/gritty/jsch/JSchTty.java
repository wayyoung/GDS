/**
 *
 */
package com.wittams.gritty.jsch;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.wittams.gritty.Questioner;
import com.wittams.gritty.Tty;
import com.wittams.gritty.swing.standalone.Main;

public class JSchTty implements Tty {
	private InputStream in = null;
	private OutputStream out = null;
	private Session session;
	private ChannelShell channel;
	private int port = 22;

	private String user = null;
	private String host = null;
	private String password = null;

	private Dimension pendingTermSize;
	private Dimension pendingPixelSize;

//	Questioner q;
	public JSchTty() {

	}

	public JSchTty(String host, String user, String password) {
		this.host = host;
		this.user = user;
		this.password = password;
	}

	@Override
	public void resize(Dimension termSize, Dimension pixelSize) {
		pendingTermSize = termSize;
		pendingPixelSize = pixelSize;
		if (channel != null)
			resizeImmediately();
	}

	private void resizeImmediately() {
		if (pendingTermSize != null && pendingPixelSize != null) {
			channel.setPtySize(pendingTermSize.width, pendingTermSize.height, pendingPixelSize.width,
					pendingPixelSize.height);
			pendingTermSize = null;
			pendingPixelSize = null;
		}
	}

	@Override
	public void close() {
		if (session != null) {
			session.disconnect();
			session = null;
			channel = null;
			in = null;
			out = null;
		}
	}

	@Override
	public boolean init(Questioner q){
		getAuthDetails(q);

		try {
			session = connectSession(q);
			channel = (ChannelShell) session.openChannel("shell");
			in = channel.getInputStream();
			out = channel.getOutputStream();
			channel.connect();
			resizeImmediately();
			return true;
		} catch (final IOException e) {
			q.showMessage(e.getMessage());
			Main.logger.error("Error opening channel", e);
//			throw e;
		} catch (final JSchException e) {
			q.showMessage(e.getMessage());
			Main.logger.error("Error opening session or channel", e);
//			throw new IOException(e);
		}
		return false;

	}
	@Override
	public void connect()throws IOException {
	}

	private Session connectSession(Questioner questioner) throws JSchException {
		JSch jsch = new JSch();
		Session session = null;
		session = jsch.getSession(user, host, port);

		final QuestionerUserInfo ui = new QuestionerUserInfo(questioner);
		if (password != null) {
			session.setPassword(password);
			ui.setPassword(password);
		}
		session.setUserInfo(ui);

		final java.util.Properties config = new java.util.Properties();
		config.put("compression.s2c", "zlib,none");
		config.put("compression.c2s", "zlib,none");
		configureSession(session, config);
		session.setTimeout(5000);
		session.connect();
		session.setTimeout(0);

		return session;
	}

	protected void configureSession(Session session, final java.util.Properties config) {
		session.setConfig(config);
	}

	private void getAuthDetails(Questioner q) {
		while (true) {
			if (host == null)
				host = q.questionVisible("host:", "");
			if (host == null || host.length() == 0)
				continue;
			if (host.indexOf(':') != -1) {
				final String portString = host.substring(host.indexOf(':') + 1);
				try {
					port = Integer.parseInt(portString);
				} catch (final NumberFormatException eee) {
					q.showMessage("Could not parse port : " + portString);
					continue;
				}
				host = host.substring(0, host.indexOf(':'));
			}

			if (user == null)
				user = q.questionVisible("user:", "");
			if (host == null || host.length() == 0)
				continue;
			if (password == null)
				password = q.questionVisible("password:", "");

			if (password == null || password.length() == 0)
				continue;
			break;
		}
	}

	@Override
	public String getName() {
		return "JSchTty Task";
	}

	@Override
	public int read(byte[] buf, int offset, int length) throws IOException {
		return in.read(buf, offset, length);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		out.write(bytes);
		out.flush();
	}

}