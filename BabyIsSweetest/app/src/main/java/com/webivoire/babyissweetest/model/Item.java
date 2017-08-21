package com.webivoire.babyissweetest.model;

import android.bluetooth.BluetoothAssignedNumbers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.webivoire.babyissweetest.base.BaseApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by fabia on 16/03/2017.
 */

public class Item extends RealmObject{

    @PrimaryKey
    private String id;
    private String type;
    private String nom;
    private String description;
    private Double lng;
    private Double lat;
    private String photos;
    private String responsable;
    private String telephone;
    private String email;
    private static Gson gson;


    public static Item createFormJson(JsonElement element){
        if (gson == null) gson = new Gson();
        Item ret = gson.fromJson(element, Item.class);   // from json
        Item local = BaseApplication.getRealmInstance() // from  local DB
                        .where(Item.class)
                        .equalTo("id", ret.getId())
                        .findFirst();
        if (local == null) return ret;

        return ret;

    }


    public Double getLat() {return lat;}

    public Double getLng() {return lng;}

    public String getDescription() {return description;}

    public String getEmail() {return email;}

    public String getId() {return id;}

    public String getNom() {return nom;}

    public String getPhotos() {return photos;}

    public String getResponsable() {return responsable;}

    public String getTelephone() {return telephone;}

    public String getType() {return type;}

}

