package com.nestedworld.nestedworld.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
            TextView textViewObjectName = (TextView) view.findViewById(R.id.textview_objectName);
            textViewObjectName.setText(shopItem.name);
        }
    }
}