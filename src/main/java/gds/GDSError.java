package gds;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/30.
 */
public class GDSError extends RuntimeException implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4662307845527003220L;
	public GDSError(Throwable e){
        super(e);
    }
    public GDSError(String msg, Throwable e){
        super(msg,e);
    }
    public GDSError(String msg){
        super(msg);
    }
}
