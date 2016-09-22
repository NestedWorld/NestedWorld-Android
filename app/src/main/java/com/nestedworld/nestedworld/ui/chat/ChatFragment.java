package com.nestedworld.nestedworld.ui.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.models.User;

import butterknife.Bind;
import butterknife.OnClick;

public class ChatFragment extends BaseFragment {

    private static Friend mFriend;
    @Bind(R.id.editText_chat)
    EditText editTextChat;
    @Bind(R.id.listView_chat)
    ListView listViewChat;
    private ArrayAdapter<String> itemAdapter;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, @NonNull final Friend friend) {
        Bundle bundle = new Bundle();
        bundle.putLong("FRIEND_ID", friend.getId());

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
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

        mFriend = Friend.findById(Friend.class, getArguments().getLong("FRIEND_ID"));

        initActionBar();
        initChat();
    }

    /*
    ** Butterknife callback
     */
    @OnClick(R.id.button_send_message)
    public void sendMessage() {
        //add the text on the adapter
        itemAdapter.add(editTextChat.getText().toString());

        //update adapter
        itemAdapter.notifyDataSetChanged();

        //clear editText content
        editTextChat.setText("");
    }

    /*
    ** Utils
     */
    private void initActionBar() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ActionBar actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            User friendInfo = mFriend.info;
            if (friendInfo != null) {
                actionBar.setTitle(friendInfo.pseudo);
            } else {
                actionBar.setTitle(getResources().getString(R.string.chat_title));
            }
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

                    sendMessage();

                    return true;
                }
                return false;
            }
        });
    }
}
