package com.nestedworld.nestedworld.fragment.mainMenu.tabs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = HomeFragment.class.getSimpleName();

    @Bind(R.id.textView_username)
    TextView textViewUsername;
    @Bind(R.id.textView_userLevel)
    TextView textViewUserLevel;
    @Bind(R.id.textView_monsterSaw)
    TextView textViewMonsterSaw;
    @Bind(R.id.textView_monsterCaptured)
    TextView textViewMonsterCaptured;
    @Bind(R.id.textView_areaCaptured)
    TextView textViewAreaCaptured;
    @Bind(R.id.textView_allyOnline)
    TextView textViewAllyOnline;
    @Bind(R.id.imageView_user)
    ImageView imageViewUser;

    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new HomeFragment());
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_home;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        //on affiche les informations de l'utilisateur
        //TODO metre les bonnes info
        textViewUsername.setText(UserManager.get(mContext).getCurrentAccountName());
        textViewUserLevel.setText("level 16");
        textViewMonsterSaw.setText("12");
        textViewMonsterCaptured.setText("5");
        textViewAreaCaptured.setText("42");
        textViewAllyOnline.setText("12");

        //On affiche l'image de profil de l'utilisateur
        //TODO récupérer l'image de profil de l'utilisateur
        Glide.with(mContext)
                .load(R.drawable.default_avatar)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(imageViewUser) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageViewUser.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }
}
