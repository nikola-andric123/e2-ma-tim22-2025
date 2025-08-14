// Nalazi se u: app/java/com/example/rpgame/Zadatak.java
package com.example.rpggame;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Zadatak implements Parcelable {

    // Enum-i i atributi ostaju potpuno isti...
    public enum Tezina { VEOMA_LAK, LAK, TEZAK, EKSTREMNO_TEZAK }
    public enum Bitnost { NORMALAN, VAZAN, EKSTREMNO_VAZAN, SPECIJALAN }
    public enum Status { AKTIVAN, URADJEN, NEURADJEN, PAUZIRAN, OTKAZAN }
    public enum TipPonavljanja { DAN, NEDELJA }

    private String id;
    private String naziv;
    private String opis;
    private String kategorijaId;
    private boolean ponavljajuci;
    private int intervalPonavljanja;
    private TipPonavljanja tipPonavljanja;
    private long datumPocetka;
    private long datumZavrsetka;
    private Tezina tezina;
    private Bitnost bitnost;
    private Status status;

    public Zadatak() {}

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
        this.status = Status.AKTIVAN;
    }

    // Getteri i Setteri ostaju isti...
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

    // --- ISPRAVLJEN KOD ZA PARCELABLE ---
    protected Zadatak(Parcel in) {
        id = in.readString();
        naziv = in.readString();
        opis = in.readString();
        kategorijaId = in.readString();
        ponavljajuci = in.readByte() != 0;
        intervalPonavljanja = in.readInt();
        datumPocetka = in.readLong();
        datumZavrsetka = in.readLong();

        // ISPRAVKA: Čitanje enum-a na siguran način
        int tmpTezina = in.readInt();
        tezina = tmpTezina == -1 ? null : Tezina.values()[tmpTezina];
        int tmpBitnost = in.readInt();
        bitnost = tmpBitnost == -1 ? null : Bitnost.values()[tmpBitnost];
        int tmpStatus = in.readInt();
        status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
        int tmpTipPonavljanja = in.readInt();
        tipPonavljanja = tmpTipPonavljanja == -1 ? null : TipPonavljanja.values()[tmpTipPonavljanja];
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(naziv);
        dest.writeString(opis);
        dest.writeString(kategorijaId);
        dest.writeByte((byte) (ponavljajuci ? 1 : 0));
        dest.writeInt(intervalPonavljanja);
        dest.writeLong(datumPocetka);
        dest.writeLong(datumZavrsetka);

        // ISPRAVKA: Pisanje enum-a na siguran način
        dest.writeInt(tezina == null ? -1 : tezina.ordinal());
        dest.writeInt(bitnost == null ? -1 : bitnost.ordinal());
        dest.writeInt(status == null ? -1 : status.ordinal());
        dest.writeInt(tipPonavljanja == null ? -1 : tipPonavljanja.ordinal());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Zadatak> CREATOR = new Creator<Zadatak>() {
        @Override
        public Zadatak createFromParcel(Parcel in) {
            return new Zadatak(in);
        }

        @Override
        public Zadatak[] newArray(int size) {
            return new Zadatak[size];
        }
    };
    // --- KRAJ ISPRAVLJENOG KODA ---
}