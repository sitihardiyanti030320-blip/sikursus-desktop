package model;

public class Orang {
    private int id;
    private String nama;
    private String noHp;

    public Orang(int id, String nama, String noHp) {
        setId(id);
        setNama(nama);
        setNoHp(noHp);
    }

    public int getId() {
        return id;
    }

    public final void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID tidak boleh negatif");
        }
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public final void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama wajib diisi");
        }
        this.nama = nama.trim();
    }

    public String getNoHp() {
        return noHp;
    }

    public final void setNoHp(String noHp) {
        String nilai = noHp == null ? "" : noHp.trim();
        if (!nilai.isEmpty() && !nilai.matches("[0-9+\\- ]{8,20}")) {
            throw new IllegalArgumentException("Format nomor HP tidak valid");
        }
        this.noHp = nilai;
    }

    public String getInfo() {
        return getNama() + " | HP: " + (getNoHp().isEmpty() ? "-" : getNoHp());
    }
}
