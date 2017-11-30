package x14532757.softwareproject.Password;

/**
 * Created by x14532757 on 21/11/2017.
 *
 * Taken from github user simbiose]
 * class name Encryption
 * https://github.com/simbiose/Encryption
 */

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import third.part.android.util.Base64;

/**
 * A class to make more easy and simple the encrypt routines, this is the core of Encryption library
 */
public class Encryption {

    private final Builder mBuilder;

    private Encryption(Builder builder) {
        mBuilder = builder;
    }

    static Encryption getDefault(String key, String salt, byte[] iv) {
        try {
            return Builder.getDefaultBuilder(key, salt, iv).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String encrypt(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException {
        if (data == null) return null;
        SecretKey secretKey = getSecretKey(hashTheKey(mBuilder.getKey()));
        byte[] dataBytes = data.getBytes(mBuilder.getCharsetName());
        Cipher cipher = Cipher.getInstance(mBuilder.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, mBuilder.getIvParameterSpec(), mBuilder.getSecureRandom());
        return Base64.encodeToString(cipher.doFinal(dataBytes), mBuilder.getBase64Mode());
    }

    /**
     * This is a sugar method that calls encrypt method and catch the exceptions returning
     * {@code null} when it occurs and logging the error
     *
     * @param data the String to be encrypted
     *
     * @return the encrypted String or {@code null} if you send the data as {@code null}
     */
    String encryptOrNull(String data) {
        try {
            return encrypt(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This is a sugar method that calls encrypt method in background, it is a good idea to use this
     * one instead the default method because encryption can take several time and with this method
     * the process occurs in a AsyncTask, other advantage is the Callback with separated methods,
     * one for success and other for the exception
     *
     * @param data     the String to be encrypted
     * @param callback the Callback to handle the results
     */
    public void encryptAsync(final String data, final Callback callback) {
        if (callback == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String encrypt = encrypt(data);
                    if (encrypt == null) {
                        callback.onError(new Exception("Encrypt return null, it normally occurs when you send a null data"));
                    }
                    callback.onSuccess(encrypt);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        }).start();
    }


    private String decrypt(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (data == null) return null;
        byte[] dataBytes = Base64.decode(data, mBuilder.getBase64Mode());
        SecretKey secretKey = getSecretKey(hashTheKey(mBuilder.getKey()));
        Cipher cipher = Cipher.getInstance(mBuilder.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, mBuilder.getIvParameterSpec(), mBuilder.getSecureRandom());
        byte[] dataBytesDecrypted = (cipher.doFinal(dataBytes));
        return new String(dataBytesDecrypted);
    }

    /**
     * This is a sugar method that calls decrypt method and catch the exceptions returning
     * {@code null} when it occurs and logging the error
     *
     * @param data the String to be decrypted
     *
     * @return the decrypted String or {@code null} if you send the data as {@code null}
     */
    String decryptOrNull(String data) {
        try {
            return decrypt(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This is a sugar method that calls decrypt method in background, it is a good idea to use this
     * one instead the default method because decryption can take several time and with this method
     * the process occurs in a AsyncTask, other advantage is the Callback with separated methods,
     * one for success and other for the exception
     *
     * @param data     the String to be decrypted
     * @param callback the Callback to handle the results
     */
    public void decryptAsync(final String data, final Callback callback) {
        if (callback == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String decrypt = decrypt(data);
                    if (decrypt == null) {
                        callback.onError(new Exception("Decrypt return null, it normally occurs when you send a null data"));
                    }
                    callback.onSuccess(decrypt);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        }).start();
    }

    /**
     * creates a 128bit salted aes key
     */
    private SecretKey getSecretKey(char[] key) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(mBuilder.getSecretKeyType());
        KeySpec spec = new PBEKeySpec(key, mBuilder.getSalt().getBytes(mBuilder.getCharsetName()), mBuilder.getIterationCount(), mBuilder.getKeyLength());
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), mBuilder.getKeyAlgorithm());
    }

    /**
     * takes in a simple string and performs an sha1 hash
     * that is 128 bits long...we then base64 encode it
     * and return the char array
     */
    private char[] hashTheKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(mBuilder.getDigestAlgorithm());
        messageDigest.update(key.getBytes(mBuilder.getCharsetName()));
        return Base64.encodeToString(messageDigest.digest(), Base64.NO_PADDING).toCharArray();
    }

    /**
     * When you encrypt or decrypt in callback mode you get noticed of result using this interface
     */
    public interface Callback {
        void onSuccess(String result);
        void onError(Exception exception);
    }

    /**
     * This class is used to create an Encryption instance, you should provide ALL data or start
     * with the Default Builder provided by the getDefaultBuilder method
     */
    public static class Builder {

        private byte[] mIv;
        private int mKeyLength;
        private int mBase64Mode;
        private int mIterationCount;
        private String mSalt;
        private String mKey;
        private String mAlgorithm;
        private String mKeyAlgorithm;
        private String mCharsetName;
        private String mSecretKeyType;
        private String mDigestAlgorithm;
        private String mSecureRandomAlgorithm;
        private SecureRandom mSecureRandom;
        private IvParameterSpec mIvParameterSpec;

        /**
         * @return an default builder with the follow defaults:
         * the default char set is UTF-8
         * the default base mode is Base64
         * the Secret Key Type is the PBKDF2WithHmacSHA1
         * the default salt is "some_salt" but can be anything
         * the default length of key is 128
         * the default iteration count is 65536
         * the default algorithm is AES in CBC mode and PKCS 5 Padding
         * the default secure random algorithm is SHA1PRNG
         * the default message digest algorithm SHA1
         */
        static Builder getDefaultBuilder(String key, String salt, byte[] iv) {
            return new Builder()
                    .setIv(iv)
                    .setKey(key)
                    .setSalt(salt)
                    .setKeyLength(256)
                    .setKeyAlgorithm("AES")
                    .setCharsetName("UTF8")
                    .setIterationCount(10000)
                    .setDigestAlgorithm("SHA1")
                    .setBase64Mode(Base64.DEFAULT)
                    .setAlgorithm("AES/CBC/PKCS5Padding")
                    .setSecureRandomAlgorithm("SHA1PRNG")
                    .setSecretKeyType("PBKDF2WithHmacSHA1");
        }

        Encryption build() throws NoSuchAlgorithmException {
            setSecureRandom(SecureRandom.getInstance(getSecureRandomAlgorithm()));
            setIvParameterSpec(new IvParameterSpec(getIv()));
            return new Encryption(this);
        }

        private String getCharsetName() {
            return mCharsetName;
        }

        Builder setCharsetName(String charsetName) {
            mCharsetName = charsetName;
            return this;
        }


        private String getAlgorithm() {
            return mAlgorithm;
        }

        Builder setAlgorithm(String algorithm) {
            mAlgorithm = algorithm;
            return this;
        }


        private String getKeyAlgorithm() {
            return mKeyAlgorithm;
        }


        Builder setKeyAlgorithm(String keyAlgorithm) {
            mKeyAlgorithm = keyAlgorithm;
            return this;
        }


        private int getBase64Mode() {
            return mBase64Mode;
        }

        Builder setBase64Mode(int base64Mode) {
            mBase64Mode = base64Mode;
            return this;
        }


        private String getSecretKeyType() {
            return mSecretKeyType;
        }


        Builder setSecretKeyType(String secretKeyType) {
            mSecretKeyType = secretKeyType;
            return this;
        }


        private String getSalt() {
            return mSalt;
        }


        Builder setSalt(String salt) {
            mSalt = salt;
            return this;
        }


        private String getKey() {
            return mKey;
        }


        public Builder setKey(String key) {
            mKey = key;
            return this;
        }


        private int getKeyLength() {
            return mKeyLength;
        }

        Builder setKeyLength(int keyLength) {
            mKeyLength = keyLength;
            return this;
        }

        /**
         * @return the number of times the password is hashed
         */
        private int getIterationCount() {
            return mIterationCount;
        }

        /**
         * @param iterationCount the number of times the password is hashed
         *
         * @return this instance to follow the Builder patter
         */
        Builder setIterationCount(int iterationCount) {
            mIterationCount = iterationCount;
            return this;
        }

        /**
         * @return the algorithm used to generate the secure random
         */
        private String getSecureRandomAlgorithm() {
            return mSecureRandomAlgorithm;
        }

        /**
         * @param secureRandomAlgorithm the algorithm to generate the secure random
         *
         * @return this instance to follow the Builder patter
         */
        Builder setSecureRandomAlgorithm(String secureRandomAlgorithm) {
            mSecureRandomAlgorithm = secureRandomAlgorithm;
            return this;
        }

        /**
         * @return the IvParameterSpec bytes array
         */
        private byte[] getIv() {
            return mIv;
        }

        /**
         * @param iv the byte array to create a new IvParameterSpec
         *
         * @return this instance to follow the Builder patter
         */
        Builder setIv(byte[] iv) {
            mIv = iv;
            return this;
        }

        /**
         * @return the SecureRandom
         */
        private SecureRandom getSecureRandom() {
            return mSecureRandom;
        }

        /**
         * @param secureRandom the Secure Random
         *
         * @return this instance to follow the Builder patter
         */
        Builder setSecureRandom(SecureRandom secureRandom) {
            mSecureRandom = secureRandom;
            return this;
        }

        /**
         * @return the IvParameterSpec
         */
        private IvParameterSpec getIvParameterSpec() {
            return mIvParameterSpec;
        }

        /**
         * @param ivParameterSpec the IvParameterSpec
         *
         * @return this instance to follow the Builder patter
         */
        Builder setIvParameterSpec(IvParameterSpec ivParameterSpec) {
            mIvParameterSpec = ivParameterSpec;
            return this;
        }

        /**
         * @return the message digest algorithm
         */
        private String getDigestAlgorithm() {
            return mDigestAlgorithm;
        }

        /**
         * @param digestAlgorithm the algorithm to be used to get message digest instance
         *
         * @return this instance to follow the Builder patter
         */
        Builder setDigestAlgorithm(String digestAlgorithm) {
            mDigestAlgorithm = digestAlgorithm;
            return this;
        }

    }

}
