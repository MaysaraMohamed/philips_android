package com.philipslight.common;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Utilities {
    public static JSONObject invoiceToJson(Invoice invoice) {
        Gson gson = new Gson();
        String json = gson.toJson(invoice);
        JSONObject js = null;
        try {
            js = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js;
    }

    public static Long stringToDate(String stringDate) {

        String [] date = stringDate.split("/");
        int year = Integer.parseInt(date[2]);
        int month = Integer.parseInt(date[1])-1;
        int day = Integer.parseInt(date[0]);
        GregorianCalendar cal = new GregorianCalendar(year, month, day);
        long millis = cal.getTimeInMillis();
        return millis;
    }

    public static Long stringToDate(String stringDate, String regex) {

        String [] date = stringDate.split(regex);
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1])-1;
        int day = Integer.parseInt(date[2]);
        GregorianCalendar cal = new GregorianCalendar(year, month, day);
        long millis = cal.getTimeInMillis();
        return millis;
    }

}
