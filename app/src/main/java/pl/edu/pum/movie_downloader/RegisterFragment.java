package pl.edu.pum.movie_downloader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class RegisterFragment extends Fragment
{
    private EditText mNickEditView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mRepeatedPasswordEditText;
    private Button mRegisterButton;
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

        mRegisterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO add new account
            }
        });

        LogFragment.AddClearButton(mNickEditView);
        LogFragment.AddClearButton(mEmailEditText);
        LogFragment.AddClearButton(mPasswordEditText);
        LogFragment.AddClearButton(mRepeatedPasswordEditText);
        return view;
    }
}
