package x14532757.softwareproject.Utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by x14532757 on 03/02/2018.
 *
 * Code Modified from:
 * Title: PBKDF2WithHmacSHA1.java
 * Author: seraphy
 * Availability: https://gist.github.com/seraphy/3072028
 *
 * Code Modified from:
 * Title: Hex to Byte Array in C# and Java Gives Different Results
 * Author: Jon Skeet
 * Availability: https://stackoverflow.com/questions/16922747/hex-to-byte-array-in-c-sharp-and-java-gives-different-results
 *
 * Code Modified from:
 * Title: Generate Secure Password Hash : MD5, SHA, PBKDF2, BCrypt Examples
 * Author: Lokesh Gupta
 * Availability: https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
 *
 *
 */

public class Hash {

    private int ITERATIONS = 10000;
    private int KEY_LENGTH = 256;
    private String ALGORITHM = "PBKDF2WithHmacSHA1";


    public String PBK(String pin) throws NoSuchAlgorithmException, InvalidKeySpecException {

        //convert pin code to char array
        char[] chars = pin.toCharArray();
        //generate secure random salt
        byte[] salt = getSalt();

        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        //generate key spec using pincode, salt, iterations up to 256 bit length
        KeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEY_LENGTH);
        //create hash
        SecretKey key = factory.generateSecret(spec);
        //encode key
        byte[] hash = key.getEncoded();

        //return salt and hash converted to hex and combined together
        return toHex(salt) + ":" + toHex(hash);
    }

    private byte[] getSalt() throws NoSuchAlgorithmException
    {
        String SALT_ALGO = "SHA1PRNG";
        //declare secure random
        SecureRandom sr = SecureRandom.getInstance(SALT_ALGO);
        //16 byte salt array
        byte[] salt = new byte[16];
        //create secure random 16 byte salt
        sr.nextBytes(salt);
        //return new random salt
        return salt;
    }

    //method used to convert byte array to hex
    private static String toHex(byte[] bytes) throws NoSuchAlgorithmException
    {
        StringBuilder builder = new StringBuilder();

        for (byte b: bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }


    public boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        //salt and hash separated by :
        String[] parts = storedPassword.split(":");
        //get salt from hash and convert back to bytes
        byte[] salt = fromHex(parts[0]);
        //get the rest of the hash and convert back to bytes
        byte[] hash = fromHex(parts[1]);

        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        //create new key using the user entered password and the salt taken from the stored hash
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
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
