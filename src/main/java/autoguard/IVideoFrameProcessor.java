package autoguard;

import java.io.IOException;

import org.bytedeco.javacpp.avutil.AVFrame;

import autoguard.AVProcessor.Status;

public interface IVideoFrameProcessor {
	public Status processVideoFrame(AVFrame pFrame, int width, int height, int iFrame) throws IOException;
}
