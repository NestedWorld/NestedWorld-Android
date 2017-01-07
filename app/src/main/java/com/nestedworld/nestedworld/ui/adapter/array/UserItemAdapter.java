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
import com.nestedworld.nestedworld.data.database.entities.UserItem;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserItemAdapter extends ArrayAdapter<UserItem> {

    @LayoutRes
    private final static int layoutRes = R.layout.item_inventoryobject;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public UserItemAdapter(@NonNull final Context context) {
        super(context, layoutRes);
    }

    /*
     * #############################################################################################
     * # ArrayAdapter<UserItem> implementation
     * #############################################################################################
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
        final UserItem currentObject = getItem(position);
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
                              @NonNull final UserItem userItem) {
        final ShopItem shopItem = userItem.getShopItem();
        final Context context = getContext();

        if (shopItem != null) {
            //Retrieve widget
            final TextView textViewObjectName = (TextView) view.findViewById(R.id.textview_object_name);
            final TextView textViewObjectDescription = (TextView) view.findViewById(R.id.textview_object_description);
            final ImageView imageViewObject = (ImageView) view.findViewById(R.id.imageView_object);

            //Populate widget
            textViewObjectName.setText(shopItem.name);
            textViewObjectDescription.setText(shopItem.description);
            Glide.with(context)
                    .load(shopItem.image)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(imageViewObject);
        }
    }
}