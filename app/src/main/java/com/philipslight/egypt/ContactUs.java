package com.philipslight.egypt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.philipslight.common.SaveSharedPreference;
import com.philipslight.common.User;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactUs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeLanguage(SaveSharedPreference.getUser(this).getLanguage());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);


        NavigationView navigationView = (NavigationView) findViewById(R.id.contact_us_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_us_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_contact_us_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // On logout button clicked
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.philips.philips.ACTION_LOGOUT");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                startActivity(ContactUs.this, LoginActivity.class);
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        setHeaderData();
        setTitle(getText(R.string.contactUs));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_contact_us_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_newBill) {
            startActivity(ContactUs.this, NewInvoice.class);
            ContactUs.this.finish();
        } else if (id == R.id.nav_compensate) {
            startActivity(ContactUs.this, Compensate.class);
            ContactUs.this.finish();
        }else if (id == R.id.nav_myProfile) {
            startActivity(ContactUs.this, Profile.class);
            ContactUs.this.finish();
//        }else if (id == R.id.nav_dashboard) {
//            ContactUs.this.finish();
//            startActivity(ContactUs.this, Home.class);
        }
        else if (id == R.id.nav_setting) {
            ContactUs.this.finish();
            startActivity(ContactUs.this, Setting.class);
        }else if (id == R.id.nav_about) {
            startActivity(ContactUs.this, AboutUs.class);
            ContactUs.this.finish();
        } else if (id == R.id.nav_contactUs) {
            startActivity(ContactUs.this, ContactUs.class);
            ContactUs.this.finish();
        } else if (id == R.id.nav_logout) {
            SaveSharedPreference.clearData(ContactUs.this);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.philips.philips.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);
        } else if (id == R.id.nav_rate) {
            Toast.makeText(ContactUs.this, "To handle rate us function", Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_contact_us_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void startActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (Exception ex){
            Log.e("ERROR", ex.getLocalizedMessage());
        }
    }


    private void setHeaderData(){
        // To get updated shared preference.
        User user = SaveSharedPreference.getUser(ContactUs.this);
        NavigationView navigationView1 = (NavigationView) findViewById(R.id.contact_us_nav_view);
        View headerView = navigationView1.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.customerIDTextView);
        navUsername.setText(user.getName());

        if(! user.getProfileImage().equals("")) {
            Log.d("Header", "SetHeader Method called from Home and image = "+user.getProfileImage());
            CircleImageView profileImage = (CircleImageView) headerView.findViewById(R.id.profileImageViewNavHome);
            Picasso.with(ContactUs.this).load(user.getProfileImage()).placeholder(R.drawable.mohamed)
                    .error(R.drawable.mohamed)
                    .into(profileImage, new com.squareup.picasso.Callback(){
                        @Override
                        public void onSuccess() {
                            Log.d("Success", "Success to Load image");
                        }
                        @Override
                        public void onError() {
                            Log.d("Failed", "Failed to Load image");
                        }
                    });
        }
    }


    private void changeLanguage(String lang) {
        String languageToLoad  = lang; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
