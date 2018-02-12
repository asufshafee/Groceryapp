package com.example.geeksera.grocery.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.example.geeksera.grocery.Objects.MyApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceForCarts extends Service {

    public static final int notify = 1000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    Intent myService;
    Date lastTime = new Date();

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    Calendar cal;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new

        cal = Calendar.getInstance();
        String mLastTime = dateFormat.format(cal.getTime());
        try {
            lastTime = dateFormat.parse(mLastTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        myService = new Intent(getApplicationContext(), ServiceForCarts.class);
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 1000, notify);   //Schedule task

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
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

                    Calendar NowTime=Calendar.getInstance();
                    Date Now=NowTime.getTime();
                    int diff=Now.getMinutes()-lastTime.getMinutes();
                    if (diff>15)
                    {
                        stopService(myService);
                        MyApp myApp=(MyApp)getApplicationContext();
                        myApp.getCart().clear();
                        mTimer.cancel();
                    }
                }
            });
        }
    }
}