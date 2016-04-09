package uk.co.drdv.rotoracing;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Car {

    // Car is 60 x 28 pixels.  Scale to a nice size.
    private static final float HALF_X = 60f / 1000;
    private static final float HALF_Y = 28f / 1000;
    private static float MIN_S = 196f / 256;
    private static float MIN_T = 228f / 256;
    private static final float[] NORMAL = {0, 0, -1, 1};

    public float x;
    public float y;
    public float z;
    public float angle;

    private FloatBuffer vtBuffer;
    private float[] carModelviewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float[] c = {0, 0, 0, 1};
    private float[] r = new float[4];
    private boolean started = false;
    private Collision collision;

    public Car(Context context) {
        x = -0.741f;
        y = 0.2f;
        z = Scene.GROUND_Z;
        angle = 90;
        this.collision = new Collision(context);
        initialiseBuffer();
    }

    public void draw(Shaders shaders, float[] worldModelviewMatrix, float[] projectionMatrix) {
        Matrix.translateM(carModelviewMatrix, 0, worldModelviewMatrix, 0, x, y, z);
        Matrix.rotateM(carModelviewMatrix, 0, angle, 0, 0, 1);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, carModelviewMatrix, 0);
        shaders.setSimpleTextureParameters(mvpMatrix, vtBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vtBuffer.capacity() / 5);
    }

    public void move(float[] worldModelviewMatrix) {
        // Calculate angle between camera line of sight and car.
        c[0] = x;
        c[1] = y;
        c[2] = z;
        VectorMaths.normalise(c);
        Matrix.multiplyMV(r, 0, worldModelviewMatrix, 0, c, 0);
        double betweenAngle = Math.toDegrees(Math.acos(VectorMaths.dotProduct(r, NORMAL)));
        if (betweenAngle < 5) {
            started = true;
        }

        if (started) {
            Matrix.multiplyMV(r, 0, worldModelviewMatrix, 0, NORMAL, 0);
            angle -= r[0] * 4;
            double nx = x + Math.cos(Math.toRadians(angle)) / 250;
            double ny = y + Math.sin(Math.toRadians(angle)) / 250;
            if (nx > 1) {
                nx = 1;
            }
            if (nx < -1) {
                nx = -1;
            }
            if (ny > 224f / 256) {
                ny = 224f / 256;
            }
            if (ny < -224f / 256) {
                ny = -224f / 256;
            }
            switch (collision.getCarState(nx, ny)) {
                case Collision.ON_ROAD:
                    // All ok.
                    x = (float) nx;
                    y = (float) ny;
                    break;
                case Collision.SLOWING_DOWN:
                    // Only allow progress at half speed.
                    x = (float) (nx + x) / 2;
                    y = (float) (ny + y) / 2;
                    break;
                case Collision.CRASH:
                    // No movement allowed.
                    break;
            }
        }
    }

    private void initialiseBuffer() {
        float[] vertexCoords = {
                // x, y, z,
                // s, t.
                -HALF_X, -HALF_Y, 0,
                MIN_S, MIN_T,
                HALF_X, -HALF_Y, 0,
                1, MIN_T,
                HALF_X, HALF_Y, 0,
                1, 1,
                -HALF_X, -HALF_Y, 0,
                MIN_S, MIN_T,
                -HALF_X, HALF_Y, 0,
                MIN_S, 1,
                HALF_X, HALF_Y, 0,
                1, 1
        };
        vtBuffer = ByteBuffer.allocateDirect(vertexCoords.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vtBuffer.put(vertexCoords);
    }
}
