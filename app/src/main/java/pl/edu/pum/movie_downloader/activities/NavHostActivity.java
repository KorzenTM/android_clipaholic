package pl.edu.pum.movie_downloader.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.FirebaseAuthentication.FireBaseAuthHandler;
import pl.edu.pum.movie_downloader.fragments.DownloadListFragment;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class NavHostActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLocker {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavController navController;
    private NavigationView navigationView;
    public static BottomNavigationView mBottomNavigationView;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.notification_bar_background));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_app_bar_open_drawer_description,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navigationView.setNavigationItemSelectedListener(this);

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        setBottomMenuVisibility();
        setBottomMenuItemActions();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ActivityCompat.requestPermissions(NavHostActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        NavHostActivity.context = getApplicationContext();

        FirebaseUser firebaseUser = FireBaseAuthHandler.getInstance().getAuthorization().getCurrentUser();
        if (firebaseUser != null && firebaseUser.isEmailVerified())
        {
            Bundle extras = getIntent().getExtras();
            if (extras!= null){
                String link = extras.getString(Intent.EXTRA_TEXT);
                Log.d("LINK", link);
                Bundle bundle = new Bundle();
                bundle.putString("link", link);
                navController.navigate(R.id.clip_information_fragment, bundle);
            }else{
                navController.navigate(R.id.home_fragment);
            }
        }
    }

    public static Context getContext() {
        return NavHostActivity.context;
    }

    private void setBottomMenuVisibility() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.clip_information_fragment ||
                destination.getId() == R.id.download_history_fragment ||
                destination.getId() == R.id.download_list_fragment) {
                setNumberOfElementToDownload();
                mBottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                mBottomNavigationView.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void setBottomMenuItemActions() {
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build();
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.clip_information_fragment:
                    navController.navigate(R.id.clip_information_fragment, null, options);
                    break;
                case R.id.download_list_fragment:
                    navController.navigate(R.id.download_list_fragment, null, options);
                    break;
                case R.id.download_history_fragment:
                    navController.navigate(R.id.download_history_fragment, null, options);
                    break;
            }
            return true;
        });
    }

    private void setNumberOfElementToDownload() {
        mBottomNavigationView.getOrCreateBadge(R.id.download_list_fragment).
                setNumber(DownloadListFragment.mVideoInformationList.size());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build();
        switch (item.getItemId()) {
            case R.id.profile_item:
                Toast.makeText(this, "User profile", Toast.LENGTH_LONG).show();
                break;
            case R.id.log_out_item:
                FireBaseAuthHandler fireBaseAuthHandler = FireBaseAuthHandler.getInstance();
                fireBaseAuthHandler.logOutUserAccount();
                navController.navigate(R.id.logFragment, null, options);
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Successfully logged out. See you later!", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.help_and_support_item:
                Toast.makeText(this, "Help and support", Toast.LENGTH_LONG).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(enabled);

        FirebaseUser user = FireBaseAuthHandler.getInstance().getAuthorization().getCurrentUser();

        if (enabled && user != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView userNick = headerView.findViewById(R.id.nav_user_nickname_textView);
            TextView email = headerView.findViewById(R.id.nav_email_textView);
            userNick.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION_STATUS", "Permission granted");
            } else {
                Toast.makeText(NavHostActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}