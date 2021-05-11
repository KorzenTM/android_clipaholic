package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ashudevs.facebookurlextractor.FacebookExtractor;
import com.ashudevs.facebookurlextractor.FacebookFile;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.data.YouTubeDataAPI;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadURL;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadUrlState;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;
import pl.edu.pum.movie_downloader.players.youtube.YouTubePlayer;


public class ClipInformationFragment extends Fragment {
    private EditText mLinkEditText;
    private YouTubePlayer mYouTubePlayer = null;
    private YouTubeDownloadURL mYouTubeDownloadURL;
    private YouTubeDataAPI youTubeDataAPI;
    private Video targetVideo = null;
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
        mLinkEditText = view.findViewById(R.id.link_edit_text);
        mLinkEditText.setText("https://www.youtube.com/watch?v=1e9B31FLT-s");

        Button mCheckLinkButton = view.findViewById(R.id.check_link_button);
        mCheckLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = mLinkEditText.getText().toString();
                if (!link.isEmpty()) {
                    if (link.contains("facebook") || link.contains("fb")) {
                        System.out.println("to jest link z facebooczka :)");
                    }
                    else if (link.contains("youtube")) {
                        setUpYouTube(link);
                        updateUI();
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

        Button mAddToListButton = view.findViewById(R.id.add_to_list_button);
        mAddToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetVideo != null){
                    String title = targetVideo.getSnippet().getTitle();
                    Thumbnail videoThumbnail = targetVideo.getSnippet().getThumbnails().getHigh();
                    DownloadListFragment.mVideoToDownloadList.add(new Pair(videoThumbnail, title));
                    Navigation.findNavController(requireView()).navigate(R.id.action_clip_information_fragment_to_download_list_fragment);
                }
            }
        });
        return view;
    }

    private void setUpYouTube(String link) {
        mYouTubeDownloadURL = new YouTubeDownloadURL(requireContext(), link);
        YouTubePlayerView youTubePlayerView = requireView().findViewById(R.id.youtube_player_view);
        youTubePlayerView.setVisibility(View.VISIBLE);
        mYouTubePlayer = new YouTubePlayer(link, youTubePlayerView);
        getLifecycle().addObserver(YouTubePlayer.youTubePlayerView);
        mYouTubePlayer.start();

        Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                youTubeDataAPI = new YouTubeDataAPI(mYouTubePlayer.getClipID());
                targetVideo = youTubeDataAPI.getData();

                String title = targetVideo.getSnippet().getTitle();
                Thumbnail videoThumbnail = targetVideo.getSnippet().getThumbnails().getHigh();
                String viewCount = targetVideo.getStatistics().getViewCount().toString();
                String description = targetVideo.getSnippet().getDescription();
                handler.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        mTitleTextView.setText(mTitleTextView.getText().toString() + title);
                        mCounterViewTextView.setText(mCounterViewTextView.getText().toString() + viewCount);
                        mDescriptionTextView.setText(mDescriptionTextView.getText().toString() + description);
                    }
                });
            }
        };
        new Thread(task).start();
    }

    private void updateUI() {
        RadioGroup mQualityRadioGroup = requireView().findViewById(R.id.quality_radio_group);
        Button mDownloadButton = requireView().findViewById(R.id.download_button);

        //clean field if was used
        mDescriptionTextView.setText("");
        mCounterViewTextView.setText("");
        mTitleTextView.setText("");
        for (int i = 0; i < mQualityRadioGroup.getChildCount(); i++) {
            mQualityRadioGroup.removeViewAt(i);
        }

        mYouTubeDownloadURL.extract(new YouTubeDownloadUrlState() {
            @Override
            public void isOperationSuccessfully(String state) {
                if (state.equals("SUCCESS")) {
                    List<RadioButton> radioButtonList = mYouTubeDownloadURL.getRadioButtonsList();
                    for (RadioButton button: radioButtonList){
                        mQualityRadioGroup.addView(button);
                        clip_layout.setVisibility(View.VISIBLE);
                        Animation emerge = AnimationUtils.loadAnimation(requireContext(), R.anim.emerge);
                        clip_layout.startAnimation(emerge);
                        mDownloadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mQualityRadioGroup.getCheckedRadioButtonId() == -1){
                                    Toast.makeText(requireContext(), "You have to choose download format", Toast.LENGTH_LONG).show();
                                }else{
                                mYouTubeDownloadURL.downloadVideo(mQualityRadioGroup.getCheckedRadioButtonId());
                            }
                        }});
                    }
                }
                else if (state.equals("NO_SUCCESS")){
                    Toast.makeText(requireContext(), "Cannot download data", Toast.LENGTH_LONG).show();
                }
            }
        });
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