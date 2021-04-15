package pl.edu.pum.movie_downloader.database;

import com.google.firebase.auth.FirebaseAuth;

public final class FireBaseAuthHandler
{
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

    public FirebaseAuth getAuthorization()
    {
        return mAuth;
    }

    public void logOutUserAccount()
    {
        mAuth.signOut();
    }
}
