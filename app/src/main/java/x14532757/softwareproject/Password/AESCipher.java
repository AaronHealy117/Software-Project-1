package x14532757.softwareproject.Password;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by x14532757 on 29/01/2018.
 *
 * Code modified from:
 * Title: Cross-platform-AES-encryption
 * Author:  Pakhee
 * Date: 10/04/2015
 * Availability: https://github.com/Pakhee/Cross-platform-AES-encryption/blob/master/Android/CryptLib.java
 *
 * Code modified from:
 * Title: encryption.java
 * Author:  itarato
 * Date: Sep 28, 2014.
 * Availability: https://gist.github.com/itarato/abef95871756970a9dad
 *
 *
 */

public class AESCipher {

    //create encrypt decrypt modes
    private enum EncryptMode {
        ENCRYPT, DECRYPT
    }

    private Cipher _cx;
    private byte[] _key;

    //create algorithm
    AESCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        _cx = Cipher.getInstance("AES/CBC/PKCS5Padding");
        _key = new byte[32]; //256 bit key space
    }

    private String encryptDecrypt(String _inputText, String _encryptionKey, EncryptMode _mode) throws UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String _out = "";

        //copies first 32 bytes of hashed pin code to make sure its the right key size
        System.arraycopy(_encryptionKey.getBytes("UTF-8"), 0, _key, 0, _key.length);

        //create secret key using the hashed pin code and AES
        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES");

        //create new random 16 byte IV
        int IV_LENGTH = 16;
        byte[] iv = generateRandomIV(IV_LENGTH);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //if the mode is encrypt
        if (_mode.equals(EncryptMode.ENCRYPT)) {
            //create new cipher using key and IV
            _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            //encrypt bytes of the password
            byte[] results = _cx.doFinal(_inputText.getBytes("UTF-8"));

            //combine the encrypted password bytes and IV bytes so we can use them again to decrypt
            byte[] combined = new byte[IV_LENGTH + results.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(results, 0, combined, IV_LENGTH, results.length);

            //encode the encrypted password bytes to a string
            _out = Base64.encodeToString(combined, Base64.DEFAULT); // ciphertext
        }

        //if mode is decrypt
        if (_mode.equals(EncryptMode.DECRYPT)) {
            //get the IV back from the encrypted bytes
            System.arraycopy(_inputText.getBytes("UTF-8"), 0, iv, 0, iv.length);
            //create same IV using these bytes
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            //create new cipher using key and IV
            _cx.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
            //decode the bytes
            byte[] decodedValue = Base64.decode(_inputText.getBytes(), Base64.DEFAULT);
            //decrypt the password bytes
            byte[] decryptedVal = _cx.doFinal(decodedValue);

            System.arraycopy(decryptedVal, 0, decryptedVal, 0, decryptedVal.length);
            //convert bytes back to a string with the original password
            String decryptedPassword = new String(decryptedVal);

            //remove IV and return password
            _out = decryptedPassword.substring(iv.length-1, decryptedPassword.length());
        }

        return _out;
    }

    //method used create random IV
    private static byte[] generateRandomIV(int length)
    {
        byte[] iv = new byte[length];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        return iv;
    }

    public String encrypt(String _plainText, String _key)
            throws InvalidKeyException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        return encryptDecrypt(_plainText, _key, EncryptMode.ENCRYPT);
    }


    public String decrypt(String _encryptedText, String _key)
            throws InvalidKeyException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        return encryptDecrypt(_encryptedText, _key, EncryptMode.DECRYPT);
    }


}
