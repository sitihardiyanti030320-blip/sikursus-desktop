package app;

import model.Kursus;

public class DemoKursus {

    public static void main(String[] args) {

        Kursus k1 = new Kursus(
            "JAVA-BSC",
            "Java Desktop Fundamental",
            "BASIC",
            500000
        );

        double hasil = k1.hitungBiayaSetelahDiskon(10);

        System.out.println("Biaya setelah diskon: " + hasil);
    }
}
