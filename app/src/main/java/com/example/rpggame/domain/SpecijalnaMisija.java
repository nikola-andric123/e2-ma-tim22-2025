package com.example.rpggame.domain;

import com.google.firebase.Timestamp;

public class SpecijalnaMisija {

    private String id; // ID misije
    private String idSaveza; // ID saveza (klana) koji učestvuje
    private int hpBosa;
    private int maksHpBosa;
    private Timestamp datumPocetka;
    private Timestamp datumZavrsetka;
    private String status; // npr. "AKTIVNA", "ZAVRSENA_USPESNO", "ZAVRSENA_NEUSPESNO"

    // Prazan konstruktor je neophodan za Firebase
    public SpecijalnaMisija() {}

    // Getteri i Setteri
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdSaveza() {
        return idSaveza;
    }

    public void setIdSaveza(String idSaveza) {
        this.idSaveza = idSaveza;
    }

    public int getHpBosa() {
        return hpBosa;
    }

    public void setHpBosa(int hpBosa) {
        this.hpBosa = hpBosa;
    }

    public int getMaksHpBosa() {
        return maksHpBosa;
    }

    public void setMaksHpBosa(int maksHpBosa) {
        this.maksHpBosa = maksHpBosa;
    }

    public Timestamp getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(Timestamp datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public Timestamp getDatumZavrsetka() {
        return datumZavrsetka;
    }

    public void setDatumZavrsetka(Timestamp datumZavrsetka) {
        this.datumZavrsetka = datumZavrsetka;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}