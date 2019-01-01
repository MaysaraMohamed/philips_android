package com.philipslight.egypt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.philipslight.common.CategoriesWithSub;
import com.philipslight.common.Configurations;
import com.philipslight.common.SaveSharedPreference;
import com.philipslight.common.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Compensate extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String defaultLang;
    Spinner giftsSP;
    Spinner pointsSP;
    TextView totalPointsTV;
    double totalPoints;
    Button submitButton;
    String userName;
    private User user;
    List<String> pointsList ;
    List<String> giftsList ;
    List<String> newgiftsList;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compensate);

        // On logout button clicked
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.philips.philips.ACTION_LOGOUT");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                startActivity(Compensate.this, LoginActivity.class);
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);



        Toolbar toolbar = (Toolbar) findViewById(R.id.compensate_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_compensate_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.compensate_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        giftsSP = findViewById(R.id.giftsSP);
        pointsSP = findViewById(R.id.pointsSP);
        totalPointsTV = findViewById(R.id.totalPointstextView);
        submitButton = findViewById(R.id.pointsToRdeemButton);

//        defaultLang= Locale.getDefault().getLanguage();
        defaultLang = SaveSharedPreference.getUser(Compensate.this).getLanguage();

        user = SaveSharedPreference.getUser(Compensate.this);
        userName = user.getUserName();
        sendGetGiftsData(userName);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Compensate.this, "Voucher sent. Please check your mail", Toast.LENGTH_LONG).show();
                sendGetGiftsData(userName);
                sendUpdatePointsData(userName, Double.parseDouble(pointsSP.getSelectedItem().toString()));
            }
        });

        pointsSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newgiftsList = new ArrayList<>();

                newgiftsList.add(giftsList.get(position));
//                String selectedPoints = (int)(Double.parseDouble(pointsSP.getSelectedItem().toString()))+"";
//                for (String gift:giftsList) {
//                    if(gift.contains(selectedPoints)){
//                        newgiftsList.add(gift);
//                    }
//                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Compensate.this,
                        android.R.layout.simple_list_item_1, newgiftsList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                giftsSP.setAdapter(dataAdapter);
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {
                // TODO on nothing selected
            }
        });

        setHeaderData();
        setTitle(getText(R.string.compensate));
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_compensate_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            // Handle navigation view item clicks here.
            int id = item.getItemId();
            if (id == R.id.nav_newBill) {
                startActivity(Compensate.this, NewInvoice.class);
                Compensate.this.finish();
//            } else if (id == R.id.nav_compensate) {
//                startActivity(Compensate.this, Compensate.class);
//                Compensate.this.finish();
            }else if (id == R.id.nav_myProfile) {
                startActivity(Compensate.this, Profile.class);
                Compensate.this.finish();
//            } else if (id == R.id.nav_dashboard) {
//                Compensate.this.finish();
//                startActivity(Compensate.this, Home.class);
            }else if (id == R.id.nav_setting) {
                Compensate.this.finish();
                startActivity(Compensate.this, Setting.class);
            }else if (id == R.id.nav_about) {
                startActivity(Compensate.this, AboutUs.class);
                Compensate.this.finish();
            } else if (id == R.id.nav_contactUs) {
                startActivity(Compensate.this, ContactUs.class);
                Compensate.this.finish();
            } else if (id == R.id.nav_logout) {
                SaveSharedPreference.clearData(Compensate.this);
//                Compensate.this.finish();
//                startActivity(Compensate.this, LoginActivity.class);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.philips.philips.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);
            } else if (id == R.id.nav_rate) {
                Toast.makeText(Compensate.this, "To handle rate us function", Toast.LENGTH_LONG).show();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_compensate_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
    }
    private void startActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
    }


    private boolean sendGetGiftsData(String userName) {
        // TODO update request to handle new updates.
        // Instantiate the RequestQueue.
        List<String> list = new ArrayList<String>();
        RequestQueue queue = Volley.newRequestQueue(Compensate.this);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Configurations.getGiftsURL()+"/"+userName,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String giftName = "";
                            pointsList = new ArrayList<String>();
                            giftsList = new ArrayList<String>();
                            JSONArray jsonArray = response.getJSONArray("gifts");
                            totalPoints = response.getDouble("totalPoints");

                            totalPointsTV.setText(getString(R.string.currentPoints) +" "+ totalPoints);
                            for (int i=0; i < jsonArray.length(); i++) {
                                if(defaultLang.toUpperCase().equals("AR")) {
                                    giftName = jsonArray.getJSONObject(i).getString("arGiftOptions");
                                    giftsList.add(giftName);
                                } else{
                                    giftName = jsonArray.getJSONObject(i).getString("giftOptions");
                                    giftsList.add(giftName);
                                }
                                pointsList.add(jsonArray.getJSONObject(i).getString("points"));
                            }
                            Log.d("Gifts list is : ", giftsList.toString());
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Compensate.this,
                                    android.R.layout.simple_list_item_1, giftsList);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            giftsSP.setAdapter(dataAdapter);

                            Log.d("Gifts list is : ", giftsList.toString());

                            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(Compensate.this,
                                    android.R.layout.simple_list_item_1, pointsList);
                            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            pointsSP.setAdapter(dataAdapter2);
                        } catch (Exception e) {
                            Log.e("Response is : ", e.toString());
                        }

                        if (totalPoints <= 0) {
                            submitButton.setEnabled(false);
                        } else {
                            submitButton.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
//                        Toast.makeText(Compensate.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
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



    private boolean sendUpdatePointsData(String userName,double usedPoints) {
        // TODO update request to handle new updates.
        // Instantiate the RequestQueue.

        JSONObject js = new JSONObject();
        try {
            js.put("userName", userName);
            js.put("totalPoints", usedPoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(Compensate.this);
        // Request a string response from the provided URL.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Configurations.getUpdatePoints(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String status="";
                        try {
                            status= response.getString("status");
                            Log.i("Response is : ", response.toString());
                        } catch (Exception e) {
                            Log.e("Response is : ", e.toString());
                        }
                        sendGetGiftsData(SaveSharedPreference.getUser(Compensate.this).getUserName());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
//                        Toast.makeText(Compensate.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
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
        User user = SaveSharedPreference.getUser(Compensate.this);
        NavigationView navigationView1 = (NavigationView) findViewById(R.id.compensate_nav_view);
        View headerView = navigationView1.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.customerIDTextView);
        navUsername.setText(user.getName());

        if(! user.getProfileImage().equals("")) {
            Log.d("Header", "SetHeader Method called from Home and image = "+user.getProfileImage());
            CircleImageView profileImage = (CircleImageView) headerView.findViewById(R.id.profileImageViewNavHome);
            Picasso.with(Compensate.this).load(user.getProfileImage()).placeholder(R.drawable.mohamed)
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

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (Exception ex){
            Log.e("ERROR", ex.getLocalizedMessage());
        }
    }
}
