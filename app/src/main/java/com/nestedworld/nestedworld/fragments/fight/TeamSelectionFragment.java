package com.nestedworld.nestedworld.fragments.fight;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.customView.viewpager.ViewPagerWithIndicator;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.models.UserMonster;
import com.orm.query.Select;

import java.util.List;

import butterknife.Bind;

public class TeamSelectionFragment extends BaseFragment {

    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.ViewPagerArrowIndicator)
    ViewPagerWithIndicator viewPagerArrowIndicator;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new TeamSelectionFragment());
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_team_selection;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        changeActionBarName();
        populateUserMonster();
    }

    /*
    ** Utils
     */
    private void changeActionBarName() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        if (mContext instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
            if (actionBar != null) {
                //TODO replace static string by R reference
                actionBar.setTitle("Team selection");
            }
        }
    }

    private void populateUserMonster() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        List<UserMonster> userMonsters = Select.from(UserMonster.class).list();

        UserMonsterPagerAdapter monsterPagerAdapter = new UserMonsterPagerAdapter(mContext, userMonsters);
        viewPager.setAdapter(monsterPagerAdapter);
        viewPagerArrowIndicator.setViewPager(viewPager);
    }

    /**
     * Simple pager Adapter for displaying our monster
     */
    private static class UserMonsterPagerAdapter extends PagerAdapter {

        private final List<UserMonster> mUserMonsters;
        private final Context mContext;

        public UserMonsterPagerAdapter(@NonNull Context context, @NonNull List<UserMonster> userMonsters) {
            mUserMonsters = userMonsters;
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //Create the view
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_monster, container, false);

            //Retrieve the monster we'll display
            UserMonster monster = mUserMonsters.get(position);

            //Complete the view with the monster name
            ((TextView) view.findViewById(R.id.textview_monster_name)).setText(monster.info().name);

            //Complete the view with the monster picture
            //TODO use good image
            final ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
            Glide.with(mContext)
                    .load(R.drawable.default_monster)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(imageViewMonster) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imageViewMonster.setImageDrawable(circularBitmapDrawable);
                        }
                    });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mUserMonsters.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mUserMonsters.get(position).info().name;
        }
    }
}
