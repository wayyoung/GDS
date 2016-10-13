package autoguard;

import java.io.File;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VLCJ {
	public boolean ERROR=false;
	public void saveStreamToFile(String mrl,String fileName,int seconds)throws Exception{
		new NativeDiscovery().discover();
		MediaPlayerFactory factory=new MediaPlayerFactory();
		EmbeddedMediaPlayer mediaPlayer = factory.newEmbeddedMediaPlayer();


		String[] options = {":sout=#duplicate{dst=file{dst="+fileName+"}}",":sout-all",":sout-keep"};


        try {
        	ERROR=false;
        	mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter(){
        		@Override
				public void error(MediaPlayer mplayer){
        			VLCJ.this.ERROR=true;
        		}
        	});
        	mediaPlayer.playMedia(mrl, options);
        	while(seconds>0){
        		if(ERROR){
        			throw new RuntimeException("VLCJ player ERROR!!");
        		}

	        	try{
	        		Thread.sleep(1000);
	        	}catch(Exception ex){

	        	}
	        	seconds-=1;
        	}
		} catch (Exception e) {
			throw e;
		}finally{
			try{
				mediaPlayer.stop();
		        mediaPlayer.release();
			}catch(Exception ex){

			}
		}

	}


	public static void main(String args[])throws Exception{
		VLCJ vlcj=new VLCJ();
		System.out.println("saving video to "+new File(args[1]).getCanonicalPath());
		vlcj.saveStreamToFile(args[0], args[1], Integer.parseInt(args[2]));
	}
}
