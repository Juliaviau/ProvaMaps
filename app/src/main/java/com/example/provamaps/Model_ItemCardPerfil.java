package com.example.provamaps;

public class Model_ItemCardPerfil {

    int img;

    String tipus, adreca;

    public Model_ItemCardPerfil(int img, String tipus, String adreca) {
        this.img = img;
        this.tipus = tipus;
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

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }


}
