package com.nestedworld.nestedworld.ui.dialog;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.nestedworld.nestedworld.data.database.entities.Portal;

public class EngagePortalFightDialog extends BaseDialogFragment {

    private final static String TAG = EngagePortalFightDialog.class.getSimpleName();

    /*
     * #############################################################################################
     * # Private field
     * #############################################################################################
     */
    private Portal mPortal;

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void show(@NonNull final FragmentManager fragmentManager,
                            @NonNull final Portal portal) {
        new EngagePortalFightDialog()
                .setPortal(portal)
                .show(fragmentManager, TAG);
    }

    /*
     * #############################################################################################
     * # BaseDialogFragment implementation
     * #############################################################################################
     */
    @Override
    protected Builder build(Builder initialBuilder) {
        if (mPortal != null) {
            initialBuilder
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

        } else {
            dismiss();
        }
        return initialBuilder;
    }

    /*
     * #############################################################################################
     * # Private method
     * #############################################################################################
     */
    @NonNull
    private EngagePortalFightDialog setPortal(@NonNull final Portal portal) {
        mPortal = portal;
        return this;
    }
}
