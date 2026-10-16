package model;


public class Instruktur extends Orang {
    private String keahlian;

    public Instruktur(int id, String nama, String noHp, String keahlian) {
        super(id, nama, noHp);
        setKeahlian(keahlian);
    }

    public String getKeahlian() {
        return keahlian;
    }

    public final void setKeahlian(String keahlian) {
        if (keahlian == null || keahlian.trim().isEmpty()) {
            throw new IllegalArgumentException("Keahlian wajib diisi");
        }
        this.keahlian = keahlian.trim();
    }

    @Override
    public String getInfo() {
        return "INSTRUKTUR | " + super.getInfo()
                + " | Keahlian: " + getKeahlian();
    }
}
