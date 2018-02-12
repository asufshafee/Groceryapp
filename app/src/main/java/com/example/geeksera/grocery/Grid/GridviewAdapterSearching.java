package com.example.geeksera.grocery.Grid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geeksera.grocery.Adopters.Home_Menu_Adopter;
import com.example.geeksera.grocery.Home;
import com.example.geeksera.grocery.Objects.Catagory;
import com.example.geeksera.grocery.Objects.DecimalUtils;
import com.example.geeksera.grocery.Objects.MyApp;
import com.example.geeksera.grocery.Objects.Product;
import com.example.geeksera.grocery.Product_Details;
import com.example.geeksera.grocery.R;
import com.example.geeksera.grocery.Search;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class GridviewAdapterSearching extends BaseAdapter {

    Context context;

    ArrayList<Catagory> Product;
    Typeface fonts1, fonts2;
    MyApp myApp;

    ArrayList<Catagory> CatagotyList;


    public GridviewAdapterSearching(Activity context, ArrayList<Catagory> Catagoty ) {
        this.Product = Catagoty;
        this.context = context;
        myApp = (MyApp) context.getApplicationContext();

    }

    @Override
    public int getCount() {
        return Product.size();
    }

    @Override
    public Object getItem(int position) {
        return Product.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        fonts1 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Lato-Light.ttf");

        fonts2 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Lato-Regular.ttf");

        ViewHolder viewHolder = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_searching, null);

            viewHolder = new ViewHolder();

            viewHolder.image1 = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.Price = (TextView) convertView.findViewById(R.id.Price);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final Catagory CProduct = (Catagory) getItem(position);


        viewHolder.Price.setText(CProduct.getPrice());
        Picasso.with(context)
                .load(CProduct.getUrl())
                .into(viewHolder.image1);



        viewHolder.image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SearchProduct(context,CProduct.getPrice());

            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView image1;
        TextView Price;
    }


    List<String> dataset=new ArrayList<>();
    public void SearchProduct(final Context context, final String Keyword) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/app-searchproduct";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("keyword", Keyword);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject Boday = jsonObject.getJSONObject("body");
                        JSONArray contacts = Boday.getJSONArray("data");
                        if (contacts != null) {

                            for (Catagory a : Product) {
                                dataset.add(a.getPrice());
                            }
                            Intent intent = new Intent(context, Search.class);
                            intent.putExtra("json", response);
                            intent.putExtra("word", Keyword);
                            intent.putExtra("cat", (Serializable) dataset);
                            context.startActivity(intent);

                        } else {
                            Toast.makeText(context, "no search found", Toast.LENGTH_LONG).show();
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void GetCatagories(Context context) {

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

                } catch (JSONException e) {
                    Log.d("", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
        };
        queue.add(sr);
    }
}


