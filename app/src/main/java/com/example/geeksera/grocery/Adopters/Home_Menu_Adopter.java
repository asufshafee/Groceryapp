package com.example.geeksera.grocery.Adopters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geeksera.grocery.Objects.Catagory;
import com.example.geeksera.grocery.R;
import com.example.geeksera.grocery.Search;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by GeeksEra on 12/22/2017.
 */
public class Home_Menu_Adopter extends RecyclerView.Adapter<Home_Menu_Adopter.MyViewHolder> {
    Context context;

    ArrayList<Catagory> CatagotyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView Price;
        TextView addToCart;

        public MyViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.image);
            Price = (TextView) view.findViewById(R.id.Price);
        }
    }


    public Home_Menu_Adopter(Activity mainActivityContacts, ArrayList<Catagory> List) {
        this.CatagotyList = List;
        this.context = mainActivityContacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_menu_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.Price.setText(CatagotyList.get(position).getPrice());
        Picasso.with(context)
                .load(CatagotyList.get(position).getUrl())
                .into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchProduct(context, CatagotyList.get(position).getPrice());
            }
        });
    }

    @Override
    public int getItemCount() {
        return CatagotyList.size();
    }

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
                        List<String> dataset = new LinkedList<>();
                        if (contacts != null) {

                            for (Catagory a : CatagotyList) {
                                dataset.add(a.getPrice());
                            }
                            Intent intent = new Intent(context, Search.class);
                            intent.putExtra("json", response);
                            intent.putExtra("word", Keyword);
                            intent.putExtra("cat", (Serializable) dataset);
                            context.startActivity(intent);
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

//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
