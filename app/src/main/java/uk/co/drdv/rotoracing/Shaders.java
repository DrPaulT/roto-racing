package uk.co.drdv.rotoracing;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

public class Shaders {

    private static final String SIMPLE_TEXTURE_VERTEX_SHADER =
            "uniform mat4 u_mvpMatrix;  \n"
                    + "attribute vec4 a_position;  \n"
                    + "attribute vec2 a_texCoord;  \n"
                    + "varying vec2 v_texCoord;  \n"

                    + "void main() {  \n"
                    + "  v_texCoord = a_texCoord;  \n"
                    + "  gl_Position = u_mvpMatrix * a_position;  \n"
                    + "}  \n";

    private static final String SIMPLE_TEXTURE_FRAGMENT_SHADER =
            "precision mediump float;  \n"
                    + "uniform sampler2D s_texture;  \n"
                    + "varying vec2 v_texCoord;  \n"

                    + "void main() {  \n"
                    + "  gl_FragColor = texture2D(s_texture, v_texCoord);  \n"
                    + "}  \n";

    private static final String BACKGROUND_VERTEX_SHADER =
            "attribute vec4 a_position;  \n" +
                    "attribute vec2 a_texCoord;  \n" +
                    "varying vec2 v_texCoord;  \n" +
                    "void main() {  \n" +
                    "  v_texCoord = a_texCoord;  \n" +
                    "  gl_Position = a_position;  \n" +
                    "}  \n";

    private static final String BACKGROUND_FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require  \n" +
                    "precision mediump float;  \n" +
                    "uniform samplerExternalOES s_texture;  \n" +
                    "varying vec2 v_texCoord;  \n" +
                    "void main() {  \n" +
                    "  vec4 colour = texture2D(s_texture, v_texCoord);  \n" +
                    "  gl_FragColor = colour;  \n" +
                    "}  \n";

    private int simpleTexture;
    private int uMvpMatrixST;
    private int positionST;
    private int texCoordST;
    private int textureST;

    private int backgroundProgram;
    private int aPositionB;
    private int uTextureB;
    private int uTextureArrayB;

    public Shaders() {
        createSimpleTextureProgram();
        createBackgroundShaderProgram();
    }

    public void setSimpleTextureParameters(float[] mvpMatrix, FloatBuffer vtBuffer) {
        GLES20.glUseProgram(simpleTexture);
        GLES20.glUniformMatrix4fv(uMvpMatrixST, 1, false, mvpMatrix, 0);
        vtBuffer.position(0);
        GLES20.glVertexAttribPointer(positionST, 3, GLES20.GL_FLOAT, false, 20, vtBuffer);
        vtBuffer.position(3);
        GLES20.glVertexAttribPointer(texCoordST, 2, GLES20.GL_FLOAT, false, 20, vtBuffer);
        GLES20.glUniform1i(textureST, 0);
    }

    public void setBackgroundShaderParameters(FloatBuffer vtBuffer) {
        GLES20.glUseProgram(backgroundProgram);
        vtBuffer.position(0);
        GLES20.glVertexAttribPointer(aPositionB, 2, GLES20.GL_FLOAT, false, 16, vtBuffer);
        GLES20.glUniform1i(uTextureB, 1);
        vtBuffer.position(2);
        GLES20.glVertexAttribPointer(uTextureArrayB, 2, GLES20.GL_FLOAT, false, 16, vtBuffer);
    }

    private void createSimpleTextureProgram() {
        simpleTexture = createProgram(
                SIMPLE_TEXTURE_VERTEX_SHADER, SIMPLE_TEXTURE_FRAGMENT_SHADER);
        uMvpMatrixST = GLES20.glGetUniformLocation(simpleTexture, "u_mvpMatrix");
        positionST = GLES20.glGetAttribLocation(simpleTexture, "a_position");
        texCoordST = GLES20.glGetAttribLocation(simpleTexture, "a_texCoord");
        textureST = GLES20.glGetUniformLocation(simpleTexture, "s_texture");
        GLES20.glEnableVertexAttribArray(positionST);
        GLES20.glEnableVertexAttribArray(texCoordST);
        checkProgram(simpleTexture);
    }

    private void createBackgroundShaderProgram() {
        backgroundProgram = createProgram(BACKGROUND_VERTEX_SHADER, BACKGROUND_FRAGMENT_SHADER);
        aPositionB = GLES20.glGetAttribLocation(backgroundProgram, "a_position");
        uTextureArrayB = GLES20.glGetAttribLocation(backgroundProgram, "a_texCoord");
        uTextureB = GLES20.glGetUniformLocation(backgroundProgram, "s_texture");
        GLES20.glEnableVertexAttribArray(aPositionB);
        GLES20.glEnableVertexAttribArray(uTextureArrayB);
        checkProgram(backgroundProgram);
    }

    private int createProgram(String vertex, String fragment) {
        int vertexShader = createShader(GLES20.GL_VERTEX_SHADER, vertex);
        int fragmentShader = createShader(GLES20.GL_FRAGMENT_SHADER, fragment);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        checkProgram(program);
        return program;
    }

    private int createShader(int type, String sourceCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, sourceCode);
        GLES20.glCompileShader(shader);
        checkShader(shader);
        return shader;
    }

    private void checkShader(int shader) {
        Log.i("GL", "Shader info:" + shader + " " + GLES20.glGetShaderInfoLog(shader));
    }

    private void checkProgram(int program) {
        Log.i("GL", "Program info:" + program + " " + GLES20.glGetProgramInfoLog(program));
    }
}
