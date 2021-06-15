package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import pl.edu.pum.movie_downloader.FirebaseAuthentication.FireBaseAuthHandler;
import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.adapters.DownloadListRecyclerViewAdapter;
import pl.edu.pum.movie_downloader.database.local.firestore.FirestoreDBHandler;
import pl.edu.pum.movie_downloader.database.local.sqlite.DBHandler;
import pl.edu.pum.movie_downloader.downloader.Downloader;
import pl.edu.pum.movie_downloader.models.DownloadListInformationDTO;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class DownloadListFragment extends Fragment {
    public static final List<DownloadListInformationDTO> mVideoInformationList = new ArrayList<>();
    public static DBHandler dbHandler;
    public DownloadListRecyclerViewAdapter downloadListRecyclerViewAdapter;
    private Button mDownloadAllButton;
    private final List<DownloadListInformationDTO> currentYTInformation = new ArrayList<>();

    public DownloadListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireView()).navigate(R.id.action_download_list_fragment_to_home_fragment);
            }
        });

    }

    public static void getDownloadList(){
        mVideoInformationList.clear();
        Cursor mCursor = dbHandler.getDownloadList();

        if (mCursor.getCount() == 0){
            Log.d("DATABASE_STATUS", "EMPTY DATABASE");
        }else{
            Log.d("DATABASE_STATUS", "THERE IS DATA IN DATABASE");
            while (mCursor.moveToNext()){
                String title = mCursor.getString(1).replace("\"", "");
                String format = mCursor.getString(2);
                String clipID = mCursor.getString(3);
                int itag = mCursor.getInt(4);
                String url = mCursor.getString(5);
                String ext = mCursor.getString(6);
                String link = mCursor.getString(7);
                String source = mCursor.getString(8);
                mVideoInformationList.add(new DownloadListInformationDTO(title,
                        format, clipID, itag, url, ext, link, source));
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);
        return inflater.inflate(R.layout.fragment_download_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDownloadAllButton = view.findViewById(R.id.download_all_selected_button);
        TextView mEmptyListTextView = view.findViewById(R.id.empty_list_text_view);

        if (!mVideoInformationList.isEmpty()){
            mEmptyListTextView.setVisibility(View.GONE);
            RecyclerView recyclerView = view.findViewById(R.id.download_list_recycler_view);
            recyclerView.setItemAnimator(new SlideInRightAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            downloadListRecyclerViewAdapter = new DownloadListRecyclerViewAdapter(requireActivity(), mVideoInformationList);
            recyclerView.setAdapter(new ScaleInAnimationAdapter(downloadListRecyclerViewAdapter));

            downloadListRecyclerViewAdapter.setOnButtonClickListeners(new DownloadListRecyclerViewAdapter.OnButtonClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onItemCheck(DownloadListInformationDTO downloadListInformationDTO) {
                    currentYTInformation.add(downloadListInformationDTO);
                    mDownloadAllButton.setText("Download all selected(" + currentYTInformation.size() + ")");
                    if (mDownloadAllButton.getVisibility() == View.GONE && !currentYTInformation.isEmpty()){
                        mDownloadAllButton.setVisibility(View.VISIBLE);
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onItemUncheck(DownloadListInformationDTO downloadListInformationDTO) {
                    currentYTInformation.remove(downloadListInformationDTO);
                    mDownloadAllButton.setText("Download all selected(" + currentYTInformation.size() + ")");
                    if (currentYTInformation.isEmpty() && mDownloadAllButton.getVisibility() == View.VISIBLE){
                        mDownloadAllButton.setVisibility(View.GONE);
                        DownloadListRecyclerViewAdapter.isSelectable = false;
                    }
                }
            });
        }else{
            mEmptyListTextView.setVisibility(View.VISIBLE);
        }

        mDownloadAllButton.setOnClickListener(v -> {
            if (!currentYTInformation.isEmpty()){
                mDownloadAllButton.setVisibility(View.GONE);
                Downloader downloader = new Downloader(requireContext());
                String userName = Objects.requireNonNull(FireBaseAuthHandler.getInstance().getAuthorization().getCurrentUser()).getDisplayName();
                FirestoreDBHandler firestoreDBHandler = new FirestoreDBHandler();
                for (DownloadListInformationDTO information : currentYTInformation){
                    String currentDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
                    firestoreDBHandler.add(userName,
                            UUID.randomUUID().toString(),
                            information.getTitle(),
                            information.getFormat(),
                            currentDate,
                            information.getSource());
                    downloader.downloadFromUrl(information.getDownloadURL(), information.getTitle());
                    int iTag = information.getITag();
                    DownloadListRecyclerViewAdapter.mClipInformationList.remove(information);
                    downloadListRecyclerViewAdapter.notifyDataSetChanged();
                    DownloadListFragment.dbHandler.deleteClipFromList(information.getTitle(),
                            iTag);
                }
                currentYTInformation.clear();
                NavHostActivity.mBottomNavigationView.getOrCreateBadge(R.id.download_list_fragment).
                        setNumber(mVideoInformationList.size());
            } else{
                Snackbar.make(requireView(), "No file has been selected.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}