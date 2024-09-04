package com.example.provamaps;

import java.io.Serializable;
import java.util.List;

public class Lavabo implements Serializable {





    public List<Integer> getTipusLavabo() {
        return tipusLavabo;
    }

    public String getDisposaPaper() {
        return disposaPaper;
    }

    public String getDisposaPica() {
        return disposaPica;
    }

    //Coordenades, tipus, url foto, usuari que l'ha creat, tipuslavabo (nadons, mixt, home, dona, accesible), paperhigienic (si, no), pica (si,no)
    public List<Integer> tipusLavabo ;//0nadons, 1mixt, 2home, 3dona, 4accesible. pot seleccionarse mes dun

    public void setTipusLavabo(List<Integer> tipusLavabo) {
        this.tipusLavabo = tipusLavabo;
    }

    public void setDisposaPaper(String disposaPaper) {
        this.disposaPaper = disposaPaper;
    }

    public void setDisposaPica(String disposaPica) {
        this.disposaPica = disposaPica;
    }

    public String disposaPaper = "";//si, no
    public String disposaPica = "";//si, no
    public String latitud = "";
    public String longitud = "";
    public String urlfoto = "";
    public String uidusuari = "";
    public String key = null;

    public Lavabo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Lavabo(String lavaboId, String latitud, String longitud, List<Integer> tipusLavabo, String disposaPaper, String disposaPica, String imageUrl) {
        this.key = lavaboId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipusLavabo = tipusLavabo;
        this.disposaPaper = disposaPaper;
        this.disposaPica = disposaPica;
        this.urlfoto = imageUrl;
        this.uidusuari = AuthManager.getInstance().obtenirUsuari().getUid(); // UID usuari actual
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getTipus() {
        return "Lavabo";
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
