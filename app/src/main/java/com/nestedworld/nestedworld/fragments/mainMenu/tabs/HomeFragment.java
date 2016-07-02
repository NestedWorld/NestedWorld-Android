package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionManager;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.models.UserMonster;
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
    @Bind(R.id.textView_creditsNumber)
    TextView textViewCreditsNumber;
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
        List<UserMonster> monsters = Select.from(UserMonster.class).list();
        gridView.setAdapter(new UserMonsterAdapter(monsters));
    }

    private void populateUserInfo() {
        //Retrieve the session
        Session session = SessionManager.get().getSession();
        if (session == null) {
            LogHelper.d(TAG, "No Session");
            onFatalError();
            return;
        }

        //Retrieve the user
        User user = session.getUser();
        if (user == null) {
            LogHelper.d(TAG, "No User");
            onFatalError();
            return;
        }

        //Display user information
        textViewUsername.setText(user.pseudo);
        textViewUserLevel.setText("lvl " + user.level);//TODO dynamic xml reference
        textViewMonsterCaptured.setText("" + Select.from(UserMonster.class).list().size());
        textViewCreditsNumber.setText("12");//TODO display correct information
        textViewAreaCaptured.setText("42");//TODO display correct information
        textViewAllyOnline.setText("12");//TODO display correct information (check ally status)

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

        public UserMonsterAdapter(@NonNull final List<UserMonster> userMonsters) {
            this.userMonsters = userMonsters;
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

            //Check if fragment hasn't been detach
            if (mContext == null) {
                return null;
            }

            View view = convertView;

            //Get user
            final UserMonster monster = (UserMonster) getItem(position);

            //Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
            }

            //Populate name & lvl
            final TextView textviewName = (TextView) view.findViewById(R.id.textview_monster_name);
            final TextView textViewLvl = (TextView) view.findViewById(R.id.textview_monster_description);

            //TODO use string from xml
            textviewName.setText(monster.info().name);
            textViewLvl.setText("lvl " + monster.level);


            //Display monster picture
            final ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);;
            Glide.with(getContext())
                    .load(monster.info().sprite)
                    .placeholder(R.drawable.default_monster)
                    .centerCrop()
                    .into(imageViewMonster);

            /*Add color shape around monster picture*/
            final LinearLayout linearLayoutShape = (LinearLayout) view.findViewById(R.id.imageView_monster_shape);

            if (monster.info().type == null) {
                linearLayoutShape.setBackgroundColor(ContextCompat.getColor(mContext, R.color.apptheme_color));
            }
            else {
                switch (monster.info().type) {
                    case "water":
                        linearLayoutShape.setBackgroundColor(ContextCompat.getColor(mContext, R.color.holo_blue_light));
                        break;
                    case "fire":
                        linearLayoutShape.setBackgroundColor(ContextCompat.getColor(mContext, R.color.holo_red_light));
                        break;
                    case "earth":
                        linearLayoutShape.setBackgroundColor(ContextCompat.getColor(mContext, R.color.DarkKhaki));
                        break;
                    case "electric":
                        linearLayoutShape.setBackgroundColor(ContextCompat.getColor(mContext, R.color.holo_orange_light));
                        break;
                    case "plant":
                        linearLayoutShape.setBackgroundColor(ContextCompat.getColor(mContext, R.color.holo_green_light));
                        break;
                    default:
                        linearLayoutShape.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
                        break;
                }
            }

            return view;
        }
    }
}