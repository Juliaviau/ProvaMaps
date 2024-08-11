package com.example.provamaps;

public class Font {

    //Coordenades, tipus, url foto, usuari que l'ha creat, potable (potable, no potable, no info), estat (em servei, sense servei)
    public String potable = "";//0: no info, 1: potable, 2: no potable
    public Boolean estat = true;//true:en servei, false:sense servei

    public String latitud = "";



    public String longitud = "";

    public String urlfoto = "";
    public String uidusuari = "";
    public String key = null;


    public Font() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Font(String usuari, String urlfoto, Boolean estat, String latitud, String longitud, String potable, String key) {
        this.uidusuari = usuari;
        this.urlfoto = urlfoto;
        this.estat = estat;
        this.latitud = latitud;
        this.longitud = longitud;
        this.potable = potable;
        this.key = key;
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
}