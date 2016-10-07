package gds.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.Writer;
import java.util.Hashtable;

import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionLogWriter extends Hashtable<String,Writer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedWriter backendWriter=null;	
	Timer flushTimer;
	private static Logger logger=LoggerFactory.getLogger(SessionLogWriter.class.getName());
	
	public SessionLogWriter(){
		super();
		flushTimer = new Timer( (int) (1000) , new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				SessionLogWriter.this.flush();
			}
		});
		
		flushTimer.start();
	}

	public BufferedWriter getBackendWriter() {
		return backendWriter;
	}

	public synchronized void setBackendWriter(BufferedWriter backendWriter) {
		this.backendWriter = backendWriter;
	}

	
	
	
	public synchronized void write(char[] cbuf, int off, int len) {
		flushTimer.start();
		for (String k : this.keySet()) {
			if(null!=this.get(k)){
				try{
					this.get(k).write(cbuf, off, len);
				}catch(Exception ex){
					logger.error("ERROR!!",ex);
				}
			}
		} 
		if(backendWriter!=null){
			try{
				backendWriter.write(cbuf, off, len);
				
			}catch(Exception ex){
				try{
					backendWriter.flush();
					backendWriter.close();
				}catch(Exception exx){
					logger.warn("WARNING!!",exx);
				}finally{
					backendWriter=null;
				}
			}
		}
		
	}
	
//	public void write(char[] cbuf, int off, int len) {
//		
//		if(backendWriter!=null){
//			try{
//				backendWriter.write(cbuf, off, len);
//			}catch(Exception ex){
//				try{
//					backendWriter.flush();
//					backendWriter.close();
//				}catch(Exception exx){
//					logger.warn(exx);
//				}finally{
//					backendWriter=null;
//				}
//			}
//		}
//		
//	}
	
	public synchronized void write(String str) {
		flushTimer.start();
		for (String k : this.keySet()) {
			if(null!=this.get(k)){
				try{
					this.get(k).write(str);
				}catch(Exception ex){
					logger.error("ERROR!!",ex);
				}
			}
		} 
		if(backendWriter!=null){
			try{
				backendWriter.write(str);
			}catch(Exception ex){
				try{
					backendWriter.flush();
					backendWriter.close();
				}catch(Exception exx){
					logger.warn("WARNING!!",exx);
				}finally{
					backendWriter=null;
				}
			}
		}
		
	}

	
	public synchronized void flush() {
		for (String k : this.keySet()) {
			if(null!=this.get(k)){
				try{
					this.get(k).flush();
				}catch(Exception ex){
					logger.error("ERROR!!",ex);
				}
			}
		} 
		if(backendWriter!=null){
			try{
				backendWriter.flush();
			}catch(Exception ex){
				try{
					backendWriter.flush();
					backendWriter.close();
				}catch(Exception exx){
					logger.warn("WARNING!!",exx);
				}finally{
					backendWriter=null;
				}
			}
		}
	}

	public synchronized void close() {
		for (String k : this.keySet()) {
			if(null!=this.get(k)){
				try{
					this.get(k).flush();
				}catch(Exception ex){
					logger.error("ERROR!!",ex);
				}
			}
		} 
		if(backendWriter!=null){
			try{
				backendWriter.close();
			}catch(Exception ex){
				try{
					backendWriter.flush();
					backendWriter.close();
				}catch(Exception exx){
					logger.warn("WARNING!!",exx);
				}
			}finally{
				backendWriter=null;
			}
		}
		
		flushTimer.stop();
	}
	
	
	public synchronized void append(CharSequence cs){
		flushTimer.start();
		for (String k : this.keySet()) {
			if(null!=this.get(k)){
				try{
					this.get(k).write(cs.toString());
				}catch(Exception ex){
					logger.error("ERROR!!",ex);
				}
			}
		} 
		if(backendWriter!=null){
			try{
				backendWriter.write(cs.toString());
			}catch(Exception ex){
				try{
					backendWriter.flush();
					backendWriter.close();
				}catch(Exception exx){
					logger.warn("WARNING!!",exx);
				}finally{
					backendWriter=null;
				}
			}
		}
	}
	
	public synchronized void append(char cs){
		flushTimer.start();
		for (String k : this.keySet()) {
			if(null!=this.get(k)){
				try{
					this.get(k).write(cs);
				}catch(Exception ex){
					logger.error("ERROR!!",ex);
				}
			}
		} 
		if(backendWriter!=null){
			try{
				backendWriter.write(cs);
			}catch(Exception ex){
				try{
					backendWriter.flush();
					backendWriter.close();
				}catch(Exception exx){
					logger.warn("WARNING!!",exx);
				}finally{
					backendWriter=null;
				}
			}
		}
	}

}
