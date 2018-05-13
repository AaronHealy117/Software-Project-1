package x14532757.softwareproject.Files;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by x14532757 on 18/03/2018.
 *
 * Code Modified from:
 * Title: Class Twofish
 * Author: Javadoc
 * Availability: http://javadoc.iaik.tugraz.at/iaik_jce/current/iaik/security/cipher/Twofish.html
 *
 * Code Modified from:
 * Title: How do I encrypt/decrypt using Twofish?
 * Author: Joris Van den Bogaert
 * Availability: http://esus.com/encryptdecrypt-using-twofish/
 *
 * Code Modified from:
 * Title: The Legion of the Bouncy Castle
 * Author: The Legion of the Bouncy Castle
 * Availability: http://www.bouncycastle.org/java.html
 *
 */

class TwoFish {

    private byte[] KEY_SIZE;
    private byte[] IV_SIZE;

    //create spongycastle security provider
    //instantiate key size and iv size
    TwoFish() {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        KEY_SIZE = new byte[32]; //256 bit key
        IV_SIZE = new byte[16];
    }

    //create the different encryption modes
    private enum EncryptMode {
        ENCRYPT, DECRYPT
    }

    private byte[] TwoFishCipher(byte[] file, String password, EncryptMode mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, UnsupportedEncodingException {

        //create algorithm and IV size
        String KEY =  "Twofish";
        String CIPHER = "Twofish/CBC/PKCS5Padding";
        int IV_LENGTH = 16;
        byte[] out = new byte[0];

        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());

        //create new secure random 16 byte IV
        byte[] iv = generateRandomIV(IV_LENGTH);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //make sure key size is 32 bytes, the pincode is hashed in the AddFile class and used in this method
        System.arraycopy(password.getBytes("UTF-8"), 0, KEY_SIZE, 0, KEY_SIZE.length);

        //create new secret using hashed pin code
        SecretKeySpec secretKey = new SecretKeySpec(KEY_SIZE, KEY);
        //create Twofish cipher
        Cipher cipher = Cipher.getInstance(CIPHER);

        //if the mode is encrypt
        if(mode.equals(EncryptMode.ENCRYPT)){
            //use cipher
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            //create encrypted bytes using iv, file and secret key
            byte[] results = cipher.doFinal(file);

            //combine the encrypted bytes with the IV bytes so they can be used again
            byte[] combined = new byte[IV_LENGTH + results.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(results, 0, combined, IV_LENGTH, results.length);

            return combined;
        }

        //if the mode is decrypt
        if(mode.equals(EncryptMode.DECRYPT)){
            //get user entered pin code
            byte[] keyBytes = secretKey.getEncoded();
            //create secret key with the entered pincode which has been hashed and validated in the AddFile class
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, KEY);

            //copy the IV back out of the encrypted bytes
            System.arraycopy(file, 0, iv, 0, iv.length);
            //create same IV again so the encrypted bytes can be decrypted
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            //decrypt the file encrypted bytes using the same secret key and IV
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            out = cipher.doFinal(file);
        }

        return out;

    }

    //method used to create new secure random IV
    private static byte[] generateRandomIV(int length)
    {
        byte[] iv = new byte[length];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        return iv;
    }

    //encrypt method
    public byte[] encrypt(byte[] file ,String password) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        return TwoFishCipher(file, password, EncryptMode.ENCRYPT);
    }
    //decrypt method
    public byte[] decrypt(byte[] file, String password) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        return TwoFishCipher(file, password, EncryptMode.DECRYPT);
    }







}
