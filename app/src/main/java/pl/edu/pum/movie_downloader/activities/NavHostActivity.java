package pl.edu.pum.movie_downloader.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import pl.edu.pum.movie_downloader.R;

public class NavHostActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }
}