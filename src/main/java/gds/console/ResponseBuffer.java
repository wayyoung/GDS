package gds.console;


public class ResponseBuffer{

	/**
	 * 
	 */
//	private static final long serialVersionUID = 1333L;
	boolean enabled=true;
	private StringBuffer buffer=new StringBuffer();
	
	public synchronized boolean isEnabled() {
		return enabled;
	}
	public synchronized void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int length(){
		return this.buffer.length();
	}
	
	public synchronized int indexOf(String str){
		return buffer.indexOf(str);
	}

	public synchronized void delete(int start,int end){
		buffer.delete(start, end);
	}
	public synchronized void clear(){
		buffer.delete(0, buffer.length());
	}
	
	public synchronized boolean contains(String str){
		return (buffer.indexOf(str)>=0);
	}
	
	
	public synchronized void append(CharSequence str){
		buffer.append(str);
	}
	
	public synchronized void append(char[] str){
		buffer.append(str);
		
	}
	
	public synchronized void append(String str){
		buffer.append(str);
	}
	
	@Override
	public synchronized String toString(){
		return this.buffer.toString();
	}
}
