package com.example.geeksera.grocery;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geeksera.grocery.Adopters.Cart_Adapter;
import com.example.geeksera.grocery.Objects.MyApp;
import com.example.geeksera.grocery.Objects.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Stage1 extends Fragment {

    public Stage1() {
        // Required empty public constructor
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Product> CartList;
    private MyApp myApp;

    private TextView Total, SubTotal;
    private int StockQty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_stage1, container, false);

        myApp = (MyApp) getActivity().getApplicationContext();
        Total = (TextView) view.findViewById(R.id.Total);
        SubTotal = (TextView) view.findViewById(R.id.SubTotal);

        view.findViewById(R.id.BtnCopan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myApp.isCopen()) {
                    EditText Coupan = (EditText) view.findViewById(R.id.Copan);
                    Coupan(Coupan.getText().toString());

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Already Taken", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        CartList = new ArrayList<Product>();

        CartList = myApp.getCart();

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new Cart_Adapter(getActivity(), CartList, Total, SubTotal);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    public void Coupan(String CoupanCode) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/app-validatecoupon";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("couponCode", CoupanCode);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject contacts = jsonObject.getJSONObject("body");

                        if (contacts.getString("status").equals("1")) {
                            Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            myApp.setDiscount(contacts.getInt("discount"));
                            myApp.setCopen(true);
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

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
