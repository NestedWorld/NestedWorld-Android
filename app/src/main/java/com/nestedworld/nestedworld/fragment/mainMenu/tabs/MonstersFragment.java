package com.nestedworld.nestedworld.fragment.mainMenu.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.Monster;
import com.nestedworld.nestedworld.api.models.apiResponse.monsters.MonstersResponse;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

import butterknife.Bind;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class MonstersFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = MonstersFragment.class.getSimpleName();

    @Bind(R.id.listview_monsters_list)
    ListView listViewMonstersList;

    @Bind(R.id.progressView)
    ProgressView progressView;

    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new MonstersFragment());
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_monsters;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        if (progressView != null) {
            progressView.start();
        }

        if (mContext == null)
            return;
        NestedWorldApi.getInstance(mContext).getMonsters(
                new com.nestedworld.nestedworld.api.callback.Callback<MonstersResponse>() {
                    @Override
                    public void onSuccess(final Response<MonstersResponse> response, Retrofit retrofit) {
                        if (progressView != null) {
                            progressView.stop();
                        }

                        final MonsterAdapter adapter = new MonsterAdapter(mContext, response.body().monsters);
                        // listViewMonstersList = null if we've change view before the end of the request
                        if (listViewMonstersList != null) {
                            listViewMonstersList.setAdapter(adapter);
                            listViewMonstersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    final Monster selectedMonster = response.body().monsters.get(position);
                                    displayMonsterDetail(selectedMonster, view);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<MonstersResponse> response) {
                        if (progressView != null) {
                            progressView.stop();
                        }

                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_cant_get_monsters_list), response);
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /*
    ** Utils
     */
    private void displayMonsterDetail(@NonNull Monster monster, @NonNull final View view) {

        PopupWindow popup = new PopupWindow(mContext);
        if (mContext == null)
            return;
        View layout = ((AppCompatActivity) mContext).getLayoutInflater().inflate(R.layout.fragment_tab_monsters_details, null);

        // Populate the popup
        ((TextView) layout.findViewById(R.id.monsterName)).setText(monster.name);
        ((TextView) layout.findViewById(R.id.monsterAtk)).setText(monster.attack);
        ((TextView) layout.findViewById(R.id.monsterDefense)).setText(monster.defense);
        ((TextView) layout.findViewById(R.id.monsterHp)).setText(monster.hp);

        popup.setContentView(layout);

        // Set content width and height
        popup.setHeight(LayoutParams.WRAP_CONTENT);
        popup.setWidth(LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.showAsDropDown(view);
    }

    /*
    ** Custom Adapter for displayin monsters
     */
    private class MonsterAdapter extends ArrayAdapter<Monster> {
        /*
        ** Constructor
         */
        public MonsterAdapter(@NonNull Context context, @NonNull ArrayList<Monster> objects) {
            super(context, 0, objects);
        }

        /*
        ** Inherit method
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Get user
            final Monster monster = getItem(position);

            //Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
            }

            //Populate the name
            final TextView textviewName = (TextView) convertView.findViewById(R.id.textview_monster_name);
            textviewName.setText(monster.name);

            //TODO utiliser la bonne image
            final ImageView imageViewMonster = (ImageView) convertView.findViewById(R.id.imageView_monster);
            Glide.with(getContext())
                    .load(R.drawable.default_monster)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(imageViewMonster) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imageViewMonster.setImageDrawable(circularBitmapDrawable);
                        }
                    });

            return convertView;
        }
    }
}
