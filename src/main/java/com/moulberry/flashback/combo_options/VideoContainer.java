package com.moulberry.flashback.combo_options;

import org.bytedeco.ffmpeg.avformat.AVOutputFormat;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;

import java.util.ArrayList;
import java.util.List;

public enum VideoContainer implements ComboOption {

    MP4("MP4", "mp4"),
    MKV("MKV", "mkv"),
    AVI("AVI", "avi"),
    MOV("MOV", "mov");

    private final String text;
    private final String extension;
    private VideoCodec[] supportedCodecs = null;

    VideoContainer(String text, String extension) {
        this.text = text;
        this.extension = extension;
    }

    @Override
    public String text() {
        return this.text;
    }

    public String extension() {
        return extension;
    }

    public VideoCodec[] getSupportedCodecs() {
        if (this.supportedCodecs == null) {
            List<VideoCodec> supportedCodecs = new ArrayList<>();
            try (AVOutputFormat outputFormat = avformat.av_guess_format(this.extension, "test."+this.extension, null)) {
                for (VideoCodec codec : VideoCodec.values()) {
                    if (codec == VideoCodec.AV1 && this != VideoContainer.MP4) {
                        continue;
                    }
                    if (codec.getEncoders().length == 0) {
                        continue;
                    }

                    int ret = avformat.avformat_query_codec(outputFormat, codec.codecId(), avcodec.FF_COMPLIANCE_NORMAL);
                    if (ret == 1) {
                        supportedCodecs.add(codec);
                    }
                }
            }

            this.supportedCodecs = supportedCodecs.toArray(new VideoCodec[0]);
        }
        return this.supportedCodecs;
    }

}
