package com.nestedworld.nestedworld.data.database.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

@Entity(active = true)
public class UserMonster {
    @Expose
    @SerializedName("id")
    @Unique
    public Long userMonsterId;// the sql table will be called usermonsterid (see sugarOrm doc)

    @Expose
    @SerializedName("infos")
    @Transient
    public Monster monster;

    @Expose
    public Long level;
    public Long monsterId;//key for Monster<->UserMonster relationship

    @Expose
    public String surname;

    @Expose
    public long experience;

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
    @Generated(hash = 1432864299)
    private transient UserMonsterDao myDao;

    @Generated(hash = 17730337)
    public UserMonster(Long userMonsterId, Long level, Long monsterId, String surname,
                       long experience, Long id) {
        this.userMonsterId = userMonsterId;
        this.level = level;
        this.monsterId = monsterId;
        this.surname = surname;
        this.experience = experience;
        this.id = id;
    }

    @Generated(hash = 120169445)
    public UserMonster() {
    }

    @Nullable
    public Monster getMonster() {
        return daoSession.getMonsterDao()
                .queryBuilder()
                .where(MonsterDao.Properties.MonsterId.eq(monsterId))
                .unique();
    }

    public Long getUserMonsterId() {
        return this.userMonsterId;
    }

    public void setUserMonsterId(Long userMonsterId) {
        this.userMonsterId = userMonsterId;
    }

    public Long getLevel() {
        return this.level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getMonsterId() {
        return this.monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getExperience() {
        return this.experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
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
    @Generated(hash = 1557456353)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserMonsterDao() : null;
    }
}
