package com.example.geeksera.grocery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geeksera.grocery.Grid.ExpandableHeightGridViewSearch;
import com.example.geeksera.grocery.Grid.GridviewAdapterSearch;
import com.example.geeksera.grocery.Objects.Catagory;
import com.example.geeksera.grocery.Objects.Product;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Search extends AppCompatActivity {

    private ExpandableHeightGridViewSearch gridview;
    private GridviewAdapterSearch gridviewAdapter;

    private ArrayList<Product> ProductList;
    List<Catagory> CatagotyList;
    TextView SearchText;

    FrameLayout Detail;
    ScrollView Scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViewById(R.id.Home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!this.equals(Home.class)) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                }
            }
        });

        findViewById(R.id.Location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Pickup.class));
            }
        });

        findViewById(R.id.Search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Searching.class));

            }
        });

        Scroll = (ScrollView) findViewById(R.id.Scroll);

        Detail = (FrameLayout) findViewById(R.id.Details);
        Detail.setVisibility(View.GONE);

        SearchText = (TextView) findViewById(R.id.SearchText);
        SearchText.setText(getIntent().getStringExtra("word"));

        findViewById(R.id.basket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CheckOut.class));
            }
        });

        ProductList = new ArrayList<>();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(getIntent().getExtras().getString("json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject Boday = null;
        try {
            Boday = jsonObject.getJSONObject("body");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONArray contacts = Boday.getJSONArray("data");

            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                Product product = new Product();

                product.setImage(c.getString("url"));
                product.setPrice(c.getString("price"));
                product.setModel(c.getString("model"));
                product.setDescription(c.getString("description"));
                product.setSpecification(c.getString("specification"));
                product.setIngredients(c.getString("ingredients"));
                product.setWarnings(c.getString("warnings"));
                product.setProductId(c.getString("productId"));
                product.setCondition(c.getString("condition"));

                ProductList.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gridview = (ExpandableHeightGridViewSearch) findViewById(R.id.gridview);
        gridviewAdapter = new GridviewAdapterSearch(Search.this, ProductList, Detail, Scroll);
        gridview.setExpanded(true);
        gridview.setAdapter(gridviewAdapter);
        final NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        final List<String> dataset = getIntent().getStringArrayListExtra("cat");
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setText(getIntent().getStringExtra("word"));

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchProduct(getApplicationContext(), dataset.get(position).toString());
                SearchText.setText(dataset.get(position).toString());

                ProductList.clear();
                gridview = (ExpandableHeightGridViewSearch) findViewById(R.id.gridview);
                gridviewAdapter = new GridviewAdapterSearch(Search.this, ProductList, Detail, Scroll);
                gridview.setExpanded(true);
                gridview.setAdapter(gridviewAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ProductList = new ArrayList<Product>();
    }

    public void SearchProduct(final Context context, final String Keyword) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/app-searchproduct";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("keyword", Keyword);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject Boday = jsonObject.getJSONObject("body");
                        try {
                            JSONArray contacts = Boday.getJSONArray("data");

                            for (int i = 0; i < contacts.length(); i++) {
                                JSONObject c = contacts.getJSONObject(i);
                                Product product = new Product();
                                product.setImage(c.getString("url"));
                                product.setPrice(c.getString("price"));
                                product.setModel(c.getString("model"));
                                product.setDescription(c.getString("description"));
                                product.setSpecification(c.getString("specification"));
                                product.setIngredients(c.getString("ingredients"));
                                product.setWarnings(c.getString("warnings"));
                                product.setProductId(c.getString("productId"));
                                product.setCondition(c.getString("condition"));

                                ProductList.add(product);
                            }
                            gridviewAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        Log.d("", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getMobileNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String strMobileNumber = telephonyManager.getLine1Number();
        return strMobileNumber;
    }

    public void LocationICon() {
        long NowTimeInMiliSecond;
        long StartTimeInMiliSecond;
        long Difference;
        SharedPreferences prefs = getSharedPreferences("Baskit", MODE_PRIVATE);
        String orderID = prefs.getString("OrderID", null);
        if (orderID == null)
            return;
        StartTimeInMiliSecond = prefs.getLong("startTime", 0);
        NowTimeInMiliSecond = Calendar.getInstance().getTimeInMillis();
        Difference = NowTimeInMiliSecond - StartTimeInMiliSecond;
        String Time = getDurationString(Difference);
        if (Integer.parseInt(Time) < 2) {
            ImageView imageView = findViewById(R.id.Location);
            imageView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.greentextcolor));
        } else {
            ImageView imageView = findViewById(R.id.Location);
            imageView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorTex));
        }
    }

    private String getDurationString(long mills) {

        int Hours = 0;
        int Mins = 0;
        int Secs = 0;
        Hours = (int) (mills / (1000 * 60 * 60));
        Mins = (int) (mills / (1000 * 60)) % 60;
        Secs = (int) (mills / 1000) % 60;

        return twoDigitString(Hours);
    }

    private String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }
}
