package pl.edu.pum.movie_downloader.activities;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import pl.edu.pum.movie_downloader.R;
import pl.edu.pum.movie_downloader.alerts.Alerts;
import pl.edu.pum.movie_downloader.database.FireBaseAuthHandler;
import pl.edu.pum.movie_downloader.fragments.DownloadListFragment;
import pl.edu.pum.movie_downloader.navigation_drawer.DrawerLocker;

public class NavHostActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLocker
{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavController navController;
    private NavigationView navigationView;
    private Alerts mAlerts;
    public static BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.notification_bar_background));

        mAlerts = new Alerts(this, NavHostActivity.this);


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
    }

    private void setBottomMenuVisibility()
    {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.clip_information_fragment ||
                    destination.getId() == R.id.download_history_fragment ||
                    destination.getId() == R.id.download_list_fragment) {
                    setNumberOfElementToDownload();
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                } else {
                    mBottomNavigationView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setBottomMenuItemActions()
    {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.clip_information_fragment:
                        navController.navigate(R.id.clip_information_fragment);
                        break;
                    case R.id.download_list_fragment:
                        navController.navigate(R.id.download_list_fragment);
                        break;
                    case R.id.download_history_fragment:
                        navController.navigate(R.id.download_history_fragment);
                        break;
                }
                return true;
            }
        });
    }

    private void setNumberOfElementToDownload() {
        mBottomNavigationView.getOrCreateBadge(R.id.download_list_fragment).
                setNumber(DownloadListFragment.mVideoInformationList.size());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.profile_item:
                Toast.makeText(this, "User profile", Toast.LENGTH_LONG).show();
                break;
            case R.id.log_out_item:
                FireBaseAuthHandler fireBaseAuthHandler = FireBaseAuthHandler.getInstance();
                fireBaseAuthHandler.logOutUserAccount();
                navController.navigate(R.id.logFragment);
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
    public void onBackPressed()
    {
        boolean handled = false;
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setDrawerEnabled(boolean enabled)
    {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(enabled);

        FirebaseUser user = FireBaseAuthHandler.getInstance().getAuthorization().getCurrentUser();

        if (enabled && user != null)
        {
            View headerView = navigationView.getHeaderView(0);
            TextView userNick = headerView.findViewById(R.id.nav_user_nickname_textView);
            TextView email = headerView.findViewById(R.id.nav_email_textView);
            userNick.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }
    }
}