package dao;

import config.Database;
import java.sql.*;
import java.util.*;
import model.Kelas;
import util.LookupItem;

public class KelasDAO {
    private Kelas map(ResultSet r)throws SQLException{Kelas x=new Kelas();x.setIdKelas(r.getInt("id_kelas"));x.setIdKursus(r.getInt("id_kursus"));x.setIdInstruktur(r.getInt("id_instruktur"));x.setNamaKelas(r.getString("nama_kelas"));x.setTanggalMulai(r.getDate("tanggal_mulai"));x.setKapasitas(r.getInt("kapasitas"));x.setKodeKursus(r.getString("kode_kursus"));x.setNamaKursus(r.getString("nama_kursus"));x.setNamaInstruktur(r.getString("nama_instruktur"));return x;}
    public boolean insert(Kelas x)throws SQLException{String s="INSERT INTO kelas(id_kursus,id_instruktur,nama_kelas,tanggal_mulai,kapasitas) VALUES(?,?,?,?,?)";try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(s)){bind(p,x);return p.executeUpdate()==1;}}
    public boolean update(Kelas x)throws SQLException{String s="UPDATE kelas SET id_kursus=?,id_instruktur=?,nama_kelas=?,tanggal_mulai=?,kapasitas=? WHERE id_kelas=?";try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(s)){bind(p,x);p.setInt(6,x.getIdKelas());return p.executeUpdate()>0;}}
    private void bind(PreparedStatement p,Kelas x)throws SQLException{p.setInt(1,x.getIdKursus());p.setInt(2,x.getIdInstruktur());p.setString(3,x.getNamaKelas());p.setDate(4,x.getTanggalMulai());p.setInt(5,x.getKapasitas());}
    public boolean delete(int id)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM kelas WHERE id_kelas=?")){p.setInt(1,id);return p.executeUpdate()>0;}}
    public List<Kelas> findAll()throws SQLException{return search("");}
    public List<Kelas> search(String q)throws SQLException{List<Kelas> o=new ArrayList<>();String s="SELECT k.id_kelas,k.id_kursus,k.id_instruktur,k.nama_kelas,k.tanggal_mulai,k.kapasitas,kr.kode_kursus,kr.nama_kursus,i.nama AS nama_instruktur FROM kelas k JOIN kursus kr ON kr.id_kursus=k.id_kursus JOIN instruktur i ON i.id_instruktur=k.id_instruktur WHERE k.nama_kelas LIKE ? OR kr.nama_kursus LIKE ? OR i.nama LIKE ? ORDER BY k.id_kelas DESC";try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(s)){String z="%"+(q==null?"":q.trim())+"%";p.setString(1,z);p.setString(2,z);p.setString(3,z);try(ResultSet r=p.executeQuery()){while(r.next())o.add(map(r));}}return o;}
    public List<LookupItem> lookup()throws SQLException{List<LookupItem> o=new ArrayList<>();String s="SELECT k.id_kelas,k.nama_kelas,kr.nama_kursus FROM kelas k JOIN kursus kr ON kr.id_kursus=k.id_kursus ORDER BY k.nama_kelas";try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(s);ResultSet r=p.executeQuery()){while(r.next())o.add(new LookupItem(r.getInt(1),r.getString(2)+" - "+r.getString(3)));}return o;}
}