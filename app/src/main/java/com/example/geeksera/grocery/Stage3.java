package com.example.geeksera.grocery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Stage3 extends Fragment {

    public Stage3() {
        // Required empty public constructor
    }


    public NiceSpinner Month, Year;
    public EditText Holdername, CCV2, CardNumber, BilingZip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_stage3, container, false);

        Holdername = (EditText) view.findViewById(R.id.Holdername);
        CCV2 = (EditText) view.findViewById(R.id.CCV2);
        CardNumber = (EditText) view.findViewById(R.id.CardNumber);
        BilingZip = (EditText) view.findViewById(R.id.ZioCode);
        Month = (NiceSpinner) view.findViewById(R.id.Month);
        Year = (NiceSpinner) view.findViewById(R.id.Year);

        NiceSpinner Month = (NiceSpinner) view.findViewById(R.id.Month);
        List<String> datasetMOnth = new LinkedList<>(Arrays.asList("January", "February", "March", "April", "May"
                , "June", "July", "August", "September"
                , "October", "November", "December "));
        Month.attachDataSource(datasetMOnth);
        NiceSpinner Year = (NiceSpinner) view.findViewById(R.id.Year);
        List<String> datasetYear = new LinkedList<>();

        for (int i = 2017; i < 2030; i++) {
            String now = String.valueOf(i);
            datasetYear.add(now);
        }
        Year.attachDataSource(datasetYear);


        return view;
    }

}
