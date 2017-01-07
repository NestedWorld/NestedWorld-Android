package com.nestedworld.nestedworld.data.database.entities.friend;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.entities.DaoSession;
import com.nestedworld.nestedworld.data.database.entities.base.BaseEntity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

@Entity(active = true)
public class Friend extends BaseEntity{
    @Expose
    @SerializedName("user")
    @Transient
    public FriendData friendData;
    @Unique
    public long friendDataIdFk;
    @Id(autoincrement = true)
    @Unique
    private Long id;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 76285035)
    private transient FriendDao myDao;


    @Generated(hash = 287143722)
    public Friend() {
    }

    @Generated(hash = 331711400)
    public Friend(long friendDataIdFk, Long id) {
        this.friendDataIdFk = friendDataIdFk;
        this.id = id;
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

    /**
     * called by internal mechanisms, do not call yourself.
     */
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
