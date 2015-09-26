package core;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AesProvider {

    public static String decrypt(String data, String password) throws Exception {
        byte[] decodedFrom64 = Base64.decode(data, Base64.DEFAULT);

        Cipher cipher = getCipher(password, Cipher.DECRYPT_MODE);
        byte[] cipherData = cipher.doFinal(decodedFrom64);

        return new String(cipherData);
    }

    public static String encrypt(String plain, String password) throws Exception {
        String encrypted_string = "";

        Cipher cipher = getCipher(password, Cipher.ENCRYPT_MODE);
        byte[] encrypted = cipher.doFinal(plain.getBytes());

        encrypted_string = Base64.encodeToString(encrypted, Base64.DEFAULT);

        return encrypted_string;
    }

    private static Cipher getCipher(String password, int encryptMode) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        byte[] key = (password).getBytes("UTF-8");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        key = md5.digest(key);
        key = Arrays.copyOf(key, 32);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(encryptMode, secretKeySpec);
        return cipher;
    }
}
