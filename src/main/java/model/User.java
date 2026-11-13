package model;

public class User {

    private int idUser;
    private String username;
    private String role;
    private boolean aktif;

    public User(int idUser, String username, String role, boolean aktif) {
        this.idUser = idUser;
        this.username = username;
        this.role = role;
        this.aktif = aktif;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isAktif() {
        return aktif;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}