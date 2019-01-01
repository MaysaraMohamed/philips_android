package com.philipslight.egypt;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // variables definition
    private Button loginButton;
    private Button registratoinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // first check login
        User user = SaveSharedPreference.getUser(this);
        changeLanguage(user.getLanguage());
        if(!user.getUserName().equals("") && user.isKeepLogin()){
            startActivity(MainActivity.this, Profile.class);
            MainActivity.this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.loginButton);
        registratoinButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentToLogin);
            }
        });

        registratoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToRegistration = new Intent(MainActivity.this, Registration.class);
                startActivity(intentToRegistration);
            }
        });
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
