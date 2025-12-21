package com.example.todo.task.dto;

public class AnalitikaOpravil {

    private long skupnoStevilo;
    private long steviloDokoncanih;
    private long steviloNedokoncanih;
    private long steviloZapadlih;
    private double odstotekDokoncanih;

    public AnalitikaOpravil() {
    }

    public AnalitikaOpravil(long skupnoStevilo,
                            long steviloDokoncanih,
                            long steviloNedokoncanih,
                            long steviloZapadlih,
                            double odstotekDokoncanih) {
        this.skupnoStevilo = skupnoStevilo;
        this.steviloDokoncanih = steviloDokoncanih;
        this.steviloNedokoncanih = steviloNedokoncanih;
        this.steviloZapadlih = steviloZapadlih;
        this.odstotekDokoncanih = odstotekDokoncanih;
    }

    public long getSkupnoStevilo() {
        return skupnoStevilo;
    }

    public void setSkupnoStevilo(long skupnoStevilo) {
        this.skupnoStevilo = skupnoStevilo;
    }

    public long getSteviloDokoncanih() {
        return steviloDokoncanih;
    }

    public void setSteviloDokoncanih(long steviloDokoncanih) {
        this.steviloDokoncanih = steviloDokoncanih;
    }

    public long getSteviloNedokoncanih() {
        return steviloNedokoncanih;
    }

    public void setSteviloNedokoncanih(long steviloNedokoncanih) {
        this.steviloNedokoncanih = steviloNedokoncanih;
    }

    public long getSteviloZapadlih() {
        return steviloZapadlih;
    }

    public void setSteviloZapadlih(long steviloZapadlih) {
        this.steviloZapadlih = steviloZapadlih;
    }

    public double getOdstotekDokoncanih() {
        return odstotekDokoncanih;
    }

    public void setOdstotekDokoncanih(double odstotekDokoncanih) {
        this.odstotekDokoncanih = odstotekDokoncanih;
    }
}
