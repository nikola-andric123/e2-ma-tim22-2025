package com.example.rpggame.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Dodajemo @Entity da označimo klasu kao tabelu u bazi
@Entity(tableName = "kategorija_table")
public class Kategorija {

    @PrimaryKey
    @NonNull
    private String id;

    private String naziv;
    private String boja; // Boju čuvamo kao heksadecimalni kod, npr. "#FF5733"

    // Prazan konstruktor je potreban za Room
    public Kategorija() {}

    // Oznaka @Ignore kaže Room-u da ne koristi ovaj konstruktor za čitanje iz baze
    @androidx.room.Ignore
    public Kategorija(@NonNull String id, String naziv, String boja) {
        this.id = id;
        this.naziv = naziv;
        this.boja = boja;
    }

    // Getteri i Setteri
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
    public String getBoja() { return boja; }
    public void setBoja(String boja) { this.boja = boja; }

    // Važno: Override toString() metode da bi Spinner ispravno prikazao ime.
    @Override
    public String toString() {
        return naziv;
    }
}