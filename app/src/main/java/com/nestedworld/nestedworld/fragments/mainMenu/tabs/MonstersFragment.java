package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.models.Monster;
import com.orm.query.Select;

import java.util.List;

import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class MonstersFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = MonstersFragment.class.getSimpleName();

    @Bind(R.id.listview_monsters_list)
    ListView listViewMonstersList;

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
        populateMonsters();
    }

    /*
    ** Utils
     */
    private void populateMonsters() {
        //Retrieve monsters from ORM
        final List<Monster> monsters = Select.from(Monster.class).list();

        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //create adapter
        final MonsterAdapter adapter = new MonsterAdapter(mContext, monsters);

        listViewMonstersList.setAdapter(adapter);
        listViewMonstersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Monster selectedMonster = monsters.get(position);
                populateMonsterDetail(selectedMonster, view);
            }
        });
    }

    private void populateMonsterDetail(@NonNull Monster monster, @NonNull final View view) {

        //Create a popup for displaying monster information
        PopupWindow popup = new PopupWindow(mContext);

        if (mContext == null) {
            return;
        }
        //Create inflater
        View layout = ((AppCompatActivity) mContext).getLayoutInflater().inflate(R.layout.fragment_tab_monsters_details, null);

        //Populate inflater
        ((TextView) layout.findViewById(R.id.textView_monsterName)).setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterName), monster.name));
        ((TextView) layout.findViewById(R.id.textView_monsterAttack)).setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterAttack), monster.attack));
        ((TextView) layout.findViewById(R.id.textView_monsterDefence)).setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterDefence), monster.defense));
        ((TextView) layout.findViewById(R.id.textView_monsterHp)).setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterHp), monster.hp));

        //Populate popup with inflater
        popup.setContentView(layout);

        // Set content width and height
        popup.setHeight(LayoutParams.WRAP_CONTENT);
        popup.setWidth(LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.showAsDropDown(view);
    }

    /**
     * Custom Adapter for displaying monsters
     */
    private class MonsterAdapter extends ArrayAdapter<Monster> {
        /*
        ** Constructor
         */
        public MonsterAdapter(@NonNull Context context, @NonNull List<Monster> objects) {
            super(context, 0, objects);
        }

        /*
        ** Inherit method
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Check if fragment hasn't been detach
            if (mContext == null) {
                return null;
            }

            View view = convertView;

            //Get current monster
            final Monster monster = getItem(position);

            //Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
            }

            //Populate name & lvl
            final TextView textViewName = (TextView) view.findViewById(R.id.textview_monster_name);
            textViewName.setText(monster.name);

            //Display monster picture
            final ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
            Glide.with(getContext())
                    .load(monster.sprite)
                    .placeholder(R.drawable.default_monster)
                    .centerCrop()
                    .into(imageViewMonster);

            //Add color shape around monster picture
            view.findViewById(R.id.imageView_monster_shape).setBackgroundColor(ContextCompat.getColor(mContext, monster.getColorResource()));

            return view;
        }
    }
}
