package util;

import java.sql.Date;

public final class Validation {
    private Validation() {}

    public static Date date(String text, String label) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " wajib diisi");
        }
        try {
            return Date.valueOf(text.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(label + " harus berformat yyyy-MM-dd");
        }
    }

    public static int positiveInt(String text, String label) {
        int n;
        try {
            n = Integer.parseInt(text.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException(label + " harus berupa angka bulat");
        }
        if (n <= 0) {
            throw new IllegalArgumentException(label + " harus lebih dari 0");
        }
        return n;
    }
}