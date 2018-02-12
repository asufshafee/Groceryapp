package com.example.geeksera.grocery;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geeksera.grocery.Objects.DecimalUtils;
import com.example.geeksera.grocery.Objects.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeSettings extends Service {



    public static final int notify = 10000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    Intent myService;


    public TimeSettings() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        myService = new Intent(getApplicationContext(), TimeSettings.class);
        mTimer.scheduleAtFixedRate(new TimeDisplay(),  1000, notify);   //Schedule task

    }

    private class Counter implements Runnable {

        private Handler testhandler = new Handler();

        @Override
        public void run() {
            testhandler.post(new Runnable() {
                @Override
                public void run() {
                    setTime();
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast

                    setTime();

                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
//        Thread counter = new Thread(new Counter());
//        counter.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setTime() {
        //riddle time setting
        final double serverLatitute = 17.7532515;
        final double serverLongitude = -64.7261261;
//
//
//        final double serverLatitute = 33.6523166;
//        final double serverLongitude = 73.0882225;

        int currentTime = (int) (Calendar.getInstance().getTimeInMillis()) / 60000; // minutes
        final SharedPreferences prefs = getSharedPreferences("Baskit", MODE_PRIVATE);
        String savedTime = prefs.getString("PickupTime", "0");
        if (!savedTime.equals("0")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Calendar cal = Calendar.getInstance();

            Date date1;
            Date date2;
            try {
                date1 = dateFormat.parse(savedTime);
                String date=dateFormat.format(cal.getTime());
                date2=dateFormat.parse(date);

                int mHour5 = date1.getHours();
                int mMinute5 = date1.getMinutes();
                //pickupTime in minutes
                int pickupTime = mHour5 * 60 + mMinute5;
                int currenttimenew=date2.getHours()*60+date2.getMinutes();
                if (pickupTime - 30 <= currenttimenew && currenttimenew <= pickupTime) {



                    GPSTracker gpsTracker=new GPSTracker(getApplicationContext());
                    double currentLatitude = gpsTracker.getLatitude();
                    double currentLongitude = gpsTracker.getLongitude();
                    gpsTracker.stopUsingGPS();
                    if (prefs.getString("UserType", "").toLowerCase().equals("walking") && distance(currentLatitude, currentLongitude, serverLatitute, serverLongitude) == 0.00) {
                        sendRequest();
                    } else if (prefs.getString("UserType", "").toLowerCase().equals("driving") && (distance(currentLatitude, currentLongitude, serverLatitute, serverLongitude) == 0.25 || distance(currentLatitude, currentLongitude, serverLatitute, serverLongitude) == 0.00)) {
                        sendRequest();
                    }


                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendRequest() {
        final SharedPreferences prefs = getSharedPreferences("Baskit", MODE_PRIVATE);
        String orderId = prefs.getString("orderId", "60");
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/assignhauling";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("orderId", orderId);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject Body = jsonObject.getJSONObject("body");
                        String message = Body.getString("msg");
                        Log.d("---------------", "-------------------");
                        Log.d("Sucess", message);
                        mTimer.cancel();
                        stopService(myService);
                    } catch (JSONException e) {
                        mTimer.cancel();
                        stopService(myService);
                        Log.d("", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mTimer.cancel();
                    stopService(myService);
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

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        return DecimalUtils.round((dist),2);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
