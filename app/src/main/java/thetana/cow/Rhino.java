package thetana.cow;

/**
 * Created by kc on 2017-11-12.
 */

public class Rhino extends Cow {
    public Rhino(int team, String id) {
        super(team, id);
        isAlive = true;
        this.mTeam = team;
        this.id = id;
        state = RUN;
        width = 220;
        imgWidth = 220;
        height = 200;
        maxHp = 200;
        hp = maxHp;
        if (mTeam == 0) {
            move1 = R.drawable.rhino_move1_left;
            move2 = R.drawable.rhino_move2_left;
            wait = R.drawable.rhino_wait_left;
            attack = R.drawable.rhino_attack_left;
            mp = 1;
            g_nX = 0;
            rp = 50;
        } else if (mTeam == 1) {
            move1 = R.drawable.rhino_move1_right;
            move2 = R.drawable.rhino_move2_right;
            wait = R.drawable.rhino_wait_right;
            attack = R.drawable.rhino_attack_right;
            mp = -1;
            g_nX = 1920 - width;
            rp = -50;
        }
    }

    public void run() {
        while (isAlive) {
            try {
                switch (state) {
                    case RUN:
                        imgWidth = 220;
                        g_nX += mp;
                        if (moveTime % 2 == 0) {
                            drawableId = move1;
                        } else {
                            drawableId = move2;
                        }
                        moveTime = (moveTime > 10000) ? 0 : moveTime + 1;
                        break;
                    case WAIT:
                        imgWidth = 220;
                        drawableId = wait;
                        attackTime = 0;
                        moveTime = 0;
                        break;
                    case ATTACK:
                        if (attackTime > 10) {
                            state = WAIT;
                            drawableId = wait;
                            attackTime = 0;
                            imgWidth = 220;
                        } else {
                            imgWidth = 300;
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
