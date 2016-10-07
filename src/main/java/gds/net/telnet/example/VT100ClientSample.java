/*
 * Created on 2004/10/4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.example;



import gds.net.TelnetClient;
import gds.net.telnet.printer.SimpleConsolePrinter;
import gds.net.telnet.printer.SimpleFilePrinter;


public class VT100ClientSample {

	/*
	 * @see TestCase#setUp()
	 */
	
	public static void m1()throws Exception{
		TelnetClient vt=new TelnetClient("192.168.1.123",23);
		vt.addPrinter(new SimpleConsolePrinter());
		vt.connect();
		
		vt.expect("login:",30);
		vt.send("ken");
		vt.expect("Password:",30);
		vt.send("ab1234");
		vt.expect("ken\\]\\$",30);
		vt.send("ls -al");
		vt.expect("ken\\]\\$",30);
	}
	
	public static void main(String[] args)throws Exception{
		m3();
	}
	
	
	public static void m2()throws Exception{
		TelnetClient vt=new TelnetClient("9.191.55.199",623);
		vt.addPrinter(new SimpleFilePrinter("c:/native.txt"));
		vt.addPrinter(new SimpleFilePrinter("c:/out.txt"));

		
		vt.setUsername("sae1");
		vt.setPassword("wb1forfn");
		vt.login("login:","Password:");
		vt.cmd("export LANG=C");
		vt.cmd("ls");
		vt.cmd("ls -al");
//		vt.asynchold();
//		Thread.sleep(15000);
//		vt.stopHolding();
	}
	
	public static void m3()throws Exception{
		while(true){
			TelnetClient vt=new TelnetClient("www.glimsoft.com",23);
//			vt.addPrinter(new SimpleFilePrinter("c:/native.txt"));
//			vt.addPrinter(new SimpleFilePrinter("c:/out.txt"));
			vt.addPrinter(new SimpleConsolePrinter());
			vt.setPrompt("\\]\\$");
			vt.setUsername("ken");
			vt.setPassword("ab1234cd");
			vt.login("login:","Password:",vt.getPrompt());
			vt.cmd("export LANG=C");
			vt.cmd("ls");
			vt.cmd("ls -al");
//			vt.send("top");
//			vt.hold(15000);
//			vt.disconnect();
			Thread.sleep(2000);
			vt.disconnect();
			
		}
	}


}
