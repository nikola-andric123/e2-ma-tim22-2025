package com.example.rpggame.domain;

public class NapredakKorisnikaUMisiji {

    private String idKorisnika;
    private String korisnickoIme;
    private int nanetaSteta; // Ukupna šteta koju je korisnik naneo bosu

    // Prazan konstruktor je neophodan za Firebase
    public NapredakKorisnikaUMisiji() {}

    // Getteri i Setteri
    public String getIdKorisnika() {
        return idKorisnika;
    }

    public void setIdKorisnika(String idKorisnika) {
        this.idKorisnika = idKorisnika;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public int getNanetaSteta() {
        return nanetaSteta;
    }

    public void setNanetaSteta(int nanetaSteta) {
        this.nanetaSteta = nanetaSteta;
    }
}