package com.nestedworld.nestedworld.activity.Launch;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nestedworld.nestedworld.activity.Base.BaseActivity;
import com.nestedworld.nestedworld.activity.MainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.fragment.Launch.LaunchFragment;
import com.nestedworld.nestedworld.R;

import butterknife.Bind;

public class LaunchActivity extends BaseActivity {
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_empty;
    }

    @Override
    protected void initVariable(Bundle savedInstanceState) {
        mImageViewBackground.setBackgroundResource(R.drawable.logo);
        mImageViewBackground.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if (!checkForExistingSession()) {
            LaunchFragment.load(this.getSupportFragmentManager());
        }
        else {
            startActivity(MainMenuActivity.class);
        }
    }

    /*
    ** ButterKnife
     */
    @Bind(R.id.default_background)
    ImageView mImageViewBackground;

    /*
    ** Utils
     */
    private boolean checkForExistingSession() {
        //TODO make a real check
        return false;
    }
}

