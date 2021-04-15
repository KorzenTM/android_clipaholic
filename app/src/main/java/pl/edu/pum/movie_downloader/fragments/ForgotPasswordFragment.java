package pl.edu.pum.movie_downloader.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class ForgotPasswordFragment extends Fragment
{
    private EditText mEmailEditText;
    private Button mResetPasswordButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.forgot_password_fragment, container, false);

        mEmailEditText= view.findViewById(R.id.reset_password_email_edit_text);
        mResetPasswordButton = view.findViewById(R.id.reset_password_button);

        mResetPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = mEmailEditText.getText().toString();
                if (!email.isEmpty())
                {
                    FirebaseAuth firebaseAuth = FireBaseAuthHandler.getInstance().getAuthorization();

                    firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Log.d("Reset password status", "Reset password e-mail has been sent");
                            Toast.makeText(getContext(), "Password reset E-mail has been sent.", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(view).navigate(R.id.action_reset_fragment_to_logFragment);
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
                else
                {
                    Toast.makeText(getContext(), "You have not entered email.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
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
}
