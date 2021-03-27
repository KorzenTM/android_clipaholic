package pl.edu.pum.movie_downloader.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.List;

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
    private static final int PASSWORD_LENGTH = 8;
    List<EditText> mForm = new ArrayList<EditText>();

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

        //get all EditText from register form
        RelativeLayout layout = view.findViewById(R.id.register_form_layout);
        Drawable default_edit_text_theme;
        for (int i = 0; i < layout.getChildCount(); i++)
        {
            if (layout.getChildAt(i) instanceof EditText)
            {
                EditText test = (EditText )layout.getChildAt(i);
                mForm.add(test);
            }
        }
        addClearButton(mForm);

        mRegisterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String repeatedPassword = mRepeatedPasswordEditText.getText().toString();

                if (ifEmptyField())
                {
                    if (checkPassword(password, repeatedPassword))
                    {
                        setPasswordFieldState("Correct password", 0);
                        mRegisterButton.setVisibility(View.INVISIBLE);
                        mRegisterProgressBar.setVisibility(View.VISIBLE);
                        FirebaseAuth firebaseAuth = FireBaseAuthHandler.getAuthorization();
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_logFragment);
                                }
                                else
                                {
                                    mRegisterButton.setVisibility(View.VISIBLE);
                                    mRegisterProgressBar.setVisibility(View.INVISIBLE);
                                    AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.drawable.rounded_corners).create();
                                    alertDialog.setTitle("Register failure");
                                    alertDialog.setMessage("An error occurred during sign in.\n" +
                                            "Please check your registration details or try again later.");
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
            }
        });
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addClearButton(List<EditText> form)
    {
        Drawable default_edit_text_theme = mForm.get(0).getBackground(); //just handle default theme of edittext to change
        for (EditText edt: form)
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
                    edt.setBackground(default_edit_text_theme);
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
    }

    private boolean ifEmptyField()
    {
        boolean isEmpty = true;
        for (EditText editText : mForm)
        {
            if (editText.getText().toString().isEmpty())
            {
                editText.setError("This field cannot be blank");
                editText.setBackgroundResource(R.drawable.error_edit_text_background);
                isEmpty = false;
            }
            else
            {
                editText.setBackgroundResource(R.drawable.confirm_edit_text_background);
            }
        }
        return isEmpty;
    }

    private boolean checkPassword(String password, String repeatedPassword)
    {
        if (!password.equals(repeatedPassword))
        {
            setPasswordFieldState("Password didn't match", 1);
            return false;
        }
        String regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        boolean isCorrect = password.matches(regexp);

        if (!isCorrect)
        {
            setPasswordFieldState("Password does not meet the requirements", 1);
        }
        return isCorrect;
    }

    private void setPasswordFieldState(String msg, int state)
    {
        if (state == 0)
        {
            mPasswordEditText.setBackgroundResource(R.drawable.confirm_edit_text_background);
            mRepeatedPasswordEditText.setBackgroundResource(R.drawable.confirm_edit_text_background);
            Log.d("PASSWORD", msg);
        }
        else if (state == 1)
        {
            mPasswordEditText.setBackgroundResource(R.drawable.error_edit_text_background);
            mRepeatedPasswordEditText.setBackgroundResource(R.drawable.error_edit_text_background);
            mRepeatedPasswordEditText.setError(msg);
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        mRegisterButton.setVisibility(View.VISIBLE);
        mRegisterProgressBar.setVisibility(View.INVISIBLE);
    }
}
