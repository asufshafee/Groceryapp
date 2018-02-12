package com.example.geeksera.grocery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geeksera.grocery.Adopters.Home_Menu_Adopter;
import com.example.geeksera.grocery.Grid.ExpandableHeightGridView;
import com.example.geeksera.grocery.Grid.GridviewAdapter;
import com.example.geeksera.grocery.Objects.Catagory;
import com.example.geeksera.grocery.Objects.MyApp;
import com.example.geeksera.grocery.Objects.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Home extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    List<String> dataset;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console.
     */
    String SENDER_ID = "Your-Sender-ID";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    private ExpandableHeightGridView gridview;
    private GridviewAdapter gridviewAdapter;

    private ArrayList<Product> ProductList;
    ArrayList<Catagory> CatagotyList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    FrameLayout Detail;
    ScrollView Scroll;

    MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dataset = new LinkedList<>();

//        startService(new Intent(getApplicationContext(),TimeSettings.class));

        LocationICon();
        checkIfAlreadyhavePermission();
        if (getIntent().getStringExtra("Complete") != null) {

//                 Created a new Dialog
            final Dialog dialog = new Dialog(Home.this);
            // inflate the layout
            dialog.setContentView(R.layout.complete_dialog);
            // Set the dialog text -- this is better done in the XML
            final LinearLayout Click = (LinearLayout) dialog.findViewById(R.id.Click);
            Click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                }
            });
            dialog.show();
            return;
        }
        findViewById(R.id.Location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Pickup.class);
                intent.putExtra("cat", (Serializable) dataset);
                startActivity(intent);
            }
        });

        Scroll = (ScrollView) findViewById(R.id.Scroll);

        Detail = (FrameLayout) findViewById(R.id.Details);
        Detail.setVisibility(View.GONE);
        myApp = (MyApp) getApplicationContext();

        GetCatagories(getApplicationContext());
        GetHomeItems(getApplicationContext());

        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  Firebase Cloud Messaging registration.


        findViewById(R.id.Search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Searching.class));
//                EditText SearchText = (EditText) findViewById(R.id.TextSearch);
//                SearchProduct(getApplicationContext(), SearchText.getText().toString());

            }
        });
        findViewById(R.id.Next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int firstVisibleItemIndex = mLayoutManager.getItemCount();
                if (firstVisibleItemIndex > 0) {
                    mLayoutManager.smoothScrollToPosition(mRecyclerView, null, firstVisibleItemIndex + 1);
                }
            }
        });
        findViewById(R.id.Previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);
                int totalItemCount = mRecyclerView.getAdapter().getItemCount();
                if (totalItemCount <= 0) return;
                int lastVisibleItemIndex = mLayoutManager.getItemCount();

                if (lastVisibleItemIndex >= totalItemCount) return;

            }
        });

        findViewById(R.id.basket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApp.getCart().size() > 0) {
                    Intent intent = new Intent(getApplicationContext(), CheckOut.class);
                    intent.putExtra("cat", (Serializable) dataset);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Cart is Empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        gridview = (ExpandableHeightGridView) findViewById(R.id.gridview);
        ProductList = new ArrayList<Product>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void GetCatagories(Context context) {

        CatagotyList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/getproductcategory", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject Boday = jsonObject.getJSONObject("body");
                    JSONArray contacts = Boday.getJSONArray("data");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        Catagory catagory = new Catagory();

                        catagory.setPrice(c.getString("name"));
                        catagory.setUrl(c.getString("url"));
                        CatagotyList.add(catagory);
                    }
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(Home.this, LinearLayoutManager.HORIZONTAL, false);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    // specify an adapter (see also next example)
                    mAdapter = new Home_Menu_Adopter(Home.this, CatagotyList);
                    mRecyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    Log.d("", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
        };
        queue.add(sr);
    }

    public void GetHomeItems(Context context) {

        CatagotyList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/app-rand20", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject Boday = jsonObject.getJSONObject("body");
                    JSONArray contacts = Boday.getJSONArray("data");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        Product product = new Product();
                        product.setName(c.getString("productName"));
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
                    gridviewAdapter = new GridviewAdapter(Home.this, ProductList, Detail, Scroll);
                    gridviewAdapter.setMyApp(getApplication());
                    gridview.setExpanded(true);
                    gridview.setAdapter(gridviewAdapter);
                } catch (JSONException e) {
                    Log.d("", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "");
            }
        }) {
        };
        queue.add(sr);
    }

    public void SearchProduct(Context context, final String Keyword) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                        JSONArray contacts = Boday.getJSONArray("data");
                        if (contacts != null) {

                            for (Catagory a : CatagotyList) {
                                dataset.add(a.getPrice());
                            }
                            Intent intent = new Intent(getApplicationContext(), Search.class);
                            intent.putExtra("json", response);
                            intent.putExtra("word", Keyword);
                            intent.putExtra("cat", (Serializable) dataset);
                            startActivity(intent);

                        } else {
                            Toast.makeText(getApplicationContext(), "no search found", Toast.LENGTH_LONG).show();
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
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(Home.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE);
        result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_WIFI_STATE,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return false;
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
