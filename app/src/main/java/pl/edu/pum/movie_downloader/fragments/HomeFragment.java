package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.adapters.SourcesRecyclerViewAdapter;
import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class HomeFragment extends Fragment
{
    private TextView mHelloUserTextView;
    private RecyclerView mRecyclerView;
    SourcesRecyclerViewAdapter mMyAdapter;
    private List<Integer> mLogos = new ArrayList<Integer>()
    {
        {
            add(R.mipmap.youtube_icon);
            add(R.mipmap.facebook_icon);
            add(R.mipmap.vimeo_icon);
        }
    };

    FirebaseUser mCurrentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(true);

        //Disabled unnecessary back button after login (avoid blank screen)
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                showExitFromApplicationAlert();
            }
        });

        mHelloUserTextView = view.findViewById(R.id.hello_user_text_view);
        mRecyclerView = view.findViewById(R.id.source_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mMyAdapter = new SourcesRecyclerViewAdapter(getActivity(), mLogos);
        mRecyclerView.setAdapter(mMyAdapter);

        FireBaseAuthHandler fireBaseAuthHandler = FireBaseAuthHandler.getInstance();
        mCurrentUser = fireBaseAuthHandler.getAuthorization().getCurrentUser();

        return view;
    }

    private void showExitFromApplicationAlert()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Exit application");
        alertDialog.setMessage("Do you really want to leave the application?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        requireActivity().finish();
                        System.exit(0);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mHelloUserTextView.setText("Hello " + mCurrentUser.getDisplayName());

    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((DrawerLocker) requireActivity()).setDrawerEnabled(true);
    }
}