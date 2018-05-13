package x14532757.softwareproject.Password;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import x14532757.softwareproject.Utils.Hash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by x14532757 on 07/05/2018.
 */
public class AESCipherTest {
    public AESCipherTest() throws NoSuchPaddingException, NoSuchAlgorithmException {
    }

    @Test
    public void encrypt() throws Exception {
        String password = "password";
        String pincode = "085713";

        Hash hash = new Hash();
        String hashed = hash.PBK(pincode);

        AESCipher cipher = new AESCipher();
        String encrypted = cipher.encrypt(password, hashed);

        assertNotNull(encrypted);

    }

    @Test
    public void decrypt() throws Exception {

        String pincode = "a8e7d48b678c4f87f842288c91b55742:564ed9822c5e79ac9f28bdb7652d8e6a";
        String password = "X1Yl2Nnx5AxPWvUiwtthlc+beJX5g8JlypbVVKAqqM8=\n";
        String expected = "aaronhealy1";

        AESCipher cipher = new AESCipher();

        String decrypted = cipher.decrypt(password, pincode);

        assertEquals(expected, decrypted);



    }



}