package x14532757.softwareproject.Text.StegClasses;

import android.graphics.Bitmap;

/**
 * Created by StealthCopter
 * Code Copied and Modified from:
 * Title: steganography
 * Author: stealthcopter
 * Date: 09/12/15
 * Availability: https://github.com/stealthcopter/steganography
 */

public class Encoded {
    private final Bitmap bitmap;

    Encoded(Bitmap bitmap) {
        // The base for this encoded object is the a bitmap
        this.bitmap = bitmap;
    }

    public Bitmap intoBitmap() {
        return bitmap;
    }

}
