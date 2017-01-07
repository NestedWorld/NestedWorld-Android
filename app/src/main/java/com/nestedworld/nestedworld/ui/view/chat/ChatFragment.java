package com.nestedworld.nestedworld.ui.view.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.friend.Friend;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendDao;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendData;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatFragment extends BaseFragment {

    private ArrayAdapter<String> mItemAdapter;
    private Friend mFriend;

    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.editText_chat)
    EditText editTextChat;
    @BindView(R.id.listView_chat)
    ListView listViewChat;

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void load(@NonNull final FragmentManager fragmentManager,
                            @NonNull final Friend friend) {
        final Bundle bundle = new Bundle();
        bundle.putLong("FRIEND_ID", friend.getId());

        final ChatFragment fragment = new ChatFragment();
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /*
    ** Butterknife callback
     */
    @OnClick(R.id.button_send_message)
    public void sendMessage() {
        //add the text on the adapter
        mItemAdapter.add(editTextChat.getText().toString());

        //update adapter
        mItemAdapter.notifyDataSetChanged();

        //clear editText content
        editTextChat.setText("");
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        //Check if fragment is attach
        if (mContext == null) {
            return;
        }

        //Check args
        final long mFriendId = getArguments().getLong("FRIEND_ID", -1);
        mFriend = NestedWorldDatabase.getInstance()
                .getDataBase()
                .getFriendDao()
                .queryBuilder()
                .where(FriendDao.Properties.Id.eq(mFriendId))
                .unique();
        if (mFriend == null) {
            Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_SHORT).show();
            ((BaseAppCompatActivity) mContext).getSupportFragmentManager().popBackStack();
        } else {
            setupActionBar();
            setUpChat();
        }
    }

    /*
    ** Internal method
     */
    private void setupActionBar() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        final ActionBar actionBar = ((BaseAppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            FriendData friendInfo = mFriend.getData();
            if (friendInfo != null) {
                actionBar.setTitle(friendInfo.pseudo);
            } else {
                actionBar.setTitle(getResources().getString(R.string.chat_title));
            }
        }
    }

    private void setUpChat() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //init a string adapter for our listView
        mItemAdapter = new ArrayAdapter<>(mContext, R.layout.item_discution, R.id.textview_discution_content);
        listViewChat.setAdapter(mItemAdapter);

        mItemAdapter.add("Welcome on the chat: " + mFriend.getData().pseudo);

        editTextChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
    }
}
