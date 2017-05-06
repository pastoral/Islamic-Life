package com.harbingerstudio.islamiclife.islamiclife.pojo.arabicdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.harbingerstudio.islamiclife.islamiclife.pojo.arabicdate.Hijri;

public class Data {

    @SerializedName("hijri")
    @Expose
    private Hijri hijri;


    public Hijri getHijri() {
        return hijri;
    }

    public void setHijri(Hijri hijri) {
        this.hijri = hijri;
    }



}