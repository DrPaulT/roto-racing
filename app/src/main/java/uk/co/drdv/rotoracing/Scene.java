package uk.co.drdv.rotoracing;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Scene {

    // Distance from road surface to GL camera.
    public static final float GROUND_Z = -0.7f;

    private FloatBuffer vtBuffer;
    private ShortBuffer iBuffer;

    public Scene() {
        create();
    }

    public void draw(Shaders shaders, float[] mvpMatrix) {
        shaders.setSimpleTextureParameters(mvpMatrix, vtBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, iBuffer.capacity(),
                GLES20.GL_UNSIGNED_SHORT, iBuffer);
    }

    private void create() {
        float mountainTopZ = GROUND_Z + 0.3f;
        float mountainMaxS = 192 / 256f;
        float mountainTopT = (256 - 28) / 256f;
        float aspect = 224f / 256f;
        float[] vertexCoords = {
                // Interleaved vertex position and texture coordinate.
                // x, y, z,
                // s, t
                // Track
                1, -aspect, GROUND_Z,
                1, aspect,
                1, aspect, GROUND_Z,
                1, 0,
                -1, -aspect, GROUND_Z,
                0, aspect,
                -1, aspect, GROUND_Z,
                0, 0,
                // Mountains
                // Side 1
                -1, aspect, GROUND_Z,
                0, 1,
                1, aspect, GROUND_Z,
                mountainMaxS, 1,
                -1, aspect, mountainTopZ,
                0, mountainTopT,
                1, aspect, mountainTopZ,
                mountainMaxS, mountainTopT,
                // Side 2
                -1, -aspect, GROUND_Z,
                mountainMaxS, 1,
                1, -aspect, GROUND_Z,
                0, 1,
                -1, -aspect, mountainTopZ,
                mountainMaxS, mountainTopT,
                1, -aspect, mountainTopZ,
                0, mountainTopT,
                // Side 3
                1, -aspect, GROUND_Z,
                mountainMaxS, 1,
                1, aspect, GROUND_Z,
                0, 1,
                1, -aspect, mountainTopZ,
                mountainMaxS, mountainTopT,
                1, aspect, mountainTopZ,
                0, mountainTopT,
                // Side 4
                -1, -aspect, GROUND_Z,
                0, 1,
                -1, aspect, GROUND_Z,
                mountainMaxS, 1,
                -1, -aspect, mountainTopZ,
                0, mountainTopT,
                -1, aspect, mountainTopZ,
                mountainMaxS, mountainTopT
        };

        vtBuffer = ByteBuffer.allocateDirect(vertexCoords.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vtBuffer.put(vertexCoords);

        short[] indices = {
                // Track
                0, 1, 2, 1, 2, 3,
                // Mountains
                4, 5, 7, 4, 7, 6,
                8, 9, 11, 8, 11, 10,
                12, 13, 15, 12, 15, 14,
                16, 17, 19, 16, 19, 18
        };
        iBuffer = ByteBuffer.allocateDirect(indices.length * 4)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        iBuffer.put(indices);
        iBuffer.rewind();
    }
}
