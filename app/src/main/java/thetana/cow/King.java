package thetana.cow;

/**
 * Created by kc on 2017-11-12.
 */

public class King extends Cow {
    public King(int team, String id) {
        super(team, id);
        isAlive = true;
        this.mTeam = team;
        this.id = id;
        state = RUN;
        width = 250;
        imgWidth = 250;
        height = 250;
        maxHp = 300;
        hp = maxHp;
        if (mTeam == 0) {
            move1 = R.drawable.king_move1_left;
            move2 = R.drawable.king_move2_left;
            wait = R.drawable.king_wait_left;
            attack = R.drawable.king_attack_left;
            mp = 3;
            g_nX = 0;
            rp = 200;
        } else if (mTeam == 1) {
            move1 = R.drawable.king_move1_right;
            move2 = R.drawable.king_move2_right;
            wait = R.drawable.king_wait_right;
            attack = R.drawable.king_attack_right;
            mp = -3;
            g_nX = 1920 - width;
            rp = -200;
        }
    }

    public void run() {
        while (isAlive) {
            try {
                switch (state) {
                    case RUN:
                        imgWidth = 250;
                        g_nX += mp;
                        if (moveTime % 2 == 0) {
                            drawableId = move1;
                        } else {
                            drawableId = move2;
                        }
                        moveTime = (moveTime > 10000) ? 0 : moveTime + 1;
                        break;
                    case WAIT:
                        imgWidth = 250;
                        drawableId = wait;
                        attackTime = 0;
                        moveTime = 0;
                        break;
                    case ATTACK:
                        if (attackTime > 10) {
                            state = WAIT;
                            attackTime = 0;
                        } else {
                            imgWidth = 500;
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
