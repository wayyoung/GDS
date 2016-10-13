package gds.ffmpeg;

import static org.bytedeco.javacpp.avcodec.av_free_packet;
import static org.bytedeco.javacpp.avcodec.avcodec_close;
import static org.bytedeco.javacpp.avcodec.avcodec_decode_video2;
import static org.bytedeco.javacpp.avcodec.avcodec_find_decoder;
import static org.bytedeco.javacpp.avcodec.avcodec_open2;
import static org.bytedeco.javacpp.avcodec.avpicture_fill;
import static org.bytedeco.javacpp.avcodec.avpicture_get_size;
import static org.bytedeco.javacpp.avformat.av_dump_format;
import static org.bytedeco.javacpp.avformat.av_read_frame;
import static org.bytedeco.javacpp.avformat.av_register_all;
import static org.bytedeco.javacpp.avformat.avformat_close_input;
import static org.bytedeco.javacpp.avformat.avformat_find_stream_info;
import static org.bytedeco.javacpp.avformat.avformat_network_init;
import static org.bytedeco.javacpp.avformat.avformat_open_input;
import static org.bytedeco.javacpp.avutil.AVMEDIA_TYPE_VIDEO;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_RGB24;
import static org.bytedeco.javacpp.avutil.av_frame_alloc;
import static org.bytedeco.javacpp.avutil.av_free;
import static org.bytedeco.javacpp.avutil.av_malloc;
import static org.bytedeco.javacpp.swscale.SWS_BILINEAR;
import static org.bytedeco.javacpp.swscale.sws_getContext;
import static org.bytedeco.javacpp.swscale.sws_scale;

import java.io.IOException;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec.AVCodec;
import org.bytedeco.javacpp.avcodec.AVCodecContext;
import org.bytedeco.javacpp.avcodec.AVPacket;
import org.bytedeco.javacpp.avcodec.AVPicture;
import org.bytedeco.javacpp.avformat.AVFormatContext;
import org.bytedeco.javacpp.avutil.AVDictionary;
import org.bytedeco.javacpp.avutil.AVFrame;
import org.bytedeco.javacpp.swscale.SwsContext;

public class AVProcessor {
	public enum Status{
		OK,STOP;
	}
	String source;
    public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	String options;
	
	
	IVideoFrameProcessor videoFrameProcessor;

    public void processVideo(int videoStreamIndex) throws IOException {
		AVFormatContext pFormatCtx = new AVFormatContext(null);
		int             i, videoStream;
		AVCodecContext  pCodecCtx = null;
		AVCodec         pCodec = null;
		AVFrame         pFrame = null;
		AVFrame         pFrameRGB = null;
		AVPacket        packet = new AVPacket();
		int[]           frameFinished = new int[1];
		int             numBytes;
		BytePointer     buffer = null;

		AVDictionary    optionsDict = null;
		SwsContext      sws_ctx = null;


		
		// Register all formats and codecs
		av_register_all();
		avformat_network_init();
		// Open video file
		if (avformat_open_input(pFormatCtx,source, null, null) != 0) {
			throw new RuntimeException("Failed to open source:"+source);
		}


		// Retrieve stream information
		if (avformat_find_stream_info(pFormatCtx, (PointerPointer)null) < 0) {
			throw new RuntimeException("Failed to find stream information"); // Couldn't find stream information
		}
		// Dump information about file onto standard error
		av_dump_format(pFormatCtx, 0, source, 0);
		// Find the first video stream
		videoStream = -1;
		int count=0;
		for (i = 0; i < pFormatCtx.nb_streams(); i++) {
			if (pFormatCtx.streams(i).codec().codec_type() == AVMEDIA_TYPE_VIDEO) {
				if(count==videoStreamIndex){
					videoStream = i;
					break;
				}else{
					count++;
				}
			}
		}
		if (videoStream == -1) {
			throw new RuntimeException("Failed to find video stream for videoStreamIndex:"+videoStreamIndex+" in count:"+count); // Didn't find a video stream
		}
		// Get a pointer to the codec context for the video stream
		pCodecCtx = pFormatCtx.streams(videoStream).codec();
		// Find the decoder for the video stream
		pCodec = avcodec_find_decoder(pCodecCtx.codec_id());
		if (pCodec == null) {
			throw new RuntimeException("Unsupported codec!");
		}
		// Open codec
		if (avcodec_open2(pCodecCtx, pCodec, optionsDict) < 0) {
			throw new RuntimeException("ERROR!!");
		}
		// Allocate video frame
		pFrame = av_frame_alloc();

		// Allocate an AVFrame structure
		pFrameRGB = av_frame_alloc();
		if(pFrameRGB == null) {
			System.exit(-1);
		}

		// Determine required buffer size and allocate buffer
		numBytes = avpicture_get_size(AV_PIX_FMT_RGB24,
				pCodecCtx.width(), pCodecCtx.height());
		buffer = new BytePointer(av_malloc(numBytes));

		sws_ctx = sws_getContext(pCodecCtx.width(), pCodecCtx.height(),
				pCodecCtx.pix_fmt(), pCodecCtx.width(), pCodecCtx.height(),
				AV_PIX_FMT_RGB24, SWS_BILINEAR, null, null, (DoublePointer)null);

		// Assign appropriate parts of buffer to image planes in pFrameRGB
		// Note that pFrameRGB is an AVFrame, but AVFrame is a superset
		// of AVPicture
		avpicture_fill(new AVPicture(pFrameRGB), buffer, AV_PIX_FMT_RGB24,
				pCodecCtx.width(), pCodecCtx.height());

		// Read frames and save first five frames to disk
		i = 0;
		while (av_read_frame(pFormatCtx, packet) >= 0) {
			// Is this a packet from the video stream?
			if (packet.stream_index() == videoStream) {
				// Decode video frame
				avcodec_decode_video2(pCodecCtx, pFrame, frameFinished, packet);
			

				// Did we get a video frame?
				if (frameFinished[0] != 0) {
					// Convert the image from its native format to RGB
					sws_scale(sws_ctx, pFrame.data(), pFrame.linesize(), 0,
							pCodecCtx.height(), pFrameRGB.data(), pFrameRGB.linesize());
					
					Status st=videoFrameProcessor.processVideoFrame(pFrameRGB, pCodecCtx.width(), pCodecCtx.height(), i);
					i++;
					if(Status.STOP==st){
						av_free_packet(packet);
						break;
					}
				
				}
			}

			// Free the packet that was allocated by av_read_frame
			av_free_packet(packet);
		}

		// Free the RGB image
		av_free(buffer);
		av_free(pFrameRGB);

		// Free the YUV frame
		av_free(pFrame);

		// Close the codec
		avcodec_close(pCodecCtx);

		// Close the video file
		avformat_close_input(pFormatCtx);
    }

	public IVideoFrameProcessor getVideoFrameProcessor() {
		return videoFrameProcessor;
	}

	public void setVideoFrameProcessor(IVideoFrameProcessor videoFrameProcessor) {
		this.videoFrameProcessor = videoFrameProcessor;
	}
}
