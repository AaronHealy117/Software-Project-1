package x14532757.softwareproject.Files;

import org.junit.Test;

import x14532757.softwareproject.Utils.Hash;

import static org.junit.Assert.assertNotNull;

/**
 * Created by x14532757 on 07/05/2018.
 */
public class TwoFishTest {
    @Test
    public void encrypt() throws Exception {
        byte[] filebytes = new byte[]{17, 12, 24, 18, 17, 21, 24, 62, 22, 20, 26, 27, 52, 122, 11, 14, 10, 23, 16, 9, 26, 86, 72, 29, 43, 5, 45, 55, 48, 73, 12, 69 };
        String pincode = "085713";

        Hash hash = new Hash();
        String hashed = hash.PBK(pincode);

        TwoFish twoFish = new TwoFish();
        byte[] encrypted = twoFish.encrypt(filebytes, hashed);

        assertNotNull(encrypted);

    }

    @Test
    public void decrypt() throws Exception {
    }

}