package gds.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface ISerialPortConnection {
	public static final String KEY_INPUT_RECV_TIMEOUT_MS = "INPUT_RECV_TIMEOUT_MS";

	InputStream getInputStream();

	OutputStream getOutputStream();

	List<String> listAvailable(boolean refresh);

	void close();

	void open() throws IOException;

	void open(SerialPortSetting setting) throws IOException;

	public void setSerialPortSetting(SerialPortSetting setting);

	public SerialPortSetting getSerialPortSetting();

	public boolean isOpened();

}
