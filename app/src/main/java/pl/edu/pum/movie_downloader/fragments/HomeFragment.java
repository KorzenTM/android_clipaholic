package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;

public class HomeFragment extends Fragment
{
    private  FireBaseAuthHandler fireBaseAuthHandler;
    private  FirebaseUser currentUser;
    private TextView mHelloUserTextView;
    private Button mLogOutButton;

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

        mHelloUserTextView = view.findViewById(R.id.hello_user_text_view);
        mLogOutButton = view.findViewById(R.id.log_out_button);

        fireBaseAuthHandler = FireBaseAuthHandler.getInstance();
        currentUser = FireBaseAuthHandler.getAuthorization().getCurrentUser();

        mLogOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fireBaseAuthHandler.logOutUserAccount();
                Navigation.findNavController(view).navigate(R.id.action_home_fragment_to_logFragment);
            }
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mHelloUserTextView.setText("Hello " + currentUser.getEmail());
    }
}