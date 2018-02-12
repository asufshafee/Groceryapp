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
public class Queue extends Fragment {


    public Queue() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pickup__stage2, container, false);

        TextView Queue = (TextView) view.findViewById(R.id.Queue);

        Queue.setText(getArguments().getString("queue"));

        return view;
    }

}
