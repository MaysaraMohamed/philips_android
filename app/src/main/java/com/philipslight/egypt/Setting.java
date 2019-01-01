package com.philipslight.egypt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.philipslight.common.SaveSharedPreference;
import com.philipslight.common.User;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    BroadcastReceiver broadcastReceiver;

    private Button changePasswordButton;
    private TextView versionTV;
    private TextView languageTV;
    private CheckBox keepLoginCheckBox;
    private Spinner languageSpinner;
    private User user;
    private Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeLanguage(SaveSharedPreference.getUser(this).getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_setting);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.setting_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setTitle(getText(R.string.setting));

        // On logout button clicked
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.philips.philips.ACTION_LOGOUT");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                startActivity(Setting.this, LoginActivity.class);
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        changePasswordButton = findViewById(R.id.changePasswordButton);
        versionTV = findViewById(R.id.versionTV);
        languageTV = findViewById(R.id.languageTV);
        keepLoginCheckBox = findViewById(R.id.keepLoginCheckBox);
        languageSpinner = findViewById(R.id.languageSpinner);
        submit = findViewById(R.id.saveButton3);

        user = SaveSharedPreference.getUser(Setting.this);
        keepLoginCheckBox.setChecked(user.isKeepLogin());

        if(user.getLanguage().equals("ar")) {
            languageSpinner.setSelection(0);
        }else{
            languageSpinner.setSelection(1);
        }
        setHeaderData();
        updateFonts();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Setting.this, ResetPassword.class);
                Setting.this.finish();
            }
        });
        keepLoginCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setKeepLogin(((CheckBox) v).isChecked());
                SaveSharedPreference.setUser(Setting.this, user);
            }
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().toUpperCase().equals("ARABIC")) {
                    user.setLanguage("ar");
                    changeLanguage("ar");
                }else{
                    user.setLanguage("en");
                    changeLanguage("en");
                }
                SaveSharedPreference.setUser(Setting.this, user);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }});

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Setting.this, Setting.class);
                Setting.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.setting_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_newBill) {
            startActivity(Setting.this, NewInvoice.class);
            Setting.this.finish();
        } else if (id == R.id.nav_compensate) {
            startActivity(Setting.this, Compensate.class);
            Setting.this.finish();
        }else if (id == R.id.nav_myProfile) {
            startActivity(Setting.this, Profile.class);
            Setting.this.finish();
//        }else if (id == R.id.nav_dashboard) {
//            ContactUs.this.finish();
//            startActivity(ContactUs.this, Home.class);
        } else if (id == R.id.nav_about) {
            startActivity(Setting.this, AboutUs.class);
            Setting.this.finish();
        } else if (id == R.id.nav_contactUs) {
            startActivity(Setting.this, ContactUs.class);
            Setting.this.finish();
        } else if (id == R.id.nav_logout) {
            SaveSharedPreference.clearData(Setting.this);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.philips.philips.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);
        } else if (id == R.id.nav_rate) {
            Toast.makeText(Setting.this, "To handle rate us function", Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = findViewById(R.id.setting_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    private void startActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
    }

    private void setHeaderData(){
        // To get updated shared preference.
        User user = SaveSharedPreference.getUser(Setting.this);
        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view_setting);
        View headerView = navigationView1.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.customerIDTextView);
        navUsername.setText(user.getName());

        if(! user.getProfileImage().equals("")) {
            Log.d("Header", "SetHeader Method called from Home and image = "+user.getProfileImage());
            CircleImageView profileImage = (CircleImageView) headerView.findViewById(R.id.profileImageViewNavHome);
            Picasso.with(Setting.this).load(user.getProfileImage()).placeholder(R.drawable.mohamed)
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


    private void updateFonts() {
        Typeface centrale_sans_bold = Typeface.createFromAsset(getAssets(), "fonts/centrale_sans_bold.otf");
        Typeface centrale_sans_medium = Typeface.createFromAsset(getAssets(), "fonts/centrale_sans_medium.otf");
        changePasswordButton.setTypeface(centrale_sans_bold);
        versionTV.setTypeface(centrale_sans_bold);
        languageTV.setTypeface(centrale_sans_bold);
        keepLoginCheckBox.setTypeface(centrale_sans_bold);
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
