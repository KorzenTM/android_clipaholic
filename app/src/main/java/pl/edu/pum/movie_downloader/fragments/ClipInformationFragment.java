package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.data.YouTubeDataAPI;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadURL;
import pl.edu.pum.movie_downloader.models.YouTubeDownloadListInformation;
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

    public  YouTubeDownloadURL mYouTubeDownloadURL;
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
        YouTubePlayer.youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        mLinkEditText = view.findViewById(R.id.link_edit_text);
        mLinkEditText.setText("https://www.youtube.com/watch?v=1e9B31FLT-s");
        restoreState(view);

        mCheckLinkButton = view.findViewById(R.id.check_link_button);
        mCheckLinkButton.setOnClickListener(v -> {
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
        });

        Button mAddToListButton = view.findViewById(R.id.add_to_list_button);
        mAddToListButton.setOnClickListener(v -> {
            if (mTargetVideo != null &&
                    mQualityRadioGroup.getCheckedRadioButtonId() != -1 &&
                    !mLinkEditText.getText().toString().isEmpty()){
                int selectedFormat = mQualityRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = view.findViewById(selectedFormat);
                int itag = mQualityRadioGroup.getCheckedRadioButtonId();

                String link = mLinkEditText.getText().toString();
                String title = mTargetVideo.getSnippet().getTitle().replace("\"", "");
                String format = radioButton.getText().toString();
                String id = mYouTubePlayer.getClipID();
                String url = mYouTubeDownloadURL.getDownloadURL(itag);
                String ext = mYouTubeDownloadURL.getExtension(itag);

                YouTubeDownloadListInformation youTubeDownloadListInformation =
                        new YouTubeDownloadListInformation(title, format, id,
                                itag, url, ext, link);
                Handler handler = new Handler();
                Runnable task = () -> {
                    DownloadListFragment.dbHandler.addYouTubeClip(youTubeDownloadListInformation);
                    DownloadListFragment.getDownloadList();
                    handler.post(() -> {
                        View download_list_view = NavHostActivity.mBottomNavigationView.findViewById(R.id.download_list_fragment);
                        download_list_view.performClick();
                        Snackbar snackbar = Snackbar.make(requireView(), "Added a clip to the download list", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    });
                };
                new Thread(task).start();
            } else{
                Snackbar.make(requireView(), "You have to choose download format.", Snackbar.LENGTH_SHORT).show();
            }
        });

        Button mDownloadButton = view.findViewById(R.id.download_button);
        mDownloadButton.setOnClickListener(v -> {
            if (mQualityRadioGroup.getCheckedRadioButtonId() == -1){
                Snackbar.make(requireView(), "You have to choose download format.", Snackbar.LENGTH_SHORT).show();
            }else{
                mYouTubeDownloadURL.downloadVideoFromITag(mQualityRadioGroup.getCheckedRadioButtonId());
            }
        });
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void restoreState(View view) {
        pageViewModel.getVideoMutableLiveData().observe(requireActivity(), video -> {
            mTargetVideo = video;
            if (mTargetVideo != null){
                String title = mTargetVideo.getSnippet().getTitle();
                String viewCount = mTargetVideo.getStatistics().getViewCount().toString();
                String description = mTargetVideo.getSnippet().getDescription();

                mTitleTextView.setText(mTitleTextView.getText().toString() + title);
                mCounterViewTextView.setText(mCounterViewTextView.getText().toString() + viewCount);
                mDescriptionTextView.setText(mDescriptionTextView.getText().toString() + description);
            }
        });
        pageViewModel.getClipLayoutVisibility().observe(requireActivity(),
                integer -> mClipLayout.setVisibility(integer));

        pageViewModel.getFormatDataList().observe(requireActivity(), pairs -> {
            mQualityRadioGroup.removeAllViews();
            for (Pair<Integer, String> information:pairs){
                RadioButton newRadioButton = new RadioButton(requireContext());
                newRadioButton.setId(information.first);
                newRadioButton.setText(information.second);
                mQualityRadioGroup.addView(newRadioButton);
            }
        });

        pageViewModel.getYouTubeDownloadURLMutableLiveData().observe(requireActivity(),
                youTubeDownloadURL -> mYouTubeDownloadURL = youTubeDownloadURL);

        pageViewModel.getYouTubePlayerMutableLiveData().observe(requireActivity(), youTubePlayer -> {
            mYouTubePlayer = youTubePlayer;
            YouTubePlayer.youTubePlayerView = view.findViewById(R.id.youtube_player_view);
            Runnable task = () -> mYouTubePlayer.init();
            new Thread(task).start();
        });

        pageViewModel.getPlayerVisibility().observe(requireActivity(),
                integer -> YouTubePlayer.youTubePlayerView.setVisibility(integer));
    }

    private void setUpYouTube(String link) {
        mYouTubeDownloadURL = new YouTubeDownloadURL(requireContext(), link);
        YouTubePlayer.youTubePlayerView.setVisibility(View.VISIBLE);
        if (mYouTubePlayer == null){
            mYouTubePlayer = new YouTubePlayer(link, getLifecycle());
            Runnable task1 = () -> mYouTubePlayer.init();

            new Thread(task1).start();
        }else {
            mYouTubePlayer.setClipID(link);
            Runnable task2 = () -> mYouTubePlayer.setNextVideo();
            new Thread(task2).start();
        }
        getInformationAboutYouTubeClipFromURL();
    }

    private void getInformationAboutYouTubeClipFromURL() {
        Handler handler = new Handler();
        @SuppressLint("SetTextI18n") Runnable task = () -> {
            mYouTubeDataAPI = new YouTubeDataAPI(mYouTubePlayer.getClipID());
            mTargetVideo = mYouTubeDataAPI.getData();
            String title = mTargetVideo.getSnippet().getTitle();
            String viewCount = mTargetVideo.getStatistics().getViewCount().toString();
            String description = mTargetVideo.getSnippet().getDescription();
            handler.post(() -> {
                updateUI();
                mTitleTextView.setText(mTitleTextView.getText().toString() + title);
                mCounterViewTextView.setText(mCounterViewTextView.getText().toString() + viewCount);
                mDescriptionTextView.setText(mDescriptionTextView.getText().toString() + description);
                getFormatsFromYouTubeUrl();
            });
        };
        new Thread(task).start();
    }

    private void getFormatsFromYouTubeUrl(){
        mYouTubeDownloadURL.extract(state -> {
            if (state.equals("SUCCESS")) {
                mQualityRadioGroup.removeAllViews();
                List<RadioButton> radioButtonList = mYouTubeDownloadURL.getRadioButtonsList();
                for (RadioButton button: radioButtonList) {
                    mQualityRadioGroup.addView(button);
                }
                setWaitProgressVisibility(View.GONE);
            }
            else if (state.equals("NO_SUCCESS")){
                mQualityRadioGroup.removeAllViews();
                Snackbar snackbar = Snackbar
                        .make(requireView(), "Failed to download the data.", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", view -> setUpYouTube(mLinkEditText.getText().toString()));
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
                setWaitProgressVisibility(View.GONE);
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
        setViewModel();
    }

    public void setViewModel(){
        pageViewModel.setClipLayoutVisibility(mClipLayout.getVisibility());
        if (mYouTubeDownloadURL != null && !mYouTubeDownloadURL.getRadioButtonsList().isEmpty()){
            List<Pair<Integer, String>> formatData = new ArrayList<>();
            List<RadioButton> radioButtonList = mYouTubeDownloadURL.getRadioButtonsList();
            for (RadioButton button:radioButtonList){
                formatData.add(new Pair<>(button.getId(), button.getText().toString()));
            }
            pageViewModel.setFormatDataList(formatData);
            pageViewModel.setYouTubeDownloadURLMutableLiveData(mYouTubeDownloadURL);
        }
        if (mTargetVideo != null){
            pageViewModel.setVideoMutableLiveData(mTargetVideo);
        }
        if (YouTubePlayer.youTubePlayerView != null){
            pageViewModel.setPlayerVisibility(YouTubePlayer.youTubePlayerView.getVisibility());
        }
        if (mYouTubePlayer != null){
            pageViewModel.setYouTubePlayerMutableLiveData(mYouTubePlayer);
        }
    }
}