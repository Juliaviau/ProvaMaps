package com.example.provamaps;

public class Model_ItemCardPerfil {

    int img;

    String tipus;



    String lat;
    String lon;

    public Model_ItemCardPerfil(int img, String tipus, String lat,  String lon) {
        this.img = img;
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
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

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }
    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getAdreca() {
        //TODO: Obtenir aqui l'adreca segons la lat i lon
        return lon;
    }

}
