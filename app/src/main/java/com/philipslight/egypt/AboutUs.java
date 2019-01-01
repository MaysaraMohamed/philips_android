package com.philipslight.egypt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.philipslight.common.Configurations;
import com.philipslight.common.SaveSharedPreference;
import com.philipslight.common.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutUs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    BroadcastReceiver broadcastReceiver;
    TextView pointsRedemptionTitle;
    TextView abouttheProgramTitleTV;
    TextView pointsEarningTV;
    TextView giftslistTV;
    User user;
    ArrayList<String> pointsList;
    ArrayList<String> giftsList;
    String defaultLang;
    ListView pointsListViewAboutUS;
    ListView giftsListViewAboutUS;

    LinearLayout redemptionLayout;
    LinearLayout pointsEarninglinearLayout;
    LinearLayout aboutProgramLinearLayout;
    LinearLayout giftsLayout;
    Typeface centrale_sans_medium;
    int pixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeLanguage(SaveSharedPreference.getUser(this).getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        NavigationView navigationView = (NavigationView) findViewById(R.id.about_us_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.about_us_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_about_us_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        pointsListViewAboutUS = findViewById(R.id.pointsListViewAboutUS);
        giftsListViewAboutUS = findViewById(R.id.giftsListViewAboutUS);

        pointsRedemptionTitle = findViewById(R.id.pointsRedemptionTitleTV2);
        abouttheProgramTitleTV = findViewById(R.id.abouttheProgramTitleTV);
        pointsEarningTV = findViewById(R.id.pointsEarningTV);
        giftslistTV = findViewById(R.id.giftslistTV);

        centrale_sans_medium = Typeface.createFromAsset(getAssets(), "fonts/centrale_sans_medium.otf");

        redemptionLayout = findViewById(R.id.redemptiondLayout);
        pointsEarninglinearLayout = findViewById(R.id.pointsEarninglinearLayout);
        aboutProgramLinearLayout = findViewById(R.id.aboutProgramLinearLayout);
        giftsLayout = findViewById(R.id.giftsLayout);

        user = SaveSharedPreference.getUser(AboutUs.this);
        sendGetGiftsData(user.getUserType());
        // On logout button clicked
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.philips.philips.ACTION_LOGOUT");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                startActivity(AboutUs.this, LoginActivity.class);
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);


//        defaultLang= Locale.getDefault().getLanguage();
        defaultLang = SaveSharedPreference.getUser(AboutUs.this).getLanguage();


        pointsRedemptionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(redemptionLayout.getVisibility() == View.GONE ) {
                    redemptionLayout.setVisibility(View.VISIBLE);
                    aboutProgramLinearLayout.setVisibility(View.GONE);
                    pointsEarninglinearLayout.setVisibility(View.GONE);
                    giftsLayout.setVisibility(View.GONE);
                }
                else {
                    redemptionLayout.setVisibility(View.GONE);
                }
            }
        });


        abouttheProgramTitleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aboutProgramLinearLayout.getVisibility() == View.GONE ) {
                    aboutProgramLinearLayout.setVisibility(View.VISIBLE);
                    redemptionLayout.setVisibility(View.GONE);
                    giftsLayout.setVisibility(View.GONE);
                    pointsEarninglinearLayout.setVisibility(View.GONE);
                }
                else {
                    aboutProgramLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        pointsEarningTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pointsEarninglinearLayout.getVisibility() == View.GONE ) {
                    pointsEarninglinearLayout.setVisibility(View.VISIBLE);
                    aboutProgramLinearLayout.setVisibility(View.GONE);
                    redemptionLayout.setVisibility(View.GONE);
                    giftsLayout.setVisibility(View.GONE);
                }
                else {
                    pointsEarninglinearLayout.setVisibility(View.GONE);
                }
            }
        });

        giftslistTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(giftsLayout.getVisibility() == View.GONE ) {
                    giftsLayout.setVisibility(View.VISIBLE);
                    pointsEarninglinearLayout.setVisibility(View.GONE);
                    aboutProgramLinearLayout.setVisibility(View.GONE);
                    redemptionLayout.setVisibility(View.GONE);
                }
                else {
                    giftsLayout.setVisibility(View.GONE);
                }
            }
        });

        setHeaderData();
        updateFonts();
        setTitle(getText(R.string.aboutProgram));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_about_us_layout);
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
            startActivity(AboutUs.this, NewInvoice.class);
            AboutUs.this.finish();
        } else if (id == R.id.nav_compensate) {
            startActivity(AboutUs.this, Compensate.class);
            AboutUs.this.finish();
        }else if (id == R.id.nav_myProfile) {
            AboutUs.this.finish();
            startActivity(AboutUs.this, Profile.class);
//        }else if (id == R.id.nav_dashboard) {
//            AboutUs.this.finish();
//            startActivity(AboutUs.this, Home.class);
//        } else if (id == R.id.nav_about) {
//            startActivity(AboutUs.this, AboutUs.class);
//            AboutUs.this.finish();
        }else if (id == R.id.nav_setting) {
            AboutUs.this.finish();
            startActivity(AboutUs.this, Setting.class);
        } else if (id == R.id.nav_contactUs) {
            startActivity(AboutUs.this, ContactUs.class);
            AboutUs.this.finish();
        } else if (id == R.id.nav_logout) {
            SaveSharedPreference.clearData(AboutUs.this);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.philips.philips.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);
        } else if (id == R.id.nav_rate) {
            Toast.makeText(AboutUs.this, "To handle rate us function", Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_about_us_layout);
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


    private boolean sendGetGiftsData(String userType) {
        // Instantiate the RequestQueue.
        List<String> list = new ArrayList<String>();
        RequestQueue queue = Volley.newRequestQueue(AboutUs.this);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Configurations.getAllGiftsURL()+"/"+userType,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String giftName = "";
                            pointsList = new ArrayList<String>();
                            giftsList = new ArrayList<String>();
                            JSONArray jsonArray = response.getJSONArray("gifts");
                            for (int i=0; i < jsonArray.length(); i++) {
                                if(defaultLang.toUpperCase().equals("AR")) {
                                    giftName = jsonArray.getJSONObject(i).getString("arGiftOptions");
                                    giftsList.add(giftName);
                                } else{
                                    giftName = jsonArray.getJSONObject(i).getString("giftOptions");
                                    giftsList.add(giftName);
                                }
                                // to Convert to integer value.
                                pointsList.add(Integer.parseInt(jsonArray.getJSONObject(i).getString("points").split("\\.")[0])+"");
                            }
                            Log.d("Gifts list is : ", giftsList.toString());
                            Log.d("Points list is : ", pointsList.toString());

                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                                    (AboutUs.this, android.R.layout.simple_spinner_item, giftsList){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent){
                                    View view = super.getView(position, convertView, parent);
                                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                    tv.setPadding(0,5,0,5);
                                    tv.setTextColor(Color.BLACK);
                                    tv.setGravity(Gravity.CENTER);
                                    tv.setTypeface(centrale_sans_medium);
                                    tv.setTextSize(13);
                                    return view;
                                }
                            };

                            giftsListViewAboutUS.setAdapter(dataAdapter);
                            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>
                                    (AboutUs.this, android.R.layout.simple_spinner_item, pointsList){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent){
                                    View view = super.getView(position, convertView, parent);
                                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                    tv.setPadding(0,5,0,5);
                                    tv.setTextColor(Color.BLACK);
                                    tv.setGravity(Gravity.CENTER);
                                            tv.setTypeface(centrale_sans_medium);
                                    tv.setTextSize(13);
                                    return view;
                                }
                            };
                            pointsListViewAboutUS.setAdapter(dataAdapter2);
                            dataAdapter.notifyDataSetChanged();
                            dataAdapter2.notifyDataSetChanged();

                        } catch (Exception e) {
                            Log.e("Response is : ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
                        Toast.makeText(AboutUs.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");

                //new for Authentication
                String credentials = Configurations.getUserName()+":"+Configurations.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return true;
    }


    private void setHeaderData(){
        // To get updated shared preference.
        User user = SaveSharedPreference.getUser(AboutUs.this);
        NavigationView navigationView1 = (NavigationView) findViewById(R.id.about_us_nav_view);
        View headerView = navigationView1.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.customerIDTextView);
        navUsername.setText(user.getName());

        if(! user.getProfileImage().equals("")) {
            Log.d("Header", "SetHeader Method called from Home and image = "+user.getProfileImage());
            CircleImageView  profileImage = (CircleImageView) headerView.findViewById(R.id.profileImageViewNavHome);
            Picasso.with(AboutUs.this).load(user.getProfileImage()).placeholder(R.drawable.mohamed)
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


    private void updateFonts()
    {
        Typeface centrale_sans_bold = Typeface.createFromAsset(getAssets(), "fonts/centrale_sans_bold.otf");
        Typeface centrale_sans_medium = Typeface.createFromAsset(getAssets(), "fonts/centrale_sans_medium.otf");
        pointsRedemptionTitle.setTypeface(centrale_sans_bold);
        abouttheProgramTitleTV.setTypeface(centrale_sans_bold);
        pointsEarningTV.setTypeface(centrale_sans_bold);
        giftslistTV.setTypeface(centrale_sans_bold);

        TextView giftsListPointsHeader = findViewById(R.id.giftsListPointsHeader);
        TextView giftsListPointsOptionHeader = findViewById(R.id.giftsListPointsOptionHeader);
        giftsListPointsHeader.setTypeface(centrale_sans_bold);
        giftsListPointsOptionHeader.setTypeface(centrale_sans_bold);

        TextView pointsRedemptionLine1 = findViewById(R.id.pointsRedemptionLine1);
        TextView pointsRedemptionLine2 = findViewById(R.id.pointsRedemptionLine2);
        TextView pointsRedemptionLine3 = findViewById(R.id.pointsRedemptionLine3);
        pointsRedemptionLine1.setTypeface(centrale_sans_medium);
        pointsRedemptionLine2.setTypeface(centrale_sans_medium);
        pointsRedemptionLine3.setTypeface(centrale_sans_medium);

        TextView aboutProgramLine1 = findViewById(R.id.aboutProgramLine1);
        TextView aboutProgramLine2 = findViewById(R.id.aboutProgramLine2);
        aboutProgramLine1.setTypeface(centrale_sans_medium);
        aboutProgramLine2.setTypeface(centrale_sans_medium);

        TextView ledPointsTV = findViewById(R.id.ledPointsTV);
        TextView lampsPointsTV = findViewById(R.id.lampsPointsTV);
        TextView tradePointsTV = findViewById(R.id.tradePointsTV);
        TextView pointsEarningsLine1TV = findViewById(R.id.pointsEarningsLine1TV);
        TextView pointsEarningsLine2TV = findViewById(R.id.pointsEarningsLine2TV);
        ledPointsTV.setTypeface(centrale_sans_medium);
        lampsPointsTV.setTypeface(centrale_sans_medium);
        tradePointsTV.setTypeface(centrale_sans_medium);
        pointsEarningsLine1TV.setTypeface(centrale_sans_medium);
        pointsEarningsLine2TV.setTypeface(centrale_sans_medium);
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
