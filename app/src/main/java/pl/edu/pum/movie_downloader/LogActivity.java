package pl.edu.pum.movie_downloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class LogActivity extends SingleFragmentActivity
{

    @Override
    protected Fragment createFragment()
    {
        return new LogFragment();
    }
}