package autoguard;

/**
 * Created by causer on 3/28/16.
 */
public class VersionMatchingFailedException extends RuntimeException {
	public VersionMatchingFailedException(){

	}
    public VersionMatchingFailedException(Throwable ex){
        super(ex);
    }
    public VersionMatchingFailedException(String message,Throwable ex){
        super(message,ex);
    }
}
