package com.example.geeksera.grocery.Grid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.geeksera.grocery.Objects.DecimalUtils;
import com.example.geeksera.grocery.Objects.MyApp;
import com.example.geeksera.grocery.Objects.Product;
import com.example.geeksera.grocery.Product_Details;
import com.example.geeksera.grocery.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridviewAdapterSearch extends BaseAdapter {

    Context context;

    ArrayList<Product> Product;
    Typeface fonts1, fonts2;
    MyApp myApp;

    FrameLayout frameLayout;
    ScrollView scrollView;

    public GridviewAdapterSearch(Context context, ArrayList<Product> Product, FrameLayout FrameLayout, ScrollView scrollView) {
        this.Product = Product;
        this.context = context;
        myApp = (MyApp) context.getApplicationContext();

        this.frameLayout = FrameLayout;
        this.scrollView = scrollView;
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
            convertView = layoutInflater.inflate(R.layout.item_product_search, null);

            viewHolder = new ViewHolder();

            viewHolder.image1 = (ImageView) convertView.findViewById(R.id.image1);
            viewHolder.Price = (TextView) convertView.findViewById(R.id.Price);
            viewHolder.COndition = convertView.findViewById(R.id.Condition);

            viewHolder.addtocart = (TextView) convertView.findViewById(R.id.AddToCatrd);

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


        viewHolder.addtocart.setOnClickListener(new View.OnClickListener() {
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

        return convertView;
    }

    private class ViewHolder {
        ImageView image1;
        TextView Price;
        TextView addtocart;
        ImageView COndition;
    }
}


