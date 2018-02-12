package com.example.geeksera.grocery;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class Advirtise_Pickup_Time extends Fragment {


    TextView Queue;

    public Advirtise_Pickup_Time() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_advirtise__pickup__time, container, false);

        final Calendar dateAndTime = Calendar.getInstance();
        Queue = (TextView) view.findViewById(R.id.time);
        final TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateAndTime.set(Calendar.MINUTE, minute);
                showTime(hourOfDay, minute);
            }
        };

        Queue.setText(getArguments().getString("Time"));
        view.findViewById(R.id.ChnageTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new TimePickerDialog(getActivity(),
                        t,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

        return view;
    }


    public void showTime(int hour, int min) {
        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        Queue.setText(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));
    }
}
