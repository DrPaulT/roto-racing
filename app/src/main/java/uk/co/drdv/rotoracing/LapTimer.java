package uk.co.drdv.rotoracing;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class LapTimer {

    private static final int STATUS_NOT_STARTED = 1;
    private static final int STATUS_START_LINE = 2;
    private static final int STATUS_HALF_WAY = 3;

    private Car car;
    private Handler handler;
    private int status = STATUS_NOT_STARTED;
    private long startTime;

    public LapTimer(Car car, Handler handler) {
        this.car = car;
        this.handler = handler;
    }

    public void update() {
        switch (status) {
            case STATUS_NOT_STARTED:
                // When the car crosses the start line we start the timer.
                if (car.x < (128 - 51) / 128.0 && car.y > (112 - 63) / 128.0) {
                    startTime = SystemClock.elapsedRealtime();
                    status = STATUS_START_LINE;
                }
                break;
            case STATUS_START_LINE:
                long now = SystemClock.elapsedRealtime();
                long secs = (now - startTime) / 1000;
                if (handler != null) {
                    Message message = handler.obtainMessage(MainActivity.CURRENT_LAP, (int) secs, 0);
                    handler.sendMessage(message);
                }
                // When we reach the opposite corner, flag half way.  This is to stop
                // blatant cheating by doing a u-turn immediately after crossing the start
                // line.
                if (car.x < (197 - 128) / 128.0 && car.y < -(167 - 128) / 128.0) {
                    status = STATUS_HALF_WAY;
                }
                break;
            case STATUS_HALF_WAY:
                now = SystemClock.elapsedRealtime();
                secs = (now - startTime) / 1000;
                if (handler != null) {
                    // Crossed the start line again?
                    if (car.x < (128 - 51) / 128.0 && car.y > (112 - 63) / 128.0) {
                        Message message = handler.obtainMessage(MainActivity.FINISHED_LAP, (int) secs, 0);
                        handler.sendMessage(message);
                        startTime = SystemClock.elapsedRealtime();
                        status = STATUS_START_LINE;
                    } else {
                        Message message = handler.obtainMessage(MainActivity.CURRENT_LAP, (int) secs, 0);
                        handler.sendMessage(message);
                    }
                }
                break;
        }
    }
}
