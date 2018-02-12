package com.example.geeksera.grocery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.geeksera.grocery.Objects.Catagory;
import com.example.geeksera.grocery.Objects.CompleteOrderBody;
import com.example.geeksera.grocery.Objects.MyApp;
import com.example.geeksera.grocery.Objects.Product;
import com.example.geeksera.grocery.Objects.cartItems;
import com.google.gson.Gson;

import net.authorize.acceptsdk.AcceptSDKApiClient;
import net.authorize.acceptsdk.datamodel.merchant.ClientKeyBasedMerchantAuthentication;
import net.authorize.acceptsdk.datamodel.transaction.CardData;
import net.authorize.acceptsdk.datamodel.transaction.EncryptTransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.TransactionType;
import net.authorize.acceptsdk.datamodel.transaction.callbacks.EncryptTransactionCallback;
import net.authorize.acceptsdk.datamodel.transaction.response.EncryptTransactionResponse;
import net.authorize.acceptsdk.datamodel.transaction.response.ErrorTransactionResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class CheckOut extends AppCompatActivity implements EncryptTransactionCallback {
    String Time, Type;

    AcceptSDKApiClient apiClient;

    private ProgressDialog progressDialog;

    Fragment fragment = null;
    Class fragmentClass = null;
    private final int MIN_CARD_NUMBER_LENGTH = 13;
    private final int MIN_YEAR_LENGTH = 2;
    private final int MIN_CVV_LENGTH = 3;

    ArrayList<Catagory> CatagotyList;

    Stage3 stage3;
    Stage2 stage2;

    int stage = 1;

    TextView Step1, Step2, Step3;
    List<cartItems> cartItems = new ArrayList<>();
    MyApp myApp;

    String Holdername, CCV2, CardNumber, BilingZip, Year, Month;
    private String TAG = CheckOut.class.getSimpleName();

    //walking or driving, default is walking
    private int pickUpMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        LocationICon();
        findViewById(R.id.Home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!this.equals(Home.class)) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                }
            }
        });

        GetCatagories(getApplicationContext());
        findViewById(R.id.Search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Searching.class));

            }
        });

        findViewById(R.id.Location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Pickup.class));
            }
        });


        myApp = (MyApp) getApplicationContext();
        Step1 = (TextView) findViewById(R.id.Step1);
        Step2 = (TextView) findViewById(R.id.Step2);
        Step3 = (TextView) findViewById(R.id.Step3);


        findViewById(R.id.Next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stage != 3) {
                    stage++;
                } else {

                    Holdername = stage3.Holdername.getText().toString();
                    CCV2 = stage3.CCV2.getText().toString();
                    CardNumber = stage3.CardNumber.getText().toString();
                    BilingZip = stage3.BilingZip.getText().toString();
                    Month = String.valueOf(stage3.Year.getSelectedIndex() + 1);
                    if (Month.length() == 1) {
                        Month = "0" + Month;
                    }
                    Year = stage3.Year.getText().toString();
                    if (Holdername.equals("")) {
                        stage3.Holdername.setError("fill this field");
                        return;
                    }
                    if (CCV2.equals("")) {
                        stage3.CCV2.setError("fill this field");
                        return;
                    }
                    if (CardNumber.equals("")) {
                        stage3.CardNumber.setError("fill this field");
                        return;
                    }
                    if (BilingZip.equals("")) {
                        stage3.BilingZip.setError("fill this field");
                        return;
                    }
                    validateFields();
                    if (validateFields() == false) {
                        return;
                    }

                    progressDialog = ProgressDialog.show(CheckOut.this, "Loading..", "Wait");

                    try {
                        apiClient = new AcceptSDKApiClient.Builder(CheckOut.this,
                                AcceptSDKApiClient.Environment.SANDBOX).connectionTimeout(
                                4000) // optional connection time out in milliseconds
                                .build();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    try {
                        EncryptTransactionObject transactionObject = prepareTransactionObject();
                        apiClient.getTokenWithRequest(transactionObject, CheckOut.this);
                    } catch (NullPointerException e) {
                        // Handle exception transactionObject or callback is null.
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        e.printStackTrace();
                    }

                }

                ChangeStageColor();


                if (stage == 1) {
                    findViewById(R.id.Back).setVisibility(View.INVISIBLE);
                    fragmentClass = Stage1.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Contaner, fragment).commit();

                }
                if (stage == 2) {
                    findViewById(R.id.Back).setVisibility(View.VISIBLE);


                    fragmentClass = Stage2.class;
                    try {
                        stage2 = (Stage2) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Contaner, stage2).commit();
                }
                if (stage == 3) {

                    if (stage2.Walk.isChecked()) {
                        Type = "Walking";
                    } else {
                        Type = "Driving";
                    }
                    Time = stage2.Hours.getText().toString() + ":" + stage2.Minites.getText().toString() + " " + stage2.AMPM.getText().toString();

                    if (fragment != null && fragment instanceof Stage2) {
                        pickUpMode = ((Stage2) fragment).Car.isChecked() ? 0 : 1;
                    }
                    fragmentClass = Stage3.class;
                    try {
                        stage3 = (Stage3) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Contaner, stage3).commit();

                }
            }
        });
        findViewById(R.id.Back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stage != 1)
                    stage--;
                ChangeStageColor();
                if (stage == 1) {
                    findViewById(R.id.Back).setVisibility(View.INVISIBLE);

                    fragmentClass = Stage1.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Contaner, fragment).commit();

                }
                if (stage == 2) {

                    findViewById(R.id.Back).setVisibility(View.VISIBLE);

                    fragmentClass = Stage2.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Contaner, fragment).commit();

                }
                if (stage == 3) {
                    fragmentClass = Stage3.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Contaner, fragment).commit();

                }
            }
        });

        fragmentClass = Stage1.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Contaner, fragment).commit();

    }

    private EncryptTransactionObject prepareTransactionObject() {
        ClientKeyBasedMerchantAuthentication merchantAuthentication =
                ClientKeyBasedMerchantAuthentication.
                        createMerchantAuthentication("6AB64hcB", "6gSuV295YD86Mq4d86zEsx8C839uMVVjfXm9N4wr6DRuhTHpDU97NFyKtfZncUq8");

        // create a transaction object by calling the predefined api for creation
        return TransactionObject.
                createTransactionObject(
                        TransactionType.SDK_TRANSACTION_ENCRYPTION) // type of transaction object
                .cardData(prepareCardDataFromFields()) // card data to get Token
                .merchantAuthentication(merchantAuthentication).build();
    }

    private CardData prepareCardDataFromFields() {
        return new CardData.Builder(CardNumber,
                Month, // MM
                Year) // YYYY
                .cvvCode(CCV2) // Optional
                .zipCode(BilingZip)// Optional
                .cardHolderName(Holdername)// Optional
                .build();
    }

    public void ChangeStageColor() {
        if (stage == 1) {
            Step1.setTextColor(getResources().getColor(R.color.colorgreen));
            Step2.setTextColor(Color.GRAY);
            Step3.setTextColor(Color.GRAY);
        }
        if (stage == 2) {
            Step2.setTextColor(getResources().getColor(R.color.colorgreen));
            Step1.setTextColor(Color.GRAY);
            Step3.setTextColor(Color.GRAY);
        }
        if (stage == 3) {
            Step3.setTextColor(getResources().getColor(R.color.colorgreen));
            Step1.setTextColor(Color.GRAY);
            Step2.setTextColor(Color.GRAY);
        }
    }

    public void OrderComplete(final Context context) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = "https://fsc4733as6.execute-api.us-east-1.amazonaws.com/dev/app-placeorder";
            JSONObject jsonBody = new JSONObject();


            for (Product p : myApp.getCart()
                    ) {
                JSONObject cartitemsjson = new JSONObject();
                com.example.geeksera.grocery.Objects.cartItems items = new cartItems();
                cartitemsjson.put("productId", p.getProductId());
                cartitemsjson.put("productName", p.getName());
                cartitemsjson.put("model", p.getModel());
                cartitemsjson.put("quantity", p.getQuantity());
                cartitemsjson.put("price", p.getPrice());

//

                items.setModel(p.getModel());
                items.setPrice(Double.parseDouble(p.getPrice()));
                items.setProductId(p.getProductId());
                items.setProductName(p.getName());
                items.setQuantity(p.getQuantity());
                cartItems.add(items);


            }
            Gson gson = new Gson();
            SharedPreferences prefs = getSharedPreferences("Baskit", MODE_PRIVATE);
            String restoredText = prefs.getString("mNumber", null);
            jsonBody.put("cartitems", gson.toJson(cartItems));

            CompleteOrderBody completeOrderBody = new CompleteOrderBody();
            completeOrderBody.setCartItems(cartItems);
            completeOrderBody.setPhone(restoredText);
            completeOrderBody.setType(Type);
            completeOrderBody.setPickupTime(Time);
            completeOrderBody.setTransactionId(TransactionID);


            final String requestBody = gson.toJson(completeOrderBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject Boday = jsonObject.getJSONObject("body");

                        String OrderID = Boday.getString("orderId");
                        long startTime = Calendar.getInstance().getTimeInMillis();

                        SharedPreferences.Editor editor = getSharedPreferences("Baskit", MODE_PRIVATE).edit();
                        editor.putString("PickupTime", Time);
                        editor.apply();


                        myApp.getCart().clear();
                        Intent intent = new Intent(getApplicationContext(), Pickup.class);
                        intent.putExtra("From", "Checkout");
                        intent.putExtra("OrderID", OrderID);
                        intent.putExtra("startTime", startTime);
                        intent.putExtra("UserType", Type);
                        intent.putExtra("Time", Time);
                        startActivity(intent);
                        //for hangling
                        startService(new Intent(getApplicationContext(),TimeSettings.class));
                        finish();



//                        try {
//                            JSONArray contacts = Boday.getJSONArray("data");
//
//                            for (int i = 0; i < contacts.length(); i++) {
//                                JSONObject c = contacts.getJSONObject(i);
//                                Product product = new Product();
//                                product.setProductId(c.getString("productId"));
//                                product.setImage(c.getString("url"));
//                                product.setPrice(c.getString("price"));
//                                product.setName(c.getString("productName"));
//
//                                ProductList.add(product);
//                            }
//                            gridviewAdapter.notifyDataSetChanged();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }


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

//                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void SearchProduct(Context context, final String Keyword) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                            Intent intent = new Intent(getApplicationContext(), Search.class);
                            intent.putExtra("json", response);
                            intent.putExtra("word", Keyword);
                            intent.putExtra("cat", (Serializable) dataset);
                            startActivity(intent);

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

    String TransactionID;

    @Override
    public void onErrorReceived(ErrorTransactionResponse error) {
        Log.d(TAG, error.getFirstErrorMessage().getMessageText());
        if (progressDialog.isShowing()) progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), error.getMessageList().get(0).getMessageText(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onEncryptionFinished(EncryptTransactionResponse response) {

        if (progressDialog.isShowing()) progressDialog.dismiss();
        TransactionID = response.getDataValue();

        OrderComplete(getApplicationContext());

    }

    private void proceedToPickupLocation() {
        if (pickUpMode == 0) {
            //walking

        } else {
            //driving mode
        }
    }


    private boolean validateFields() {


        Holdername = stage3.Holdername.getText().toString();
        CCV2 = stage3.CCV2.getText().toString();
        CardNumber = stage3.CardNumber.getText().toString();
        BilingZip = stage3.BilingZip.getText().toString();
        Month = String.valueOf(stage3.Year.getSelectedIndex() + 1);
        if (Month.length() == 1) {
            Month = "0" + Month;
        }
        Year = stage3.Year.getText().toString();
        if (stage3.CardNumber.getText().toString().length() < MIN_CARD_NUMBER_LENGTH) {
            stage3.CardNumber.requestFocus();
            stage3.CardNumber.setError("invalid card number");

            return false;
        }
        int monthNum = Integer.parseInt(Month);
        if (monthNum < 1 || monthNum > 12) {
            stage3.Month.setError("invalid");

            return false;
        }

        if (CCV2.length() < MIN_CVV_LENGTH) {
            stage3.CCV2.requestFocus();
            stage3.CCV2.setError("invalid ccv2");

            return false;
        }
        return true;
    }

    public String getMobileNumber() {


        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String strMobileNumber = telephonyManager.getLine1Number();
        return strMobileNumber;
    }

    public void LocationICon() {
        long NowTimeInMiliSecond;
        long StartTimeInMiliSecond;
        long Difference;
        SharedPreferences prefs = getSharedPreferences("Baskit", MODE_PRIVATE);

        String orderID = prefs.getString("OrderID", null);
        if (orderID == null)
            return;
        StartTimeInMiliSecond = prefs.getLong("startTime", 0);
        NowTimeInMiliSecond = Calendar.getInstance().getTimeInMillis();
        Difference = NowTimeInMiliSecond - StartTimeInMiliSecond;
        String Time = getDurationString(Difference);
        if (Integer.parseInt(Time) < 2) {
            ImageView imageView = findViewById(R.id.Location);
            imageView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.greentextcolor));

        } else {
            ImageView imageView = findViewById(R.id.Location);
            imageView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorTex));

        }
    }

    private String getDurationString(long mills) {

        int Hours = 0;
        int Mins = 0;
        int Secs = 0;
        Hours = (int) (mills / (1000 * 60 * 60));
        Mins = (int) (mills / (1000 * 60)) % 60;
        Secs = (int) (mills / 1000) % 60;

        return twoDigitString(Hours);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
