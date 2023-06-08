package me.srrapero720.watermedia.compat;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;

public class YoutubeCompatTest {
    public static void main(String[] arg) {
        var url = new YoutubeDownloader().getVideoInfo(new RequestVideoInfo("1kXWYD5rhz8")).data().bestVideoWithAudioFormat().url();
        System.out.println(url);
    }
}
