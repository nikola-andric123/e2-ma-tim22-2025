package com.example.rpggame.domain;

import java.util.ArrayList;
import java.util.List;

public class NapredakKorisnikaUMisiji {

    private String idKorisnika;
    private String korisnickoIme;
    private int nanetaSteta;

    // NOVA POLJA ZA PRAĆENJE KVOTA
    private int brojKupovina = 0;
    private int brojUdaracaBosa = 0;
    private int brojLaksihZadataka = 0;
    private int brojTezihZadataka = 0;
    // Lista datuma (kao string npr. "2025-09-22") da znamo da li je korisnik već slao poruku danas
    private List<String> daniSaPorukama = new ArrayList<>();

    public NapredakKorisnikaUMisiji() {}

    // Getteri i Setteri...
    public String getIdKorisnika() { return idKorisnika; }
    public void setIdKorisnika(String idKorisnika) { this.idKorisnika = idKorisnika; }
    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }
    public int getNanetaSteta() { return nanetaSteta; }
    public void setNanetaSteta(int nanetaSteta) { this.nanetaSteta = nanetaSteta; }

    // NOVI GETTERI I SETTERI
    public int getBrojKupovina() { return brojKupovina; }
    public void setBrojKupovina(int brojKupovina) { this.brojKupovina = brojKupovina; }
    public int getBrojUdaracaBosa() { return brojUdaracaBosa; }
    public void setBrojUdaracaBosa(int brojUdaracaBosa) { this.brojUdaracaBosa = brojUdaracaBosa; }
    public int getBrojLaksihZadataka() { return brojLaksihZadataka; }
    public void setBrojLaksihZadataka(int brojLaksihZadataka) { this.brojLaksihZadataka = brojLaksihZadataka; }
    public int getBrojTezihZadataka() { return brojTezihZadataka; }
    public void setBrojTezihZadataka(int brojTezihZadataka) { this.brojTezihZadataka = brojTezihZadataka; }
    public List<String> getDaniSaPorukama() { return daniSaPorukama; }
    public void setDaniSaPorukama(List<String> daniSaPorukama) { this.daniSaPorukama = daniSaPorukama; }
}