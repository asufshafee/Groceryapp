package com.example.geeksera.grocery.Adopters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
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
import com.example.geeksera.grocery.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by GeeksEra on 12/22/2017.
 */
public class Cart_Adapter extends RecyclerView.Adapter<Cart_Adapter.MyViewHolder> {
    Context context;
    MyApp myApp;

    private List<Product> List;
    Float mTotal = 0f, mSubTotal = 0f;
    TextView Total, SubTotal;
    private int StockQty;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView Price;
        TextView Name;
        TextView TotalPrice;
        ImageView Next, Prevoius, Exit;
        TextView Quantity;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            Price = (TextView) view.findViewById(R.id.Price);
            Name = (TextView) view.findViewById(R.id.Productname);
            TotalPrice = (TextView) view.findViewById(R.id.TotalPrice);
            Next = (ImageView) view.findViewById(R.id.Next);
            Prevoius = (ImageView) view.findViewById(R.id.Previous);
            Exit = (ImageView) view.findViewById(R.id.Exit);
            Quantity = (TextView) view.findViewById(R.id.Quantity);
        }
    }

    public Cart_Adapter(Activity mainActivityContacts, List<Product> List, TextView Total, TextView SubTotal) {
        this.List = List;
        myApp = (MyApp) mainActivityContacts.getApplicationContext();
        this.List = myApp.getCart();
        this.context = mainActivityContacts;

        this.SubTotal = SubTotal;
        this.Total = Total;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.Quantity.setText(String.valueOf(List.get(position).getQuantity()));

        Picasso.with(context)
                .load(List.get(position).getImage())
                .into(holder.image);

        Double Price = Double.parseDouble(List.get(position).getPrice());
        holder.Price.setText("$" + String.valueOf(DecimalUtils.round(Price, 2)));
        holder.Name.setText(List.get(position).getName());

        Float TPrie = Float.parseFloat(List.get(position).getPrice()) * Float.parseFloat(String.valueOf(List.get(position).getQuantity()));
        mSubTotal = mSubTotal + TPrie;
        mTotal = mTotal + TPrie;

        SubTotal.setText("$" + String.valueOf(DecimalUtils.round(mSubTotal, 2)));
        Total.setText("$" + String.valueOf(DecimalUtils.round(mTotal, 2)));

        holder.TotalPrice.setText("$" + String.valueOf(DecimalUtils.round(TPrie, 2)));
        holder.Prevoius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(holder.Quantity.getText().toString()) != 1) {
                    myApp.UpdateQuantity(-1, position);

                    mSubTotal = mSubTotal - Float.parseFloat(List.get(position).getPrice());
                    mTotal = mTotal - Float.parseFloat(List.get(position).getPrice());

                    SubTotal.setText("$" + String.valueOf(DecimalUtils.round(mSubTotal, 2)));
                    Total.setText("$" + String.valueOf(DecimalUtils.round(mTotal, 2)));

                    myApp.setSubTotal(mSubTotal);
                    myApp.setTotal(mTotal);
                }

                holder.Quantity.setText(String.valueOf(List.get(position).getQuantity()));
                Float TPrie = Float.parseFloat(List.get(position).getPrice()) * Float.parseFloat(String.valueOf(List.get(position).getQuantity()));
                double f = DecimalUtils.round(Float.parseFloat(List.get(position).getPrice()) * Float.parseFloat(String.valueOf(List.get(position).getQuantity())), 2);
                holder.TotalPrice.setText("$" + f);

            }
        });
        holder.Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(holder.Quantity.getText().toString())<List.get(position).getAvailable_stock_qty()) {
                    myApp.UpdateQuantity(1, position);
                    holder.Quantity.setText(String.valueOf(List.get(position).getQuantity()));
                    Float TPrie = Float.parseFloat(List.get(position).getPrice()) * Float.parseFloat(String.valueOf(List.get(position).getQuantity()));
                    holder.TotalPrice.setText("$" + String.valueOf(DecimalUtils.round(TPrie, 2)));

                    mSubTotal = mSubTotal + Float.parseFloat(List.get(position).getPrice());
                    mTotal = mTotal + Float.parseFloat(List.get(position).getPrice());

                    SubTotal.setText("$" + String.valueOf(DecimalUtils.round(mSubTotal, 2)));
                    Total.setText("$" + String.valueOf(DecimalUtils.round(mTotal, 2)));

                    myApp.setSubTotal(mSubTotal);
                    myApp.setTotal(mTotal);
                } else{
                    LowQuantity(String.valueOf(List.get(position).getAvailable_stock_qty()));
                }
            }
        });

        holder.Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float TPrie = Float.parseFloat(List.get(position).getPrice()) * Float.parseFloat(String.valueOf(List.get(position).getQuantity()));

                mSubTotal = mSubTotal - TPrie;
                mTotal = mTotal - TPrie;

                myApp.setSubTotal(mSubTotal);
                myApp.setTotal(mTotal);

                SubTotal.setText("$" + String.valueOf(DecimalUtils.round(mSubTotal, 2)));
                Total.setText("$" + String.valueOf(DecimalUtils.round(mTotal, 2)));

                myApp.RemoveCart(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public void LowQuantity(String Availablle) {
        final Dialog dialog = new Dialog(context);
        // inflate the layout
        dialog.setContentView(R.layout.item_short_dialog_low);
        // Set the dialog text -- this is better done in the XML

        TextView Message = (TextView) dialog.findViewById(R.id.Message);
        Message.setText("we have only " + Availablle);
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
