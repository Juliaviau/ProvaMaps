package com.example.provamaps;

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
    String urlfoto;
    String tipus;
    String lat;
    String lon;

    public Model_ItemCardPerfil(String tipus, String lat,  String lon, String urlfoto) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlfoto;
    }

    public Model_ItemCardPerfil(String tipus, String lat,  String lon, String urlfoto, Font font) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlfoto;
        this.font = font;
    }


    public Model_ItemCardPerfil(String tipus, String lat,  String lon, String urlfoto, Lavabo lavabo) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlfoto;
        this.lavabo = lavabo;
    }

    public Model_ItemCardPerfil(String tipus, String lat,  String lon, String urlfoto, Picnic picnic) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlfoto;
        this.picnic = picnic;
    }

    public Model_ItemCardPerfil(String tipus, String lat, String lon, String urlFoto, Contenidor contenidor) {
        this.tipus = tipus;
        this.lat = lat;
        this.lon = lon;
        this.urlfoto = urlFoto;
        this.contenidor = contenidor;
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

    public String getAdreca() {
        //TODO: Obtenir aqui l'adreca segons la lat i lon
        return lon;
    }

}
