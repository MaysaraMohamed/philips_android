package com.philipslight.egypt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.philipslight.common.Configurations;
import com.philipslight.common.SaveSharedPreference;
import com.philipslight.common.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Challenge;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Route;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    final static int RESULT_LOAD_IMAGE = 1;
    private TextView nameTV;
    private TextView userNameTV;
    private TextView totalNetPointsTV;
    private TextView totalPointsTillDateTV;
    private TextView totalRedeemedPointsTV;
    private TextView bonusPointsTv;
    private TextView totalNetPointsTVTitle;
    private TextView totalPointsTillDateTVTitle;
    private TextView totalRedeemedPointsTVTitle;
    private TextView bonusPointsTvTitle;

    private CircleImageView profileImageView;
    private Button editProfile;
    private Button helloButton;
    private Button addNewInvoiceButton;
    private Bitmap bitmap;
    private Toolbar toolbar;
    NavigationView navigationView;
    private Button invoicesHistoryBT;
    TableLayout invoicesHistoryTL;
    TableRow invoiceHistoryTableHeader;


    static int numberOfInstances = 0;

    private User user;
    private BroadcastReceiver broadcastReceiver;
    String oldLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        oldLanguage = SaveSharedPreference.getUser(this).getLanguage();
        user = SaveSharedPreference.getUser(Profile.this);
        changeLanguage(user.getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // On logout button clicked
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.philips.philips.ACTION_LOGOUT");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                startActivity(Profile.this, LoginActivity.class);
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        totalNetPointsTV = findViewById(R.id.netTotalPointsTv);
        totalPointsTillDateTV = findViewById(R.id.pointsTillDateTv);
        totalRedeemedPointsTV = findViewById(R.id.redeemedPointsTv);
        bonusPointsTv = findViewById(R.id.bonusPointsTv);
        invoicesHistoryBT = findViewById(R.id.invoicesHistoryButton);
        invoicesHistoryTL = findViewById(R.id.invoicesHistoryLayout);

        totalNetPointsTVTitle = findViewById(R.id.netTotalPointsTvTitle);
        totalPointsTillDateTVTitle = findViewById(R.id.pointsTillDateTvTitle);
        totalRedeemedPointsTVTitle = findViewById(R.id.redeemedPointsTvTitle);
        bonusPointsTvTitle = findViewById(R.id.bonusPointsTvTitle);
        addNewInvoiceButton = findViewById(R.id.addNewInvoiceButton);
        invoiceHistoryTableHeader = findViewById(R.id.invoiceHistoryHeader);

        nameTV = findViewById(R.id.nametextView7);
        userNameTV = findViewById(R.id.userNametextView8);
        profileImageView = findViewById(R.id.profileImageView);
        editProfile = findViewById(R.id.editProfileBT2);
        helloButton = findViewById(R.id.helloEditProfileBT1);

        toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.profile_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_profile);
        navigationView.setNavigationItemSelectedListener(this);

        profileImageView.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        addNewInvoiceButton.setOnClickListener(this);
        invoicesHistoryBT.setOnClickListener(this);

        // refresh data.
        setNameAndUserName();
        sendGetProfileData();
        getImageFromUrl(user.getProfileImage());
        setHeaderData();
        setTitle(getText(R.string.myProfile));


        updateFonts();
        numberOfInstances ++;
        Log.d("Profile", "Number of instances is "+numberOfInstances);
    }

    private void setNameAndUserName(){
        nameTV.setText(user.getName());
        userNameTV.setText(user.getUserName());
    }


    private void getImageFromUrl(String url){
        Log.d("Image", "Trying to Load image from "+ url);
        Picasso.with(Profile.this).load(url).placeholder(R.drawable.mohamed)
                .error(R.drawable.mohamed)
                .into(profileImageView, new com.squareup.picasso.Callback(){
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

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.profile_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            // To Exist on double click
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getText(R.string.doubleClickToExit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
          }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // refresh data.
//        changeLanguage(user.getLanguage());
//        setNameAndUserName();
//        sendGetProfileData();
//        getImageFromUrl(user.getProfileImage());
//        setHeaderData();
//        setTitle(getText(R.string.myProfile));
//    }

    @Override
    public void onResume(){
        super.onResume();
        if (!oldLanguage.equals(SaveSharedPreference.getUser(Profile.this).getLanguage())){
            startActivity(getIntent());
            finish();
        }

    }

//        @Override
//    protected void onResume() {
//        super.onResume();
//        // refresh data.
//        changeLanguage(user.getLanguage());
//        setHeaderData();
//        setTitle(getText(R.string.myProfile));
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_newBill) {
            startActivity(Profile.this, NewInvoice.class);
            if(numberOfInstances > 1) {
                Profile.this.finish();
            }
        } else if (id == R.id.nav_setting) {
            if(numberOfInstances > 1) {
                Profile.this.finish();
            }
            startActivity(Profile.this, Setting.class);
        }else if (id == R.id.nav_myProfile) {
//            Profile.this.finish();
//            startActivity(Profile.this, Profile.class);
        } else if (id == R.id.nav_compensate) {
            startActivity(Profile.this, Compensate.class);
            if(numberOfInstances > 1) {
                Profile.this.finish();
            }
        } else if (id == R.id.nav_about) {
            startActivity(Profile.this, AboutUs.class);
            if(numberOfInstances > 1) {
                Profile.this.finish();
            }
        } else if (id == R.id.nav_contactUs) {
            startActivity(Profile.this, ContactUs.class);
            if(numberOfInstances > 1) {
                Profile.this.finish();
            }
        } else if (id == R.id.nav_logout) {
            SaveSharedPreference.clearData(Profile.this);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.philips.philips.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);
        } else if (id == R.id.nav_rate) {
            Toast.makeText(Profile.this, "To handle rate us function", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.profile_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startActivity(Context from, Class<?> to){
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


    private boolean sendGetProfileData() {
        // Instantiate the RequestQueue.
        List<String> list = new ArrayList<String>();
        RequestQueue queue = Volley.newRequestQueue(Profile.this);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Configurations.getUserProfile()+"/"+user.getUserName(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<String> list = new ArrayList<String>();
                            double totalPointsTileDate = response.getDouble("totalPointsTileDate");
                            double totalRedeemedPoints = response.getDouble("totalRedeemedPoints");
                            double totalNetPoints = response.getDouble("totalPoints");
                            double totalPendingPoints = response.getDouble("totalPendingPoints");
                            totalPointsTillDateTV.setText(totalPointsTileDate+"");
                            totalRedeemedPointsTV.setText(totalRedeemedPoints+"");
                            totalNetPointsTV.setText(totalNetPoints+"");
                            bonusPointsTv.setText(totalPendingPoints+"");
                            Log.i("Response is : ", response.toString());
                        } catch (Exception e) {
                            Log.e("Response is : ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
//                        if(error != null) {
//                            if (!error.getMessage().contains("JSONException")) {
//                                Toast.makeText(Profile.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
//                            }
//                        }
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



    private boolean sendGetSubmitedInvoices() {
        // Instantiate the RequestQueue.
        List<String> list = new ArrayList<String>();
        RequestQueue queue = Volley.newRequestQueue(Profile.this);
        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Configurations.getSubmitedInvoices()+"/"+user.getUserName(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Clean table every time before calling data.
                            invoicesHistoryTL.removeAllViews();
                            invoicesHistoryTL.addView(invoiceHistoryTableHeader);
                            for (int n=0 ; n<response.length(); n++) {
                                String submissionDate = response.getJSONObject(n).get("submissionDate").toString().split("T")[0];
                                submissionDate = submissionDate.split("-")[1]+"/"+submissionDate.split("-")[2];
                                String status = response.getJSONObject(n).getString("status");
                                String salesId = response.getJSONObject(n).getString("salesId");
                                double invoicePoints = response.getJSONObject(n).getDouble("invoicePoints");
                                double netSale = response.getJSONObject(n).getDouble("totalNetSale");
                                loadInvoicesHistory(n, salesId, submissionDate, invoicePoints, netSale, mapStatus(status));
                            }
                            Log.i("Response is : ", response.toString());
                        } catch (Exception e) {
                            Log.e("Response is : ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
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
        queue.add(jsonArrayRequest);
        return true;
    }


    @Override
    public void onClick(View v) {
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        switch (v.getId()){
            case R.id.profileImageView:
                startActivityForResult(gallaryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.editProfileBT2:
                startActivityForResult(gallaryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.addNewInvoiceButton:
                startActivity(Profile.this, NewInvoice.class);
                break;
            case R.id.invoicesHistoryButton:
                        if(invoicesHistoryTL.getVisibility() == View.GONE ) {
                            invoicesHistoryTL.setVisibility(View.VISIBLE);
                            //TODO call the below method to add loaded invoices from backend.
                            sendGetSubmitedInvoices();
//                            loadInvoicesHistory(1,"SO-341234","10/11/2018", 249492,"Validiating");
                        }
                        else {
                            invoicesHistoryTL.setVisibility(View.GONE);
                        }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null ){
            Uri selectedImage = data.getData();
//            profileImageView.setImageURI(selectedImage);
//            profileImageView.setImageBitmap(getBitMapImage(getRealPathFromURI(selectedImage)));
            user.setProfileImage(getRealPathFromURI(selectedImage));
            Log.d("Image profile path is :", getRealPathFromURI(selectedImage));
            SaveSharedPreference.setUser(Profile.this, user);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = null;
            try {
                file = bitMapToFile(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("LOG", "imageFile is : "+ file);

            Log.d("LOG", "Starting Async Task ");
            new MyAsyncTask(file).execute();
        }
    }


    private void setHeaderData(){
        // To get updated shared preference.
        User user = SaveSharedPreference.getUser(Profile.this);
        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view_profile);
        View headerView = navigationView1.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.customerIDTextView);
        navUsername.setText(user.getName());

        if(! user.getProfileImage().equals("")) {
            Log.d("Header", "SetHeader Method called from Home and image = "+user.getProfileImage());
            CircleImageView  profileImage = (CircleImageView) headerView.findViewById(R.id.profileImageViewNavHome);
            Picasso.with(Profile.this).load(user.getProfileImage()).placeholder(R.drawable.mohamed)
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
            try {
                Thread.sleep(500            );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public File bitMapToFile(Bitmap mybitmap) throws IOException
    {
        //create a file to write bitmap data
                File f = new File(Profile.this.getCacheDir(), user.getUserName()+".png");
                f.createNewFile();

        //Convert bitmap to byte array
                Bitmap bitmap = mybitmap;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                return f;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void uploadFileProfileImage(File file) {
        Log.d("Upload Image", "Uploading image method called");
        OkHttpClient client = new OkHttpClient();



        String url = Configurations.getUserProfileImage();
        Log.d("Upload Image", "Preparing request body");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName()
                , RequestBody.create(MediaType.parse("image/jpeg"),file))
                .build();
        Log.d("Upload Image", "Preparing request");

        //new for Authentication
        String credentials = Configurations.getUserName()+":"+Configurations.getPassword();
        String auth = "Basic "
                + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("Authorization", auth)
                .url(url)
                .post(requestBody)
                .build();

        try {
            Log.d("Upload Image", "Executing request");
            okhttp3.Response response = client.newCall(request).execute();
            Log.d("Upload Image", "Uploaded successfully");
            String jsonData = response.body().string();
            JSONObject Jobject = null;
            try {
                // Update profile image in shared preference once uploaded.
                Jobject = new JSONObject(jsonData);
                String userProfile = Jobject.getString("fileDownloadUri");
                Log.d("Upload Image", userProfile);
                user.setProfileImage(userProfile);
                SaveSharedPreference.setUser(Profile.this, user);
//                getImageFromUrl(user.getProfileImage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        byte [] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        File file;
        public MyAsyncTask(File file) {
            super();
            this.file = file;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            uploadFileProfileImage(file);
            return null;
        }
    }

    private void updateFonts()
    {
        Typeface centrale_sans_bold = Typeface.createFromAsset(getAssets(), "fonts/centrale_sans_bold.otf");
        Typeface centrale_sans_medium = Typeface.createFromAsset(getAssets(), "fonts/centrale_sans_medium.otf");

        editProfile.setTypeface(centrale_sans_bold);
        helloButton.setTypeface(centrale_sans_bold);
        nameTV.setTypeface(centrale_sans_bold);
        userNameTV.setTypeface(centrale_sans_bold);

//        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
//        toolbar_title.setTypeface(centrale_sans_bold);

        totalNetPointsTV.setTypeface(centrale_sans_medium);
        totalPointsTillDateTV.setTypeface(centrale_sans_medium);
        totalRedeemedPointsTV.setTypeface(centrale_sans_medium);
        bonusPointsTv.setTypeface(centrale_sans_medium);
        totalNetPointsTVTitle.setTypeface(centrale_sans_medium);
        totalPointsTillDateTVTitle.setTypeface(centrale_sans_medium);
        totalRedeemedPointsTVTitle.setTypeface(centrale_sans_medium);
        bonusPointsTvTitle.setTypeface(centrale_sans_medium);
    }

    private void changeLanguage(String lang) {
        String languageToLoad  = lang; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }



    public void loadInvoicesHistory(int rowNumber, String salesID, String invoiceDate, double invoicePoints, double netSale, String status) {
        int textSize = 0, smallTextSize = 0, mediumTextSize = 0;
        textSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.font_size_medium);

        final TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        tv.setGravity(Gravity.LEFT);
        tv.setPadding(5, 15, 0, 15);
        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
        tv.setText(salesID);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);


        final TextView tv2 = new TextView(this);

        tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT));
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

        tv2.setGravity(Gravity.LEFT);
        tv2.setPadding(5, 15, 0, 15);
        tv2.setBackgroundColor(Color.parseColor("#ffffff"));
        tv2.setTextColor(Color.parseColor("#000000"));
        tv2.setText(invoiceDate);


        final TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

        tv3.setGravity(Gravity.LEFT);
        tv3.setPadding(5, 15, 0, 15);
        tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
        tv3.setTextColor(Color.parseColor("#000000"));
        tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        tv3.setText(invoicePoints+"");


        final TextView tv4 = new TextView(this);
        tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

        tv4.setGravity(Gravity.LEFT);
        tv4.setPadding(5, 15, 0, 15);
        tv4.setBackgroundColor(Color.parseColor("#f8f8f8"));
        tv4.setTextColor(Color.parseColor("#000000"));
        tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        tv4.setText(netSale+"");


        final TextView tv5 = new TextView(this);
        tv5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        tv5.setGravity(Gravity.LEFT);
        tv5.setPadding(5, 15, 0, 15);
        tv5.setBackgroundColor(Color.parseColor("#ffffff"));
        tv5.setTextColor(Color.parseColor("#000000"));
        tv5.setText(status);
        tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

        final TableRow tr = new TableRow(this);
        tr.setId(rowNumber);

        // add table row
        tr.addView(tv);
        tr.addView(tv2);
        tr.addView(tv3);
        tr.addView(tv4);
        tr.addView(tv5);

        invoicesHistoryTL.addView(tr);
    }

    private String mapStatus(String status) {
        if (status.equals("404")) {
            return getText(R.string.invalidSalesId).toString();
        } else if (status.equals("411")) {
            return getText(R.string.wrongData).toString();
        } else if (status.equals("410")) {
            return getText(R.string.wrongUserName).toString();
        } else if (status.equals("412")) {
            return getText(R.string.wrongData).toString();
        } else if (status.equals("200")) {
            return getText(R.string.validatedSuccessfully).toString();
        }else{
            return getText(R.string.underValidation).toString();
        }
    }

//    private String mapStatus(String status) {
//        if (status.equals("200")) {
//            return getText(R.string.validatedSuccessfully).toString();
//        }else if(status.equals("SCHEDULED")) {
//            return getText(R.string.underValidation).toString();
//        }else{
//            return getText(R.string.error).toString();
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        numberOfInstances --;
        Log.d("Profile", "Number of instances is "+numberOfInstances);
    }
}
