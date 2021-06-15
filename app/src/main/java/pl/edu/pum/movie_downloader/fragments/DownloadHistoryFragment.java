package pl.edu.pum.movie_downloader.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import pl.edu.pum.movie_downloader.FirebaseAuthentication.FireBaseAuthHandler;
import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.adapters.DownloadHistoryRecyclerViewAdapter;
import pl.edu.pum.movie_downloader.adapters.DownloadListRecyclerViewAdapter;
import pl.edu.pum.movie_downloader.database.local.firestore.FirestoreDBHandler;
import pl.edu.pum.movie_downloader.database.local.firestore.FirestoreState;
import pl.edu.pum.movie_downloader.models.DownloadHistoryDTO;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class DownloadHistoryFragment extends Fragment {
    private static List<DownloadHistoryDTO> mDownloadHistoryList = new ArrayList<>();
    private List<DownloadHistoryDTO> mYouTubeDownloadHistoryList = new ArrayList<>();
    private List<DownloadHistoryDTO> mVimeoDownloadHistoryList = new ArrayList<>();
    public static FirestoreDBHandler firestoreDBHandler = new FirestoreDBHandler();
    private RecyclerView vimeoRecyclerView;
    private RecyclerView youTubeRecyclerView;
    private DownloadHistoryRecyclerViewAdapter mYTAdapter;
    private DownloadHistoryRecyclerViewAdapter mVimeoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireView()).navigate(R.id.action_download_history_fragment_to_home_fragment);
            }
        });
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);
        View view = inflater.inflate(R.layout.fragment_download_history, container, false);

        vimeoRecyclerView = view.findViewById(R.id.vimeo_history_recycler_view);
        youTubeRecyclerView = view.findViewById(R.id.youtube_history_recycler_view);
        Button mDeleteAllHistoryButton = view.findViewById(R.id.delete_history_button);
        mDeleteAllHistoryButton.setOnClickListener(v -> {
            if (!mDownloadHistoryList.isEmpty()) {
                String userName = Objects.requireNonNull(FireBaseAuthHandler.getInstance().getAuthorization().getCurrentUser()).getDisplayName();
                for (DownloadHistoryDTO obj : mDownloadHistoryList) {
                    firestoreDBHandler.deleteAllData(userName, obj.getId());
                    if (obj.getSource().equals("youtube")) {mYTAdapter.notifyDataSetChanged();}
                    if (obj.getSource().equals("vimeo")) {mVimeoAdapter.notifyDataSetChanged();}
                }
                mDownloadHistoryList.clear();
                mYouTubeDownloadHistoryList.clear();
                mVimeoDownloadHistoryList.clear();
            }
        });

        ImageButton showYouTubeHistoryButton = view.findViewById(R.id.show_youtube_history);
        showYouTubeHistoryButton.setOnClickListener(v -> {
            if (youTubeRecyclerView.getVisibility() == View.GONE && !mYouTubeDownloadHistoryList.isEmpty()){
                youTubeRecyclerView.setVisibility(View.VISIBLE);
                showYouTubeHistoryButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }else {
                youTubeRecyclerView.setVisibility(View.GONE);
                showYouTubeHistoryButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            }
        });

        ImageButton showVimeoHistoryButton = view.findViewById(R.id.show_vimeo_history);
        showVimeoHistoryButton.setOnClickListener(v -> {
            if (vimeoRecyclerView.getVisibility() == View.GONE && !mVimeoDownloadHistoryList.isEmpty()){
                vimeoRecyclerView.setVisibility(View.VISIBLE);
                showVimeoHistoryButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }else {
                vimeoRecyclerView.setVisibility(View.GONE);
                showVimeoHistoryButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            }
        });


        return view;
    }

    public void getData() {
        String userName = Objects.requireNonNull(FireBaseAuthHandler.getInstance().getAuthorization().getCurrentUser()).getDisplayName();
        Runnable task = () -> {
            mDownloadHistoryList = firestoreDBHandler.getAllData(userName, new FirestoreState() {
                @Override
                public void isOperationSuccessfully(String state) {
                    if (state.equals("SUCCESS")) {
                        setUpRecyclerViews();
                    }
                    else if (state.equals("NO_SUCCESS")) {
                        Snackbar snackbar = Snackbar
                                .make(requireView(), "Failed to download the data.", Snackbar.LENGTH_LONG)
                                .setAction("RETRY", view -> getData());
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();

                    }
                }
            });
        };
        new Thread(task).start();
    }

    private void setUpRecyclerViews() {
        if (!mDownloadHistoryList.isEmpty()) {
            divideDataDependsOfSource();
            if (!mYouTubeDownloadHistoryList.isEmpty()) {
                youTubeRecyclerView .setItemAnimator(new SlideInRightAnimator());
                youTubeRecyclerView .setLayoutManager(new LinearLayoutManager(requireContext()));
                mYTAdapter = new DownloadHistoryRecyclerViewAdapter(requireActivity(), mYouTubeDownloadHistoryList);
                youTubeRecyclerView.setAdapter(new ScaleInAnimationAdapter(mYTAdapter));
            }
            if (!mVimeoDownloadHistoryList.isEmpty()) {
                vimeoRecyclerView.setItemAnimator(new SlideInRightAnimator());
                vimeoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                mVimeoAdapter = new DownloadHistoryRecyclerViewAdapter(requireActivity(), mVimeoDownloadHistoryList);
                vimeoRecyclerView.setAdapter(new ScaleInAnimationAdapter(mVimeoAdapter));
            }
        }
    }

    private void divideDataDependsOfSource() {
        for (DownloadHistoryDTO obj : mDownloadHistoryList) {
            if (obj.getSource().equals("youtube")) {
                mYouTubeDownloadHistoryList.add(obj);
            }
            if (obj.getSource().equals("vimeo")) {
                mVimeoDownloadHistoryList.add(obj);
            }
        }
    }
}