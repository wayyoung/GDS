/*
 * Created on 2005/2/26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet;

import java.nio.ByteBuffer;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NonBlockingPipe {
	ByteBuffer readerBuffer;
	ByteBuffer writterBuffer;
	int incremental=1024;
	
	public NonBlockingPipe(int readerBufferLength,int writterBufferLength){
		readerBuffer=ByteBuffer.allocate(readerBufferLength);
		writterBuffer=ByteBuffer.allocate(writterBufferLength);
	}
	
	public NonBlockingPipe(){
		this(2048,2048);
	}
	
	private void moveDataFromWritterToReader(){
		writterBuffer.flip();
		
		if(readerBuffer.remaining()>writterBuffer.remaining()){
			readerBuffer.put(writterBuffer);
		}else{
			byte[] data=new byte[readerBuffer.remaining()];
			writterBuffer.get(data);
			readerBuffer.put(data);
		}
		readerBuffer.flip();
		writterBuffer.compact();
	}
	
	public int read(ByteBuffer bfr){
		moveDataFromWritterToReader();
		
		int result=readerBuffer.remaining();
		if(bfr.remaining()>readerBuffer.remaining()){
			bfr.put(readerBuffer);
		}else{
			byte[] data=new byte[bfr.remaining()];
			readerBuffer.get(data);
			bfr.put(data);
			result=data.length;
		}
		readerBuffer.compact();
		return result;
	}
	public int write(ByteBuffer bfr){
		int length=bfr.remaining();
		
		while(writterBuffer.remaining()<length){
			increaseWritterBuffer();
		}
		
		writterBuffer.put(bfr);
		
		return length;
	}
	
	private void increaseWritterBuffer(){
		int newLength=writterBuffer.capacity()+incremental;
		writterBuffer=ByteBuffer.allocate(newLength).put((ByteBuffer)writterBuffer.flip());
	}
	public static void main(String[] args){
		NonBlockingPipe pipe=new NonBlockingPipe();
		pipe.write((ByteBuffer)ByteBuffer.allocate(512).put(new String("1234567890ABCDEFG").getBytes()).flip());
		ByteBuffer bfr=ByteBuffer.allocate(512);
		pipe.read(bfr);
		bfr.flip();
		
		byte[] data=new byte[bfr.remaining()];
		bfr.get(data);
		System.out.println(new String(data));
	}
}
