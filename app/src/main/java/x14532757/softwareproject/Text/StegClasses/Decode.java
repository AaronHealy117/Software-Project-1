package x14532757.softwareproject.Text.StegClasses;

/**
 * Created by StealthCopter
 * Code Copied and Modified from:
 * Title: steganography
 * Author: stealthcopter
 * Date: 09/12/15
 * Availability: https://github.com/stealthcopter/steganography
 */

public class Decode {
    private final byte[] bytes;

    Decode(byte[] bytes) {
        // The base for this decoded class is a byte array of the decoded data
        this.bytes = bytes;
    }

    public String intoString() {
        return new String(bytes);
    }


}
