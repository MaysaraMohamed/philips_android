package com.philipslight.egypt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.philipslight.common.HighLightArrayAdapter;
import com.philipslight.common.Invoice;
import com.philipslight.common.SaveSharedPreference;
import com.philipslight.common.SubCategory;
import com.philipslight.common.User;
import com.philipslight.common.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class NewInvoice extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Button invoiceDateButton;
    Button submitInvoiceButton;
    Button addInvoiceItemButton;
    EditText invoiceSalesNumberET;
    EditText invoiceItemNetSaleET;
    Spinner invoiceCategorySP;
    Spinner invoiceSubCategorySP;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    private TableLayout invoicesTableLayout;
    private TableLayout invoicesSummaryTableLayout;
    private View focusView = null;
    private String invoiceDate, invoiceSalesNumber, invoiceCategory, invoiceSubCategory;
    private double invoiceNetSale;
    private String defaultLang;
    TextView totalNetSaleTv;
    private int rowNumber=-1;

    private List<CategoriesWithSub> categoriesWithSubs = new ArrayList<CategoriesWithSub>();
    ArrayList<String> listItems = new ArrayList<String>();
    List<String> invoiceItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    private User user;
    BroadcastReceiver broadcastReceiver;
    HighLightArrayAdapter categoryArrayAdapter;
    HighLightArrayAdapter subCategoryArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice);

        // On logout button clicked
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.philips.philips.ACTION_LOGOUT");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                startActivity(NewInvoice.this, LoginActivity.class);
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);


        NavigationView navigationView = (NavigationView) findViewById(R.id.invoice_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.invoice_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_invoice_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        user = SaveSharedPreference.getUser(NewInvoice.this);
        totalNetSaleTv = findViewById(R.id.totalNetSaleTV);
        invoiceDateButton = findViewById(R.id.invoiceDateButton);
        submitInvoiceButton = findViewById(R.id.submitInvoiceButton);
        addInvoiceItemButton = findViewById(R.id.addInvoiceItemButton);
        invoiceSalesNumberET = findViewById(R.id.invoiceSalesNumberET);
        invoiceItemNetSaleET = findViewById(R.id.invoiceINetSaleET);
        invoiceCategorySP = findViewById(R.id.invoiceCategory);
        invoiceSubCategorySP = findViewById(R.id.subCategorySP);

        invoicesTableLayout = (TableLayout)findViewById(R.id.tableInvoices);
        invoicesTableLayout.setStretchAllColumns(true);

        invoicesSummaryTableLayout = (TableLayout) findViewById(R.id.tableinvoicesSummary);
        invoicesSummaryTableLayout.setStretchAllColumns(true);

        loadData(true, rowNumber++, "", "", "", 0);
        loadInvoicesSummary(true, 0, "", "", 0);


        registerViews();
        sendGetCategories();
//        defaultLang = Locale.getDefault().getLanguage();
        defaultLang = user.getLanguage();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        // move focus to date button.
        focusView = invoiceDateButton;

        invoiceCategorySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                invoiceCategory = parent.getItemAtPosition(position).toString();
                addItemsOnSpinnerSubCategory(invoiceCategory, defaultLang);
                categoryArrayAdapter.setSelection(position);
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {
                // TODO on nothing selected
            }
        });

        invoiceSubCategorySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                invoiceSubCategory = parent.getItemAtPosition(position).toString();
                subCategoryArrayAdapter.setSelection(position);
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {
                // TODO on nothing selected
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                invoiceDateButton.setText(dayOfMonth + "/" + month + "/" + year);
            }
        };

        setHeaderData();
        setTitle(getText(R.string.newBill));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_invoice_layout);
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
//            startActivity(NewInvoice.this, NewInvoice.class);
        } else if (id == R.id.nav_compensate) {
            startActivity(NewInvoice.this, Compensate.class);
            NewInvoice.this.finish();
        }else if (id == R.id.nav_myProfile) {
            startActivity(NewInvoice.this, Profile.class);
            NewInvoice.this.finish();
//        }else if (id == R.id.nav_dashboard) {
//            NewInvoice.this.finish();
//            startActivity(NewInvoice.this, Home.class);
        } else if (id == R.id.nav_setting) {
            NewInvoice.this.finish();
            startActivity(NewInvoice.this, Setting.class);
        }else if (id == R.id.nav_about) {
            startActivity(NewInvoice.this, AboutUs.class);
            NewInvoice.this.finish();
        } else if (id == R.id.nav_contactUs) {
            startActivity(NewInvoice.this, ContactUs.class);
            NewInvoice.this.finish();
        } else if (id == R.id.nav_logout) {
            SaveSharedPreference.clearData(NewInvoice.this);
//            NewInvoice.this.finish();
//            startActivity(NewInvoice.this, LoginActivity.class);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.philips.philips.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);
        } else if (id == R.id.nav_rate) {
            Toast.makeText(NewInvoice.this, "To handle rate us function", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_invoice_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invoiceDateButton:
                Calendar calender = Calendar.getInstance();
                int day = calender.get(Calendar.DAY_OF_MONTH);
                int month = calender.get(Calendar.MONTH);
                int year = calender.get(Calendar.YEAR);
//                DatePickerDialog dialog = new DatePickerDialog(NewInvoice.this, android.R.style.Theme_DeviceDefault, onDateSetListener, year, month, day);
                DatePickerDialog dialog = new DatePickerDialog(NewInvoice.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, onDateSetListener, year, month, day);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                dialog.show();
                break;
            case R.id.addInvoiceItemButton:
                invoiceSalesNumber = invoiceSalesNumberET.getText().toString();
                invoiceDate = invoiceDateButton.getText().toString();
                boolean dataValidated = validateData(invoiceSalesNumber, invoiceDate, invoiceItemNetSaleET.getText().toString(),invoiceCategorySP.getSelectedItem().toString());
                if (dataValidated) {
                    invoiceNetSale = Double.parseDouble(invoiceItemNetSaleET.getText().toString());
                    invoiceItems.add(invoiceCategory + "," + invoiceNetSale);
                    loadData(false, rowNumber++, invoiceSalesNumber, invoiceDate, invoiceCategory, invoiceNetSale);
                    loadInvoicesSummary(false, 1, invoiceSalesNumber, invoiceCategory, invoiceNetSale);
                    // to Clear data once invoice item added.
                    invoiceItemNetSaleET.setText("");
                    int emptySelection = categoryArrayAdapter.getPosition(getText(R.string.empty).toString());
                    if(emptySelection <= 0) {
                        categoryArrayAdapter.add(getText(R.string.empty).toString());
                        emptySelection = categoryArrayAdapter.getPosition(getText(R.string.empty).toString());
                    }
                    invoiceCategorySP.setSelection(emptySelection);
                }

                break;
            case R.id.submitInvoiceButton:

                invoiceSalesNumber = invoiceSalesNumberET.getText().toString();
                invoiceDate = invoiceDateButton.getText().toString();
                boolean validated = validateDataBeforeSubmission(invoiceSalesNumber, invoiceDate, invoiceItems);
                if (validated) {
                    Invoice invoice = new Invoice(user.getUserName(), user.getName(), Utilities.stringToDate(invoiceDate), invoiceSalesNumber, "", invoiceItems);
                    sendSubmitInvoice(invoice);
                }
                break;
        }
    }

    public void registerViews() {
        invoiceDateButton.setOnClickListener(NewInvoice.this);
        submitInvoiceButton.setOnClickListener(NewInvoice.this);
        addInvoiceItemButton.setOnClickListener(NewInvoice.this);
    }


    private boolean validateData(String invoiceNumber, String date, String netSale, String selectedCategory) {
        if (TextUtils.isEmpty(invoiceNumber)) {
            invoiceSalesNumberET.setError(getString(R.string.error_field_required));
            focusView = invoiceSalesNumberET;
            return false;
        } else if (TextUtils.isEmpty(date) || date.equals(getString(R.string.invoiceDate))) {
            invoiceDateButton.setError(getString(R.string.error_field_required));
            focusView = invoiceDateButton;
            return false;
        } else if (TextUtils.isEmpty(netSale)) {
            invoiceItemNetSaleET.setError(getString(R.string.error_field_required));
            focusView = invoiceItemNetSaleET;
            return false;
        }else if (selectedCategory.equals(getText(R.string.empty).toString())) {
            setSpinnerError(invoiceCategorySP, getText(R.string.error_field_required).toString());
        return false;
        }
        return true;
    }


    private boolean validateDataBeforeSubmission(String invoiceNumber, String date, List<String> invoiceItems) {
        if (TextUtils.isEmpty(invoiceNumber)) {
            invoiceSalesNumberET.setError(getString(R.string.error_field_required));
            focusView = invoiceSalesNumberET;
            return false;
        } else if (TextUtils.isEmpty(date) || date.equals(getString(R.string.invoiceDate))) {
            invoiceDateButton.setError(getString(R.string.error_field_required));
            focusView = invoiceDateButton;
            return false;
        }else if (invoiceItems.size()<=0) {
            Toast.makeText(NewInvoice.this, getText(R.string.error_data), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerSubCategory(String category, String lang) {

        List<String> list = new ArrayList<>();
        for (CategoriesWithSub categoriesWithSub : categoriesWithSubs) {
            if (lang.toUpperCase().equals("AR")) {
                if (categoriesWithSub.getArCategoryName().equals(category)) {
                    for (SubCategory sub : categoriesWithSub.getSubCategories()) {
                        list.add(sub.getArSubCategoryName());
                    }
                }
            } else {
                if (categoriesWithSub.getCategoryName().equals(category)) {
                    for (SubCategory sub : categoriesWithSub.getSubCategories()) {
                        list.add(sub.getSubCategoryName());
                    }
                }
            }
        }

        // to Show empty in subcategory in case there is no sub categories.
        if (list.size() == 0 ){
            list.add(getString(R.string.empty));
            invoiceSubCategorySP.setEnabled(false);
        }else{
            invoiceSubCategorySP.setEnabled(true);
        }

        subCategoryArrayAdapter = new HighLightArrayAdapter(NewInvoice.this,android.R.layout.simple_spinner_dropdown_item, list);
        invoiceSubCategorySP.setAdapter(subCategoryArrayAdapter);

    }


    private boolean sendSubmitInvoice(Invoice invoice) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(NewInvoice.this);
        JSONObject js = null;
        try {
            js = Utilities.invoiceToJson(invoice);
            Log.d("Request is : ", js.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Configurations.getSubmitInvoice(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String status = "";
                        try {
                            status = response.getString("status");
                        } catch (Exception e) {
                            Log.e("Response is : ", e.toString());
                        }
                        if (status.equals("200")) {
                            Log.d("Response is : ", response.toString());
                            Toast.makeText(NewInvoice.this, getString(R.string.invoiceSubmitted), Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("Response is : ", response.toString());
                            Toast.makeText(NewInvoice.this, getString(R.string.invoiceNotSubmitted), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
                        Toast.makeText(NewInvoice.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
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

    String url;

    private boolean sendGetCategories() {
        // TODO update request to handle new updates.
        // Instantiate the RequestQueue.
        List<String> list = new ArrayList<String>();
        RequestQueue queue = Volley.newRequestQueue(NewInvoice.this);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Configurations.getCategoriesWithSub(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<String> list = new ArrayList<String>();
                            String categoryName = "";
                            JSONArray jsonArray = response.getJSONArray("categoriesWithSubs");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (defaultLang.toUpperCase().equals("AR")) {
                                    categoryName = jsonArray.getJSONObject(i).get("arCategoryName").toString();
                                    list.add(categoryName);
                                } else {
                                    categoryName = jsonArray.getJSONObject(i).get("categoryName").toString();
                                    list.add(categoryName);
                                }
                                Gson gson = new GsonBuilder().create();
                                CategoriesWithSub categoriesWithSub = gson.fromJson(jsonArray.get(i).toString(), CategoriesWithSub.class);
                                categoriesWithSubs.add(categoriesWithSub);

                            }
                            Log.d("CatWithSubs is : ", categoriesWithSubs.toString());
                            categoryArrayAdapter = new HighLightArrayAdapter(NewInvoice.this,android.R.layout.simple_spinner_dropdown_item, list);
                            invoiceCategorySP.setAdapter(categoryArrayAdapter);

                        } catch (Exception e) {
                            Log.e("Response is : ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response is : ", error.toString());
                        Toast.makeText(NewInvoice.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
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


    public void loadData(boolean header, final int rowNumber, final String invoiceNumber, String invoiceDate, String category, double netSale) {
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        int textSize = 0, smallTextSize = 0, mediumTextSize = 0;

        textSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.font_size_medium);



        final TextView deleteButton = new TextView(this);
        deleteButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        if (header) {
            deleteButton.setBackgroundColor(Color.parseColor("#dbd9d9"));
                    deleteButton.setPadding(5, 18, 0, 16);
        } else {
            // Set width and hight for delete button.
            deleteButton.getLayoutParams().height = (int) getResources().getDimension(R.dimen.deleteButtonHight);
            deleteButton.getLayoutParams().width = (int) getResources().getDimension(R.dimen.deleteButtonWidth);
            deleteButton.setPadding(5, 5, 5, 5);
            deleteButton.setBackgroundColor(Color.parseColor("#ffffff"));
            deleteButton.setBackground(NewInvoice.this.getResources().getDrawable(R.mipmap.delete_icon));
        }


        TextView textSpacer = null;
        textSpacer = new TextView(this);
        textSpacer.setText("");
        final TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        tv.setGravity(Gravity.LEFT);
        tv.setPadding(5, 15, 0, 15);
        if (header) {
            tv.setText(getString(R.string.invoiceNumber));
            tv.setBackgroundColor(Color.parseColor("#dbd9d9"));
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        } else {
            tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
            tv.setText(invoiceNumber);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        }

        final TextView tv2 = new TextView(this);
        if (header) {
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        } else {
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        }

        tv2.setGravity(Gravity.LEFT);
        tv2.setPadding(5, 15, 0, 15);
        if (header) {
            tv2.setText(getString(R.string.invoiceDate));
            tv2.setBackgroundColor(Color.parseColor("#dbd9d9"));
            tv2.setTextColor(Color.parseColor("#000000"));
        } else {
            tv2.setBackgroundColor(Color.parseColor("#ffffff"));
            tv2.setTextColor(Color.parseColor("#000000"));
            tv2.setText(invoiceDate);
        }

        final LinearLayout layCustomer = new LinearLayout(this);
        layCustomer.setOrientation(LinearLayout.VERTICAL);
        layCustomer.setBackgroundColor(Color.parseColor("#f8f8f8"));

        final TextView tv3 = new TextView(this);
        if (header) {
            tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        } else {
            tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        }

        tv3.setGravity(Gravity.LEFT);
        tv3.setPadding(5, 15, 0, 15);
        if (header) {
            tv3.setText(getString(R.string.category));
            tv3.setBackgroundColor(Color.parseColor("#dbd9d9"));
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            tv3.setTextColor(Color.parseColor("#000000"));
        } else {
            tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
            tv3.setTextColor(Color.parseColor("#000000"));
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            tv3.setText(category);
        }
        layCustomer.addView(tv3);

        final LinearLayout layAmounts = new LinearLayout(this);
        layAmounts.setOrientation(LinearLayout.VERTICAL);
        layAmounts.setGravity(Gravity.LEFT);
        layAmounts.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,

                TableRow.LayoutParams.MATCH_PARENT));

        final TextView tv4 = new TextView(this);
        if (header) {
            tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            layAmounts.setBackgroundColor(Color.parseColor("#f7f7f7"));
        } else {
            tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            layAmounts.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        tv4.setGravity(Gravity.LEFT);
        tv4.setPadding(5, 15, 0, 15);
        if (header) {
            tv4.setText(getString(R.string.netSale));
            tv4.setBackgroundColor(Color.parseColor("#dbd9d9"));
            tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            tv4.setTextColor(Color.parseColor("#000000"));
        } else {
            tv4.setBackgroundColor(Color.parseColor("#ffffff"));
            tv4.setTextColor(Color.parseColor("#000000"));
            tv4.setText(netSale + "");
            tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
        }

        layAmounts.addView(tv4);

        // add table row
        final TableRow tr = new TableRow(this);
        tr.setId(rowNumber);
        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
        tr.setPadding(0, 0, 0, 0);
        tr.setLayoutParams(trParams);

        tr.addView(deleteButton);
        tr.addView(tv);
        tr.addView(tv2);
        tr.addView(layCustomer);
        tr.addView(layAmounts);

        if (!header) {
            tr.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final TableRow tr = (TableRow) v;
                    TextView deleteBT = (TextView) (tr.getChildAt(0));

                    deleteBT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(NewInvoice.this)
                                    .setTitle("Remove Invoice Item")
                                    .setMessage("Do you really want to remove this Invoice Item ?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            tr.removeAllViews();
                                            // to remove data also from list that will be submitted to invoice.
                                            invoiceItems.remove(tr.getId());
                                            NewInvoice.this.rowNumber --;
                                            // Create function to update everything related to this remove.
                                            loadInvoicesSummary(false, 1,invoiceNumber , "", 0);
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();

                        }
                    });
                }
            });
        }
        invoicesTableLayout.addView(tr, trParams);
        if (!header) {
            // add separator row
            final TableRow trSep = new TableRow(this);
            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            trSep.setLayoutParams(trParamsSep);
            TextView tvSep = new TextView(this);
            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            tvSepLay.span = 4;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
            tvSep.setHeight(1);
            trSep.addView(tvSep);
            invoicesTableLayout.addView(trSep, trParamsSep);
        }
    }


    public void loadInvoicesSummary(boolean header, int rowNumber, String invoiceNumber, String category1, double netSale1) {
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        int smallTextSize = 0;
        double sumOfNetSale=0;


        smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);

        TextView textSpacer = null;
        textSpacer = new TextView(this);
        textSpacer.setText("");

        invoicesSummaryTableLayout.removeAllViews();

        HashMap<String, Double> invoiceItemsSummary = new HashMap<>();
        for (String item : invoiceItems) {
            double oldvalue = 0;
            if (invoiceItemsSummary.get(item.split(",")[0]) != null)
                oldvalue = invoiceItemsSummary.get(item.split(",")[0]);
            invoiceItemsSummary.put(item.split(",")[0], Double.parseDouble(item.split(",")[1]) + oldvalue);
            sumOfNetSale +=Double.parseDouble(item.split(",")[1]);
        }



        for (Map.Entry<String, Double> entry : invoiceItemsSummary.entrySet()) {
            String category = entry.getKey();
            Double netSale = entry.getValue();


            final TextView tv = new TextView(this);
            final TextView tv2 = new TextView(this);
            final TextView tv3 = new TextView(this);
            final TextView tv4 = new TextView(this);
            final TableRow tr = new TableRow(this);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);

            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tv.setGravity(Gravity.LEFT);
            tv.setPadding(5, 15, 0, 15);
            tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
            tv.setText(invoiceNumber);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

            tv2.setGravity(Gravity.LEFT);
            tv2.setPadding(5, 15, 0, 15);

            tv2.setBackgroundColor(Color.parseColor("#ffffff"));
            tv2.setTextColor(Color.parseColor("#000000"));
            tv2.setText(invoiceDate);

            tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

            tv3.setGravity(Gravity.LEFT);
            tv3.setPadding(5, 15, 0, 15);

            tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
            tv3.setTextColor(Color.parseColor("#000000"));
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            tv3.setText(category);
            tv4.setGravity(Gravity.LEFT);
            tv4.setPadding(5, 15, 0, 15);

            tv4.setBackgroundColor(Color.parseColor("#ffffff"));
            tv4.setTextColor(Color.parseColor("#000000"));
            tv4.setText(netSale + "");
            tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

            // add table row
            tr.setId(rowNumber);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0, 0, 0, 0);
            tr.setLayoutParams(trParams);

            final TextView emptyTV = new TextView(this);
            emptyTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
//            emptyTV.setPadding(10, 20, 10, 15);
              emptyTV.setBackgroundColor(Color.parseColor("#f8f8f8"));
//                emptyTV.setBackgroundColor(Color.parseColor("#ffffff"));


            tr.addView(emptyTV);
            tr.addView(tv);
            tr.addView(tv2);
            tr.addView(tv3);
            tr.addView(tv4);
            if (!header) {
                tr.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TableRow tr = (TableRow) v;
                    }
                });
            }
            invoicesSummaryTableLayout.addView(tr, trParams);
        }
        totalNetSaleTv.setText(sumOfNetSale+"");
    }


    private void setHeaderData(){
        // To get updated shared preference.
        User user = SaveSharedPreference.getUser(NewInvoice.this);
        NavigationView navigationView1 = (NavigationView) findViewById(R.id.invoice_nav_view);
        View headerView = navigationView1.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.customerIDTextView);
        navUsername.setText(user.getName());

        if(! user.getProfileImage().equals("")) {
            Log.d("Header", "SetHeader Method called from Home and image = "+user.getProfileImage());
            CircleImageView profileImage = (CircleImageView) headerView.findViewById(R.id.profileImageViewNavHome);

            Picasso.with(NewInvoice.this).load(user.getProfileImage()).placeholder(R.drawable.mohamed)
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

    private void setSpinnerError(Spinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText(error); // actual error message
            spinner.performClick(); // to open the spinner list if error is found.

        }
    }
}
