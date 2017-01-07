package com.nestedworld.nestedworld.data.database.entities.friend;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.entities.DaoSession;
import com.nestedworld.nestedworld.data.database.entities.base.BaseEntity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity(active = true)
public class FriendData extends BaseEntity {
    @Expose
    @SerializedName("registered_at")
    public String registeredAt;
    @Expose
    @SerializedName("level")
    public Long level;
    @Expose
    public String background;
    @Expose
    @SerializedName("birth_date")
    public String birthDate;
    @Expose
    public String gender;
    @SerializedName("id")
    @Expose
    public
    long playerId;
    @Expose
    public String pseudo;
    @Expose
    @SerializedName("is_connected")
    public Boolean isConnected;
    @Expose
    public String avatar;
    @Expose
    public String city;
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
    @Generated(hash = 1900484213)
    private transient FriendDataDao myDao;

    @Generated(hash = 513498745)
    public FriendData(String registeredAt, Long level, String background, String birthDate,
            String gender, long playerId, String pseudo, Boolean isConnected, String avatar,
            String city, Long id) {
        this.registeredAt = registeredAt;
        this.level = level;
        this.background = background;
        this.birthDate = birthDate;
        this.gender = gender;
        this.playerId = playerId;
        this.pseudo = pseudo;
        this.isConnected = isConnected;
        this.avatar = avatar;
        this.city = city;
        this.id = id;
    }

    @Generated(hash = 951877157)
    public FriendData() {
    }

    public long getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getIsConnected() {
        return this.isConnected;
    }

    public void setIsConnected(Boolean isConnected) {
        this.isConnected = isConnected;
    }

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Long getLevel() {
        return this.level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getRegisteredAt() {
        return this.registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
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
    @Generated(hash = 1312897803)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFriendDataDao() : null;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FriendData{" +
                "avatar='" + avatar + '\'' +
                ", playerId=" + playerId +
                ", city='" + city + '\'' +
                ", gender='" + gender + '\'' +
                ", isConnected=" + isConnected +
                ", background='" + background + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", level=" + level +
                ", pseudo='" + pseudo + '\'' +
                ", registeredAt='" + registeredAt + '\'' +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                '}';
    }
}
