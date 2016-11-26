package com.nestedworld.nestedworld.database.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.database.implementation.NestedWorldDatabase;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

@Entity(active = true)
public class Friend {
    @Id(autoincrement = true)
    @Unique
    private Long id;

    @Unique
    private long playerId;

    @Expose
    @SerializedName("user")
    @Transient
    public Player player;

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

    @Generated(hash = 787398198)
    public Friend(Long id, long playerId) {
        this.id = id;
        this.playerId = playerId;
    }

    @Generated(hash = 287143722)
    public Friend() {
    }

    public Player getPlayer() {
        return daoSession
                .getPlayerDao()
                .queryBuilder()
                .where(PlayerDao.Properties.PlayerId.eq(playerId))
                .unique();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
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
}
