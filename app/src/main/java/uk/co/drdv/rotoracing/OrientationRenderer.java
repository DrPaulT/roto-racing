package uk.co.drdv.rotoracing;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrientationRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private OrientationGlSurfaceView orientationGlSurfaceView;
    private Handler handler;
    private Shaders shaders;
    private Scene scene;
    private GlCamera glCamera;
    private Car car;
    private LapTimer lapTimer;
    private Background background;

    public OrientationRenderer(Context context, OrientationGlSurfaceView orientationGlSurfaceView,
                               Handler handler) {
        this.context = context;
        this.orientationGlSurfaceView = orientationGlSurfaceView;
        this.handler = handler;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.7f, 1f, 1f);
        GLES20.glEnable(GLES20.GL_DITHER);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        scene = new Scene();
        new TextureHelper(context);
        car = new Car(context);
        lapTimer = new LapTimer(car, handler);
        glCamera = new GlCamera(context, car);
        onResume();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (shaders == null) {
            shaders = new Shaders();
            background = new Background(shaders, orientationGlSurfaceView, width, height);
            glCamera.initialise(width, height);
            GLES20.glViewport(0, 0, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        background.draw();
        scene.draw(shaders, glCamera.mvpMatrix);
        car.draw(shaders, glCamera.modelViewMatrix, glCamera.projectionMatrix);
        lapTimer.update();
    }

    protected void onResume() {
        glCamera.onResume();
    }

    protected void onPause() {
        background.shutdown();
        glCamera.onPause();
    }
}
