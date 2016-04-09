package uk.co.drdv.rotoracing;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.SurfaceHolder;

public class OrientationGlSurfaceView extends GLSurfaceView {

    private OrientationRenderer orientationRenderer;

    public OrientationGlSurfaceView(Context context, Handler handler) {
        super(context, null);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(true);
        orientationRenderer = new OrientationRenderer(context, this, handler);
        setRenderer(orientationRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        orientationRenderer.onPause();
        super.surfaceDestroyed(holder);
    }
}
