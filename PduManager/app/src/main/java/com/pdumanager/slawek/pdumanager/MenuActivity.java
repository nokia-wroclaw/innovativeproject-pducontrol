package com.pdumanager.slawek.pdumanager;

import android.app.Application;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.pdumanager.slawek.pdumanager.fragments.DevicesActivity;
import com.pdumanager.slawek.pdumanager.fragments.DevicesFromGroupActivity;
import com.pdumanager.slawek.pdumanager.fragments.MyGroupsActivity;
import com.pdumanager.slawek.pdumanager.fragments.PublicGroupsActivity;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    public NavigationView getNavigationView() {
        return navigationView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ((GlobalApplication) getApplication()).setSelectedGroupName(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        MenuItem itemDevices = navigationView.getMenu().getItem(0);
        itemDevices.setChecked(true);
        onNavigationItemSelected(itemDevices);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_devices) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new DevicesActivity()).commit();
        } else if (id == R.id.nav_my_groups) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MyGroupsActivity()).commit();
        } else if (id == R.id.nav_public_groups) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new PublicGroupsActivity()).commit();
        } else if (id == R.id.nav_log_out) {

        } else if (id == R.id.nav_chosen_devices) {
            if(((GlobalApplication) this.getApplication()).getSelectedGroupName() == null){
                Toast.makeText(this, "You didn't select any groups yet", Toast.LENGTH_LONG).show();
                return false;
            } else {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new DevicesFromGroupActivity()).commit();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
