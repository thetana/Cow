package thetana.cow;

import android.content.Context;

/**
 * Created by kc on 2017-10-05.
 */

public class GameThread extends Thread {
    private boolean isRun;

    public GameThread(Context context) {
        isRun = true;
    }
    public void run() {
        while (isRun) {
            try {
                GameActivity.mGLView.requestRender();
                Thread.sleep(17);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
