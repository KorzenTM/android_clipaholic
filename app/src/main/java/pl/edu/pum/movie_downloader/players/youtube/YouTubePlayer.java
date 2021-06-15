package pl.edu.pum.movie_downloader.players.youtube;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubePlayer {
    public static YouTubePlayerView youTubePlayerView = null;
    private final Lifecycle mLifecycle;
    private String mYouTubeID;
    private com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer mYouTubePlayer;

    public YouTubePlayer(String URL, Lifecycle lifecycle) {

        this.mYouTubeID = getYouTubeId(URL);
        this.mLifecycle = lifecycle;
    }

    public String getClipID()
    {
        return mYouTubeID;
    }

    public void setClipID(String url) {this.mYouTubeID = getYouTubeId(url); }

    private String getYouTubeId(String youTubeUrl) {
        String pattern =
                "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public void init() {
        mLifecycle.addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                mYouTubePlayer = youTubePlayer;
                YouTubePlayerUtils.loadOrCueVideo(youTubePlayer, mLifecycle, mYouTubeID, 0f);
            }

            @Override
            public void onError(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
                Log.d("YouTubePlayerStatus", error.toString());
            }
        });
    }

    public void setNextVideo(){
        if (mYouTubePlayer != null){
            YouTubePlayerUtils.loadOrCueVideo(mYouTubePlayer, mLifecycle, mYouTubeID, 0f);
        }
    }
}
