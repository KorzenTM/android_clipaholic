package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.alerts.Alerts;
import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class LoginFragment extends Fragment {
    private Button mLogInButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private ProgressBar mLoginProgressBar;
    private Alerts mAlerts;
    GoogleSignInOptions mGoogleSignInOptions;
    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ((DrawerLocker) requireActivity()).setDrawerEnabled(false);

        mLogInButton = view.findViewById(R.id.login_button);
        TextView mRegisterTextView = view.findViewById(R.id.register_text_view);
        TextView mForgotPasswordTextView = view.findViewById(R.id.forgot_password_text_view);
        mEmailEditText = view.findViewById(R.id.email_field);
        mPasswordEditText = view.findViewById(R.id.password_field);
        mLoginProgressBar = view.findViewById(R.id.wait_for_login_progress_bar);
        SignInButton mGoogleSignInButton = view.findViewById(R.id.google_sing_in_button);

        //configure google sign in
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), mGoogleSignInOptions);

        mLogInButton.setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(requireView(), "You have not entered your email or password.", Snackbar.LENGTH_SHORT).show();
            } else {
                mLogInButton.setVisibility(View.INVISIBLE);
                mLoginProgressBar.setVisibility(View.VISIBLE);
                FireBaseAuthHandler fireBaseAuthHandler = FireBaseAuthHandler.getInstance();
                fireBaseAuthHandler.signInUser(email, password, state -> {
                    switch (state) {
                        case "SUCCESS_LOGIN":
                            Navigation.findNavController(LoginFragment.this.requireView()).navigate(R.id.action_logFragment_to_home_fragment);
                            break;
                        case "NO_EMAIL_VERIFIED":
                            mLoginProgressBar.setVisibility(View.INVISIBLE);
                            mLogInButton.setVisibility(View.VISIBLE);
                            mAlerts.showNoActivatedAccountAlert(value -> {
                                if (value) {
                                    fireBaseAuthHandler.sendActivationEmailAgain(state1 -> {
                                        if (state1.equals("EMAIL_SENT")) {
                                            Snackbar.make(requireView(), "Verification E-mail has been sent again.", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            Snackbar.make(requireView(), "Verification E-mail has been not sent. Try again.", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            break;
                        case "INCORRECT_LOGIN_DATA":
                            mLoginProgressBar.setVisibility(View.INVISIBLE);
                            mLogInButton.setVisibility(View.VISIBLE);
                            mAlerts.showWrongUserDataAlert();
                            break;
                    }
                });
            }
        });

        mForgotPasswordTextView.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_logFragment_to_reset_fragment));

        mRegisterTextView.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_logFragment_to_registerFragment));

        mGoogleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        AddClearButton(mEmailEditText);
        AddClearButton(mPasswordEditText);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private static void AddClearButton(EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_baseline_clear_24,0);
                } else {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, 0,0);
                }
            }
        });

        edt.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (edt.getCompoundDrawables()[2] != null) {
                    if(event.getX() >= (edt.getRight()- edt.getLeft() - edt.getCompoundDrawables()[2].getBounds().width())) {
                        edt.setText("");
                    }
                }
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoginProgressBar.setVisibility(View.INVISIBLE);
        mLogInButton.setVisibility(View.VISIBLE);
        ((DrawerLocker) requireActivity()).setDrawerEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + Objects.requireNonNull(account).getId());
                FireBaseAuthHandler fireBaseAuthHandler = FireBaseAuthHandler.getInstance();

                fireBaseAuthHandler.signWithGoogleAccount(account.getIdToken(), state -> {
                    if (state.equals("SUCCESS_LOGIN_WITH_GOOGLE")) {
                        Snackbar.make(requireView(), account.getEmail() + ": successfully login with Google account", Snackbar.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.action_logFragment_to_home_fragment);
                    }
                    else if (state.equals("NO_SUCCESS_LOGIN_WITH_GOOGLE")) {
                        mAlerts.showWrongUserDataAlert();
                    }
                });
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
}
