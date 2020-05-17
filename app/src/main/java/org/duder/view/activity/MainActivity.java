package org.duder.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.duder.R;
import org.duder.databinding.ActivityMainBinding;
import org.duder.databinding.DrawerHeaderBinding;
import org.duder.util.UserSession;
import org.duder.view.fragment.HomeFragment;
import org.duder.view.fragment.dudes.DudesFragment;
import org.duder.view.fragment.event.EventMainFragment;

import static org.duder.util.UserSession.PREF_NAME;

public class MainActivity extends BaseActivity {

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment eventFragment = new EventMainFragment();
    private final Fragment duderFragment = new DudesFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = homeFragment;
    private ActivityMainBinding binding;
    private DrawerHeaderBinding headerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        View headerView = binding.navView.getHeaderView(0);
        headerBinding = DrawerHeaderBinding.bind(headerView);
        initializeLayout();
        initializeListeners();
    }

    private void initializeLayout() {
        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        navView.setOnNavigationItemSelectedListener(this::onNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.nav_host_fragment, duderFragment, "duder_fragment").hide(duderFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, eventFragment, "event_fragment").hide(eventFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "home_fragment").commit();

        initializeActionBar();
        setProfileImage();
    }

    private void initializeActionBar() {
        setSupportActionBar(binding.customBar.customBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void setProfileImage() {
        String imageUrl = getSharedPreferences(PREF_NAME, MODE_PRIVATE).getString(UserSession.IMAGE_URL, "");
        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).noFade().into(binding.customBar.viewProfile);
            Picasso.get().load(imageUrl).noFade().into(headerBinding.drawerHeaderImage);
        }
    }

    private void initializeListeners() {
        binding.customBar.viewProfile.setOnClickListener((v) -> {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        binding.navView.setNavigationItemSelectedListener(this::drawerMenuNavigation);
    }

    private boolean drawerMenuNavigation(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                logout();
                break;
        }
        return true;
    }

    private void logout() {
        UserSession.logOut(getSharedPreferences(PREF_NAME, MODE_PRIVATE), LoginManager.getInstance());
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean onNavigationItemSelectedListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fm.beginTransaction().hide(active).show(homeFragment).commit();
                active = homeFragment;
                return true;
            case R.id.navigation_events:
                fm.beginTransaction().hide(active).show(eventFragment).commit();
                active = eventFragment;
                return true;
            case R.id.navigation_dudes:
                fm.beginTransaction().hide(active).show(duderFragment).commit();
                active = duderFragment;
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat:
                final Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(chatIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
