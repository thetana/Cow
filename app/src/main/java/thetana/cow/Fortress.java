package thetana.cow;

/**
 * Created by kc on 2017-10-21.
 */

public class Fortress extends Cow {
    boolean isAlive;
    int drawableId = R.drawable.fortress1;
    int run = R.drawable.fortress1;
    int broken = R.drawable.fortress1_broken;
    public static final int RUN = 0;
    public static final int BROKEN = 1;

    public Fortress(int team, String id) {
        super(team, id);
        isAlive = true;
        this.mTeam = team;
        this.id = id;
        state = RUN;
        maxHp = 300;
        hp = maxHp;
        width = 200;
        height = 250;
        if (mTeam == 0) {
            mp = 0;
            g_nX = 0;
            rp = 0;
        } else if (mTeam == 1) {
            mp = -0;
            g_nX = 1920 - width;
            rp = 0;
        }
    }

    public void run() {
        while (isAlive) {
            try {
                switch (state) {
                    case RUN:
                        drawableId = run;
                        break;
                    case BROKEN:
                        drawableId = broken;
                        break;
                }
                Thread.sleep(17);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
