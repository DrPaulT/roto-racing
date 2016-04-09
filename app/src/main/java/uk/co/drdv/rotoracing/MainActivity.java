package uk.co.drdv.rotoracing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements Handler.Callback {

    public static final int CURRENT_LAP = 1;
    public static final int FINISHED_LAP = 2;

    private TextView lapTimeTextView;
    private TextView bestLapTextView;
    private int best = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        lapTimeTextView = (TextView) findViewById(R.id.lap_time_text_view);
        bestLapTextView = (TextView) findViewById(R.id.best_time_text_view);
        OrientationGlSurfaceView orientationGlSurfaceView = new OrientationGlSurfaceView(
                getApplicationContext(), new Handler(this));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        orientationGlSurfaceView.setLayoutParams(params);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.addView(orientationGlSurfaceView, 0);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CURRENT_LAP:
                if (lapTimeTextView != null) {
                    lapTimeTextView.setText(String.format("Lap: %1$d", msg.arg1));
                }
                return true;
            case FINISHED_LAP:
                if (msg.arg1 < best) {
                    best = msg.arg1;
                    bestLapTextView.setText(String.format("Best: %1$d", msg.arg1));
                }
                return true;
        }
        return false;
    }

}
