package com.nestedworld.nestedworld.data.database.entities;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.entities.base.BaseEntity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

@Entity(active = true)
public class UserItem extends BaseEntity {
    @Expose
    @SerializedName("id")
    @Unique
    public Long userItemId;

    @Expose
    @ToOne(joinProperty = "shopItemId")
    @SerializedName("infos")
    @Transient
    public ShopItem shopItem;

    public long shopItemId;

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
    @Generated(hash = 1940058155)
    private transient UserItemDao myDao;

    @Generated(hash = 2011444659)
    public UserItem(Long userItemId, long shopItemId, Long id) {
        this.userItemId = userItemId;
        this.shopItemId = shopItemId;
        this.id = id;
    }

    @Generated(hash = 402134942)
    public UserItem() {
    }

    @Nullable
    public ShopItem getShopItem() {
        return daoSession
                .getShopItemDao()
                .queryBuilder()
                .where(ShopItemDao.Properties.ShopItemId.eq(shopItemId))
                .unique();
    }

    public Long getUserItemId() {
        return this.userItemId;
    }

    public void setUserItemId(Long userItemId) {
        this.userItemId = userItemId;
    }

    public long getShopItemId() {
        return this.shopItemId;
    }

    public void setShopItemId(long shopItemId) {
        this.shopItemId = shopItemId;
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
    @Generated(hash = 1771036168)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserItemDao() : null;
    }
}
