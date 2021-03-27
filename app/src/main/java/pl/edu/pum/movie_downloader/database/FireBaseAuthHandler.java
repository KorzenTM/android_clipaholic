package pl.edu.pum.movie_downloader.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.edu.pum.movie_downloader.activities.NavHostActivity;

public final class FireBaseAuthHandler
{
    private static final String TAG = "EmailPassword";
    private static FireBaseAuthHandler instance;
    public static boolean isDone;
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

    public void logOutUserAccount()
    {
        mAuth.signOut();
    }
}
