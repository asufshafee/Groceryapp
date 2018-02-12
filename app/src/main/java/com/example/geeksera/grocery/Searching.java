package com.example.geeksera.grocery;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geeksera.grocery.Grid.ExpandableHeightGridView;
import com.example.geeksera.grocery.Grid.GridviewAdapter;
import com.example.geeksera.grocery.Grid.GridviewAdapterSearching;
import com.example.geeksera.grocery.Objects.Catagory;
import com.example.geeksera.grocery.Objects.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Searching extends AppCompatActivity {



    private ExpandableHeightGridView gridview;
    private GridviewAdapterSearching gridviewAdapter;
    ArrayList<Catagory> CatagotyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


        gridview = (ExpandableHeightGridView) findViewById(R.id.gridview);

        GetHomeItems(getApplicationContext());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

    }


    public void GetHomeItems(Context context) {


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
                    gridviewAdapter = new GridviewAdapterSearching(Searching.this, CatagotyList);
//                    gridviewAdapter.setMyApp(getApplication());
                    gridview.setExpanded(true);
                    gridview.setAdapter(gridviewAdapter);
                } catch (JSONException e) {
                    Log.d("", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "");
            }
        }) {
        };
        queue.add(sr);
    }
}
