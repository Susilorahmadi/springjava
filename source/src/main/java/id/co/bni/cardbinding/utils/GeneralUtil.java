package id.co.bni.cardbinding.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class GeneralUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    public boolean isValidPrefix(String data, String valid) {
        if (data.length() < 6) {
            return false;
        }
        String prefix = data.substring(0, 6);
        return valid.contains(prefix);
    }

    public boolean iaValidChannel(String data, String valid) {
        return valid.contains(data);
    }

    public boolean constain(String authcode, String state) {
        return authcode.contains(state);
    }

    public String generateAuthcode(String state) {
        return generateRandomString(10)+state+generateRandomString(10);
    }

    public static int generateRandomNumber(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static String generateRandomNumberWithDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);
        int randomNumber = generateRandomNumber(1000, 9999);
        return formattedDate + randomNumber;
    }

    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public static String formatOffsetDateTime() {
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return currentOffsetDateTime.format(formatter);
    }

    public static String convertPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceFirst("^0", "");
        phoneNumber = "62" + phoneNumber;
        return phoneNumber;
    }

    public static String maskAccountNumber(String accountNo) {
        // Cek apakah nomor rekening memiliki panjang yang valid
        if (accountNo.length() < 8) {
            return "";
        }

        // Ambil substring untuk bagian depan dan belakang nomor rekening
        int visibleDigits = Math.min(accountNo.length() - 8, 6);
        String front = accountNo.substring(0, 4);
        String back = accountNo.substring(accountNo.length() - 4);

        // Buat string dengan tanda bintang (*) untuk bagian tengah nomor rekening
        String maskedMiddle = "*".repeat(visibleDigits);
        return  front + maskedMiddle + back;
    }


}
