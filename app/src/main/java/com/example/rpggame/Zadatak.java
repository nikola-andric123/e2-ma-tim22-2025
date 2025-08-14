package com.example.rpggame;

public class Zadatak {

    // Definišemo "enum" tipove da bismo ograničili moguće vrednosti.
    // Ovo je mnogo bolje nego da koristimo običan tekst ("String") jer smanjuje mogućnost greške.
    public enum Tezina {
        VEOMA_LAK, LAK, TEZAK, EKSTREMNO_TEZAK
    }

    public enum Bitnost {
        NORMALAN, VAZAN, EKSTREMNO_VAZAN, SPECIJALAN
    }

    public enum Status {
        AKTIVAN, URADJEN, NEURADJEN, PAUZIRAN, OTKAZAN
    }

    public enum TipPonavljanja {
        DAN, NEDELJA
    }

    // Atributi (polja) naše klase Zadatak
    private String id;
    private String naziv;
    private String opis;
    private String kategorijaId; // Čuvaćemo ID kategorije koju kreira Student 1

    private boolean ponavljajuci; // true ako je ponavljajući, false ako je jednokratni
    private int intervalPonavljanja;
    private TipPonavljanja tipPonavljanja;
    private long datumPocetka; // Čuvamo datume kao brojeve (timestamp) radi lakšeg rada
    private long datumZavrsetka;

    private Tezina tezina;
    private Bitnost bitnost;
    private Status status;

    // Prazan konstruktor je potreban za Firebase
    public Zadatak() {
    }

    // Konstruktor koji ćemo koristiti za kreiranje novog zadatka
    public Zadatak(String id, String naziv, String opis, String kategorijaId, boolean ponavljajuci, int intervalPonavljanja, TipPonavljanja tipPonavljanja, long datumPocetka, long datumZavrsetka, Tezina tezina, Bitnost bitnost) {
        this.id = id;
        this.naziv = naziv;
        this.opis = opis;
        this.kategorijaId = kategorijaId;
        this.ponavljajuci = ponavljajuci;
        this.intervalPonavljanja = intervalPonavljanja;
        this.tipPonavljanja = tipPonavljanja;
        this.datumPocetka = datumPocetka;
        this.datumZavrsetka = datumZavrsetka;
        this.tezina = tezina;
        this.bitnost = bitnost;
        this.status = Status.AKTIVAN; // Svaki novokreirani zadatak je automatski aktivan [cite: 438]
    }

    // Getteri i Setteri - metode za pristup i postavljanje vrednosti atributa
    // Možete ih generisati automatski: Desni klik -> Generate -> Getter and Setter -> Select All

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }
    public String getKategorijaId() { return kategorijaId; }
    public void setKategorijaId(String kategorijaId) { this.kategorijaId = kategorijaId; }
    public boolean isPonavljajuci() { return ponavljajuci; }
    public void setPonavljajuci(boolean ponavljajuci) { this.ponavljajuci = ponavljajuci; }
    public int getIntervalPonavljanja() { return intervalPonavljanja; }
    public void setIntervalPonavljanja(int intervalPonavljanja) { this.intervalPonavljanja = intervalPonavljanja; }
    public TipPonavljanja getTipPonavljanja() { return tipPonavljanja; }
    public void setTipPonavljanja(TipPonavljanja tipPonavljanja) { this.tipPonavljanja = tipPonavljanja; }
    public long getDatumPocetka() { return datumPocetka; }
    public void setDatumPocetka(long datumPocetka) { this.datumPocetka = datumPocetka; }
    public long getDatumZavrsetka() { return datumZavrsetka; }
    public void setDatumZavrsetka(long datumZavrsetka) { this.datumZavrsetka = datumZavrsetka; }
    public Tezina getTezina() { return tezina; }
    public void setTezina(Tezina tezina) { this.tezina = tezina; }
    public Bitnost getBitnost() { return bitnost; }
    public void setBitnost(Bitnost bitnost) { this.bitnost = bitnost; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}