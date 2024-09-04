package com.example.provamaps;

import java.io.Serializable;
import java.util.List;

public class Picnic implements Serializable {

    public String getBancsIOTaules() {
        return bancsIOTaules;
    }

    public List<Integer> getQueHiHa() {
        return queHiHa;
    }

    //Coordenades, tipus, url foto, usuari que l'ha creat, bancsIOTaules (bancs, taules, tot), quehiha (barbacoa, herba, contenidors, sombrilles, llum)
    public String bancsIOTaules = "";//bancs, taules, tot

    public void setBancsIOTaules(String bancsIOTaules) {
        this.bancsIOTaules = bancsIOTaules;
    }

    public void setQueHiHa(List<Integer> queHiHa) {
        this.queHiHa = queHiHa;
    }

    public List<Integer> queHiHa;//0barbacoa, 1herba, 2contenidors, 3sombrilles, 4llum
    public String latitud = "";
    public String longitud = "";
    public String urlfoto = "";
    public String uidusuari = "";
    public String key = null;


    public Picnic() {}

    public Picnic(String picnicId, String latitud, String longitud, String bancsIOTaules, List<Integer> queHiHa, String imageUrl) {
        this.key = picnicId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.bancsIOTaules = bancsIOTaules;
        this.queHiHa = queHiHa;
        this.urlfoto = imageUrl;
        this.uidusuari = AuthManager.getInstance().obtenirUsuari().getUid(); // UID usuari actual
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getTipus() {
        return "Picnic";
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