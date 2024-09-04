package com.example.provamaps;

import java.io.Serializable;
import java.util.List;

public class Contenidor implements Serializable {
    public List<String> getTipusContenidor() {
        return tipusContenidor;
    }

    public void setTipusContenidor(List<String> tipusContenidor) {
        this.tipusContenidor = tipusContenidor;
    }

    //Coordenades, tipus, url foto, usuari que l'ha creat, tipus contenidor (vidre, paper, envasos, roba, organic, rebuig, paperera)
    public List<String> tipusContenidor;//vidre, paper, envasos, roba, organic, rebuig, paperera. Poden ser uns quants

    public String latitud = "";

    public String longitud = "";
    public String urlfoto = "";
    public String uidusuari = "";
    public String key = null;


    public Contenidor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Contenidor(String fontId, String latitud, String longitud, List<String> tipusContenidor, String imageUrl) {
        this.key = fontId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipusContenidor = tipusContenidor;
        this.urlfoto = imageUrl;
        this.uidusuari = AuthManager.getInstance().obtenirUsuari().getUid(); // UID usuari actual
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTipus() {
        return "Contenidor";
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
