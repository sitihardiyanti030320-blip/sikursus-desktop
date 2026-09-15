/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package latihan; //package

/**
 *
 * @author USER
 */
public class LatihanBiayaKursus { //class
    public static void main(String[] args) {  //main method
        String kode = "JAVA-BSC";
        String nama = "Java Desktop Fundamental";
        double biaya = 2120000;
        double registrasi = 500000;
        boolean aktif = true;

        // Total sebelum diskon
        double totalSebelumDiskon = biaya + registrasi;

        // Menentukan diskon
        double diskon;
        if (totalSebelumDiskon >= 2120000) {
            diskon = 0.15;
        } else {
            diskon = 0.05;
        }

        double potongan = totalSebelumDiskon * diskon;
        double total = totalSebelumDiskon - potongan;

        // Menentukan status
        String status;
        if (total > 3000000) {
            status = "MAHAL";
        } else {
            status = "TERJANGKAU";
        }

        // Output
        System.out.println("Kode                : " + kode);
        System.out.println("Kursus              : " + nama);
        System.out.println("Aktif               : " + aktif);
        System.out.printf("Biaya Kursus        : Rp%,.0f%n", biaya);
        System.out.printf("Biaya Registrasi    : Rp%,.0f%n", registrasi);
        System.out.printf("Total Sebelum Diskon: Rp%,.0f%n", totalSebelumDiskon);
        System.out.printf("Diskon              : %.0f%%%n", diskon * 100);
        System.out.printf("Potongan            : Rp%,.0f%n", potongan);
        System.out.printf("Total Bayar         : Rp%,.0f%n", total);
        System.out.println("Status              : " + status);
    }
}/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class latihanbiayakursus {
    
}
