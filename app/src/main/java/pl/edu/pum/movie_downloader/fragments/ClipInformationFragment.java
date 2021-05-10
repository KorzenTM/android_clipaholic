package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ashudevs.facebookurlextractor.FacebookExtractor;
import com.ashudevs.facebookurlextractor.FacebookFile;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.api.services.youtube.model.Video;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.data.YouTubeDataAPI;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;
import pl.edu.pum.movie_downloader.players.youtube.YouTubePlayer;

public class ClipInformationFragment extends Fragment {
    private EditText mLinkEditText;
    private YouTubePlayer mYouTubePlayer = null;
    private TextView mTitleTextView;
    private TextView mCounterViewTextView;
    private TextView mDescriptionTextView;
    RelativeLayout clip_layout;

    public ClipInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireView()).navigate(R.id.home_fragment);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clip_information, container, false);
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);

        clip_layout = view.findViewById(R.id.clip_information_relative_layout);
        mTitleTextView = view.findViewById(R.id.title_text_view);
        mCounterViewTextView = view.findViewById(R.id.view_counter_text_view);
        mDescriptionTextView = view.findViewById(R.id.description_text_view);
        Button mCheckLinkButton = view.findViewById(R.id.check_link_button);
        mLinkEditText = view.findViewById(R.id.link_edit_text);
        mLinkEditText.setText("https://fb.watch/5oOXKl4hBF/");

        mCheckLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = mLinkEditText.getText().toString();
                if (!link.isEmpty()) {
                    if (link.contains("facebook") || link.contains("fb")) {
                        System.out.println("to jest link z facebooczka :)");
                        updateUI();
                        fb(link);
                    }
                    else if (link.contains("youtube")) {
                        updateUI();
                        showYouTubePlayer(link);
                    }
                    else if (link.contains("vimeo")) {
                        Toast.makeText(requireContext(), "Vimeo", Toast.LENGTH_SHORT).show();
                        //TODO handle vimeo link
                    } else {
                        mLinkEditText.setError("Wrong link or we do not support this source");
                    }
                } else {
                    mLinkEditText.setError("This field cannot be empty!");
                }
            }
        });
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void fb(String link) {
        new FacebookExtractor(requireContext(),"https://www.facebook.com/Juliusmagic/videos/1042033149627825/",false)
        {
            @Override
            protected void onExtractionComplete(FacebookFile facebookFile) {
                Log.e("TAG","---------------------------------------");
                Log.e("TAG","facebookFile AutherName :: "+facebookFile.getAuthor());
                Log.e("TAG","facebookFile FileName :: "+facebookFile.getFilename());
                Log.e("TAG","facebookFile Ext :: "+facebookFile.getExt());
                Log.e("TAG","facebookFile SD :: "+facebookFile.getSdUrl());
                Log.e("TAG","facebookFile HD :: "+facebookFile.getHdUrl());
                Log.e("TAG","---------------------------------------");
            }

            @Override
            protected void onExtractionFail(Exception error) {
                Log.e("Error","Error :: "+error.getMessage());
                error.printStackTrace();
            }
        };
    }

    private void showYouTubePlayer(String link) {
        YouTubePlayerView youTubePlayerView = requireView().findViewById(R.id.youtube_player_view);
        youTubePlayerView.setVisibility(View.VISIBLE);
        mYouTubePlayer = new YouTubePlayer(link, youTubePlayerView);
        getLifecycle().addObserver(YouTubePlayer.youTubePlayerView);
        mYouTubePlayer.start();

        Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                YouTubeDataAPI youTubeDataAPI = new YouTubeDataAPI(mYouTubePlayer.getClipID());
                Video targetVideo = youTubeDataAPI.getData();
                handler.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        mTitleTextView.setText(mTitleTextView.getText().toString() +
                                targetVideo.getSnippet().getTitle().toString());
                        mCounterViewTextView.setText(mCounterViewTextView.getText().toString() +
                                targetVideo.getStatistics().getViewCount().toString());
                        mDescriptionTextView.setText(mDescriptionTextView.getText().toString() +
                                targetVideo.getSnippet().getDescription());
                    }
                });
            }
        };
        new Thread(task).start();
    }

    private void updateUI() {
        //clean field if was used
        mDescriptionTextView.setText("");
        mCounterViewTextView.setText("");
        mTitleTextView.setText("");
        clip_layout.setVisibility(View.VISIBLE);
        Animation emerge = AnimationUtils.loadAnimation(this.requireContext(), R.anim.emerge);
        clip_layout.startAnimation(emerge);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
            mYouTubePlayer = null;
        }
    }
}