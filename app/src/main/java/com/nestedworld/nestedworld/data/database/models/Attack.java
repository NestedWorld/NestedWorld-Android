package com.nestedworld.nestedworld.data.database.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity()
public class Attack {
    @Expose
    @SerializedName("id")
    @Unique
    public Long attackId;

    @Expose
    public String name;

    @Expose
    public String type;

    @Id(autoincrement = true)
    @Unique
    private Long id;

    @Generated(hash = 175367029)
    public Attack(Long attackId, String name, String type, Long id) {
        this.attackId = attackId;
        this.name = name;
        this.type = type;
        this.id = id;
    }

    @Generated(hash = 699029211)
    public Attack() {
    }

    @NonNull
    public AttackType getTypeKind() {
        if (this.type != null) {
            switch (this.type) {
                case "attack":
                    return AttackType.ATTACK;
                case "attacksp":
                    return AttackType.ATTACK_SP;
                case "defense":
                    return AttackType.DEFENSE;
                case "defensesp":
                    return AttackType.DEFENSE_SP;
                default:
                    break;
            }
        }
        return AttackType.UNKNOWN;
    }

    public Long getAttackId() {
        return this.attackId;
    }

    public void setAttackId(Long attackId) {
        this.attackId = attackId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public enum AttackType {
        ATTACK,
        ATTACK_SP,
        DEFENSE,
        DEFENSE_SP,
        OBJECT_USE,
        UNKNOWN
    }
}
