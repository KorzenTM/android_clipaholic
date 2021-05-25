package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.ashudevs.facebookurlextractor.FacebookExtractor;
import com.ashudevs.facebookurlextractor.FacebookFile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.data.vimeo.VimeoDataAPI;
import pl.edu.pum.movie_downloader.data.YouTubeDataAPI;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadURL;
import pl.edu.pum.movie_downloader.models.DownloadListInformation;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;
import pl.edu.pum.movie_downloader.players.youtube.YouTubePlayer;
import pl.edu.pum.movie_downloader.viemodel.PageViewModel;


public class ClipInformationFragment extends Fragment {
    private PageViewModel pageViewModel;
    private EditText mLinkEditText;
    private YouTubePlayer mYouTubePlayer = null;
    private VideoView mOtherVideoPlayer;
    private TextView mTitleTextView;
    private TextView mCounterViewTextView;
    private TextView mDescriptionTextView;
    private TextView mWaitTextView;
    private ProgressBar mWaitProgressBar;
    private FloatingActionButton mCheckLinkButton;
    private FloatingActionButton mAddToListButton;
    private RadioGroup mQualityRadioGroup;
    private RelativeLayout mClipLayout;
    public  YouTubeDownloadURL mYouTubeDownloadURL;
    private YouTubeDataAPI mYouTubeDataAPI;
    private VimeoDataAPI mVimeoDataAPI;
    private Video mTargetVideo = null;
    private int RequestForDataCounter = 0;
    private String mLink;

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
        mOtherVideoPlayer = view.findViewById(R.id.other_player_view);
        YouTubePlayer.youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        mLinkEditText = view.findViewById(R.id.link_edit_text);
        mAddToListButton = view.findViewById(R.id.add_to_list_button);
        mCheckLinkButton = view.findViewById(R.id.check_link_button);
        Button mDownloadButton = view.findViewById(R.id.download_button);
        mAddToListButton.setClickable(false);
        mAddToListButton.setVisibility(View.GONE);
        restoreState(view);
        addClearButton(mLinkEditText);

        if (mLinkEditText.requestFocus()){
            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        mCheckLinkButton.setOnClickListener(v -> {
            mLink = mLinkEditText.getText().toString();
            if (!mLink.isEmpty()) {
                setWaitProgressVisibility(View.VISIBLE);
                if (isYouTubeUrl(mLink)) {
                    initYouTube();
                }
                else if (isVimeoURL(mLink)) {
                    initVimeo();
                }
                else {
                    setWaitProgressVisibility(View.GONE);
                    mLinkEditText.setError("Wrong link or we do not support this source");
                }
            } else {
                mLinkEditText.setError("This field cannot be empty!");
            }
        });

        mAddToListButton.setOnClickListener(v -> {
            if (mQualityRadioGroup.getCheckedRadioButtonId() != -1 && !mLink.isEmpty()){
                int selectedFormat = mQualityRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = view.findViewById(selectedFormat);
                String title, format, id, url, ext;
                int itag;
                if (isYouTubeUrl(mLink)){
                    if (mYouTubeDownloadURL != null && mTargetVideo != null){
                        title = mTargetVideo.getSnippet().getTitle().replace("\"", "");
                        format = radioButton.getText().toString();
                        id = mYouTubePlayer.getClipID();
                        itag = mQualityRadioGroup.getCheckedRadioButtonId();
                        url = mYouTubeDownloadURL.getDownloadURL(itag);
                        ext = mYouTubeDownloadURL.getExtension(itag);
                        addToList(title, format, id, itag, url, ext, mLink);
                    }else{
                        Snackbar.make(requireView(), "Failed to get the correct data. Please try again.", Snackbar.LENGTH_SHORT).show();
                    }
                }
                else if(isVimeoURL(mLink)){
                    if (mVimeoDataAPI != null){
                        title = mVimeoDataAPI.getTitle();
                        format = radioButton.getText().toString();
                        id = "";
                        itag = -1;
                        url = mVimeoDataAPI.getStreamsMap().get(mQualityRadioGroup.getCheckedRadioButtonId() + "p");
                        ext = "mp4";
                        addToList(title, format, id, itag, url, ext, mLink);
                    }else {
                        Snackbar.make(requireView(), "Failed to get the correct data. Please try again.", Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Snackbar.make(requireView(), "Incorrect link or source. Check the data.", Snackbar.LENGTH_SHORT).show();
                }
            } else{
                Snackbar.make(requireView(), "You have to choose download format.", Snackbar.LENGTH_SHORT).show();
            }
        });

        mDownloadButton.setOnClickListener(v -> {
            if (mQualityRadioGroup.getCheckedRadioButtonId() == -1){
                Snackbar.make(requireView(), "You have to choose download format.", Snackbar.LENGTH_SHORT).show();
            }else{
                if (!mLink.isEmpty()) {
                    int key = mQualityRadioGroup.getCheckedRadioButtonId();
                    setWaitProgressVisibility(View.VISIBLE);
                    if (mLink.contains("facebook") || mLink.contains("fb")) {
                        initFacebook(mLink);
                    }
                    if (isYouTubeUrl(mLink) && mYouTubeDownloadURL != null) {
                        mYouTubeDownloadURL.downloadVideoFromITag(key);
                        setWaitProgressVisibility(View.GONE);
                    }
                    if (isVimeoURL(mLink) && mVimeoDataAPI != null) {
                        String vimeoKey = key + "p";
                        String url = mVimeoDataAPI.getStreamsMap().get(vimeoKey);
                        mVimeoDataAPI.download(url);
                        setWaitProgressVisibility(View.GONE);
                    }
                    else {
                        setWaitProgressVisibility(View.GONE);
                        mLinkEditText.setError("Wrong link or we do not support this source.");
                    }
                } else {
                    mLinkEditText.setError("This field cannot be empty!");
                }
            }
        });
        return view;
    }

    private void addToList(String title, String format, String ID, int ITag,
                      String downloadURL, String ext, String link){
        DownloadListInformation downloadListInformation =
                new DownloadListInformation(title, format, ID,
                        ITag, downloadURL, ext, link);
        Handler handler = new Handler();
        Runnable task = () -> {
            DownloadListFragment.dbHandler.addYouTubeClip(downloadListInformation);
            DownloadListFragment.getDownloadList();
            handler.post(() -> {
                View download_list_view = NavHostActivity.mBottomNavigationView.findViewById(R.id.download_list_fragment);
                download_list_view.performClick();
                Snackbar.make(requireView(), "Added a clip to the download list", Snackbar.LENGTH_SHORT).show();
            });
        };
        new Thread(task).start();
    }

    @SuppressLint("SetTextI18n")
    private void restoreState(View view) {
        pageViewModel.getClipLayoutVisibility().observe(requireActivity(), integer -> {
            mClipLayout.setVisibility(integer);
            mAddToListButton.setVisibility(integer);
        });

        pageViewModel.getLinkMutableLiveData().observe(requireActivity(), link -> mLink = link);

        if (mLink != null){
            mLinkEditText.setText(mLink);
            if (!mLink.isEmpty() && mLink.contains("vimeo")){
                restoreVimeoState();
            }
            else if (!mLink.isEmpty() && isYouTubeUrl(mLink)){
                restoreYouTubeState(view);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void restoreVimeoState(){
        pageViewModel.getVimeoDataAPIMutableLiveData().observe(requireActivity(), vimeoDataAPI -> {
            if (vimeoDataAPI != null){
                mVimeoDataAPI = vimeoDataAPI;
                mOtherVideoPlayer.setVisibility(View.VISIBLE);
                setUpOtherSourcesPlayer(vimeoDataAPI.getURLToPlay());
                String title = vimeoDataAPI.getTitle();
                String author = vimeoDataAPI.getAuthorName();
                mTitleTextView.setText(title);
                mCounterViewTextView.setText("Not available");
                mDescriptionTextView.setText("This video is from Vimeo and is owned by " + author);
            }
        });

        pageViewModel.getFormatDataList().observe(requireActivity(), pairs -> {
            mQualityRadioGroup.removeAllViews();
            for (Pair<Integer, String> information:pairs){
                RadioButton newRadioButton = new RadioButton(requireContext());
                newRadioButton.setId(information.first);
                newRadioButton.setText(information.second);
                mQualityRadioGroup.addView(newRadioButton);
            }
        });
    }

    private void restoreYouTubeState(View view){
        pageViewModel.getVideoMutableLiveData().observe(requireActivity(), video -> {
            mTargetVideo = video;
            if (mTargetVideo != null){
                String title = mTargetVideo.getSnippet().getTitle();
                String viewCount = mTargetVideo.getStatistics().getViewCount().toString();
                String description = mTargetVideo.getSnippet().getDescription();

                mTitleTextView.setText(title);
                mCounterViewTextView.setText(viewCount);
                mDescriptionTextView.setText(description);
            }
        });

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
            YouTubePlayer.youTubePlayerView.setVisibility(View.VISIBLE);
            Runnable task = () -> mYouTubePlayer.init();
            new Thread(task).start();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addClearButton(EditText edt)
    {
        Drawable default_edit_text_theme = edt.getBackground(); //just handle default theme of edittext to change

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edt.setBackground(default_edit_text_theme);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_baseline_clear_24,0);
                } else {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, 0,0);
                }
            }
        });

        edt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (edt.getCompoundDrawables()[2] != null) {
                    if(event.getX() >= (edt.getRight()- edt.getLeft() - edt.getCompoundDrawables()[2].getBounds().width())) {
                        edt.setText("");
                    }
                }
            }
            return false;
        });

    }

    @SuppressLint("StaticFieldLeak")
    private void initFacebook(String link) {
        new FacebookExtractor(requireContext(), "https://fb.watch/5BSrFxIW3b", false) {
            @Override
            protected void onExtractionComplete(FacebookFile facebookFile) {
                System.out.println(facebookFile.getAuthor());
                System.out.println(facebookFile.getFilename());
                System.out.println(facebookFile.getSdUrl());
            }
            @Override
            protected void onExtractionFail(Exception Error) {
                Log.e("Error", "Error :: " + Error.getMessage());
                Error.printStackTrace();
            }
        };
    }

    private boolean isYouTubeUrl(String link){
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        return link.matches(pattern);
    }

    private boolean isVimeoURL(String link){
        String pattern = "(?:http|https)?:?\\/?\\/?(?:www\\.|player\\.)?vimeo\\.com\\/(?:channels\\/(?:\\w+\\/)?|groups\\/(?:[^\\/]*)\\/videos\\/|video\\/|)(\\d+)(?:|\\/\\?)";
        return link.matches(pattern);
    }

    private void initYouTube() {
        mYouTubeDownloadURL = null;
        mYouTubeDownloadURL = new YouTubeDownloadURL(requireContext(), mLink);
        RequestForDataCounter = 0;
        setUpYouTubePlayer();
        getInformationAboutYouTubeClipFromURL();
        getFormatsFromYouTubeUrl();
    }

    private void setUpYouTubePlayer(){
        if (mOtherVideoPlayer.getVisibility() == View.VISIBLE){
            mOtherVideoPlayer.setVisibility(View.GONE);
        }
        YouTubePlayer.youTubePlayerView.setVisibility(View.VISIBLE);
        if (mYouTubePlayer == null){
            mYouTubePlayer = new YouTubePlayer(mLink, getLifecycle());
            Runnable task1 = () -> mYouTubePlayer.init();
            new Thread(task1).start();
        }else {
            mYouTubePlayer.setClipID(mLink);
            Runnable task2 = () -> mYouTubePlayer.setNextVideo();
            new Thread(task2).start();
        }
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
                mTitleTextView.setText(title);
                mCounterViewTextView.setText(viewCount);
                mDescriptionTextView.setText(description);
            });
        };
        new Thread(task).start();
    }

    private void getFormatsFromYouTubeUrl(){
        YouTubeDownloadURL.extract(state -> {
            if (state.equals("SUCCESS")) {
                RequestForDataCounter = 0;
                mQualityRadioGroup.removeAllViews();
                List<RadioButton> radioButtonList = mYouTubeDownloadURL.getRadioButtonsList();
                for (RadioButton button: radioButtonList) {
                    System.out.println(button.getText().toString());
                    mQualityRadioGroup.addView(button);
                }
                setWaitProgressVisibility(View.GONE);
                updateUI();
            }
            if (state.equals("NO_SUCCESS")){
                if (RequestForDataCounter > 3){
                    RequestForDataCounter = 0;
                    mQualityRadioGroup.removeAllViews();
                    Snackbar snackbar = Snackbar
                            .make(requireView(), "Failed to download the data.", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", view -> initYouTube());
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                    setWaitProgressVisibility(View.GONE);
                }else{
                    RequestForDataCounter++;
                    System.out.println(RequestForDataCounter);
                    setWaitProgressVisibility(View.VISIBLE);
                    getFormatsFromYouTubeUrl();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initVimeo(){
        mVimeoDataAPI = new VimeoDataAPI(requireContext(), mLink);
        Handler handler = new Handler();
        mVimeoDataAPI.extract(state -> {
            if (state.equals("SUCCESS")){
                handler.post(() -> {
                    setUpOtherSourcesPlayer(mVimeoDataAPI.getURLToPlay());
                    mTitleTextView.setText(mVimeoDataAPI.getTitle());
                    mCounterViewTextView.setText("Not available");
                    mDescriptionTextView.setText("This video is from Vimeo and is owned by "
                            + mVimeoDataAPI.getAuthorName());
                    setWaitProgressVisibility(View.GONE);
                    mQualityRadioGroup.removeAllViews();
                    List<RadioButton> radioButtonList = mVimeoDataAPI.getRadioButtonsList();
                    for (RadioButton button: radioButtonList) {
                        mQualityRadioGroup.addView(button);
                    }
                    updateUI();
                });
            }
            else if (state.equals("NO_SUCCESS")){
                mQualityRadioGroup.removeAllViews();
                Snackbar snackbar = Snackbar
                        .make(requireView(), "Failed to download the data.", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", view -> initVimeo());
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
                setWaitProgressVisibility(View.GONE);
            }
        });
    }

    public void setUpOtherSourcesPlayer(String link){
        if (YouTubePlayer.youTubePlayerView.getVisibility() == View.VISIBLE){
            YouTubePlayer.youTubePlayerView.setVisibility(View.GONE);
        }
        mOtherVideoPlayer.setVisibility(View.VISIBLE);
        final MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(mOtherVideoPlayer);
        mOtherVideoPlayer.setMediaController(mediaController);
        mOtherVideoPlayer.setBackgroundColor(Color.TRANSPARENT);
        Uri video = Uri.parse(link);
        mOtherVideoPlayer.setVideoURI(video);

        mOtherVideoPlayer.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mediaController.setAnchorView(mOtherVideoPlayer);
            mOtherVideoPlayer.requestFocus();
        });
    }

    private void updateUI() {
        mAddToListButton.setVisibility(View.VISIBLE);
        mAddToListButton.setClickable(true);
        Animation emerge = AnimationUtils.loadAnimation(requireContext(), R.anim.emerge);
        mClipLayout.setVisibility(View.VISIBLE);
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
        mOtherVideoPlayer.setOnCompletionListener(MediaPlayer::release);
    }

    public void setViewModel(){
        if (mLink != null && !mLink.isEmpty()){
            pageViewModel.setLinkMutableLiveData(mLink);
        }

        pageViewModel.setClipLayoutVisibility(mClipLayout.getVisibility());

        if (mYouTubeDownloadURL != null && !mYouTubeDownloadURL.getRadioButtonsList().isEmpty()){
            List<Pair<Integer, String>> formatData = new ArrayList<>();
            List<RadioButton> radioButtonList = mYouTubeDownloadURL.getRadioButtonsList();
            for (RadioButton button:radioButtonList){
                formatData.add(new Pair<>(button.getId(), button.getText().toString()));
            }
            pageViewModel.setFormatDataList(formatData);
            pageViewModel.setYouTubeDownloadURLMutableLiveData(mYouTubeDownloadURL);
            mYouTubeDownloadURL = null;
        }

        if (mTargetVideo != null){
            pageViewModel.setVideoMutableLiveData(mTargetVideo);
            mTargetVideo = null;
        }

        if (mYouTubePlayer != null){
            pageViewModel.setYouTubePlayerMutableLiveData(mYouTubePlayer);
        }

        if (mVimeoDataAPI != null){
            List<Pair<Integer, String>> formatData = new ArrayList<>();
            List<RadioButton> radioButtonList = mVimeoDataAPI.getRadioButtonsList();
            for (RadioButton button:radioButtonList){
                formatData.add(new Pair<>(button.getId(), button.getText().toString()));
            }
            pageViewModel.setFormatDataList(formatData);
            pageViewModel.setVimeoDataAPIMutableLiveData(mVimeoDataAPI);
            mVimeoDataAPI = null;
        }
    }
}