package org.duder.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import org.duder.R;
import org.duder.view.fragment.DuderFragment;
import org.duder.view.fragment.EventFragment;
import org.duder.view.fragment.HomeFragment;

import java.util.List;

public class MainActivity extends BaseActivity {

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment eventFragment = new EventFragment();
    private final Fragment duderFragment = new DuderFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        navView.setOnNavigationItemSelectedListener(this::onNavigationItemSelectedListener);

//        if (savedInstanceState == null) { //Creating first time
            fm.beginTransaction().add(R.id.nav_host_fragment, duderFragment, "duder_fragment").hide(duderFragment).commit();
            fm.beginTransaction().add(R.id.nav_host_fragment, eventFragment, "event_fragment").hide(eventFragment).commit();
            fm.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "home_fragment").commit();
//        } else { //Screen was rotated, fragments are attached to fm
//            setActiveFragment();
//        }
    }

//    private void setActiveFragment() {
//        List<Fragment> fragments = fm.getFragments();
//        active = fragments.stream().filter(f -> !f.isHidden()).findAny().orElse(homeFragment);
//    }

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
            case R.id.navigation_duders:
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
}
