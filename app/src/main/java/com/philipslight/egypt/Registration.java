package com.philipslight.egypt;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Registration extends AppCompatActivity {

    // define UI references.
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText addressEditText;
    private EditText mailEditText;
    private EditText confirmPasswordEditText;
    private EditText userNameEditText;
    private EditText phoneEditText;
    private Button registerButton;
    private View focusView = null;
    private Spinner userTypeSP;
    private ArrayList<String> userTypes = new ArrayList<String>();

    // define user data.
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeLanguage(SaveSharedPreference.getUser(this).getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // intialize UI reference
        registerButton = findViewById(R.id.registerButton);
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText1);
        addressEditText = findViewById(R.id.addressEditText);
        mailEditText = findViewById(R.id.mailEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText2);
        userNameEditText = findViewById(R.id.customerIdEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        userTypeSP = findViewById(R.id.subCategorySP);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeVariables();
                validateData(user);
                confirmPassword(user);
                sendRegisterRequest(user);
            }
        });


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Registration.this,
                android.R.layout.simple_list_item_1, getUserTypes());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSP.setAdapter(dataAdapter);
    }


    private boolean confirmPassword(User user) {
        if (! user.getPassword().equals(user.getConfirmPassword())) {
            confirmPasswordEditText.setError(getString(R.string.error_password_not_match));
            focusView = confirmPasswordEditText;
            return false;
        } else {
            return true;
        }
    }


    private boolean validateData(User user) {
        if (TextUtils.isEmpty(user.getName())) {
            nameEditText.setError(getString(R.string.error_field_required));
            focusView = nameEditText;
            return false;
        } else if (TextUtils.isEmpty(user.getPhone())) {
            phoneEditText.setError(getString(R.string.error_field_required));
            focusView = phoneEditText;
            return false;
        }
//      else if (TextUtils.isEmpty(user.getMail())) {
//            mailEditText.setError(getString(R.string.error_field_required));
//            focusView = mailEditText;
//            return false;
//        }
        else if (TextUtils.isEmpty(user.getAddress())){
            addressEditText.setError(getString(R.string.error_field_required));
            focusView = addressEditText;
            return false;
        } else if (TextUtils.isEmpty(user.getUserName())) {
            userNameEditText.setError(getString(R.string.error_field_required));
            focusView = userNameEditText;
            return false;
        } else if (TextUtils.isEmpty(user.getPassword())) {
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
            return false;
        } else if (TextUtils.isEmpty(user.getConfirmPassword())) {
            confirmPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = confirmPasswordEditText;
            return false;
        }
        return true;
    }

    private void initializeVariables()
    {
        user = new User();
        user.setUserName(userNameEditText.getText().toString().toUpperCase());
        user.setPhone(phoneEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());
        user.setConfirmPassword(confirmPasswordEditText.getText().toString());
        user.setAddress(addressEditText.getText().toString());
        user.setName(nameEditText.getText().toString());
        user.setMail(mailEditText.getText().toString());
    }

    private boolean sendRegisterRequest(User user) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(Registration.this);

        JSONObject js = new JSONObject();
        try {
            js.put("name",user.getName());
            js.put("userName",user.getUserName());
            js.put("password",user.getPassword());
            js.put("title",user.getTitle());
            js.put("mail",user.getMail());
            js.put("phone",user.getPhone());
            js.put("address",user.getAddress());
            js.put("phone",user.getPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                Configurations.getRegisterURL(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // replace new response check instead of status
                        User user = null;
                        String userName="";
                        try {
                            userName = response.getString("userName");
                            user = new User(response.getString("name"), userName, response.getString("profileImage"),  response.getString("phone"),  response.getString("mail"), response.getString("userType"));
                        } catch (JSONException e) {
                            Log.e("Response is : ", e.toString());
                        }
                        if(!userName.equals("")){
                            Log.d("Response is : ", response.toString());
                            SaveSharedPreference.setUser(Registration.this, user);
                            Registration.this.finish();
                            startActivity(Registration.this, Profile.class);
                        }else{
                            Log.e("Response is : ", response.toString());
                            Toast.makeText(Registration.this, getString(R.string.userNotFound), Toast.LENGTH_LONG).show();
                            userNameEditText.setError(getString(R.string.userNotFound));
                            focusView = userNameEditText;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
//                        if(error != null && error.getMessage().contains("JSONException")) {
//                            Toast.makeText(Registration.this, getString(R.string.wrongData), Toast.LENGTH_LONG).show();
//                        }else {
                            Toast.makeText(Registration.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
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

    private void startActivity(Context from, Class<?> to){
        Intent intent = new Intent(from, to);
        startActivity(intent);
    }

    private ArrayList<String> getUserTypes()
    {
        userTypes.add(getString(R.string.type1));
        userTypes.add(getString(R.string.type2));
        return userTypes;
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
