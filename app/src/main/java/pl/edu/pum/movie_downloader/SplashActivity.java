package pl.edu.pum.movie_downloader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        mHandler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }
}