package com.philipslight.egypt;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.philipslight.common.Configurations;
import com.philipslight.common.SaveSharedPreference;
import com.philipslight.common.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button submitButton;
    private TextView restPasswordButton;
    private View focusView = null;

    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeLanguage(SaveSharedPreference.getUser(this).getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        userNameEditText = (EditText) findViewById(R.id.recievedOrOldPasswordEditText);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        submitButton = findViewById(R.id.submitButton);
        restPasswordButton = findViewById(R.id.forgetPasswrodButton);

        restPasswordButton.setPaintFlags(restPasswordButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameEditText.getText().toString().replace(" ","");
                password = passwordEditText.getText().toString();
                if (validateData(userName, password)) {
                    sendLoginRequest(userName, password);
                }
            }
        });
        restPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LoginActivity.this, SendPassword.class);
            }
        });
    }


    private boolean validateData(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {
            userNameEditText.setError(getString(R.string.error_field_required));
            focusView = userNameEditText;
            return false;
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
            return false;
        }
        return true;
    }

    private boolean sendLoginRequest(final String userName, final String password) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        JSONObject js = new JSONObject();
        try {
            js.put("userName", userName);
            js.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Configurations.getLoginURL(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // replace new response check instead of status
                        User user = null;
                        String userName = "";
                        try {
                                userName = response.getString("userName");
                                user = new User(response.getString("name"), userName, response.getString("profileImage"), response.getString("phone"), response.getString("mail"),response.getString("userType"));
                        } catch (Exception e) {
                            Log.e("Response is : ", e.toString());
                        }
                        if (!userName.equals("")) {
                            Log.d("Response is : ", response.toString());
                            user.setPassword(password);
                            SaveSharedPreference.setUser(LoginActivity.this, user);
                            LoginActivity.this.finish();
                            startActivity(LoginActivity.this, Profile.class);
                        } else {
                            Log.e("Response is : ", response.toString());
                            Toast.makeText(LoginActivity.this, getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
                        try {
                            if (error.getMessage().contains("JSONException")) {
                                Toast.makeText(LoginActivity.this, getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e)
                        {
                            Toast.makeText(LoginActivity.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
                        }
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

    private void startActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
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

