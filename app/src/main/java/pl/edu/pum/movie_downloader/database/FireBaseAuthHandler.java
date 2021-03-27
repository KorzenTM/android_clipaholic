package pl.edu.pum.movie_downloader.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import pl.edu.pum.movie_downloader.fragments.HomeFragment;
import pl.edu.pum.movie_downloader.fragments.LogFragment;
import pl.edu.pum.movie_downloader.fragments.RegisterFragment;

public final class FireBaseAuthHandler
{
    private static final String TAG = "EmailPassword";
    private static FireBaseAuthHandler instance;
    private final FirebaseAuth mAuth;

    private FireBaseAuthHandler()
    {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public static FireBaseAuthHandler getInstance()
    {
        if (instance == null)
        {
            instance = new FireBaseAuthHandler();
        }
        return instance;
    }

    public static FirebaseAuth getAuthorization()
    {
        return FirebaseAuth.getInstance();
    }

    public void registerNewUser(final String email, final String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "createUserWithEmail:success");
                        }
                        else
                        {
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    public void loginUserAccount(final String email, final String password)
    {
        mAuth.
                signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithEmail:succes");
                        }
                        else
                        {
                            Log.d(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    };

    public void logOutUserAccount()
    {
        mAuth.signOut();
    }
}
