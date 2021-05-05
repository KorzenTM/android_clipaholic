package pl.edu.pum.movie_downloader.players.youtube;

import android.util.Log;
import android.view.View;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

public class YouTubePlayer {
    private final YouTubePlayerView mYouTubePlayerView;
    private final String mLink;

    public YouTubePlayer(String URL, YouTubePlayerView youTubePlayerView) {

        this.mLink = URL;
        this.mYouTubePlayerView = youTubePlayerView;
    }

    public String getURL()
    {
        return mLink;
    }

    public YouTubePlayerView getYouTubePlayerView() {return mYouTubePlayerView;}

    public void start() {
        mYouTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                String videoId = "1FJHYqE0RDg";
                youTubePlayer.loadVideo(videoId, 0);
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
        mYouTubePlayerView.release();
    }

}
