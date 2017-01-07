package com.nestedworld.nestedworld.data.database.models.session;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.models.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity(active = true)
public class SessionData {
    @SerializedName("id")
    public
    long playerId;
    @Expose
    public String city;
    @Expose
    public String gender;
    @Expose
    public String avatar;
    @Expose
    @SerializedName("is_connected")
    public Boolean isConnected;
    @Expose
    public String background;
    @Expose
    @SerializedName("birth_date")
    public String birthDate;
    @Expose
    public String email;
    @Expose
    public Long level;
    @Expose
    @SerializedName("is_active")
    public String isActive;
    @Expose
    public String pseudo;
    @Expose
    @SerializedName("registered_at")
    public String registeredAt;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1698094633)
    private transient SessionDataDao myDao;

    @Generated(hash = 1664055197)
    public SessionData(long playerId, String city, String gender, String avatar,
                       Boolean isConnected, String background, String birthDate, String email,
                       Long level, String isActive, String pseudo, String registeredAt) {
        this.playerId = playerId;
        this.city = city;
        this.gender = gender;
        this.avatar = avatar;
        this.isConnected = isConnected;
        this.background = background;
        this.birthDate = birthDate;
        this.email = email;
        this.level = level;
        this.isActive = isActive;
        this.pseudo = pseudo;
        this.registeredAt = registeredAt;
    }

    @Generated(hash = 1955036032)
    public SessionData() {
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getLevel() {
        return this.level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getIsActive() {
        return this.isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
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
    @Generated(hash = 645204408)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSessionDataDao() : null;
    }
}
