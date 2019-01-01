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

public class SendPassword extends AppCompatActivity {
    EditText mailOrUserET;
//    EditText userNameET;
    Button restPasswordButton;
    String mailOrUser;
    private View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        User user = SaveSharedPreference.getUser(this);
        changeLanguage(user.getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_password);
        restPasswordButton = findViewById(R.id.restPasswordButton2);
        mailOrUserET = findViewById(R.id.MailToResteditText);
//        userNameET = findViewById(R.id.userNameEditTextSendPassword);

        restPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData(mailOrUserET.getText().toString())){
                    SendPassword.this.finish();
//                    TODO send mail then open rest password activity
                    sendPassword(mailOrUserET.getText().toString());
                    startActivity(SendPassword.this, ResetPassword.class);
                }
            }
        });
        setTitle(getText(R.string.sendPassword));
    }

    private void startActivity(Context from, Class<?> to){
        Intent intent = new Intent(from, to);
        startActivity(intent);
    }


    private boolean sendPassword(String mailOrUser) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(SendPassword.this);
        JSONObject js = new JSONObject();
        try {
            //TODO to be handled
            js.put("mail",mailOrUserET);
            js.put("userName",mailOrUserET);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Configurations.getSendPasswordURL(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String status="";
                        try {
                            status = response.getString("status");
                        } catch (JSONException e) {
                            Log.e("Response is : ", e.toString());
                        }
                        if(status.equals("200")){
                            Log.d("Response is : ", response.toString());
                            Toast.makeText(SendPassword.this, getString(R.string.passwordSent), Toast.LENGTH_LONG).show();
                        }else{
                            Log.e("Response is : ", response.toString());
                            Toast.makeText(SendPassword.this, getString(R.string.failedToSendPassword), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
                        Toast.makeText(SendPassword.this, getString(R.string.failedToSendPassword), Toast.LENGTH_LONG).show();
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


    private boolean validateData(String mailOrUser)
    {
        if (TextUtils.isEmpty(mailOrUser)) {
            mailOrUserET.setError(getString(R.string.error_field_required));
            focusView = mailOrUserET;
            return false;
        }
        return true;
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
