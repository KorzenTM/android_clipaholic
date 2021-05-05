package pl.edu.pum.movie_downloader.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;
import pl.edu.pum.movie_downloader.players.youtube.YouTubePlayer;

public class ClipInformationFragment extends Fragment {
    private EditText mLinkEditText;
    private YouTubePlayer mYouTubePlayer;

    public ClipInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clip_information, container, false);
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);

        Button mCheckLinkButton = view.findViewById(R.id.check_link_button);
        mLinkEditText = view.findViewById(R.id.link_edit_text);

        mCheckLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = mLinkEditText.getText().toString();
                if (!link.isEmpty()) {
                    updateUI();
                    //YouTubePlayerView mYouTubePlayerView = view.findViewById(R.id.youtube_player_window);
                    //mYouTubePlayer = new YouTubePlayer(link, mYouTubePlayerView);
                    //getLifecycle().addObserver(mYouTubePlayer.getYouTubePlayerView());
                    //mYouTubePlayer.start();
                } else {
                    mLinkEditText.setError("This field cannot be empty!");
                }
            }
        });
        return view;
    }

    public void updateUI() {
        //mLinkEditText.setText("");
        //show clip information
        RelativeLayout clip_layout = ClipInformationFragment.this.requireView().findViewById(R.id.clip_information_relative_layout);
        clip_layout.setVisibility(View.VISIBLE);
        Animation emerge = AnimationUtils.loadAnimation(this.requireContext(), R.anim.emerge);
        clip_layout.startAnimation(emerge);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mYouTubePlayer.release();
    }
}