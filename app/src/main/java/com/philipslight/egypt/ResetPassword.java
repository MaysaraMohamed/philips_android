package com.philipslight.egypt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ResetPassword extends AppCompatActivity{

    private Button submitPassword;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;
    private User user;
    private View focusView;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeLanguage(SaveSharedPreference.getUser(this).getLanguage());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        submitPassword = findViewById(R.id.resetPasswordButton21);
        oldPassword = findViewById(R.id.recievedOrOldPasswordEditText);
        newPassword = findViewById(R.id.newPasswordEditText2);
        confirmNewPassword = findViewById(R.id.confirmNewPasswordeditText3);
        user = SaveSharedPreference.getUser(ResetPassword.this);

        // On logout button clicked
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.philips.philips.ACTION_LOGOUT");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                startActivity(ResetPassword.this, LoginActivity.class);
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        submitPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setPassword(oldPassword.getText().toString());
                user.setConfirmPassword(confirmNewPassword.getText().toString());
                user.setNewPassword(newPassword.getText().toString());
                if(confirmPassword(user))
                {
                    sendChangePassword(user);
                }
            }
        });
        setTitle(getText(R.string.restPassword));
    }


    private boolean confirmPassword(User user) {
        if (! user.getNewPassword().equals(user.getConfirmPassword())) {
            confirmNewPassword.setError(getString(R.string.error_password_not_match));
            focusView = confirmNewPassword;
            return false;
        } else {
            if (oldPassword.getText().toString().equals(user.getNewPassword())) {
                oldPassword.setError(getString(R.string.error_password_should_be_different));
                focusView = oldPassword;
                return false;
            }
            return true;
        }
    }

    private void sendChangePassword(User user)
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(ResetPassword.this);

        JSONObject js = new JSONObject();
        try {
            js.put("userName",user.getUserName());
            js.put("password",user.getPassword());
            js.put("newPassword",user.getNewPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                Configurations.getChangePassword(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // replace new response check instead of status
                        User user = null;
                        int status=0;
                        try {
                            status = response.getInt("status");
                        } catch (JSONException e) {
                            Log.e("Response 1 is : ", e.toString());
                        }
                        if(status == 200 ){
                            Log.d("Response 2 is : ", response.toString());
                            SaveSharedPreference.clearData(ResetPassword.this);
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.philips.philips.ACTION_LOGOUT");
                            sendBroadcast(broadcastIntent);
                        }else{
                            Log.e("Response is : ", response.toString());
                            Toast.makeText(ResetPassword.this, getString(R.string.oldPasswordNotMatch), Toast.LENGTH_LONG).show();
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
        queue.add(jsonObjectRequest);
    }

        private void startActivity(Context from, Class<?> to){
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
