package dao;

import config.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.User;
import util.PasswordUtil;

public class UserDAO {

    /** Login: mengembalikan User jika username aktif dan password cocok, selain itu null. */
    public User authenticate(String username, char[] password) throws SQLException {
        String sql = "SELECT id_user, username, password_hash, role, aktif "
                + "FROM users WHERE username=? AND aktif=1";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()
                        && PasswordUtil.verify(password, rs.getString("password_hash"))) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /** Mencari user berdasarkan username atau role. */
    public List<User> search(String keyword) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id_user, username, role, aktif FROM users "
                + "WHERE username LIKE ? OR role LIKE ? ORDER BY id_user";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public boolean insert(String username, char[] password, String role, boolean aktif)
            throws SQLException {
        String sql = "INSERT INTO users(username, password_hash, role, aktif) VALUES (?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));
            ps.setString(3, role);
            ps.setBoolean(4, aktif);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(int idUser, String role, boolean aktif) throws SQLException {
        String sql = "UPDATE users SET role=?, aktif=? WHERE id_user=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setBoolean(2, aktif);
            ps.setInt(3, idUser);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean resetPassword(int idUser, char[] password) throws SQLException {
        String sql = "UPDATE users SET password_hash=? WHERE id_user=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hash(password));
            ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int idUser) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id_user"),
                rs.getString("username"),
                rs.getString("role"),
                rs.getBoolean("aktif"));
    }
}