package gds.serial;

import java.awt.Dimension;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wittams.gritty.Questioner;
import com.wittams.gritty.Tty;

public class SerialPortTty implements Tty {
	private static final Logger logger = LoggerFactory.getLogger(SerialPortTty.class.getName());
	ISerialPortConnection con;

	SerialPortSetting setting;

	public SerialPortTty(SerialPortSetting setting) {
		this.setting = setting;
	}

	@Override
	public void connect() throws IOException{
		try {
			if(con!=null && (!con.getSerialPortSetting().equals(setting))){
				con.close();
				con=null;
			}
			if(con==null){
				con = SerialPortManager.newConnection();
				con.open(setting);
			}
		}catch(IOException ex){
			throw ex;
		}catch (Exception ex) {
			throw new IOException(ex.getMessage());
		}


	}

	@Override
	public boolean init(Questioner q){
		return true;
	}

	@Override
	public void close() {
		if(con!=null){
			con.close();
			con=null;
		}

	}

	@Override
	public void resize(Dimension termSize, Dimension pixelSize) {
		// TODO Auto-generated method stub

	}

	@Override

	public String getName() {
		return "SerialPortTty Task";
	}

	@Override
	public int read(byte[] buf, int offset, int length) throws IOException {
		return con.getInputStream().read(buf, offset, length);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		con.getOutputStream().write(bytes);
		con.getOutputStream().flush();
	}

}
