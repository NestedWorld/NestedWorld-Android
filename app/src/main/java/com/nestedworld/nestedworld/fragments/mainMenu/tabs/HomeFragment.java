package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.registration.RegistrationActivity;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helpers.session.SessionManager;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.models.UserMonster;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.orm.query.Select;

import java.util.List;

import butterknife.Bind;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
    ** Private method
     */
    private void populateMonstersList() {
        if (mContext == null) {
            return;
        }

        List<UserMonster> monsters = Select.from(UserMonster.class).list();
        gridView.setAdapter(new UserMonsterAdapter(monsters));
    }

    private void populateUserInfo() {
        //Retrieve the session
        Session session = SessionManager.get().getSession();
        if (session == null) {
            onFatalError();
            return;
        }

        //Retrieve the user
        User user = session.getUser();
        if (user == null) {
            onFatalError();
            return;
        }

        //Display user information
        textViewUsername.setText(user.pseudo);
        textViewUserLevel.setText("level 16");//TODO metre les bonnes info
        textViewMonsterSaw.setText("12");
        textViewMonsterCaptured.setText("5");
        textViewAreaCaptured.setText("42");
        textViewAllyOnline.setText("12");

        //Make placeHolder rounded
        Resources resources = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

        //Display user picture
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

        private final List<UserMonster> userMonsters;
        private final RoundedBitmapDrawable defaultMonsterAvatar;

        public UserMonsterAdapter(@NonNull final List<UserMonster> userMonsters) {
            this.userMonsters = userMonsters;

            //Init placeHolder
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
            return userMonsters.get(position).info().monster_id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;

            //Get user
            final UserMonster monster = (UserMonster) getItem(position);

            //Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
            }

            //Populate the name
            final TextView textviewName = (TextView) view.findViewById(R.id.textview_monster_name);
            textviewName.setText(monster.info().name);

            //Display monster picture
            final ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
            Glide.with(getContext())
                    .load(monster.info().sprite)
                    .placeholder(defaultMonsterAvatar)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .centerCrop()
                    .into(imageViewMonster);

            return view;
        }
    }
}