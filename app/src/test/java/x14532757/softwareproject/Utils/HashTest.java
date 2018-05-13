package x14532757.softwareproject.Utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by x14532757 on 08/04/2018.
 */
public class HashTest {
    @Test
    public void PBK() throws Exception {

        Hash hash = new Hash();
        String testVal = "085085";
        String hashed = hash.PBK(testVal);
        String savedHash = "63920b7396989acbae80100e0f7fe231:28055a01714dbbbfdf5c7eac48598727e800a304d7e63d061a67c09945b23293";

        assertEquals(savedHash, hashed);


    }

    @Test
    public void validatePassword() throws Exception {

        Hash hash = new Hash();
        String testVal = "085713";
        String savedHash = "2fea20deb93a8a62737f346da5d0751:040f5d8f12c9fe0e7a7a486e46476e7a849a515f835e358b31a0e4efd5952fa1";
        boolean match = hash.validatePassword(testVal, savedHash);

        assertEquals(true, match);

    }

}