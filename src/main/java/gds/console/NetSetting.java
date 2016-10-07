package gds.console;

import com.jcraft.jsch.Proxy;

public class NetSetting {
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	Proxy proxy = null;

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	String host = null;
	String user = null;
	String password = null;
	int port = 22;

	public void parse(String str) {
		int p = 22;
		String host = str;
		String user = null;
		if (host != null && host.trim().length() > 0) {
			if (host.indexOf("|") > 0) {
				host = host.substring(host.indexOf("|") + 1);
			}
			if (host.indexOf("@") > 0) {
				user = host.substring(0, host.indexOf('@'));
				host = host.substring(host.indexOf('@') + 1);
			}
			if (host.indexOf(":") > 0) {
				port = Integer.valueOf(host.substring(host.indexOf(":") + 1)).intValue();
				host = host.substring(0, host.indexOf(":"));
			}
		}

		this.host = host;
		this.user = user;
		this.port = p;
	}

	public String toString() {
		return ("" + ((user != null) ? (user + "@") : "") + ((host != null) ? host : "") + ":" + port);

	}

}
