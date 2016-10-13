package com.nestedworld.nestedworld.ui.fight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.base.BaseFragment;

import butterknife.BindView;

public class FightResultFragment extends BaseFragment {

    @BindView(R.id.textview_result)
    TextView textViewResult;

    /*
    ** public static method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, @NonNull final String result) {
        FightResultFragment fightResultFragment = new FightResultFragment();

        Bundle args = new Bundle();
        args.putString("RESULT", result);

        fightResultFragment.setArguments(args);

        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fightResultFragment)
                .commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_fight_result;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        String result = getArguments().getString("RESULT");
        textViewResult.setText(result);
    }
}
