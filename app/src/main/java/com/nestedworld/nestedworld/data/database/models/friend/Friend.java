package com.nestedworld.nestedworld.data.database.models.friend;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.nestedworld.nestedworld.data.database.models.DaoSession;

@Entity(active = true)
public class Friend {
    @Expose
    @SerializedName("user")
    @Transient
    public FriendData friendData;

    @Id(autoincrement = true)
    @Unique
    private Long id;

    @Unique
    public long friendDataIdFk;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 76285035)
    private transient FriendDao myDao;


    @Generated(hash = 287143722)
    public Friend() {
    }

    @Generated(hash = 1376049821)
    public Friend(Long id, long friendDataIdFk) {
        this.id = id;
        this.friendDataIdFk = friendDataIdFk;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1516049992)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFriendDao() : null;
    }

    @Nullable
    public FriendData getData() {
        return daoSession
                .getFriendDataDao()
                .queryBuilder()
                .where(FriendDataDao.Properties.PlayerId.eq(friendDataIdFk))
                .unique();
    }

    public long getFriendDataIdFk() {
        return this.friendDataIdFk;
    }

    public void setFriendDataIdFk(long friendDataIdFk) {
        this.friendDataIdFk = friendDataIdFk;
    }
}
