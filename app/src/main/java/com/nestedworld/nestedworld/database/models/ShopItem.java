package com.nestedworld.nestedworld.database.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity()
public class ShopItem {

    @Expose
    @SerializedName("id")
    @Unique
    public long shopItemId;
    @Expose
    public String name;
    @Expose
    public String kind;
    @Expose
    public String power;
    @Expose
    public Boolean premium;
    @Expose
    public String description;
    @Expose
    public long price;
    @Expose
    public String image;
    @Id(autoincrement = true)
    @Unique
    private Long id;

    @Generated(hash = 33734647)
    public ShopItem(long shopItemId, String name, String kind, String power,
                    Boolean premium, String description, long price, String image,
                    Long id) {
        this.shopItemId = shopItemId;
        this.name = name;
        this.kind = kind;
        this.power = power;
        this.premium = premium;
        this.description = description;
        this.price = price;
        this.image = image;
        this.id = id;
    }

    @Generated(hash = 872247774)
    public ShopItem() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getShopItemId() {
        return this.shopItemId;
    }

    public void setShopItemId(long shopItemId) {
        this.shopItemId = shopItemId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPower() {
        return this.power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public Boolean getPremium() {
        return this.premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return this.price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
