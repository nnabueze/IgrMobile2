package com.example.eze.igrmobile;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.eze.igrmobile.model.RemittanceModel;
import com.example.eze.igrmobile.parser.RemittnaceParser;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by EZE on 9/7/2017.
 */

public class Remittance extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView lastMonthRemitted, currentMonthRemitted, lastMonth, currentMonth;
    private RemittanceModel remittanceModel;
    private LinearLayout lastMR, currentMR, lastMU, currentMU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remittance);

        setUpToolBarMenu();
        setTextView();
        pullData();
        buttonClick();
    }

    private void buttonClick() {
        lastMR = (LinearLayout) findViewById(R.id.lastMR);
        currentMR = (LinearLayout) findViewById(R.id.currentMR);
        lastMU = (LinearLayout) findViewById(R.id.lastMU);
        currentMU = (LinearLayout) findViewById(R.id.currentMU);

        lastMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Remittance.this, "Details Unconstruction", Toast.LENGTH_SHORT).show();
            }
        });

        currentMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Remittance.this, "Details Unconstruction", Toast.LENGTH_SHORT).show();
            }
        });

        lastMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Remittance.this, "Details Unconstruction", Toast.LENGTH_SHORT).show();
            }
        });

        lastMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Remittance.this, "Details Unconstruction", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pieChatGraph() {
        PieChart pieChart = (PieChart) findViewById(R.id.chart);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(Float.parseFloat(remittanceModel.getLastMonthRemite()), 0));
        entries.add(new Entry(Float.parseFloat(remittanceModel.getCurrentMonthRemite()), 1));
        entries.add(new Entry(Float.parseFloat(remittanceModel.getLastMonth()), 2));
        entries.add(new Entry(Float.parseFloat(remittanceModel.getCurrentMonth()), 3));

        PieDataSet dataset = new PieDataSet(entries, "");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Last M R");
        labels.add("Current M R");
        labels.add("Last M U");
        labels.add("Current M U");

        PieData data = new PieData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        pieChart.setDescription("Remittance Amount");
        pieChart.setData(data);

        pieChart.animateY(5000);

        pieChart.saveToGallery("/sd/mychart.jpg", 85); // 85 is the quality of the image
    }

    private void setUpToolBarMenu() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Remittance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void setTextView() {
        lastMonthRemitted = (TextView) findViewById(R.id.lastMonth);
        currentMonthRemitted = (TextView) findViewById(R.id.currentMonth);
        lastMonth = (TextView) findViewById(R.id.yestarday);
        currentMonth = (TextView) findViewById(R.id.today);
    }

    private void pullData() {
        if (!isOnLine()) {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_SHORT).show();
        } else {
            makeCall();
        }
    }

    private void makeCall() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        StringRequest request = new StringRequest(Request.Method.POST, Utility.REMITTANCE_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(MdaActivity.this, response, Toast.LENGTH_SHORT).show();
                        parseRemittanceFeed(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            onLoginFailDialog("Communication Error!");

                        } else if (error instanceof AuthFailureError) {
                            onLoginFailDialog("Authentication Error!");
                        } else if (error instanceof ServerError) {
                            onLoginFailDialog("Server Side Error!");
                        } else if (error instanceof NetworkError) {
                            onLoginFailDialog("Network Error!");
                        } else if (error instanceof ParseError) {
                            onLoginFailDialog("Parse Error!");
                        }

                    }
                }) {
            //adding header param

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("billerId", preferences.getString("billerId", null).toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + preferences.getString("token", null).toString());
                return headers;
            }
        };
        RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void parseRemittanceFeed(String response) {
        remittanceModel = RemittnaceParser.parseFeed(response);

        lastMonthRemitted.setText(numberFormat(remittanceModel.getLastMonthRemite()));
        currentMonthRemitted.setText(numberFormat(remittanceModel.getCurrentMonthRemite()));
        lastMonth.setText(numberFormat(remittanceModel.getLastMonth()));
        currentMonth.setText(numberFormat(remittanceModel.getCurrentMonth()));

        pieChatGraph();
    }

    private void onLoginFailDialog(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private boolean isOnLine() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String numberFormat(String number){
        double num = Double.parseDouble(number);
        DecimalFormat money = new DecimalFormat("###,###,###,###");
        String formattedText = "₦" + money.format(num);

        return formattedText;
    }
}
