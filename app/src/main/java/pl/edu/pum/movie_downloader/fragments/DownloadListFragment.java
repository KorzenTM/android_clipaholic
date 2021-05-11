package pl.edu.pum.movie_downloader.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.youtube.model.Thumbnail;

import java.util.ArrayList;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.adapters.DownloadListRecyclerViewAdapter;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class DownloadListFragment extends Fragment {
    public static List<Pair<Thumbnail, String>> mVideoToDownloadList = new ArrayList<Pair<Thumbnail, String>>();
    public DownloadListRecyclerViewAdapter downloadListRecyclerViewAdapter;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);
        View view =  inflater.inflate(R.layout.fragment_download_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.download_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        downloadListRecyclerViewAdapter = new DownloadListRecyclerViewAdapter(requireActivity(), mVideoToDownloadList);
        recyclerView.setAdapter(downloadListRecyclerViewAdapter);
        return view;
    }
}