
package dao;

import config.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Peserta;

public class PesertaDAO {

    // Mengubah hasil query menjadi objek Peserta
    private Peserta map(ResultSet rs) throws SQLException {
        Peserta p = new Peserta();

        p.setId(rs.getInt("id_peserta"));
        p.setNim(rs.getString("nim"));
        p.setNama(rs.getString("nama"));
        p.setJenisKelamin(rs.getString("jenis_kelamin"));
        p.setProdi(rs.getString("prodi"));
        p.setNoHp(rs.getString("no_hp"));
        p.setEmail(rs.getString("email"));

        return p;
    }

    // Menambahkan peserta
    public boolean insert(Peserta p) throws SQLException {
        String sql = "INSERT INTO peserta "
                + "(nim, nama, jenis_kelamin, prodi, no_hp, email) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            bind(ps, p);
            return ps.executeUpdate() > 0;
        }
    }

    // Mengubah data peserta
    public boolean update(Peserta p) throws SQLException {
        String sql = "UPDATE peserta SET "
                + "nim=?, nama=?, jenis_kelamin=?, "
                + "prodi=?, no_hp=?, email=? "
                + "WHERE id_peserta=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            bind(ps, p);
            ps.setInt(7, p.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // Mengisi parameter query
    private void bind(PreparedStatement ps, Peserta p)
            throws SQLException {

        ps.setString(1, p.getNim());
        ps.setString(2, p.getNama());
        ps.setString(3, p.getJenisKelamin());
        ps.setString(4, p.getProdi());
        ps.setString(5, p.getNoHp());
        ps.setString(6, p.getEmail());
    }

    // Menghapus peserta
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM peserta WHERE id_peserta=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Menampilkan seluruh peserta
    public List<Peserta> findAll() throws SQLException {
        return search("");
    }

    // Mencari peserta
    public List<Peserta> search(String keyword) throws SQLException {
        List<Peserta> list = new ArrayList<>();

        String sql = "SELECT id_peserta, nim, nama, "
                + "jenis_kelamin, prodi, no_hp, email "
                + "FROM peserta "
                + "WHERE nim LIKE ? OR nama LIKE ? OR prodi LIKE ? "
                + "ORDER BY id_peserta DESC";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String cari = "%" + (keyword == null
                    ? "" : keyword.trim()) + "%";

            ps.setString(1, cari);
            ps.setString(2, cari);
            ps.setString(3, cari);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }

        return list;
    }
}
