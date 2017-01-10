package com.nestedworld.nestedworld.ui.view.connected.fight.battle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;

import butterknife.BindView;

public class BattleResultFragment extends BaseFragment {

    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.textview_result)
    TextView textViewResult;

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void load(@NonNull final FragmentManager fragmentManager,
                            @NonNull final String result) {
        final BattleResultFragment fightResultFragment = new BattleResultFragment();

        final Bundle args = new Bundle();
        args.putString("RESULT", result);

        fightResultFragment.setArguments(args);

        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fightResultFragment)
                .commit();
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_fight_result;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        final String result = getArguments().getString("RESULT");
        textViewResult.setText(result);
    }
}
