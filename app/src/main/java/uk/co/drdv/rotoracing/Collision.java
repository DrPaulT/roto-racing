package uk.co.drdv.rotoracing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Collision {

    public static final int ON_ROAD = 0x80;
    public static final int SLOWING_DOWN = 0xff;
    public static final int CRASH = 0x0; // Crash just means the car has stopped.

    private int[] pixels;
    private int w;
    private int h;

    public Collision(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.collision);
        w = bitmap.getWidth();
        h = bitmap.getHeight();
        pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
    }

    // Check collision bitmap pixel at the car position.
    public int getCarState(double x, double y) {
        int ix = (int) ((x + 1) * w / 2.0);
        if (ix < 0) {
            ix = 0;
        }
        if (ix >= w) {
            ix = w - 1;
        }
        int iy = (int) ((y + (double) h / w) * w / 2.0);
        if (iy < 0) {
            iy = 0;
        }
        if (iy >= h) {
            iy = h - 1;
        }
        return pixels[(h - iy - 1) * w + ix] & 0xff;
    }
}
