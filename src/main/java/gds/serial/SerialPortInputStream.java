package gds.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InputStream implementation for Console
 */
public class SerialPortInputStream extends InputStream {

	private static Logger logger=LoggerFactory.getLogger(SerialPortInputStream.class.getName());
//	private static final int EOF = -1;
	private boolean closed = false;

	private boolean isDisposed = false;

//	private InputStream source;
	ByteBuffer bf;// = ByteBuffer.allocate(4096 * 4).limit(0);

	public SerialPortInputStream() {
		bf = ByteBuffer.allocate(4096 * 4);
		bf.limit(0);
	}

//	public void setSource(InputStream source) {
//		this.source = source;
//	}

	public synchronized void accept(InputStream source) throws IOException {
		int len = 0;
		while ((source.available()>0) && (len = (bf.capacity() - bf.limit())) > 0) {

			len=source.read(bf.array(), bf.limit(), len);
			if(len<=0){
				break;
			}
			bf.limit(bf.limit()+len);
		}
		notifyAll();

	}

	@Override
	public void close() throws IOException {
		if (isDisposed) {
			return;
		}

		// buffer = new ByteBufferQueue();
		closed = false;
	}

	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		this.read(b, 0, 1);
//		System.out.println(""+b);
		return b[0];
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		while (!bf.hasRemaining()) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new InterruptedIOException();
			}
		}
		len = (len > bf.remaining()) ? bf.remaining() : len;
		bf.get(b, off, len).compact().flip();
		return len;
	}

	/**
	 * Close ConsoleInputStream forever
	 */
	public void dispose() {
		isDisposed = true;
		closed = true;
		synchronized (this) {
			notifyAll();
		}
	}

	public boolean isClosed(){
		return closed;
	}
	/**
	 * Sets the InputStream to closed state (EOF), when is reached the end of
	 * stream by read method
	 */
	public void setCloseState() {
		closed = true;

		synchronized (this) {
			notifyAll();
		}
	}
}