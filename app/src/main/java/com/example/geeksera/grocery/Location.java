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
public class Location extends Fragment {


    public Location() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pickup__stage1, container, false);

        TextView Lot = (TextView) view.findViewById(R.id.Lot);
        TextView OrderNumber = (TextView) view.findViewById(R.id.OrderNUmber);
        Lot.setText(getArguments().getString("lot"));
        OrderNumber.setText("Order Number is " + getArguments().getString("Order") + ".");

        return view;
    }

}
