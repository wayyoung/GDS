/*
 * Created on Feb 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet.printer;

import java.util.Arrays;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ASCIICharacterFilter{
	//private Logger log=LoggerFactory.getLogger(getClass().getName());
	int NORMAL=0;
	int IN_CTRL_LB=1;
	int IN_ESCAPE=2;
	
	int state=NORMAL;
	StringBuffer stb=new StringBuffer();
	
	String receivedCC="";
	
	char[] CONTROL_END_CHARS=new char[]{'A','B','C','D','f','g','H','h','J','K','l','m','p','s','u'};
	public String filter(String source)throws UnExceptedControlCodeException{
		stb.delete(0,stb.length());
		char[] chars=source.toCharArray();
		for(int i=0;i<chars.length;i++){
			if(state==NORMAL){
				if(chars[i]==15){
//					logger.warn("not implemented ASCII CODE:"+(byte)chars[i]);
				}else if(chars[i]==27){
					receivedCC="";
					state=IN_CTRL_LB;
				}else if(chars[i]==8){
					if(stb.length()>0){
						stb.deleteCharAt(stb.length()-1);
					}
				}else{
					if(chars[i]!='\u0000'){
						stb.append(chars[i]);
					}
				}
			}else if(state==IN_CTRL_LB){
				if(chars[i]=='['){
					receivedCC=receivedCC+chars[i];
					state=IN_ESCAPE;
				}else if(chars[i]=='J'){
					receivedCC=receivedCC+chars[i];
					state=NORMAL;
					stb.append('\n');
				}else if(chars[i]=='M'){
					receivedCC=receivedCC+chars[i];
					state=NORMAL;
					stb.append('\r');
				}else{
					state=NORMAL;
//					throw new UnExceptedControlCodeException("unknow Code after CTRL:"+(byte)chars[i]);
				}
			}else if(state==IN_ESCAPE){
				if(Arrays.binarySearch(CONTROL_END_CHARS,chars[i])!=-1){
					receivedCC=receivedCC+chars[i];
					if(receivedCC.equals("[H")){
						stb.append("\r\n");
					}else if(receivedCC.equals("[J")){
						stb.append("\r\n");
					}
					receivedCC="";
					state=NORMAL;
				}
			}
		}
		if(stb.length()>0){
			return stb.toString();
		}else{
			return "";
		}
	}
	
}
class UnExceptedControlCodeException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2461341685623239220L;

	public UnExceptedControlCodeException(String msg){
		super(msg);
	}
}
