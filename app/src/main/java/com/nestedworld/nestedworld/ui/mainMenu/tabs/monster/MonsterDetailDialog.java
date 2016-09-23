package com.nestedworld.nestedworld.ui.mainMenu.tabs.monster;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.models.Monster;
import com.orm.query.Condition;
import com.orm.query.Select;

public class MonsterDetailDialog extends DialogFragment {

    private Monster mMonster;

    /*
    ** Static method
     */
    public static MonsterDetailDialog newInstance(@NonNull final Monster monster) {
        MonsterDetailDialog monsterDetailDialog = new MonsterDetailDialog();

        // Supply monsterId as an argument.
        Bundle args = new Bundle();
        args.putLong("monsterId", monster.monster_id);

        monsterDetailDialog.setArguments(args);

        return monsterDetailDialog;
    }

    /*
    ** Life cycle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseArg();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Create view
        View view = inflater.inflate(R.layout.fragment_tab_monsters_details, container, false);

        if (mMonster != null) {
            //Populate title
            getDialog().setTitle(mMonster.name);

            //Populate view
            ((TextView) view.findViewById(R.id.textView_monsterName)).setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterName), mMonster.name));
            ((TextView) view.findViewById(R.id.textView_monsterAttack)).setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterAttack), mMonster.attack));
            ((TextView) view.findViewById(R.id.textView_monsterDefence)).setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterDefence), mMonster.defense));
            ((TextView) view.findViewById(R.id.textView_monsterHp)).setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterHp), mMonster.hp));

            //TODO retrieve monster skill and display them
        } else {
            Toast.makeText(getActivity(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
            getDialog().dismiss();
        }

        return view;
    }

    /*
    ** Internal method
     */
    private void parseArg() {
        if (getArguments().containsKey("monsterId")) {
            long monsterId = getArguments().getLong("monsterId");

            Monster monster = Select.from(Monster.class).where(Condition.prop("monsterid").eq(monsterId)).first();
            if (monster != null) {
                mMonster = monster;
            }
        }
    }

}
