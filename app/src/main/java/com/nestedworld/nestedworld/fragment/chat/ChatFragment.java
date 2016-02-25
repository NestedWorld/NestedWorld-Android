package com.nestedworld.nestedworld.fragment.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.models.apiResponse.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import butterknife.Bind;

public class ChatFragment extends BaseFragment {

    private static FriendsResponse.Friend mFriend;
    private ArrayAdapter<String> itemAdapter;

    @Bind(R.id.editText_chat)
    EditText editTextChat;

    @Bind(R.id.listView_chat)
    ListView listViewChat;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, @NonNull final FriendsResponse.Friend friend) {
        //TODO avoid leak on static reference
        mFriend = friend;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ChatFragment());
        fragmentTransaction.commit();
    }


    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        initActionBar();
        initChat();
    }

    /*
    ** Utils
     */
    private void initActionBar() {
        if (mContext == null)
            return;
        ActionBar actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mFriend.info.pseudo);
        }
    }

    private void initChat() {
        //init a string adapter for our listView
        itemAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_expandable_list_item_1);
        listViewChat.setAdapter(itemAdapter);

        editTextChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {

                    //add the text on the adapter
                    itemAdapter.add(editTextChat.getText().toString());

                    //update adapter
                    itemAdapter.notifyDataSetChanged();

                    //clear editText content
                    editTextChat.setText("");

                    return true;
                }
                return false;
            }
        });
    }
}
