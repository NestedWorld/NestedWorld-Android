package com.nestedworld.nestedworld.fragment.mainMenu.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.MonsterAdapter;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.monsters.MonstersList;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MonstersFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = MonstersFragment.class.getSimpleName();

    @Bind(R.id.listview_monsters_list)
    ListView listViewMonstersList;

    @Bind(R.id.progressView)
    ProgressView progressView;

    public static void load(final FragmentManager fragmentManager) {
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
    protected void initUI(Bundle savedInstanceState) {

    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {
        progressView.start();

        NestedWorldApi.getInstance(mContext).getMonstersList(
                new Callback<MonstersList>() {
                    @Override
                    public void success(final MonstersList json, Response response) {
                        progressView.stop();

                        final MonsterAdapter adapter = new MonsterAdapter(mContext, json.monsters);
                        // listViewMonstersList = null if we've change view before the end of the request
                        if (listViewMonstersList != null) {
                            listViewMonstersList.setAdapter(adapter);
                            listViewMonstersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    final MonstersList.Monster selectedMonster = json.monsters.get(position);
                                    displayMonsterDetail(selectedMonster, view);
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressView.stop();
                    }
                }
        );

    }

    /*
    ** Utils
     */
    private void displayMonsterDetail(@NonNull MonstersList.Monster monster, @NonNull final View view) {
        PopupWindow popup = new PopupWindow(mContext);
        View layout = ((AppCompatActivity)mContext).getLayoutInflater().inflate(R.layout.fragment_tab_monsters_details, null);
        ((TextView)layout.findViewById(R.id.monsterName)).setText(monster.name);
        popup.setContentView(layout);

        // Set content width and height
        popup.setHeight(LayoutParams.WRAP_CONTENT);
        popup.setWidth(LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.showAsDropDown(view);
    }

}
