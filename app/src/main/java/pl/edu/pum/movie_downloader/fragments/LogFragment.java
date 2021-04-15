package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;

public class LogFragment extends Fragment
{
    private Button mLogInButton;
    private TextView mRegisterTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private ImageButton mGoogleSignImageButton;
    private ImageButton mFacebookSignImageButton;
    private ProgressBar mLoginProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.log_fragment, container, false);

        mLogInButton = view.findViewById(R.id.login_button);
        mRegisterTextView = view.findViewById(R.id.register_text_view);
        mEmailEditText = view.findViewById(R.id.email_field);
        mPasswordEditText = view.findViewById(R.id.password_field);
        mGoogleSignImageButton = view.findViewById(R.id.google_login_button);
        mFacebookSignImageButton = view.findViewById(R.id.facebook_login_button);
        mLoginProgressBar = view.findViewById(R.id.wait_for_login_progress_bar);

        mLogInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(
                            getContext(),
                            "You have not entered your email or password."
                            , Toast.LENGTH_LONG)
                            .show();
                }
                else
                {
                    mLogInButton.setVisibility(View.INVISIBLE);
                    mLoginProgressBar.setVisibility(View.VISIBLE);

                    FirebaseAuth firebaseAuth = FireBaseAuthHandler.getInstance().getAuthorization();
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                Log.d("User login status", "The user has logged in");
                                Navigation.findNavController(view).navigate(R.id.action_logFragment_to_home_fragment);
                            }
                            else
                            {
                                Log.d("User login status", "Incorrect login data");
                                mLoginProgressBar.setVisibility(View.INVISIBLE);
                                mLogInButton.setVisibility(View.VISIBLE);
                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Login failure");
                                alertDialog.setMessage("An error occurred during sign in.\n" +
                                        "Please check your login details or try again later.");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    });
                }
            }
        });

        mRegisterTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Navigation.findNavController(view).navigate(R.id.action_logFragment_to_registerFragment);
            }
        });

        mGoogleSignImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO log by Google
            }
        });

        mFacebookSignImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Todo log by Facebook
            }
        });
        AddClearButton(mEmailEditText);
        AddClearButton(mPasswordEditText);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void AddClearButton(EditText edt)
    {
        edt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_baseline_clear_24,0);
                }
                else
                {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, 0,0);
                }
            }
        });

        edt.setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (edt.getCompoundDrawables()[2] != null)
                    {
                        if(event.getX() >= (edt.getRight()- edt.getLeft() - edt.getCompoundDrawables()[2].getBounds().width()))
                        {
                            edt.setText("");
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser firebaseUser = FireBaseAuthHandler.getInstance().getAuthorization().getCurrentUser();

        if (firebaseUser != null)
        {
            Navigation.findNavController(view).navigate(R.id.action_logFragment_to_home_fragment);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mLoginProgressBar.setVisibility(View.INVISIBLE);
        mLogInButton.setVisibility(View.VISIBLE);
    }
}
