package uk.co.drdv.rotoracing;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

public class Background implements SurfaceTexture.OnFrameAvailableListener {

    private Shaders shaders;
    private OrientationGlSurfaceView orientationGlSurfaceView;
    private FloatBuffer vtBuffer;
    private ShortBuffer iBuffer;
    private int[] texture = new int[1];
    private SurfaceTexture surfaceTexture;
    private Camera camera;
    private boolean shouldUpdate = false;
    private float[] projectionMatrix = new float[16];

    public Background(Shaders shaders, OrientationGlSurfaceView orientationGlSurfaceView,
                      int width, int height) {
        this.shaders = shaders;
        this.orientationGlSurfaceView = orientationGlSurfaceView;
        createBuffers();
        initialiseTexture();
        initialiseCamera(width, height);
    }


    public void draw() {
        if (shouldUpdate) {
            surfaceTexture.updateTexImage();
            shouldUpdate = false;
        }
        shaders.setBackgroundShaderParameters(vtBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, iBuffer);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (shouldUpdate == false) {
            shouldUpdate = true;
            orientationGlSurfaceView.requestRender();
        }
    }

    public void shutdown() {
        shouldUpdate = false;
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void createBuffers() {
        vtBuffer = ByteBuffer.allocateDirect(4 * 4 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        iBuffer = ByteBuffer.allocateDirect(4 * 2 * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();

        // Interleaved position and texture coordinates, x, y, s, t.  z = 0 assumed.
        float[] coords = {
                1f, -1f, 1f, 0f,
                -1f, -1f, 1f, 1f,
                1f, 1f, 0f, 0f,
                -1f, 1f, 0f, 1f
        };
        vtBuffer.put(coords);

        short[] indices = {
                0, 2, 1, 1, 2, 3
        };
        iBuffer.put(indices);
        iBuffer.rewind();
    }

    private void initialiseTexture() {
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        surfaceTexture = new SurfaceTexture(texture[0]);
        surfaceTexture.setOnFrameAvailableListener(this);
    }

    private void initialiseCamera(int width, int height) {
        Matrix.perspectiveM(projectionMatrix, 0, 90, (float) width / height, 0.1f, 100f);
        camera = Camera.open();
        try {
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GLES20.glViewport(0, 0, width, height);
        Camera.Parameters param = camera.getParameters();
        List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
        if (previewSizes.size() > 0) {
            int i;
            for (i = 0; i < previewSizes.size(); i++) {
                if (previewSizes.get(i).width < width || previewSizes.get(i).height < height)
                    break;
            }
            if (i > 0) {
                i--;
            }
            param.setPreviewSize(previewSizes.get(i).width, previewSizes.get(i).height);
        }

        List<int[]> fpsRange = param.getSupportedPreviewFpsRange();
        int frameMin = 0;
        int frameMax = 0;
        for (int i = 0; i < fpsRange.size(); i++) {
            if (fpsRange.get(i)[1] > frameMax) {
                frameMin = fpsRange.get(i)[0];
                frameMax = fpsRange.get(i)[1];
            }
        }
        param.setPreviewFpsRange(frameMin, frameMax);
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        param.setPreviewFormat(ImageFormat.NV21); // Default.
        camera.setParameters(param);
        camera.startPreview();
    }
}
