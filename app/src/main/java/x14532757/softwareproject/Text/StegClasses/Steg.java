package x14532757.softwareproject.Text.StegClasses;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by StealthCopter
 * Code Copied from:
 * Title: steganography
 * Author: stealthcopter
 * Date: 09/12/15
 * Availability: https://github.com/stealthcopter/steganography
 */

public class Steg {

    private final int PASS_NONE = 0;
    private final int PASS_SIMPLE_XOR = 1;

    private String key = null;
    private int passmode = PASS_NONE;
    private Bitmap inBitmap = null;

    //this steg method is used to create the steg bitmap
    public static Steg withInput(@NonNull Bitmap bitmap) {
        Steg steg = new Steg();
        steg.setInputBitmap(bitmap);
        return steg;
    }

    //set the input bitmap to operate on
    private void setInputBitmap(@NonNull Bitmap bitmap) {
        this.inBitmap = bitmap;
    }

     //Additional password to encrypt the data with (or decrypt)
     //Mode to use the password with, by default we'll prob
    private void withPassword(@NonNull String key, int mode) {
        this.key = key;
        this.passmode = mode;

        // FIXME:
        if (1 == 1) {
            throw new RuntimeException("Not implemented yet");
        }
    }

    //used the bitmap encoder class to decode the bitmap
    public Decode decode() throws Exception {
        return new Decode(BitmapEncoder.decode(inBitmap));
    }

    //encodes the taken string text input and passes it to the method below as bytes
    public Encoded encode(@NonNull String string) throws Exception {
        return encode(string.getBytes());
    }

    //takes the input text bytes and creates the bitmap with the text in it
    private Encoded encode(@NonNull byte[] bytes) throws Exception {

        // Check there is enough space for bitmap to be encoded into image
        if (bytes.length>bytesAvaliableInBitmap()){
            throw new IllegalArgumentException("Not enough space in bitmap to hold data (max:"+bytesAvaliableInBitmap()+")");
        }
        // Create an encoded object from our bitmap
        return new Encoded(BitmapEncoder.encode(inBitmap, bytes));
    }

    /**
     *
     * @return The bytes available to store in the bitmap
     */
    private int bytesAvaliableInBitmap() {
        if (inBitmap == null) return 0;
        return (inBitmap.getWidth() * inBitmap.getHeight())*3/8 - BitmapEncoder.HEADER_SIZE;
    }


}
