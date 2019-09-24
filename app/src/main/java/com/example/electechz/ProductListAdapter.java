package com.example.electechz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.electechz.STORE.Product;
import java.util.ArrayList;


public class ProductListAdapter extends ArrayAdapter{
    private Context context;
    private ArrayList<Product> products;

    public ProductListAdapter(Context context, int resID, ArrayList<Product> products) {
        super(context, resID, products);
        this.context = context;
        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.product_list_item, null);
        }
        else {
            listItemView = convertView;
        }

        Product p = products.get(position);

        ((TextView)listItemView.findViewById(R.id.productName)).setText(p.name);
        ((TextView)listItemView.findViewById(R.id.productDescription)).setText(p.description);
        ((TextView)listItemView.findViewById(R.id.productCategory)).setText("in " + p.category);

        if(p.cartmode)
            ((TextView)listItemView.findViewById(R.id.productPriceAndQuantity)).setText("$" + p.price);
        else
            ((TextView)listItemView.findViewById(R.id.productPriceAndQuantity)).setText("$" + p.price + " (" + p.quantity + " left)");

        if(!p.filename.equals("default"))
            Glide.with(getContext()).load(STORE.BASE_URL + "images/" + p.filename + ".jpg").thumbnail(0.1f).into((ImageView)listItemView.findViewById(R.id.productImage));
        else
            ((ImageView)listItemView.findViewById(R.id.productImage)).setImageResource(R.mipmap.defaultpicture);


        return listItemView;
    }
}