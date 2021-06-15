package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.adapters.AvailableSourcesRecyclerViewAdapter;
import pl.edu.pum.movie_downloader.alerts.Alerts;
import pl.edu.pum.movie_downloader.FirebaseAuthentication.FireBaseAuthHandler;
import pl.edu.pum.movie_downloader.database.local.sqlite.DBHandler;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class HomeFragment extends Fragment {
    private TextView mHelloUserTextView;
    AvailableSourcesRecyclerViewAdapter mMyAdapter;
    FirebaseUser mCurrentUser;
    private Alerts mAlerts;
    private final List<Pair<Integer, String>> mSupportedSources = new ArrayList<Pair<Integer, String>>() {{
            add(new Pair<>(R.mipmap.youtube_icon, "YouTube"));
            add(new Pair<>(R.mipmap.vimeo_icon, "Vimeo"));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlerts = new Alerts(getContext(), requireActivity());
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                mAlerts.showExitFromApplicationAlert();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);

        mHelloUserTextView = view.findViewById(R.id.hello_user_text_view);
        FireBaseAuthHandler fireBaseAuthHandler = FireBaseAuthHandler.getInstance();
        mCurrentUser = fireBaseAuthHandler.getAuthorization().getCurrentUser();

        FloatingActionButton nextSectionButton = view.findViewById(R.id.next_section_button);


        nextSectionButton.setOnClickListener(v -> {
            DownloadListFragment.dbHandler = new DBHandler(requireContext());
            Handler handler = new Handler();
            Runnable task = () -> {
                DownloadListFragment.getDownloadList();
                handler.post(() -> NavHostActivity.mBottomNavigationView.getOrCreateBadge(R.id.download_list_fragment).
                        setNumber(DownloadListFragment.mVideoInformationList.size()));
            };
            new Thread(task).start();
            Navigation.findNavController(requireView()).navigate(R.id.action_home_fragment_to_clip_information_fragment);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = view.findViewById(R.id.source_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mMyAdapter = new AvailableSourcesRecyclerViewAdapter(requireActivity(), mSupportedSources);
        mRecyclerView.setAdapter(mMyAdapter);
        setHelloMessageDependOfTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);
    }

    @SuppressLint("SetTextI18n")
    public void setHelloMessageDependOfTime() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String userName = mCurrentUser.getDisplayName();

        if(timeOfDay < 12) {
            mHelloUserTextView.setText("Good Morning " + userName);
        }
        else if(timeOfDay < 16) {
            mHelloUserTextView.setText("Good Afternoon " + userName);
        }
        else if(timeOfDay < 21) {
            mHelloUserTextView.setText("Good Evening " + userName);
        } else {
            mHelloUserTextView.setText("Good Night " + userName);
        }
    }
}