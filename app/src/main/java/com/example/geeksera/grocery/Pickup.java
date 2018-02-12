package com.example.geeksera.grocery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geeksera.grocery.Objects.Catagory;
import com.example.geeksera.grocery.Objects.GPSTracker;
import com.example.geeksera.grocery.Objects.MyApp;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class Pickup extends AppCompatActivity {
    Fragment fragment = null;
    Class fragmentClass = null;
    ArrayList<Catagory> CatagotyList;
    List<String> dataset = new LinkedList<>();

    LatLng LatLong = new LatLng(17.7526431, 17.7526431);

    GPSTracker gpsTracker;

    MyApp myApp;
    Runnable run;
    Handler handler;

    int Hours = 0;
    int Mins = 0;
    int Secs = 0;
    long NowTimeInMiliSecond;
    long StartTimeInMiliSecond;
    long Difference;
    String OrderID;
    long startTime;
    String UserType;

    Boolean FinishOrPause = false;

    String Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);


        GetCatagories(getApplicationContext());

        gpsTracker = new GPSTracker(this);
//        gpsTracker.checkLocationPermission();

        LocationICon();
        if (getIntent().getStringExtra("From") != null) {


            SharedPreferences prefs = getSharedPreferences("Baskit", MODE_PRIVATE);
            String restoredText = prefs.getString("mNumber", null);

            Phone = restoredText;
            OrderID = getIntent().getStringExtra("OrderID");
            startTime = getIntent().getLongExtra("startTime", 0);
            UserType = getIntent().getStringExtra("UserType");


            SharedPreferences.Editor editor = getSharedPreferences("Baskit", MODE_PRIVATE).edit();
            editor.putString("OrderID", OrderID);
            editor.putLong("startTime", startTime);
            editor.putString("UserType", UserType);
            editor.apply();
            LocationICon();


            fragmentClass = Advirtise_Pickup_Time.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("Time", getIntent().getStringExtra("Time"));
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.Contaner, fragment).commit();
            StartTimeInMiliSecond = startTime;
            long endTime = System.currentTimeMillis();
            Difference = endTime - startTime;
//            scheduleThread();

        } else {

            LocationICon();
            SharedPreferences prefs = getSharedPreferences("Baskit", MODE_PRIVATE);
            String restoredText = prefs.getString("mNumber", null);

            Phone = restoredText;
            OrderID = prefs.getString("OrderID", null);
            startTime = prefs.getLong("startTime", 0);
            UserType = prefs.getString("UserType", null);
            StartTimeInMiliSecond = startTime;
            long endTime = System.currentTimeMillis();
            Difference = endTime - startTime;
            scheduleThread();

        }


        findViewById(R.id.Home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!this.equals(Home.class)) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                }
            }
        });


        myApp = (MyApp) getApplicationContext();

        findViewById(R.id.Location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), Pickup.class);
                intent.putExtra("cat", (Serializable) dataset);
                startActivity(intent);
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
                    Toast.makeText(getApplicationContext(), "Cart is Empity", Toast.LENGTH_LONG).show();
                }
            }
        });
        findViewById(R.id.Search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Searching.class));

            }
        });

    }

    public void updateTime() {

        scheduleThread();
        NowTimeInMiliSecond = Calendar.getInstance().getTimeInMillis();
        Difference = NowTimeInMiliSecond - StartTimeInMiliSecond;
        String Time = getDurationString(Difference);
        if (Integer.parseInt(Time) < 2) {
            if (UserType.toLowerCase().contains("w")) {
                fragmentClass = On_Foot.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("Order", OrderID);
                    fragment.setArguments(bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!FinishOrPause) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Contaner, fragment).commit();
                }

            } else {

//
//                LatLng latLong = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
//                if (getDistance(LatLong,latLong)<0.5f)
//                {
//                }else {
//                    NotifyApi(getApplicationContext(),Phone,OrderID,true);
//                }
                NotifyApi(getApplicationContext(),Phone,OrderID,false);


            }

        } else {
            Toast.makeText(getApplicationContext(), "No Order in Queue", Toast.LENGTH_LONG).show();
            handler.removeCallbacks(run);
            finish();
        }
        Log.d("", "Time    " + getDurationString(Difference));

    }

    public Double getDistance(LatLng my_latlong, LatLng frnd_latlong) {



        return distance(my_latlong.latitude,my_latlong.longitude,frnd_latlong.latitude,frnd_latlong.longitude);
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    protected void onPause() {
        FinishOrPause = true;
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        FinishOrPause = true;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        FinishOrPause = false;
        super.onResume();
    }

    private String getDurationString(long mills) {

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

    public void scheduleThread() {
        handler = new Handler();
        run = new Runnable() {

            @Override
            public void run() {
                updateTime();
            }
        };
        handler.postDelayed(run, 1000);
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


    public void NotifyApi(Context context, String phone, final String orderId, Boolean notArrived) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/notify";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("phone", phone);
            jsonBody.put("orderId", orderId);
            jsonBody.put("notArrived", notArrived);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject Boday = jsonObject.getJSONObject("body");


//                        if (Boday.getString("status")==null)
//                        {
                        String Lot = Boday.getString("lot");
                        String Queue = Boday.getString("queue");

                        Bundle bundle = new Bundle();
                        bundle.putString("Order", OrderID);
                        bundle.putString("lot", Lot);
                        bundle.putString("queue", Queue);
                        if (Queue.equals("0")) {
                            fragmentClass = Location.class;
                        } else {
                            fragmentClass = com.example.geeksera.grocery.Queue.class;
                        }


                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                            fragment.setArguments(bundle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!FinishOrPause) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.Contaner, fragment).commit();
                        }
//                        }else {
//                            Toast.makeText(getApplicationContext(),"else",Toast.LENGTH_LONG).show();
//
//                        }


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {
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


}
