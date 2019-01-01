//package com.philips.philips;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.MenuItem;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.gson.JsonArray;
//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.GridLabelRenderer;
//import com.jjoe64.graphview.LegendRenderer;
//import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;
//import com.philips.common.Configurations;
//import com.philips.common.PointsRecord;
//import com.philips.common.SaveSharedPreference;
//import com.philips.common.User;
//import com.philips.common.Utilities;
//import com.squareup.picasso.Picasso;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//
//public class Home extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener {
//
//    private User user;
//    private BroadcastReceiver broadcastReceiver;
//    private GraphView graphView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        // On logout button clicked
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.philips.philips.ACTION_LOGOUT");
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.d("onReceive","Logout in progress");
//                startActivity(Home.this, LoginActivity.class);
//                finish();
//            }
//        };
//        registerReceiver(broadcastReceiver, intentFilter);
//
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        // Draw dashboard
//        graphView = (GraphView) findViewById(R.id.graph);
//        graphView.setTitle("Points History");
//
////        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
////        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graphView.getContext(), simpleFormat));
////        graphView.getGridLabelRenderer().setNumHorizontalLabels(4);
////
////        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Date");
////        graphView.getGridLabelRenderer().setVerticalAxisTitle("Points");
//
//        user = SaveSharedPreference.getUser(Home.this);
//        setHeaderData();
//        sendGetUserPoints();
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//        if (id == R.id.nav_newBill) {
//            Home.this.finish();
//            startActivity(Home.this, NewInvoice.class);
//        }else if (id == R.id.nav_myProfile) {
//            Home.this.finish();
//            startActivity(Home.this, Profile.class);
//        } else if (id == R.id.nav_compensate) {
//            Home.this.finish();
//            startActivity(Home.this, Compensate.class);
//        } else if (id == R.id.nav_about) {
//            Home.this.finish();
//            startActivity(Home.this, AboutUs.class);
//        } else if (id == R.id.nav_contactUs) {
//            Home.this.finish();
//            startActivity(Home.this, ContactUs.class);
//        } else if (id == R.id.nav_logout) {
//            SaveSharedPreference.clearData(Home.this);
//            Home.this.finish();
////            startActivity(Home.this, LoginActivity.class);
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction("com.philips.philips.ACTION_LOGOUT");
//            sendBroadcast(broadcastIntent);
//        } else if (id == R.id.nav_rate) {
//            Toast.makeText(Home.this, "To handle rate us function", Toast.LENGTH_LONG).show();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
////        Home.this.finish();
//        return true;
//    }
//
//    private LineGraphSeries<DataPoint> prepareDashboardList(List<DataPoint>  points)
//    {
//        DataPoint list [] = points.toArray(new DataPoint[points.size()]);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(list);
//        return series;
//    }
//
//    private void startActivity(Context from, Class<?> to){
//        Intent intent = new Intent(from, to);
//        startActivity(intent);
//    }
//
//    private void setHeaderData(){
//        // To get updated shared preference.
//        User user = SaveSharedPreference.getUser(Home.this);
//        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
//        View headerView = navigationView1.getHeaderView(0);
//        TextView navUsername = (TextView) headerView.findViewById(R.id.customerIDTextView);
//        navUsername.setText(user.getName());
//
//        if(! user.getProfileImage().equals("")) {
//            Log.d("Header", "SetHeader Method called from Home and image = "+user.getProfileImage());
//            CircleImageView  profileImage = (CircleImageView) headerView.findViewById(R.id.profileImageViewNavHome);
//        Picasso.with(Home.this).load(user.getProfileImage()).placeholder(R.drawable.mohamed)
//                .error(R.drawable.mohamed)
//                .into(profileImage, new com.squareup.picasso.Callback(){
//                    @Override
//                    public void onSuccess() {
//                        Log.d("Success", "Success to Load image");
//                    }
//                    @Override
//                    public void onError() {
//                        Log.d("Failed", "Failed to Load image");
//                    }
//                });
//        }
//    }
//
//    private boolean sendGetUserPoints() {
//        // Instantiate the RequestQueue.
//        List<String> list = new ArrayList<String>();
//        RequestQueue queue = Volley.newRequestQueue(Home.this);
//        // Request a string response from the provided URL.
//        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                Configurations.getUserPoints()+"/"+user.getUserName(),
//                null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                            List<DataPoint> pointList = new ArrayList<>();
//                            for (int i=0; i<response.length();  i++)
//                            {
//                                String point = response.getJSONObject(i).get("points").toString();
//                                String stringDate = response.getJSONObject(i).get("pointsDate").toString();
//                                Log.d("PointsHistory", "Points is "+ point +", Date is : "+ Utilities.stringToDate(stringDate,"-"));
//                                pointList.add(new DataPoint(Utilities.stringToDate(stringDate,"-"), Double.parseDouble(point)));
////                                pointList.add(new DataPoint(i+1, Double.parseDouble(point)));
//                            }
//                            LineGraphSeries<DataPoint> series = prepareDashboardList(pointList);
//                            series.setDrawDataPoints(true);
//                            series.setAnimated(true);
//                            graphView.addSeries(series);
//                        } catch (Exception e) {
//                            Log.e("ERROR : ", e.toString());
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Response is : ", error.toString());
//                        Toast.makeText(Home.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                return headers;
//            }
//        };
//        queue.add(jsonObjectRequest);
//        return true;
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            unregisterReceiver(broadcastReceiver);
//        }catch (Exception ex){
//            Log.e("ERROR", ex.getLocalizedMessage());
//        }
//    }
//
//}
