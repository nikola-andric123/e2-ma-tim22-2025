// Nalazi se u: app/java/com/example/rpgame/Kategorija.java
package com.example.rpggame;

public class Kategorija {
    private String id;
    private String naziv;
    private String boja; // Boju ćemo čuvati kao heksadecimalni kod, npr. "#FF5733"

    public Kategorija(String id, String naziv, String boja) {
        this.id = id;
        this.naziv = naziv;
        this.boja = boja;
    }

    // Getteri
    public String getId() { return id; }
    public String getNaziv() { return naziv; }
    public String getBoja() { return boja; }

    // Važno: Override toString() metode.
    // Spinner će pozvati ovu metodu da bi znao šta da prikaže u listi.
    @Override
    public String toString() {
        return naziv;
    }
}