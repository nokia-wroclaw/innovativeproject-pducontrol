package com.pdumanager.slawek.pdumanager;

import android.app.FragmentManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Toast;

import com.pdumanager.slawek.pdumanager.fragments.DevicesActivity;
import com.pdumanager.slawek.pdumanager.fragments.DevicesFromGroupActivity;
import com.pdumanager.slawek.pdumanager.fragments.MyGroupsActivity;
import com.pdumanager.slawek.pdumanager.fragments.PublicGroupsActivity;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private Menu menu;

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public static final String MyPREFERENCES = "MyPrefs";
    private boolean backPressedToExit;
    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username = sharedPrefs.getString(LoginActivity.Username, "");
        ((GlobalApplication) this.getApplication()).setUsername(username);

        if(TextUtils.isEmpty(username)) {
        //if(username.equals("")) {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        else {
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
            MenuItem usernameItem = (MenuItem) navigationView.getMenu().getItem(4).getSubMenu().getItem(0);
            usernameItem.setTitle(username);
            MenuItem itemDevices = navigationView.getMenu().getItem(0);
            itemDevices.setChecked(true);
            onNavigationItemSelected(itemDevices);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager.getBackStackEntryCount() <= 1){
                if(backPressedToExit){
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                } else {
                    backPressedToExit = true;
                    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                }
            } else {
                backPressedToExit = false;
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_devices) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new DevicesActivity()).addToBackStack( "devices" ).commit();
        } else if (id == R.id.nav_my_groups) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MyGroupsActivity()).addToBackStack( "private_groups" ).commit();
        } else if (id == R.id.nav_public_groups) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new PublicGroupsActivity()).addToBackStack( "public_groups" ).commit();
        } else if (id == R.id.nav_log_out) {
            SharedPreferences.Editor editor = sharedPrefs.edit();

            Toast.makeText(this, "Logged out: " + sharedPrefs.getString("username", ""), Toast.LENGTH_SHORT).show();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_chosen_devices) {
            if(((GlobalApplication) this.getApplication()).getSelectedGroupName() == null){
                Toast.makeText(this, "You didn't select any groups yet", Toast.LENGTH_LONG).show();
                return false;
            } else {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new DevicesFromGroupActivity()).addToBackStack( "devices_from_group" ).commit();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
