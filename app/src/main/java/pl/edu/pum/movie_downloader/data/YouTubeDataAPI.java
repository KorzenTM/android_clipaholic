package pl.edu.pum.movie_downloader.data;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.List;

public class YouTubeDataAPI {
    private final String mClipID;
    private static final String APP_NAME = "pl.edu.pum.movie_downloader";
    private static final String API_KEY = "AIzaSyDk1azLe3s0JkDcJ3rHqglckDtDiUHaSPY";
    private YouTube.Videos.List videoRequest = null;

    public YouTubeDataAPI(String clipID){
        this.mClipID = clipID;
    }

    private void setRequest() {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                request -> {
                }).setApplicationName(APP_NAME).build();

        try {
            videoRequest = youtube.videos().list("snippet,statistics,contentDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert videoRequest != null;
        videoRequest.setId(mClipID);
        videoRequest.setKey(API_KEY);
    }

    private YouTube.Videos.List getRequest() {
        return videoRequest;
    }

    public Video getData() {
        setRequest();
        YouTube.Videos.List videoRequest = getRequest();

        VideoListResponse listResponse = null;
        try {
            listResponse = videoRequest.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert listResponse != null;
        List<Video> videoList = listResponse.getItems();

        return videoList.iterator().next();
    }
}
