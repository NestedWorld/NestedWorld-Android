package com.nestedworld.nestedworld.adapter.ArrayAdapter;

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
import com.nestedworld.nestedworld.database.models.ShopItem;
import com.nestedworld.nestedworld.database.models.UserItem;

public class UserItemAdapter extends ArrayAdapter<UserItem> {

    @LayoutRes
    private final static int layoutRes = R.layout.item_inventoryobject;

    /*
    ** Constructor
     */
    public UserItemAdapter(@NonNull final Context context) {
        super(context, layoutRes);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        //Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(layoutRes, parent, false);
        }

        //Get current object
        UserItem currentObject = getItem(position);
        if (currentObject == null) {
            return view;
        }

        populateView(view, currentObject);

        return view;
    }

    /*
    ** Internal method
     */
    private void populateView(@NonNull final View view, @NonNull final UserItem userItem) {
        ShopItem shopItem = userItem.infos();
        if (shopItem != null) {
            //Retrieve widget
            TextView textViewObjectName = (TextView) view.findViewById(R.id.textview_object_name);
            TextView textViewObjectKind = (TextView) view.findViewById(R.id.textview_object_kind);
            TextView textViewObjectPower = (TextView) view.findViewById(R.id.textview_object_power);
            TextView textViewObjectDescription = (TextView) view.findViewById(R.id.textview_object_description);
            ImageView imageViewObject = (ImageView) view.findViewById(R.id.imageView_object);

            //Populate widget
            textViewObjectName.setText("Name: " + shopItem.name);
            textViewObjectKind.setText("Kind: " + shopItem.kind);
            textViewObjectPower.setText("Power: " + shopItem.power);
            textViewObjectDescription.setText("Description : " + shopItem.description);
            Glide.with(getContext()).load(shopItem.image).into(imageViewObject);
        }
    }
}