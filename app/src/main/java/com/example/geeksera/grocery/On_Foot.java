package com.example.geeksera.grocery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class On_Foot extends Fragment {


    public On_Foot() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pickup__stage3, container, false);

        TextView Queue = (TextView) view.findViewById(R.id.OrderNumber);

        Queue.setText(getArguments().getString("Order"));

        return view;
    }

}
