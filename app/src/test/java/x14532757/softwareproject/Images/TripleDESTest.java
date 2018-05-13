package x14532757.softwareproject.Images;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by x14532757 on 07/05/2018.
 */
public class TripleDESTest {
    @Test
    public void encrypt() throws Exception {

        byte[] imagebytes =  new byte[]{17, 12, 24, 18, 17, 21, 24, 62, 22, 20, 26, 27, 52, 122, 11, 14, 10, 23, 16, 9, 26, 86, 72, 29, 43, 5, 45, 55, 48, 73, 12, 69 };
        String pincode = "085713";
        TripleDES des = new TripleDES();
        String hash = des.PBK(pincode);
        byte[] encrypted = des.encrypt(imagebytes, hash);
        System.out.print(Arrays.toString(encrypted) +"  "+ hash);

        assertNotEquals(imagebytes, encrypted);

    }

    @Test
    public void decrypt() throws Exception {

        byte[] encrypted = new byte[]{42, 57, 121, 31, -87, 11, 93, -28, -122, 117, 61, -51, -40, -64, 44, 28, 46, -18, -74, -59, -92, 79, 29, 54, -80, 91, 83, 26, 73, -5, -27, 32, 33, -73, 69, 6, -98, -52, 34, -52};
        byte[] expected  = new byte[]{17, 12, 24,  18, 17,  21, 24,  62,  22,  20,  26,  27,  52, 122, 11, 14, 10,  23,  16,  9,   26, 86, 72, 29,  43, 5,  45, 55, 48,  73, 12, 69 };
        String pincode = "46c6332a2c700547:5285fd023f0c912257991214bce97a9e6406f5516cb6bb41";
        TripleDES des = new TripleDES();
        byte[] decrypted = des.decrypt(encrypted, pincode);
        System.out.print(Arrays.toString(decrypted));

        assertArrayEquals(expected, decrypted);
    }

}