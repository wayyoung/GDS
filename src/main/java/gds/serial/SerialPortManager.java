package gds.serial;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gds.LibraryLoader;
import gds.WinRegistry;

public class SerialPortManager {
	private static Logger logger = LoggerFactory.getLogger(SerialPortManager.class.getName());
	private static HashMap<Integer,String> friendlyNameMap=new HashMap<Integer,String>();
	private static Pattern patternCOM=Pattern.compile("\\d+");
	private static Constructor<ISerialPortConnection> serialConnectionConstructor;
	public static boolean SERIAL_DRIVER_LOADED = false;

	public static boolean SERIAL_DRIVER_IS_SERIALIO = false;
	public static boolean SERIAL_DRIVER_IS_JSSC = false;
	private static ISerialPortConnection siocon;
	private static void loadSerailDriverNativeLibrary() {
		if (SERIAL_DRIVER_LOADED == true)
			return;

		try {
			if (LibraryLoader.instance().loadLibrary("Serialio/impl", "SerialIO")){
				SERIAL_DRIVER_IS_SERIALIO = true;
			}else if(LibraryLoader.instance().loadLibrary("libs", "jSSC-2.8")){
				SERIAL_DRIVER_IS_JSSC = true;
			}else{
				LibraryLoader.instance().loadLibrary("gnu/io/impl/serial", "rxtxSerial");
			}
		} catch (Exception ee) {

			LibraryLoader.instance().loadLibrary("gnu/io/impl/serial", "rxtxSerial");
//			SERIAL_DRIVER_IS_SERIALIO = false;
		}
		SERIAL_DRIVER_LOADED = true;
	}

	private static void load() {
		if (siocon == null) {
			String serialConnectionClassName = "gds.serial.RxTxSerialPortConnection";
			loadSerailDriverNativeLibrary();
			if (SERIAL_DRIVER_IS_SERIALIO) {
				serialConnectionClassName = "Serialio.SerialIOConnection";
			}else if(SERIAL_DRIVER_IS_JSSC){
				serialConnectionClassName="gds.serial.JSSCSerialPortConnection";
			}


			try {
//				@SuppressWarnings("rawtypes")
				serialConnectionConstructor = (Constructor<ISerialPortConnection>) Class.forName(serialConnectionClassName).getConstructor();
				siocon = serialConnectionConstructor.newInstance();
			} catch (Exception e) {
				logger.error("ERROR!!", e);
			}
			readFriendlyNameMap();
		}


	}

	private static void readFriendlyNameMap(){

		try{

			if(LibraryLoader.getOsClass().equals(LibraryLoader.OS_WINDOWS)){
				String regPath="SYSTEM\\CurrentControlSet\\Enum\\USB\\";
				for (String idKey : WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, regPath) ) {
					for (String deviceKey : WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, regPath+idKey+"\\")){
						String friendlyName=WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, regPath+idKey+"\\"+deviceKey, "FriendlyName");
						 if (friendlyName != null && friendlyName.indexOf("COM") >= 0) {
							List<String> controls=WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, regPath+idKey+"\\"+deviceKey+"\\Control\\");
							if(controls!=null){
						        String substr = friendlyName.substring(friendlyName.indexOf("COM"));
						        Matcher matchInt = patternCOM.matcher(substr);
						        if (matchInt.find()) {
						        	friendlyNameMap.put(Integer.parseInt(matchInt.group()),friendlyName);
						        }
							}
						 }
					}
				}

				regPath="SYSTEM\\CurrentControlSet\\Enum\\FTDIBUS\\";
				for (String idKey : WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, regPath) ) {
					for (String deviceKey : WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, regPath+idKey+"\\")){
						String friendlyName=WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, regPath+idKey+"\\"+deviceKey, "FriendlyName");
						 if (friendlyName != null && friendlyName.indexOf("COM") >= 0) {
							List<String> controls=WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, regPath+idKey+"\\"+deviceKey+"\\Control\\");
							if(controls!=null){
						        String substr = friendlyName.substring(friendlyName.indexOf("COM"));
						        Matcher matchInt = patternCOM.matcher(substr);
						        if (matchInt.find()) {
						        	friendlyNameMap.put(Integer.parseInt(matchInt.group()),friendlyName);
						        }
							}
						 }
					}
				}

			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static List<String> listAvailable(boolean refresh) {

		List<String> result=new ArrayList<String>();
		load();
		if(refresh){
			friendlyNameMap.clear();
			readFriendlyNameMap();
		}
		for (String str : siocon.listAvailable(refresh)) {
			if(LibraryLoader.getOsClass().equals(LibraryLoader.OS_WINDOWS)){
				Matcher matchInt = patternCOM.matcher(str);
		        if (matchInt.find()) {
		        	String fName=friendlyNameMap.get(Integer.parseInt(matchInt.group()));
		        	if(fName!=null && fName.length()>0){
		        		str+=" - "+fName;
		        	}
		        }
			}
			result.add(str);
		}

		return result;
	}

	public static ISerialPortConnection newConnection() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		load();
		return serialConnectionConstructor.newInstance();
	}
}
