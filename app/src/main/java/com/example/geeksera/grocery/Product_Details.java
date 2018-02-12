package com.example.geeksera.grocery;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.geeksera.grocery.Objects.DecimalUtils;
import com.example.geeksera.grocery.Objects.MyApp;
import com.example.geeksera.grocery.Objects.Product;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.UnsupportedEncodingException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Product_Details extends Fragment {
    private static final String TAG = Product_Details.class.getSimpleName();
    Product product;
    int Q = 1;

    MyApp myApp;
    TextView Quantity;
    int StockQty;

    public Product_Details() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_product__details, container, false);

        myApp = (MyApp) getActivity().getApplicationContext();
        Bundle bundle = getArguments();
        product = (Product) bundle.getSerializable("Product");

        ImageView Image = (ImageView) view.findViewById(R.id.image1);
        Picasso.with(getActivity().getApplicationContext())
                .load(product.getImage())
                .into(Image);

        TextView Name = (TextView) view.findViewById(R.id.Name);
        TextView Price = (TextView) view.findViewById(R.id.Price);
        Quantity = (TextView) view.findViewById(R.id.Quantity);
        HtmlTextView Description = view.findViewById(R.id.Description);
        TextView Specification = (TextView) view.findViewById(R.id.Specification);
        TextView Ingradients = (TextView) view.findViewById(R.id.ingradients);
        TextView Warinigs = (TextView) view.findViewById(R.id.Warnings);

        try {
            Description.setHtmlFromString(Html.fromHtml(product.getDescription()).toString(), true);
            Ingradients.setText(product.getIngredients());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        CheckQuantity();

        try {
            JSONObject jsonObject = new JSONObject(product.getSpecification());
            String Weight = jsonObject.getString("weight");
            String length = jsonObject.getString("length");
            String width = jsonObject.getString("width");
            String height = jsonObject.getString("height");
            Specification.setText("weight:" + Weight + "\n"
                    + "length:" + length + "\n"
                    + "width:" + width + "\n"
                    + "height:" + height + "\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Quantity.setText(String.valueOf(product.getQuantity()));
        Name.setText(product.getName());

        Double Price1 = Double.parseDouble(product.getPrice());

        Price.setText("$" + String.valueOf(DecimalUtils.round(Price1, 2)));
        Warinigs.setText(product.getWarnings());

        view.findViewById(R.id.AddToCatrd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setQuantity(Integer.parseInt(Quantity.getText().toString()));
                CheckQuantity();
                view.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.Next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Q++;
                if (Q <= StockQty) {
                    Log.d(TAG, "StockQty " + StockQty + " Q " + Q);
                    product.setQuantity(Q);
                    Quantity.setText(String.valueOf(product.getQuantity()));
                } else {
                    LowQuantity(String.valueOf(Q));
                    // OutOFStock();
                    Q--;
                }
            }
        });
        view.findViewById(R.id.Previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Q != 1) {
                    Q--;
                    product.setQuantity(Q);
                    Quantity.setText(String.valueOf(product.getQuantity()));
                }
            }
        });

        view.findViewById(R.id.CancelDetaild).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
            }
        });
        return view;
    }

    public void CheckQuantity() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
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
                            Toast.makeText(getActivity(), "No Quantity Available", Toast.LENGTH_LONG).show();
                            OutOFStock();

                            return;
                        } else {
                            StockQty = Integer.parseInt(Status);

                            if (StockQty < Integer.parseInt(Quantity.getText().toString())) {
                                LowQuantity(Status);
                                return;
                            } else {
                                product.setAvailable_stock_qty(StockQty);
                                if (myApp.setCart(product)) {
                                    AddToCart();
                                    Toast.makeText(getActivity(), "success", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "already in cart", Toast.LENGTH_LONG).show();
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

    public void AddToCart() {
        if (getActivity() == null)
            return;
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
        final Dialog dialog = new Dialog(getActivity());
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
        final Dialog dialog = new Dialog(getActivity());
        // inflate the layout
        dialog.setContentView(R.layout.item_short_dialog_low);
        // Set the dialog text -- this is better done in the XML

        TextView Message = (TextView) dialog.findViewById(R.id.Message);
        Message.setText("you want only " + Quantity.getText());
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
