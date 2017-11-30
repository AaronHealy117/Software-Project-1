package x14532757.softwareproject.LoginRegister;
//https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#bcrypt-scrypt
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by x14532757 on 22/10/2017.
 */

public class Hash {

    private static final String SHA = "SHA1PRNG";
    private static final int ITERATIONS = 1000;
    private static final String PBK = "PBKDF2WithHmacSHA1";

    private static String GenerateHash(String password) throws Exception{

        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec keyspec = new PBEKeySpec(chars, salt, ITERATIONS, 64*8);
        SecretKeyFactory secretkey = SecretKeyFactory.getInstance(PBK);
        byte[] hash = secretkey.generateSecret(keyspec).getEncoded();
        return ITERATIONS + ":" + toHex(salt) + toHex(hash);
    }

    private static String toHex(byte[] Array) {

        BigInteger bigInt = new BigInteger(1, Array);
        String hex = bigInt.toString(16);
        int padding = (Array.length * 2) - hex.length();

        if(padding > 0){
            return String.format("%0" + padding + "d", 0) + hex;
        }else{
            return hex;
        }

    }

    private static byte[] getSalt() throws Exception{

        SecureRandom random = SecureRandom.getInstance(SHA);
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

}
