package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.data.YouTubeDataAPI;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadURL;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadUrlState;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;
import pl.edu.pum.movie_downloader.players.youtube.YouTubePlayer;
import pl.edu.pum.movie_downloader.viemodel.PageViewModel;


public class ClipInformationFragment extends Fragment {
    private PageViewModel pageViewModel;

    private EditText mLinkEditText;
    private YouTubePlayer mYouTubePlayer = null;
    private TextView mTitleTextView;
    private TextView mCounterViewTextView;
    private TextView mDescriptionTextView;
    private TextView mWaitTextView;
    private ProgressBar mWaitProgressBar;
    private Button mCheckLinkButton;
    private RadioGroup mQualityRadioGroup;
    private RelativeLayout mClipLayout;

    public static YouTubeDownloadURL mYouTubeDownloadURL;
    private YouTubePlayerView mYouTubePlayerView;
    private YouTubeDataAPI mYouTubeDataAPI;
    private Video mTargetVideo = null;

    public ClipInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(requireActivity()).get(PageViewModel.class);
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
        View view = inflater.inflate(R.layout.fragment_clip_information, container, false);
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);

        mClipLayout = view.findViewById(R.id.clip_information_relative_layout);
        mTitleTextView = view.findViewById(R.id.title_text_view);
        mCounterViewTextView = view.findViewById(R.id.view_counter_text_view);
        mQualityRadioGroup = view.findViewById(R.id.quality_radio_group);
        mDescriptionTextView = view.findViewById(R.id.description_text_view);
        mWaitTextView = view.findViewById(R.id.wait_text_view);
        mWaitProgressBar = view.findViewById(R.id.wait_for_info_progress_bar);
        mYouTubePlayerView = view.findViewById(R.id.youtube_player_view);
        mLinkEditText = view.findViewById(R.id.link_edit_text);
        mLinkEditText.setText("https://www.youtube.com/watch?v=1e9B31FLT-s");
        restoreState(view);

        mCheckLinkButton = view.findViewById(R.id.check_link_button);
        mCheckLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = mLinkEditText.getText().toString();
                if (!link.isEmpty()) {
                    setWaitProgressVisibility(View.VISIBLE);
                    if (link.contains("facebook") || link.contains("fb")) {
                        System.out.println("to jest link z facebooczka :)");
                    }
                    else if (link.contains("youtube")) {
                        setUpYouTube(link);
                    }
                    else if (link.contains("vimeo")) {
                        Toast.makeText(requireContext(), "Vimeo", Toast.LENGTH_SHORT).show();
                        //TODO handle vimeo link
                    } else {
                        setWaitProgressVisibility(View.GONE);
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
                if (mTargetVideo != null && mQualityRadioGroup.getCheckedRadioButtonId() != -1){
                    String title = mTargetVideo.getSnippet().getTitle();
                    int selectedFormat = mQualityRadioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = view.findViewById(selectedFormat);
                    String format = radioButton.getText().toString();
                    DownloadListFragment.mVideoInformationList.add(new Pair<String, String>(format, title));
                    int itag = mQualityRadioGroup.getCheckedRadioButtonId();
                    String id = mYouTubePlayer.getClipID();
                    DownloadListFragment.mDownloadInformationList.add(new Pair<Integer, String>(itag, id));
                    Navigation.findNavController(requireView()).navigate(R.id.action_clip_information_fragment_to_download_list_fragment);
                } else{
                    Toast.makeText(requireContext(), "You have to choose download format", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button mDownloadButton = view.findViewById(R.id.download_button);
        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQualityRadioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(requireContext(), "You have to choose download format", Toast.LENGTH_LONG).show();
                }else{
                    mYouTubeDownloadURL.downloadVideo(mQualityRadioGroup.getCheckedRadioButtonId());
                }
            }});
        return view;
    }

    private void restoreState(View view) {
        pageViewModel.getVideoMutableLiveData().observe(requireActivity(), new Observer<Video>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Video video) {
                mTargetVideo = video;
                if (mTargetVideo != null){
                    String title = mTargetVideo.getSnippet().getTitle();
                    String viewCount = mTargetVideo.getStatistics().getViewCount().toString();
                    String description = mTargetVideo.getSnippet().getDescription();

                    mTitleTextView.setText(mTitleTextView.getText().toString() + title);
                    mCounterViewTextView.setText(mCounterViewTextView.getText().toString() + viewCount);
                    mDescriptionTextView.setText(mDescriptionTextView.getText().toString() + description);
                }
            }
        });
        pageViewModel.getClipLayoutVisibility().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mClipLayout.setVisibility(integer);
            }
        });

        pageViewModel.getFormatDataList().observe(requireActivity(), new Observer<List<Pair<Integer, String>>>() {
            @Override
            public void onChanged(List<Pair<Integer, String>> pairs) {
                mQualityRadioGroup.removeAllViews();
                for (Pair<Integer, String> information:pairs){
                    RadioButton newRadioButton = new RadioButton(requireContext());
                    newRadioButton.setId(information.first);
                    newRadioButton.setText(information.second);
                    mQualityRadioGroup.addView(newRadioButton);
                }
            }
        });

        pageViewModel.getYouTubeDownloadURLMutableLiveData().observe(requireActivity(), new Observer<YouTubeDownloadURL>() {
            @Override
            public void onChanged(YouTubeDownloadURL youTubeDownloadURL) {
                mYouTubeDownloadURL = youTubeDownloadURL;
            }
        });

        pageViewModel.getYouTubePlayerMutableLiveData().observe(requireActivity(), new Observer<YouTubePlayer>() {
            @Override
            public void onChanged(YouTubePlayer youTubePlayer) {
                mYouTubePlayer = youTubePlayer;
                YouTubePlayer.youTubePlayerView = view.findViewById(R.id.youtube_player_view);
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        mYouTubePlayer.start();
                    }
                };
                new Thread(task).start();
            }
        });

        pageViewModel.getPlayerVisibility().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mYouTubePlayerView.setVisibility(integer);
            }
        });
    }

    private void setUpYouTube(String link) {
        mYouTubeDownloadURL = new YouTubeDownloadURL(requireContext(), link);
        mYouTubePlayerView.setVisibility(View.VISIBLE);
        mYouTubePlayer = new YouTubePlayer(link, mYouTubePlayerView);
        getLifecycle().addObserver(YouTubePlayer.youTubePlayerView);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mYouTubePlayer.start();
            }
        };
        new Thread(task).start();

        getInformationAboutYouTubeClipFromURL();
    }

    private void getInformationAboutYouTubeClipFromURL() {
        Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mYouTubeDataAPI = new YouTubeDataAPI(mYouTubePlayer.getClipID());
                mTargetVideo = mYouTubeDataAPI.getData();
                String title = mTargetVideo.getSnippet().getTitle();
                Thumbnail videoThumbnail = mTargetVideo.getSnippet().getThumbnails().getHigh();
                String viewCount = mTargetVideo.getStatistics().getViewCount().toString();
                String description = mTargetVideo.getSnippet().getDescription();
                handler.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        updateUI();
                        mTitleTextView.setText(mTitleTextView.getText().toString() + title);
                        mCounterViewTextView.setText(mCounterViewTextView.getText().toString() + viewCount);
                        mDescriptionTextView.setText(mDescriptionTextView.getText().toString() + description);
                        getFormatsFromYouTubeUrl();
                    }
                });
            }
        };
        new Thread(task).start();
    }

    private void getFormatsFromYouTubeUrl(){
        mYouTubeDownloadURL.extract(new YouTubeDownloadUrlState() {
            @Override
            public void isOperationSuccessfully(String state) {
                if (state.equals("SUCCESS")) {
                    mQualityRadioGroup.removeAllViews();
                    List<RadioButton> radioButtonList = mYouTubeDownloadURL.getRadioButtonsList();
                    for (RadioButton button: radioButtonList) {
                        mQualityRadioGroup.addView(button);
                    }
                    setWaitProgressVisibility(View.GONE);
                }
                else if (state.equals("NO_SUCCESS")){
                    Toast.makeText(requireContext(), "Cannot download data", Toast.LENGTH_LONG).show();
                    setWaitProgressVisibility(View.GONE);
                }
            }
        });
    }

    private void updateUI() {
        //clean field if was used
        mTitleTextView.setText("");
        mCounterViewTextView.setText("");
        mDescriptionTextView.setText("");

        mClipLayout.setVisibility(View.VISIBLE);
        Animation emerge = AnimationUtils.loadAnimation(requireContext(), R.anim.emerge);
        mClipLayout.startAnimation(emerge);
    }

    private void setWaitProgressVisibility(int isVisible){
        int buttonVisibility = (isVisible == View.VISIBLE) ? View.GONE : View.VISIBLE;
        mCheckLinkButton.setVisibility(buttonVisibility);
        mWaitTextView.setVisibility(isVisible);
        mWaitProgressBar.setVisibility(isVisible);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mYouTubePlayer.release();
        setViewModel();
    }

    public void setViewModel(){
        pageViewModel.setClipLayoutVisibility(mClipLayout.getVisibility());
        if (mYouTubeDownloadURL != null && !mYouTubeDownloadURL.getRadioButtonsList().isEmpty()){
            List<Pair<Integer, String>> formatData = new ArrayList<>();
            List<RadioButton> radioButtonList = mYouTubeDownloadURL.getRadioButtonsList();
            for (RadioButton button:radioButtonList){
                formatData.add(new Pair<Integer, String>(button.getId(), button.getText().toString()));
            }
            pageViewModel.setFormatDataList(formatData);
            pageViewModel.setYouTubeDownloadURLMutableLiveData(mYouTubeDownloadURL);
        }
        if (mTargetVideo != null){
            pageViewModel.setVideoMutableLiveData(mTargetVideo);
        }
        if (mYouTubePlayerView != null){
            pageViewModel.setPlayerVisibility(mYouTubePlayerView.getVisibility());
        }
        if (mYouTubePlayer != null){
            pageViewModel.setYouTubePlayerMutableLiveData(mYouTubePlayer);
        }
    }
}