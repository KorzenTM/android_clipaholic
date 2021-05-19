package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.alerts.Alerts;
import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class ResetPasswordFragment extends Fragment {
    private EditText mEmailEditText;
    private Button mResetPasswordButton;
    private ProgressBar mWaitingForSendProgressBar;
    private Alerts mAlerts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        ((DrawerLocker) requireActivity()).setDrawerEnabled(false);

        mEmailEditText= view.findViewById(R.id.reset_password_email_edit_text);
        mResetPasswordButton = view.findViewById(R.id.reset_password_button);
        mWaitingForSendProgressBar = view.findViewById(R.id.wait_for_send_reset_email_progress_bar);
        mAlerts = new Alerts(requireContext(), requireActivity());

        mResetPasswordButton.setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString();
            if (!email.isEmpty()) {
                mResetPasswordButton.setActivated(false);
                mResetPasswordButton.setText("Wait...");
                mWaitingForSendProgressBar.setVisibility(View.VISIBLE);
                FireBaseAuthHandler fireBaseAuthHandler = FireBaseAuthHandler.getInstance();
                fireBaseAuthHandler.sendResetEmailToUser(email, state -> {
                    if (state.equals("RESET_EMAIL_SENT")) {
                        Snackbar.make(requireView(), "Password reset E-mail has been sent.", Snackbar.LENGTH_SHORT).show();
                        Navigation.findNavController(ResetPasswordFragment.this.requireView()).navigate(R.id.action_reset_fragment_to_logFragment);
                    }
                    else if (state.equals("RESET_EMAIL_NOT_SENT")) {
                        mAlerts.showSendEmailErrorAlert();
                    }
                });
            } else {
                Snackbar.make(requireView(), "You have not entered email.", Snackbar.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        mResetPasswordButton.setActivated(true);
        mResetPasswordButton.setText("RESET PASSWORD");
        mWaitingForSendProgressBar.setVisibility(View.INVISIBLE);
        ((DrawerLocker) requireActivity()).setDrawerEnabled(false);
    }
}
