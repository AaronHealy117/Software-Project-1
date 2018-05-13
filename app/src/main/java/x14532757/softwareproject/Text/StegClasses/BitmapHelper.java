package x14532757.softwareproject.Text.StegClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorInt;

import java.security.SecureRandom;

/**
 * Created by StealthCopter
 *
 * Code Copied and Modified from:
 * Title: steganography
 * Author: stealthcopter
 * Date: 09/12/15
 * Availability: https://github.com/stealthcopter/steganography
 */

public class BitmapHelper {

    /**
     * Create a bitmap of specific size with a specific color
     *
     * @param w - width
     * @param h - height
     * @param color - color integer (not resource id)
     * @return - bitmap of random(ish) color
     */

    //create a random bitmap with a random colour
    public static Bitmap createTestBitmap(int w, int h, @ColorInt Integer color) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (color == null) {
            int colors[] = new int[] { Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.WHITE };
            SecureRandom rgen = new SecureRandom();
            color = colors[rgen.nextInt(colors.length - 1)];
        }

        canvas.drawColor(color);
        return bitmap;
    }
}
