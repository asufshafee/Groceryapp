package com.example.geeksera.grocery.Grid;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.example.geeksera.grocery.Objects.DecimalUtils;
import com.example.geeksera.grocery.Objects.MyApp;
import com.example.geeksera.grocery.Objects.Product;
import com.example.geeksera.grocery.Product_Details;
import com.example.geeksera.grocery.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class GridviewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> Product;
    private Typeface fonts1, fonts2;

    private FrameLayout frameLayout;
    private ScrollView scrollView;
    private MyApp myApp;
    private int StockQty;

    public GridviewAdapter(Context context, ArrayList<Product> Product, FrameLayout FrameLayout, ScrollView scrollView) {
        this.Product = Product;
        this.context = context;
        this.frameLayout = FrameLayout;
        this.scrollView = scrollView;
    }

    public void setMyApp(Application myApp) {
        this.myApp = (MyApp) myApp;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        fonts1 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Lato-Light.ttf");

        fonts2 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Lato-Regular.ttf");

        ViewHolder viewHolder = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_product_search, null);

            viewHolder = new ViewHolder();

            viewHolder.image1 = (ImageView) convertView.findViewById(R.id.image1);
            viewHolder.Price = (TextView) convertView.findViewById(R.id.Price);
            viewHolder.addToCart = convertView.findViewById(R.id.AddToCatrd);
            viewHolder.COndition = convertView.findViewById(R.id.Condition);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Product CProduct = (Product) getItem(position);


        Double Price = Double.parseDouble(CProduct.getPrice());
        viewHolder.Price.setText("$" + String.valueOf(DecimalUtils.round(Price, 2)));

        Picasso.with(context)
                .load(CProduct.getImage())
                .into(viewHolder.image1);

        if (CProduct.getCondition().toLowerCase().contains("beyond")) {

            viewHolder.COndition.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.badges_n));
        } else if (CProduct.getCondition().toLowerCase().contains("no")) {
            viewHolder.COndition.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.badges_d));

        }


        viewHolder.image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scrollView.fullScroll(ScrollView.FOCUS_UP);


                Fragment fragment = null;
                Class fragmentClass = null;

                fragmentClass = Product_Details.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Product", CProduct);
                    fragment.setArguments(bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.Details, fragment).commit();

                frameLayout.setVisibility(View.VISIBLE);

            }
        });
        viewHolder.addToCart.setTag(CProduct);
        viewHolder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    Product product = (Product) v.getTag();
                    CheckQuantity(product);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView image1;
        TextView Price;
        ImageView COndition;
        public TextView addToCart;
    }

    public void CheckQuantity(final Product product) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/app-checkquantity";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("productId", product.getProductId());
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject Boday = jsonObject.getJSONObject("body");
                        String Status = Boday.getString("availableQuantity");

                        if (Status.equals("0")) {
                            Toast.makeText(context, "No Quantity Available", Toast.LENGTH_LONG).show();
                            OutOFStock();

                            return;
                        } else {
                            StockQty = Integer.parseInt(Status);
                            if (Integer.parseInt(Status) < 1) {
                                LowQuantity(Status);
                                return;
                            } else {
                                product.setAvailable_stock_qty(StockQty);
                                if (myApp.setCart(product)) {
                                    AddToCart(product);
                                    Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "already in cart", Toast.LENGTH_LONG).show();

                                }
                            }
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

    public void AddToCart(Product product) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/app-addtocart";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("productId", product.getProductId());
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject Boday = jsonObject.getJSONObject("body");
//

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

    public void OutOFStock() {
        final Dialog dialog = new Dialog(context);
        // inflate the layout
        dialog.setContentView(R.layout.item_product);
        // Set the dialog text -- this is better done in the XML
        final LinearLayout Click = (LinearLayout) dialog.findViewById(R.id.Click);
        Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.hide();

            }
        });
        dialog.show();
    }

    public void LowQuantity(String Availablle) {
        final Dialog dialog = new Dialog(context);
        // inflate the layout
        dialog.setContentView(R.layout.item_short_dialog_low);
        // Set the dialog text -- this is better done in the XML

        TextView Message = (TextView) dialog.findViewById(R.id.Message);
        Message.setText("We have only " + Availablle);
        final LinearLayout Click = (LinearLayout) dialog.findViewById(R.id.Click);
        Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.hide();

            }
        });
        dialog.show();
    }
}


