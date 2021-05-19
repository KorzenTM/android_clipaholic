package pl.edu.pum.movie_downloader.database;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import pl.edu.pum.movie_downloader.models.User;

public final class FireBaseAuthHandler {
    private static FireBaseAuthHandler instance;
    private final FirebaseAuth mAuth;
    private static final String TAG = "GoogleActivity";

    private FireBaseAuthHandler()
    {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public static FireBaseAuthHandler getInstance() {
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

    public void signInUser(String email, String password, FireBaseAuthState fireBaseAuthState) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (Objects.requireNonNull(user).isEmailVerified()) {
                    Log.d("User login status", "The user has logged in");
                    fireBaseAuthState.isOperationSuccessfully("SUCCESS_LOGIN");
                } else {
                    fireBaseAuthState.isOperationSuccessfully("NO_EMAIL_VERIFIED");
                }
            } else {
                fireBaseAuthState.isOperationSuccessfully("INCORRECT_LOGIN_DATA");
                Log.d("User login status", "Incorrect login data");
            }
        });
    }

    public void sendActivationEmailAgain(FireBaseAuthState fireBaseAuthState) {
        FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnSuccessListener(aVoid -> {
            fireBaseAuthState.isOperationSuccessfully("EMAIL_SENT");
            logOutUserAccount(); })
                .addOnFailureListener(e -> Log.d("Activation link status", "onFailure: Email not sent " + e.toString()));
    }

    public void createNewUser(String nickname, String email, String password, FireBaseAuthState fireBaseAuthState) {
        User newUser = new User(nickname, email, password);
        mAuth.createUserWithEmailAndPassword(newUser.getUserEmail(),
                newUser.getUserPassword()).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        sendActivationEmailToUser(Objects.requireNonNull(user));
                        setDisplayNameForNewUser(newUser.getUserNickname(), user);
                        fireBaseAuthState.isOperationSuccessfully("NEW_USER_CREATED");
                        logOutUserAccount();
                        Log.d("User register status", "New account registration successful");
                    } else {
                        fireBaseAuthState.isOperationSuccessfully("NEW_USER_NOT_CREATED");
                        Log.d("User register status", "New account registration unsuccessful");
                    }
                });
    }

    private void sendActivationEmailToUser(FirebaseUser user) {
        //send verification email for new user email
        user.sendEmailVerification()
                .addOnSuccessListener(aVoid -> Log.d("Activation link status", "onSuccess: Email sent "))
                .addOnFailureListener(e -> Log.d("Activation link status", "onFailure: Email not sent " + e.toString()));
    }

    private void setDisplayNameForNewUser(String nick, FirebaseUser user) {
        //set Display name for new user
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                .Builder()
                .setDisplayName(nick)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> Log.d("User account update status", "User profile updated"));
    }

    public void sendResetEmailToUser(String email, FireBaseAuthState fireBaseAuthState) {
        FirebaseAuth firebaseAuth = FireBaseAuthHandler.getInstance().getAuthorization();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
            Log.d("Reset password status", "Reset password e-mail has been sent");
            fireBaseAuthState.isOperationSuccessfully("RESET_EMAIL_SENT");
                })
                .addOnFailureListener(e -> {
            Log.d("Reset password status", "onFailure: email " + e.toString());
            fireBaseAuthState.isOperationSuccessfully("RESET_EMAIL_NOT_SENT");
        });
    }

    public void signWithGoogleAccount(String idToken, FireBaseAuthState fireBaseAuthState) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        fireBaseAuthState.isOperationSuccessfully("SUCCESS_LOGIN_WITH_GOOGLE");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        fireBaseAuthState.isOperationSuccessfully("NO_SUCCESS_LOGIN_WITH_GOOGLE");
                    }
                });
    }

    public void logOutUserAccount()
    {
        mAuth.signOut();
    }
}
