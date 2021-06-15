package pl.edu.pum.movie_downloader.viemodel;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.api.services.youtube.model.Video;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import pl.edu.pum.movie_downloader.data.vimeo.VimeoDataAPI;
import pl.edu.pum.movie_downloader.data.youtube.YouTubeFormatAPI;
import pl.edu.pum.movie_downloader.players.youtube.YouTubePlayer;

public class PageViewModel extends ViewModel {
    private final MutableLiveData<String> mLinkMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Pair<Integer, String>>> mFormatData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mClipLayoutVisibility = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPlayerVisibility = new MutableLiveData<>();
    //YouTube state
    private final MutableLiveData<YouTubeFormatAPI> mYouTubeDownloadURLMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Video> mVideoMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<YouTubePlayer> mYouTubePlayerMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<YouTubePlayerView> mYouTubePlayerViewMutableLiveData = new MutableLiveData<>();
    //VimeoState
    private final MutableLiveData<VimeoDataAPI> mVimeoDataAPIMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<String>  getLinkMutableLiveData() {
        return this.mLinkMutableLiveData;
    }

    public void setLinkMutableLiveData(String link){
        this.mLinkMutableLiveData.setValue(link);
    }

    public MutableLiveData<VimeoDataAPI> getVimeoDataAPIMutableLiveData(){
        return mVimeoDataAPIMutableLiveData;
    }

    public void setVimeoDataAPIMutableLiveData(VimeoDataAPI vimeoDataAPI){
        this.mVimeoDataAPIMutableLiveData.setValue(vimeoDataAPI);
    }

    public MutableLiveData<YouTubePlayerView> getYouTubePlayerViewMutableLiveData() {
        return mYouTubePlayerViewMutableLiveData;
    }

    public void setYouTubePlayerViewMutableLiveData(YouTubePlayerView mYouTubePlayerViewMutableLiveData) {
        this.mYouTubePlayerViewMutableLiveData.setValue(mYouTubePlayerViewMutableLiveData);
    }
    public MutableLiveData<Integer> getPlayerVisibility() {
        return mPlayerVisibility;
    }

    public void setPlayerVisibility(Integer playerVisibility) {
        this.mPlayerVisibility.setValue(playerVisibility);
    }

    public void setYouTubePlayerMutableLiveData(YouTubePlayer youTubePlayer){
        this.mYouTubePlayerMutableLiveData.setValue(youTubePlayer);
    }

    public MutableLiveData<YouTubePlayer> getYouTubePlayerMutableLiveData(){
        return this.mYouTubePlayerMutableLiveData;
    }

    public void setVideoMutableLiveData(Video video){
        this.mVideoMutableLiveData.setValue(video);
    }

    public MutableLiveData<Video> getVideoMutableLiveData(){
        return mVideoMutableLiveData;
    }

    public void setYouTubeDownloadURLMutableLiveData(YouTubeFormatAPI youTubeFormatAPI){
        this.mYouTubeDownloadURLMutableLiveData.setValue(youTubeFormatAPI);
    }

    public MutableLiveData<YouTubeFormatAPI> getYouTubeDownloadURLMutableLiveData(){
        return mYouTubeDownloadURLMutableLiveData;
    }

    public MutableLiveData<List<Pair<Integer, String>>> getFormatDataList(){
        return mFormatData;
    }

    public void setFormatDataList(List<Pair<Integer, String>> radioButtonList){
        mFormatData.setValue(radioButtonList);
    }

    public MutableLiveData<Integer> getClipLayoutVisibility() {
        return mClipLayoutVisibility;
    }

    public void setClipLayoutVisibility(int isVisible) {
        this.mClipLayoutVisibility.setValue(isVisible);
    }
}
