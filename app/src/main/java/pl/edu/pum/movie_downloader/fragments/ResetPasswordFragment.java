package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;

public class ResetPasswordFragment extends Fragment
{
    private EditText mEmailEditText;
    private Button mResetPasswordButton;
    private ProgressBar mWaitingForSendProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.reset_password_fragment, container, false);

        mEmailEditText= view.findViewById(R.id.reset_password_email_edit_text);
        mResetPasswordButton = view.findViewById(R.id.reset_password_button);
        mWaitingForSendProgressBar = view.findViewById(R.id.wait_for_send_reset_email_progress_bar);

        mResetPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                String email = mEmailEditText.getText().toString();
                if (!email.isEmpty())
                {
                    mResetPasswordButton.setActivated(false);
                    mResetPasswordButton.setText("Wait...");
                    mWaitingForSendProgressBar.setVisibility(View.VISIBLE);
                    sendResetEmailToUser(email);
                }
                else
                {
                    Toast.makeText(getContext(), "You have not entered email.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    private void sendResetEmailToUser(String email)
    {
        FirebaseAuth firebaseAuth = FireBaseAuthHandler.getInstance().getAuthorization();

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d("Reset password status", "Reset password e-mail has been sent");
                Toast.makeText(getContext(), "Password reset E-mail has been sent.", Toast.LENGTH_LONG).show();
                Navigation.findNavController(ResetPasswordFragment.this.requireView()).navigate(R.id.action_reset_fragment_to_logFragment);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d("Reset password status", "onFailure: email " + e.toString());
                showSendEmailErrorAlert();
            }
        });

    }

    private void showSendEmailErrorAlert()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Sending error email");
        alertDialog.setMessage("An error occurred during sending an email.\n" +
                "Please check your detail or try again later.");
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume()
    {
        super.onResume();
        mResetPasswordButton.setActivated(true);
        mResetPasswordButton.setText("RESET PASSWORD");
        mWaitingForSendProgressBar.setVisibility(View.INVISIBLE);
    }
}
