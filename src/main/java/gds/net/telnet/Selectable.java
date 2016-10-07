/*
 * Created on 2004/10/6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gds.net.telnet;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;

/**
 * @author ken
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Selectable {
	public  Object 	blockingLock();
    
	public   SelectableChannel 	configureBlocking(boolean block);
    
	public   boolean 	isBlocking();
    
	public   boolean 	isRegistered();
    
	public   SelectionKey 	keyFor(Selector sel);
    
	public   SelectorProvider 	provider();
    
	public SelectionKey 	register(Selector sel, int ops);
    
	public   SelectionKey 	register(Selector sel, int ops, Object att);
    
	public   int 	validOps() ;
}
