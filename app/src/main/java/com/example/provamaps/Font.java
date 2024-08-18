package com.example.provamaps;

import android.location.Address;

import java.io.IOException;
import java.util.List;

public class Font {

    public String getPotable() {
        return potable;
    }

    public String getEstat() {
        return estat;
    }

    //Coordenades, tipus, url foto, usuari que l'ha creat, potable (potable, no potable, no info), estat (em servei, sense servei)
    public String potable = "";//0: no info, 1: potable, 2: no potable
    public String estat = "";//true:en servei, false:sense servei

    public String latitud = "";

    public String longitud = "";
    public String urlfoto = "";
    public String uidusuari = "";
    public String key = null;


    public Font() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    /*public Font(String usuari, String urlfoto, Boolean estat, String latitud, String longitud, String potable, String key) {
        this.uidusuari = usuari;
        this.urlfoto = urlfoto;
        this.estat = estat;
        this.latitud = latitud;
        this.longitud = longitud;
        this.potable = potable;
        this.key = key;
    }*/

    public Font(String fontId, String latitud, String longitud, String potable, String estat, String imageUrl) {
        this.key = fontId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.potable = potable;
        this.estat = estat;
        this.urlfoto = imageUrl;
        this.uidusuari = AuthManager.getInstance().obtenirUsuari().getUid(); // UID usuari actual
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTipus() {
        return "Font";
    }

    public String getLatitud() {
        return latitud;
    }
    public String getLongitud() {
        return longitud;
    }
    public String getUrlfoto() {
        return urlfoto;
    }

    public void setImageUrl(String imageUrl) {
        this.urlfoto = imageUrl;
    }


    public String getKey() {
        return key;
    }
}