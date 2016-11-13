package com.nestedworld.nestedworld.dialog;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.nestedworld.nestedworld.database.models.Portal;

public class EngagePortalFightDialog extends BaseDialogFragment {

    private final static String TAG = EngagePortalFightDialog.class.getSimpleName();
    private Portal mPortal;

    /*
    ** Constructor
     */
    private EngagePortalFightDialog(@NonNull final Portal portal) {
        mPortal = portal;
    }

    /*
    ** Public method
    */
    public static void show(@NonNull final FragmentManager fragmentManager, @NonNull final Portal portal) {
        new EngagePortalFightDialog(portal).show(fragmentManager, TAG);
    }

    /*
    ** Life cycle
     */
    @Override
    protected Builder build(Builder initialBuilder) {
        return initialBuilder
                .setTitle(mPortal.name)
                .setMessage("Engage a fight with this portal ?")
                .setPositiveButton("yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO display team selection
                    }
                })
                .setNegativeButton("no", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
    }
}
