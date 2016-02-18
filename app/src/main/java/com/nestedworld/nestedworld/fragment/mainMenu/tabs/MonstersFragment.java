package com.nestedworld.nestedworld.fragment.mainMenu.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.MonsterAdapter;
import com.nestedworld.nestedworld.api.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.monsters.MonstersResponse;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.rey.material.widget.ProgressView;

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

        NestedWorldApi.getInstance(mContext).getMonstersList(
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
                                    final MonstersResponse.Monster selectedMonster = response.body().monsters.get(position);
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
    private void displayMonsterDetail(@NonNull MonstersResponse.Monster monster, @NonNull final View view) {

        PopupWindow popup = new PopupWindow(mContext);
        View layout = ((AppCompatActivity) mContext).getLayoutInflater().inflate(R.layout.fragment_tab_monsters_details, null);

        // Populate the popup
        //TODO transformer les string static en référence vers R
        ((TextView) layout.findViewById(R.id.monsterName)).setText("Nom : " + monster.name);
        ((TextView) layout.findViewById(R.id.monsterAtk)).setText("Attaque : " + monster.attack);
        ((TextView) layout.findViewById(R.id.monsterDefense)).setText("Defence : " + monster.defense);
        ((TextView) layout.findViewById(R.id.monsterHp)).setText("Pv : " + monster.hp);


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
