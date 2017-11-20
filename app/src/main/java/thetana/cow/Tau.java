package thetana.cow;

/**
 * Created by kc on 2017-11-12.
 */

public class Tau extends Cow {
    public Tau(int team, String id) {
        super(team, id);
        isAlive = true;
        this.mTeam = team;
        this.id = id;
        state = RUN;
        width = 180;
        imgWidth = 200;
        height = 200;
        maxHp = 100;
        hp = maxHp;
        if (mTeam == 0) {
            move1 = R.drawable.tau_move1_left;
            move2 = R.drawable.tau_move2_left;
            wait = R.drawable.tau_wait_left;
            attack = R.drawable.tau_attack_left;
            mp = 2;
            g_nX = 0;
            rp = 150;
        } else if (mTeam == 1) {
            move1 = R.drawable.tau_move1_right;
            move2 = R.drawable.tau_move2_right;
            wait = R.drawable.tau_wait_right;
            attack = R.drawable.tau_attack_right;
            mp = -2;
            g_nX = 1920 - width;
            rp = -150;
        }
    }

    public void run() {
        while (isAlive) {
            try {
                switch (state) {
                    case RUN:
                        imgWidth = 200;
                        g_nX += mp;
                        if (moveTime % 2 == 0) {
                            drawableId = move1;
                        } else {
                            drawableId = move2;
                        }
                        moveTime = (moveTime > 10000) ? 0 : moveTime + 1;
                        break;
                    case WAIT:
                        imgWidth = 130;
                        drawableId = wait;
                        attackTime = 0;
                        moveTime = 0;
                        break;
                    case ATTACK:
                        imgWidth = 200;
                        if (attackTime > 10) {
                            state = WAIT;
                            drawableId = wait;
                            attackTime = 0;
                            imgWidth = 130;
                        } else {
                            drawableId = attack;
                        }
                        attackTime++;
                        break;
                }
                Thread.sleep(17);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
