package pl.edu.pum.movie_downloader.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseUser;

import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;

public class SplashActivity extends AppCompatActivity
{
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private final Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(!isFinishing())
            {
                startActivity(new Intent(getApplicationContext(), NavHostActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        mHandler.postDelayed(runnable, 200);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }
}