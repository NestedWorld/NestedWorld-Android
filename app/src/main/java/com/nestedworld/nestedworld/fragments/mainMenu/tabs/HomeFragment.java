package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.http.callback.Callback;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.common.User;
import com.nestedworld.nestedworld.api.http.models.response.users.monster.UserMonsterResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;

import java.util.ArrayList;

import butterknife.Bind;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Response;
import retrofit.Retrofit;

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
    @Bind(R.id.gridLayout_home_monsters)
    GridView gridView;

    /*
    ** Public method
     */
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
        populateUserInfo();
        populateMonstersList();
    }

    /*
    ** Utils
     */
    private void populateMonstersList() {
        //TODO afficher un spinner sur la gridview
        if (mContext == null) {
            return;
        }
        NestedWorldHttpApi.getInstance(mContext).getUserMonster(new Callback<UserMonsterResponse>() {
            @Override
            public void onSuccess(Response<UserMonsterResponse> response, Retrofit retrofit) {
                if (gridView != null) {
                    gridView.setAdapter(new UserMonsterAdapter(response.body().monsters));
                }
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<UserMonsterResponse> response) {

            }
        });
    }

    private void populateUserInfo() {

        if (mContext == null) {
            return;
        }

        User user = UserManager.get(mContext).getCurrentUser(mContext);
        if (user == null) {
            return;
        }

        //on affiche les informations de l'utilisateur
        textViewUsername.setText(user.pseudo);
        textViewUserLevel.setText("level 16");//TODO metre les bonnes info
        textViewMonsterSaw.setText("12");
        textViewMonsterCaptured.setText("5");
        textViewAreaCaptured.setText("42");
        textViewAllyOnline.setText("12");

        //On arrondie le placeHolder
        Resources resources = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

        //On affiche l'image de profil de l'utilisateur
        Glide.with(mContext)
                .load(user.avatar)
                .placeholder(roundedBitmapDrawable)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .centerCrop()
                .into(imageViewUser);
    }

    /*
    ** Adapter for userMonsters
     */
    private class UserMonsterAdapter extends BaseAdapter {

        private final ArrayList<UserMonsterResponse.UserMonsters> userMonsters;
        private final RoundedBitmapDrawable defaultMonsterAvatar;

        public UserMonsterAdapter(@NonNull final ArrayList<UserMonsterResponse.UserMonsters> userMonsters) {
            this.userMonsters = userMonsters;

            //On init un placeHolder
            Resources resources = getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_monster);
            defaultMonsterAvatar = RoundedBitmapDrawableFactory.create(resources, bitmap);
            defaultMonsterAvatar.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        }

        @Override
        public int getCount() {
            return userMonsters.size();
        }

        @Override
        public Object getItem(int position) {
            return userMonsters.get(position);
        }

        @Override
        public long getItemId(int position) {
            return userMonsters.get(position).infos.id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Get user
            final UserMonsterResponse.UserMonsters monster = (UserMonsterResponse.UserMonsters) getItem(position);

            //Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
            }

            //Populate the name
            final TextView textviewName = (TextView) convertView.findViewById(R.id.textview_monster_name);
            textviewName.setText(monster.infos.name);

            //On affiche l'image du monstre
            final ImageView imageViewMonster = (ImageView) convertView.findViewById(R.id.imageView_monster);
            Glide.with(getContext())
                    .load(monster.infos.sprite)
                    .placeholder(defaultMonsterAvatar)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .centerCrop()
                    .into(imageViewMonster);

            return convertView;
        }
    }
}