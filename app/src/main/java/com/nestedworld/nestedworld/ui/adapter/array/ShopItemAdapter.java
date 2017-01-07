package com.nestedworld.nestedworld.ui.adapter.array;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.ShopItem;

public class ShopItemAdapter extends ArrayAdapter<ShopItem> {

    @LayoutRes
    private final static int layoutRes = R.layout.item_shop_object;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public ShopItemAdapter(@NonNull final Context context) {
        super(context, layoutRes);
    }

    /*
     * #############################################################################################
     * # ArrayAdapter<ShopItem> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {
        View view = convertView;

        //Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(layoutRes, parent, false);
        }

        //Get current object
        final ShopItem currentObject = getItem(position);
        if (currentObject == null) {
            return view;
        }

        populateView(view, currentObject);

        return view;
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void populateView(@NonNull final View view,
                              @NonNull final ShopItem shopObject) {
        //Retrieve widget
        final TextView textViewObjectName = (TextView) view.findViewById(R.id.textview_object_name);
        final TextView textViewObjectKind = (TextView) view.findViewById(R.id.textview_object_kind);
        final TextView textViewObjectPower = (TextView) view.findViewById(R.id.textview_object_power);
        final TextView textViewObjectPremium = (TextView) view.findViewById(R.id.textview_object_prenium);
        final TextView textViewObjectDescription = (TextView) view.findViewById(R.id.textview_object_description);
        final TextView textViewObjectPrice = (TextView) view.findViewById(R.id.textview_object_price);
        final ImageView imageViewObject = (ImageView) view.findViewById(R.id.imageView_object);

        //Populate widget
        textViewObjectName.setText(shopObject.name);
        textViewObjectDescription.setText(shopObject.description);
        textViewObjectKind.setText("Kind: " + shopObject.kind);
        textViewObjectPower.setText("Power: " + shopObject.power);
        textViewObjectPremium.setText("Is prenium : " + (shopObject.premium ? "yes" : "no"));
        textViewObjectPrice.setText("Price : " + String.valueOf(shopObject.price));
        Glide.with(getContext()).load(shopObject.image).into(imageViewObject);
    }
}