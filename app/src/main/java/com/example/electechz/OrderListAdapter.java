package com.example.electechz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.electechz.STORE.Vacancy;
import java.util.ArrayList;

public class OrderListAdapter extends ArrayAdapter{
    private Context context;
    private ArrayList<STORE.Order> orders;

    public OrderListAdapter (Context context, int resID, ArrayList<STORE.Order> orders) {
        super(context, resID, orders);
        this.context = context;
        this.orders = orders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.order_list_item, null);
        }
        else {
            listItemView = convertView;
        }

        STORE.Order p = orders.get(position);

        String productList = "";
        String[] products = p.products.split(";-;-;");

        for(int i = 0; i < products.length; i++)
            productList += products[i].split("\\+")[1] + "\n";

        if(p.status.equals("pending")){
            ((TextView) listItemView.findViewById(R.id.orderStatusPending)).setVisibility(View.VISIBLE);
            ((TextView) listItemView.findViewById(R.id.orderStatusCompleted)).setVisibility(View.GONE);
        }
        else if(p.status.equals("completed")) {
            ((TextView) listItemView.findViewById(R.id.orderStatusPending)).setVisibility(View.GONE);
            ((TextView) listItemView.findViewById(R.id.orderStatusCompleted)).setVisibility(View.VISIBLE);
        }

        ((TextView)listItemView.findViewById(R.id.orderID)).setText("Order ID: " + p.id);
        ((TextView)listItemView.findViewById(R.id.orderUser)).setText("from: " + p.email);
        ((TextView)listItemView.findViewById(R.id.userCC)).setText("Credit Card: " + p.cc);
        ((TextView)listItemView.findViewById(R.id.userExp)).setText("Expiry: " + p.exp);
        ((TextView)listItemView.findViewById(R.id.userCVV)).setText("CVV: " + p.cvv);
        ((TextView)listItemView.findViewById(R.id.productList)).setText(productList);

        return listItemView;
    }
}