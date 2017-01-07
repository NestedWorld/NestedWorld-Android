package com.nestedworld.nestedworld.helpers.battle;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.models.Attack;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.ui.view.fight.battle.BattleFragment;

public final class BattleHelper {
    /*
    ** Constructor
     */
    private BattleHelper() {
        //Private constructor for avoiding this class to be construct
    }

    /*
    ** Public method
     */
    @NonNull
    public static Attack.AttackType gestureToAttackType(@NonNull final String gestureInput) {
        LogHelper.d(BattleFragment.class.getSimpleName(), "gestureToAttackType > gestureInput=" + gestureInput);

        Attack.AttackType attackType;
        switch (gestureInput) {
            case "41":
                attackType = Attack.AttackType.ATTACK;
                break;
            case "62":
                attackType = Attack.AttackType.DEFENSE;
                break;
            case "456123":
                attackType = Attack.AttackType.ATTACK_SP;
                break;
            case "432165":
                attackType = Attack.AttackType.DEFENSE_SP;
                break;
            case "6253":
                attackType = Attack.AttackType.OBJECT_USE;
                break;
            default:
                attackType = Attack.AttackType.UNKNOWN;
                break;
        }

        return attackType;
    }
}
