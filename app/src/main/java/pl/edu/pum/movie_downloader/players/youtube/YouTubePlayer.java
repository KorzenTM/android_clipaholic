package pl.edu.pum.movie_downloader.players.youtube;

import android.util.Log;
import android.view.View;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubePlayer {
    public static YouTubePlayerView youTubePlayerView = null;
    private final String mYouTubeID;

    public YouTubePlayer(String URL, YouTubePlayerView playerView) {

        this.mYouTubeID = getYouTubeId(URL);
        youTubePlayerView = playerView;
    }

    public String getClipID()
    {
        return mYouTubeID;
    }

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

    public void start() {
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                youTubePlayer.loadVideo(mYouTubeID, 0);
                youTubePlayer.pause();
            }

            @Override
            public void onError(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
                Log.d("YouTubePlayerStatus", error.toString());
            }
        });
    }

    public void release()
    {
        youTubePlayerView.release();
    }

}
