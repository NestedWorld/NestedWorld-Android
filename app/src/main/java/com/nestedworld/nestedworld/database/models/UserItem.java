package com.nestedworld.nestedworld.database.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;
import com.orm.query.Condition;
import com.orm.query.Select;

public class UserItem extends SugarRecord {

    @Expose
    @SerializedName("id")
    @Unique
    public Long userItemId;
    @Expose
    public ShopItem infos;
    public long shopItemId;

    //Empty constructor for SugarRecord
    public UserItem() {
        //Keep empty
    }

    @Nullable
    public ShopItem infos() {
        return Select.from(ShopItem.class).where(Condition.prop("shop_item_id").eq(shopItemId)).first();
    }
}
