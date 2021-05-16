package pl.edu.pum.movie_downloader.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.adapters.DownloadListRecyclerViewAdapter;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class DownloadListFragment extends Fragment {
    public static List<Pair<String, String>> mVideoInformationList = new ArrayList<Pair<String, String>>();
    public static List<Pair<Integer, String>> mDownloadInformationList = new ArrayList<>();
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
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);
        View view =  inflater.inflate(R.layout.fragment_download_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.download_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        downloadListRecyclerViewAdapter = new DownloadListRecyclerViewAdapter(requireActivity(), mVideoInformationList, mDownloadInformationList);
        recyclerView.setAdapter(downloadListRecyclerViewAdapter);
        return view;
    }
}