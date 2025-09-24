package com.example.rpggame;

import androidx.room.TypeConverter;

import com.example.rpggame.domain.Zadatak;

public class Converters {

    // Konverteri za Zadatak.Tezina
    @TypeConverter
    public static String fromTezina(Zadatak.Tezina tezina) {
        return tezina == null ? null : tezina.name();
    }

    @TypeConverter
    public static Zadatak.Tezina toTezina(String name) {
        return name == null ? null : Zadatak.Tezina.valueOf(name);
    }

    // Konverteri za Zadatak.Bitnost
    @TypeConverter
    public static String fromBitnost(Zadatak.Bitnost bitnost) {
        return bitnost == null ? null : bitnost.name();
    }

    @TypeConverter
    public static Zadatak.Bitnost toBitnost(String name) {
        return name == null ? null : Zadatak.Bitnost.valueOf(name);
    }

    // Konverteri za Zadatak.Status
    @TypeConverter
    public static String fromStatus(Zadatak.Status status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static Zadatak.Status toStatus(String name) {
        return name == null ? null : Zadatak.Status.valueOf(name);
    }

    // Konverteri za Zadatak.TipPonavljanja
    @TypeConverter
    public static String fromTipPonavljanja(Zadatak.TipPonavljanja tip) {
        return tip == null ? null : tip.name();
    }

    @TypeConverter
    public static Zadatak.TipPonavljanja toTipPonavljanja(String name) {
        return name == null ? null : Zadatak.TipPonavljanja.valueOf(name);
    }
}