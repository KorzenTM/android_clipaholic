package pl.edu.pum.movie_downloader.fragments;

import android.content.ClipData;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.adapters.DownloadListRecyclerViewAdapter;
import pl.edu.pum.movie_downloader.database.local.DBHandler;
import pl.edu.pum.movie_downloader.downloader.YouTubeURL.YouTubeDownloadURL;
import pl.edu.pum.movie_downloader.models.YouTubeDownloadListInformation;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class DownloadListFragment extends Fragment {
    public static List<Object> mVideoInformationList = new ArrayList<Object>();
    public static DBHandler dbHandler;
    private static Cursor mCursor;
    public DownloadListRecyclerViewAdapter downloadListRecyclerViewAdapter;
    private Button mDownloadAllButton;
    private List<YouTubeDownloadListInformation> currentYTInformation = new ArrayList<>();
    private TextView mEmptyListTextView;
    public DownloadListFragment() {
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

    public static void getDownloadList(){
        mVideoInformationList.clear();
        mCursor = dbHandler.getDownloadList();

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
                mVideoInformationList.add(new YouTubeDownloadListInformation(title,
                        format, clipID, itag, url, ext, link));
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);
        View view =  inflater.inflate(R.layout.fragment_download_list, container, false);
        mDownloadAllButton = view.findViewById(R.id.download_all_selected_button);
        mEmptyListTextView = view.findViewById(R.id.empty_list_text_view);

        if (!mVideoInformationList.isEmpty()){
            mEmptyListTextView.setVisibility(View.GONE);
            RecyclerView recyclerView = view.findViewById(R.id.download_list_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            downloadListRecyclerViewAdapter = new DownloadListRecyclerViewAdapter(requireActivity(), mVideoInformationList);
            recyclerView.setAdapter(downloadListRecyclerViewAdapter);

            downloadListRecyclerViewAdapter.setOnButtonClickListeners(new DownloadListRecyclerViewAdapter.OnButtonClickListener() {
                @Override
                public void onItemCheck(int position, YouTubeDownloadListInformation youTubeDownloadListInformation) {
                    currentYTInformation.add(youTubeDownloadListInformation);
                    mDownloadAllButton.setText("Download all selected(" + currentYTInformation.size() + ")");
                    if (mDownloadAllButton.getVisibility() == View.GONE && !currentYTInformation.isEmpty()){
                        mDownloadAllButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onItemUncheck(int position, YouTubeDownloadListInformation youTubeDownloadListInformation) {
                    currentYTInformation.remove(youTubeDownloadListInformation);
                    mDownloadAllButton.setText("Download all selected(" + currentYTInformation.size() + ")");
                    if (currentYTInformation.isEmpty() && mDownloadAllButton.getVisibility() == View.VISIBLE){
                        mDownloadAllButton.setVisibility(View.GONE);
                        return;
                    }
                }
            });
        }else{
            mEmptyListTextView.setVisibility(View.VISIBLE);
        }

        mDownloadAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentYTInformation.isEmpty()){
                    for (YouTubeDownloadListInformation information : currentYTInformation){
                        new YouTubeDownloadURL(requireContext(), information.getLink()).downloadVideoFromURL(
                                information.getDownloadURL(),
                                information.getTitle(),
                                information.getExtension()
                        );
                    }
                } else{
                    Snackbar.make(requireView(), "No file has been selected.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}