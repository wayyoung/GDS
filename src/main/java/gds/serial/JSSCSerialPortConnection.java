package gds.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class JSSCSerialPortConnection  implements ISerialPortConnection {

	SerialPort sp;
	protected SerialPortSetting settings;
	SerialPortInputStream inputStream=new SerialPortInputStream();
	protected JSSCInputStream spis;
	protected JSSCOutputStream ous;
	@Override
	public InputStream getInputStream() {
		return this.inputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return this.ous;
	}

	public JSSCSerialPortConnection() {

	}

	@Override
	public List<String> listAvailable(boolean refresh) {
		ArrayList<String> res = new ArrayList<String>();
		String[] portNames = SerialPortList.getPortNames();
		for(int i=0;i<portNames.length;i++){
			res.add(portNames[i]);
		}
		return res;
	}

	@Override
	public void close() {
		 try {
	           sp.closePort();
	        } catch (SerialPortException e) {
	            e.printStackTrace();
	        }

	}

	@Override
	public void open() throws IOException {
		try {
			this.sp = new SerialPort(this.settings.name);
			this.sp.openPort();
			this.sp.setParams(settings.baudRate, this.settings.dataBits, this.settings.stopBits, this.settings.parity);

			this.spis = new JSSCInputStream(sp);
			this.ous = new JSSCOutputStream(sp);
		} catch (SerialPortException ex) {
			throw new IOException(ex);
		}

	}

	@Override
	public void open(SerialPortSetting setting) throws IOException {
		this.setSerialPortSetting(setting);
		this.open();
		try{
			setUpSerialPortDataListener();
		} catch (SerialPortException ee) {
			throw new IOException(ee);
		}
	}

	@Override
	public void setSerialPortSetting(SerialPortSetting setting) {
		this.settings=setting;
	}

	@Override
	public SerialPortSetting getSerialPortSetting() {
		return this.settings;
	}

	@Override
	public boolean isOpened() {
		if(sp!=null){
			return sp.isOpened();
		}
		return false;
	}

	protected void setUpSerialPortDataListener() throws SerialPortException {
		if (null == sp)
			return;

		int mask = SerialPort.MASK_RXCHAR;
		sp.setEventsMask(mask);
		sp.addEventListener(new SerialPortEventListener() {

			@Override
			public void serialEvent(SerialPortEvent event) {
				 if (event.isRXCHAR()) {

			            try {

			                if (spis.available() > 0) {
			                	inputStream.accept(spis);
			                }
			            } catch (IOException e) {
			                e.printStackTrace();
			            }

			    }

			}
		});

	}

}
