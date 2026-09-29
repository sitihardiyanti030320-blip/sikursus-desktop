package model;

public class Kursus {
    
    String kode;
    String nama;
    String level;
    double biaya;
    
    public Kursus() {
    }
    
    public Kursus(String kode, String nama, String level, double biaya) {
        this.kode = kode;
        this.nama = nama;
        this.level = level;
        this.biaya = biaya;
    }
    
    public double hitungBiayaSetelahDiskon(double persenDiskon) {
        return biaya - (biaya * persenDiskon / 100.0);
    }
}
