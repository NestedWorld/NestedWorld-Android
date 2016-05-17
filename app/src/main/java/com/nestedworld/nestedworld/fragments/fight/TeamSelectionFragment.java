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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.models.Monster;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class TeamSelectionFragment extends BaseFragment {

    @Bind(R.id.viewpager)
    ViewPager viewPager;

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
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ArrayList<Monster> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Monster monster = new Monster();
            monster.name = getString(R.string.app_name) + i;

            list.add(monster);
        }

        MonsterPagerAdapter monsterPagerAdapter = new MonsterPagerAdapter(mContext, list);
        viewPager.setAdapter(monsterPagerAdapter);
    }

    /**
     * Simple pager Adapter
     */
    private static class MonsterPagerAdapter extends PagerAdapter {

        private final String TAG = MonsterPagerAdapter.class.getSimpleName();
        private final List<Monster> mMonsters;
        private final Context mContext;

        public MonsterPagerAdapter(@NonNull Context context, @NonNull ArrayList<Monster> monsters) {
            mMonsters = monsters;
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //Create the view
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_monster, container, false);

            //Retrieve the monster we'll display
            Monster monster = mMonsters.get(position);

            //Complete the view with the monster name
            ((TextView) view.findViewById(R.id.textview_monster_name)).setText(monster.name);

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
            return mMonsters.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mMonsters.get(position).name;
        }
    }

}
