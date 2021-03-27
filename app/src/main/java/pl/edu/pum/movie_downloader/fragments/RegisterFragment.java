package pl.edu.pum.movie_downloader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseUser;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;

public class RegisterFragment extends Fragment
{
    private EditText mNickEditView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mRepeatedPasswordEditText;
    private Button mRegisterButton;
    private ProgressBar mRegisterProgressBar;
    private final FireBaseAuthHandler fireBaseAuthHandler = FireBaseAuthHandler.getInstance();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        mNickEditView = view.findViewById(R.id.nick);
        mEmailEditText = view.findViewById(R.id.email);
        mPasswordEditText = view.findViewById(R.id.password);
        mRepeatedPasswordEditText = view.findViewById(R.id.repeat_password);
        mRegisterButton = view.findViewById(R.id.register_button);
        mRegisterProgressBar = view.findViewById(R.id.wait_for_register_bar);

        mRegisterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mRegisterButton.setVisibility(View.INVISIBLE);
                mRegisterProgressBar.setVisibility(View.VISIBLE);
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String repeatedPassword = mRepeatedPasswordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getContext(), "Complete all fields.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Runnable runnable = () ->
                    {
                        fireBaseAuthHandler.registerNewUser(email, password);
                    };
                    Thread t1 = new Thread(runnable);
                    t1.start();

                    FirebaseUser currentUser = FireBaseAuthHandler.getAuthorization().getCurrentUser();
                    while (currentUser == null) //wait for login succesfull
                    {
                        currentUser = FireBaseAuthHandler.getAuthorization().getCurrentUser(); //check again
                    }
                    Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_logFragment);
                }
            }
        });

        LogFragment.AddClearButton(mNickEditView);
        LogFragment.AddClearButton(mEmailEditText);
        LogFragment.AddClearButton(mPasswordEditText);
        LogFragment.AddClearButton(mRepeatedPasswordEditText);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mRegisterButton.setVisibility(View.VISIBLE);
        mRegisterProgressBar.setVisibility(View.INVISIBLE);
    }
}
