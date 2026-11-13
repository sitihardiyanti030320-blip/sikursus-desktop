package model;

import java.sql.Date;

public class Kelas {
    private int idKelas;
    private int idKursus;
    private int idInstruktur;
    private String namaKelas;
    private Date tanggalMulai;
    private int kapasitas;
    private String kodeKursus;
    private String namaKursus;
    private String namaInstruktur;

    public int getIdKelas() { return idKelas; }
    public void setIdKelas(int idKelas) { this.idKelas = idKelas; }
    public int getIdKursus() { return idKursus; }
    public void setIdKursus(int idKursus) { this.idKursus = idKursus; }
    public int getIdInstruktur() { return idInstruktur; }
    public void setIdInstruktur(int idInstruktur) { this.idInstruktur = idInstruktur; }
    public String getNamaKelas() { return namaKelas; }
    public void setNamaKelas(String namaKelas) {
        if (namaKelas == null || namaKelas.trim().isEmpty()) throw new IllegalArgumentException("Nama kelas wajib diisi");
        this.namaKelas = namaKelas.trim();
    }
    public Date getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(Date tanggalMulai) {
        if (tanggalMulai == null) throw new IllegalArgumentException("Tanggal mulai wajib diisi");
        this.tanggalMulai = tanggalMulai;
    }
    public int getKapasitas() { return kapasitas; }
    public void setKapasitas(int kapasitas) {
        if (kapasitas <= 0) throw new IllegalArgumentException("Kapasitas harus lebih dari 0");
        this.kapasitas = kapasitas;
    }
    public String getKodeKursus() { return kodeKursus; }
    public void setKodeKursus(String kodeKursus) { this.kodeKursus = kodeKursus; }
    public String getNamaKursus() { return namaKursus; }
    public void setNamaKursus(String namaKursus) { this.namaKursus = namaKursus; }
    public String getNamaInstruktur() { return namaInstruktur; }
    public void setNamaInstruktur(String namaInstruktur) { this.namaInstruktur = namaInstruktur; }
}