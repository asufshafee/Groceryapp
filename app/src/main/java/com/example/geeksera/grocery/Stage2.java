package com.example.geeksera.grocery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Stage2 extends Fragment {


    public Stage2() {
        // Required empty public constructor
    }

    public RadioButton Car, Walk;
    public NiceSpinner AMPM;
    public NiceSpinner Minites;
    public NiceSpinner Hours;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stage2, container, false);


        Car = (RadioButton) view.findViewById(R.id.Car);
        Walk = (RadioButton) view.findViewById(R.id.walk);

        Car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Car.setChecked(true);
                Walk.setChecked(false);
            }
        });
        Walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Car.setChecked(false);
                Walk.setChecked(true);
            }
        });


        Hours = (NiceSpinner) view.findViewById(R.id.Hours);
        List<String> dataset = new LinkedList<>(Arrays.asList("01", "02", "03", "04", "05"
                , "06", "07", "08", "09"
                , "10", "11", "12"));
        Hours.attachDataSource(dataset);

        Minites = (NiceSpinner) view.findViewById(R.id.Minit);
        List<String> dataset1 = new LinkedList<>();

        for (int i = 0; i < 60; i++) {
            String now = String.valueOf(i);
            if (now.length() == 1) {
                now = "0" + now;
            }
            dataset1.add(now);
        }
        Minites.attachDataSource(dataset1);

        AMPM = (NiceSpinner) view.findViewById(R.id.AMPM);
        List<String> dataset11 = new LinkedList<>(Arrays.asList("AM", "PM"));
        AMPM.attachDataSource(dataset11);

        return view;


    }

}
