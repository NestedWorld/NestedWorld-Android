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
import com.nestedworld.nestedworld.network.http.models.response.users.inventory.InventoryResponse;

public class InventoryObjectAdapter extends ArrayAdapter<InventoryResponse.InventoryObject> {

    @LayoutRes
    private final static int layoutRes = R.layout.item_inventoryobject;

    /*
    ** Constructor
     */
    public InventoryObjectAdapter(@NonNull final Context context) {
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
        InventoryResponse.InventoryObject currentObject = getItem(position);
        if (currentObject == null) {
            return view;
        }

        populateView(view, currentObject);

        return view;
    }

    /*
    ** Internal method
     */
    private void populateView(@NonNull final View view, @NonNull final InventoryResponse.InventoryObject inventoryObject) {
        TextView textViewObjectName = (TextView) view.findViewById(R.id.textview_objectName);
        textViewObjectName.setText(inventoryObject.object);
    }
}