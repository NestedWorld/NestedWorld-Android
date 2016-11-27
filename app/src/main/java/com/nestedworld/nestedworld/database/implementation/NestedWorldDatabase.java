package com.nestedworld.nestedworld.database.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.DaoMaster;
import com.nestedworld.nestedworld.database.models.DaoSession;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

public final class NestedWorldDatabase {
    private final static String TAG = NestedWorldDatabase.class.getSimpleName();

    private static NestedWorldDatabase mSingleton = null;
    private final DaoSession mDaoSession;
    private final DaoMaster.DevOpenHelper mDevOpenHelper;

    /*
    ** Constructor
     */
    private NestedWorldDatabase(@NonNull final Context context) {
        mDevOpenHelper = new DaoMaster.DevOpenHelper(context, "nestedworld.db");
        Database database = mDevOpenHelper.getWritableDb();
        mDaoSession = new DaoMaster(database).newSession();
    }

    /*
    ** Public (static) method
     */
    //Avoid leak
    public static void reset() {
        LogHelper.d(TAG, "stop");

        if (mSingleton == null) {
            throw new IllegalArgumentException("Must call setup() before calling stop()");
        } else {
            mSingleton.clearTables();
        }
    }

    public static void setup(@NonNull final Context context) {
        LogHelper.d(TAG, "setup");

        if (mSingleton == null) {
            mSingleton = new NestedWorldDatabase(context);
        } else {
            throw new IllegalArgumentException("You should call stop() before calling setup() again");
        }
    }

    public static NestedWorldDatabase getInstance() {
        if (mSingleton == null) {
            throw new IllegalArgumentException("You should call setup() before calling getInstance()");
        }
        return mSingleton;
    }

    public static void enableQueryBuilderLog() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /*
    ** Public method
     */
    public void clearTables() {
        for (AbstractDao<?, ?> table : mDaoSession.getAllDaos()) {
            table.deleteAll();
        }
    }

    public DaoSession getDataBase() {
        return mDaoSession;
    }
}
