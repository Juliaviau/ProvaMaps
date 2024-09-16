package com.example.provamaps;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

public class Model_ItemCardPerfil {

    public Font getFont() {
        return font;
    }

    public Contenidor getContenidor() {
        return contenidor;
    }

    public Picnic getPicnic() {
        return picnic;
    }

    public Lavabo getLavabo() {
        return lavabo;
    }

    private Font font;
    private Contenidor contenidor;
    private Picnic picnic;
    private Lavabo lavabo;

    int img;
    String adreca;
    String urlfoto;
    String tipus;
    String lat;
    String lon;

    public Model_ItemCardPerfil(String tipus, String lat,  String lon, String adreca) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlfoto;
        this.adreca = adreca;
    }

    public Model_ItemCardPerfil(String tipus, String lat,  String lon, String urlfoto, Font font, String adreca) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlfoto;
        this.font = font;
        this.adreca = adreca;
    }


    public Model_ItemCardPerfil(String tipus, String lat,  String lon, String urlfoto, Lavabo lavabo, String adreca) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlfoto;
        this.lavabo = lavabo;
        this.adreca = adreca;
    }

    public Model_ItemCardPerfil(String tipus, String lat,  String lon, String urlfoto, Picnic picnic, String adreca) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlfoto;
        this.picnic = picnic;
        this.adreca = adreca;
    }

    public Model_ItemCardPerfil(String tipus, String lat, String lon, String urlFoto, Contenidor contenidor, String adreca) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlFoto;
        this.contenidor = contenidor;
        this.adreca = adreca;
    }


    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTipus() {
        return tipus;
    }
    public String getUrlfoto() {
        return urlfoto;
    }
    public void setTipus(String tipus) {
        this.tipus = tipus;
    }
    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getAdreca(Geocoder geocoder) {
        //TODO: Obtenir aqui l'adreca segons la lat i lon

        return adreca;
    }
}
