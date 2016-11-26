package com.nestedworld.nestedworld.database.models;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity(active = true)
public class Combat {
    @Unique
    public String combatId;
    public String type;
    public String origin;
    public long monsterId;
    public String opponentPseudo;
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
    @Generated(hash = 1538902355)
    private transient CombatDao myDao;

    @Generated(hash = 2144243660)
    public Combat(String combatId, String type, String origin, long monsterId,
                  String opponentPseudo, Long id) {
        this.combatId = combatId;
        this.type = type;
        this.origin = origin;
        this.monsterId = monsterId;
        this.opponentPseudo = opponentPseudo;
        this.id = id;
    }

    @Generated(hash = 175565533)
    public Combat() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCombatId() {
        return this.combatId;
    }

    public void setCombatId(String combatId) {
        this.combatId = combatId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public long getMonsterId() {
        return this.monsterId;
    }

    public void setMonsterId(long monsterId) {
        this.monsterId = monsterId;
    }

    public String getOpponentPseudo() {
        return this.opponentPseudo;
    }

    public void setOpponentPseudo(String opponentPseudo) {
        this.opponentPseudo = opponentPseudo;
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
    @Generated(hash = 255570220)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCombatDao() : null;
    }
}
