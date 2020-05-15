package org.duder.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.duder.R;
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
    Fragment active = homeFragment;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageView actionBarProfileImage;
    private ImageView drawerHeaderProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeLayout();
        initializeListeners();
    }

    private void initializeLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        navView.setOnNavigationItemSelectedListener(this::onNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.nav_host_fragment, duderFragment, "duder_fragment").hide(duderFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, eventFragment, "event_fragment").hide(eventFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "home_fragment").commit();

        initializeActionBar();
        initializeDrawerMenu();
        setProfileImage();
    }

    private void initializeActionBar() {
        toolbar = findViewById(R.id.custom_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        actionBarProfileImage = findViewById(R.id.view_profile);
    }

    private void initializeDrawerMenu() {
        navigationView = findViewById(R.id.nav_view);
        drawerHeaderProfileImage = navigationView.getHeaderView(0).findViewById(R.id.drawer_header_image);
    }

    private void setProfileImage() {
        String imageUrl = getSharedPreferences(PREF_NAME, MODE_PRIVATE).getString(UserSession.IMAGE_URL, "");
        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).noFade().into(actionBarProfileImage);
            Picasso.get().load(imageUrl).noFade().into(drawerHeaderProfileImage);
        }
    }

    private void initializeListeners() {
        actionBarProfileImage.setOnClickListener((v) -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this::drawerMenuNavigation);
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
