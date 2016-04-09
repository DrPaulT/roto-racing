package uk.co.drdv.rotoracing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import java.nio.IntBuffer;

public class TextureHelper {

    private Context context;
    private int textureWidth;
    private int textureHeight;
    private int[] textures = new int[1];

    public TextureHelper(Context context) {
        this.context = context;
        makeTexture();
    }

    private void makeTexture() {
        int[] pixels = loadBitmapAsPixels();
        argbToAbgr(pixels);
        createGlTexture(pixels);
    }

    private int[] loadBitmapAsPixels() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.track_hollow, options);
        textureWidth = bitmap.getWidth();
        textureHeight = bitmap.getHeight();
        int[] pixels = new int[textureWidth * textureHeight];
        bitmap.getPixels(pixels, 0, textureWidth, 0, 0,
                textureWidth, textureHeight);
        return pixels;
    }

    private void argbToAbgr(int[] pixels) {
        int length = pixels.length;
        for (int i = 0; i < length; i++) {
            int red = (pixels[i] >> 16) & 0xff;
            int green = (pixels[i] >> 8) & 0xff;
            int blue = pixels[i] & 0xff;
            int alpha = pixels[i] & 0xff000000;
            pixels[i] = alpha | (green << 8) | (red) | (blue << 16);
        }
    }

    private void createGlTexture(int[] pixels) {
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        IntBuffer intBuffer = IntBuffer.wrap(pixels);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,
                GLES20.GL_RGBA, textureWidth, textureHeight, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
    }
}
