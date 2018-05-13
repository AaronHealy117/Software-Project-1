package x14532757.softwareproject.Images;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by x14532757 on 28/02/2018.
 * https://stackoverflow.com/questions/20227/how-do-i-use-3des-encryption-decryption-in-java
 * https://gist.github.com/adityasatrio/a8ef4f3c96345980a9cc
 * https://github.com/thanosba/3Des-Encryption-Decryption-in-Java/blob/master/TDes.java
 * http://blog.icodejava.com/1232/tutorial-encryption-and-decryption-using-desede-triple-des-in-java/
 *
 * Code Modified from:
 * Title: Tutorial – Encryption And Decryption Using DESede (Triple DES) In Java
 * Author: icodejava
 * Date: 30/01/15
 * Availability: http://blog.icodejava.com/1232/tutorial-encryption-and-decryption-using-desede-triple-des-in-java/
 *
 * Code Modified from:
 * Title: 3Des-Encryption-Decryption-in-Java
 * Author: thanosba
 * Date: 19/08/17
 * Availability: https://github.com/thanosba/3Des-Encryption-Decryption-in-Java/blob/master/TDes.java
 *
 * Code Modified from:
 * Title: Triple des bouncy castle.
 * Author: adityasatrio
 * Availability: https://gist.github.com/adityasatrio/a8ef4f3c96345980a9cc
 */

public class TripleDES {

    //create algorithms, cipher and charset
    private String TRIPLE_DES_TRANSFORMATION = "DESede/CBC/PKCS5Padding";
    private String ALGORITHM = "DESede";
    private String CHARSET = "UTF-8";

    //create variables needed for the hashing needed in Triple DES
    private int ITERATIONS = 10000;
    private int BIT_LENGTH = 192;
    private String PBKDF2 = "PBKDF2WithHmacSHA1";
    private byte[] IV_LENGTH = new byte[8];

    //create decurity provider
    private static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    //Triple DES encryot method
    byte[] encrypt(byte[] image, String pin) throws Exception {

        //get entered pincode which is hashed to byte array
        byte[] keyBytes = pin.getBytes(CHARSET);
        //copy the first 24 bytes so it is the Triple DES correct key size
        byte[] newBytes = Arrays.copyOfRange(keyBytes, 0, 24);

        Key key;
        //Create Triple Des key using the hashed pin code bytes
        DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(newBytes);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(ALGORITHM);
        // create cryptographic key
        key = keyfactory.generateSecret(deSedeKeySpec);

        //create new IV
        IvParameterSpec iv = new IvParameterSpec(IV_LENGTH);

        //create triple des cipher
        Cipher cipher = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION);
        //encrypt the image bytes
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        return cipher.doFinal(image);

    }

    public byte[] decrypt(byte[] image, String pin) throws Exception {

        //get entered pincode which is hashed to byte array
        byte[] keyBytes = pin.getBytes(CHARSET);
        //copy the first 24 bytes so it is the Triple DES correct key size
        byte[] newBytes = Arrays.copyOfRange(keyBytes, 0, 24);

        Key key;
        //Create Triple Des key using the hashed pin code bytes
        DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(newBytes);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(ALGORITHM);
        // create cryptographic key
        key = keyfactory.generateSecret(deSedeKeySpec);

        //create new IV again
        IvParameterSpec iv = new IvParameterSpec(IV_LENGTH);

        //create triple des cipher
        Cipher decipher = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION);
        //decrypt the image bytes
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        return decipher.doFinal(image);

    }

    //method to create random IV
    private static byte[] generateRandomIV(int length)
    {
        byte[] iv = new byte[length];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        return iv;
    }

    //method used to hash the users pin code
    String PBK(String pin) throws NoSuchAlgorithmException, InvalidKeySpecException {

        //get pincode to char array
        char[] chars = pin.toCharArray();
        //get secure random 8 byte salt
        byte[] salt = getSalt();

        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2);
        //generate key spec using pincode, salt, iterations up to 192 bit length
        KeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, BIT_LENGTH);
        //create hash
        SecretKey key = factory.generateSecret(spec);
        //encode key
        byte[] hash = key.getEncoded();

        //return salt and hash converted to hex and combined together
        return toHex(salt) + ":" + toHex(hash);

    }

    //method to create a 8 byte salt array
    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        String SHA1PRNG = "SHA1PRNG";
        //declare secure random
        SecureRandom sr = SecureRandom.getInstance(SHA1PRNG);
        //8 byte salt array
        byte[] salt = new byte[8];
        //create secure random 8 byte salt
        sr.nextBytes(salt);
        //return new random salt
        return salt;
    }

    boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        //salt and hash separated by :
        String[] parts = storedPassword.split(":");
        //get salt from hash and convert back to bytes
        byte[] salt = fromHex(parts[0]);
        //get the rest of the hash and convert back to bytes
        byte[] hash = fromHex(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, ITERATIONS, BIT_LENGTH);
        //create new key using the user entered password and the salt taken from the stored hash
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2);
        //create hash
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        //determine whether there is a different between the new created hash and the stored hash
        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    //method used to convert byte array to hex
    private static String toHex(byte[] bytes) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, bytes);
        String hex = bi.toString(16);
        int paddingLength = (bytes.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    //method used to convert hex to byte array
    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }



}
