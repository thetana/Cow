package thetana.cow;

public class Cow extends Thread {
    boolean isAlive;
    String id;
    int mp;
    int hp;
    int maxHp;
    int rp;
    int g_nX;
    int width;
    int imgWidth;
    int height;
    int drawableId = R.drawable.cow_move1_left;
    int moveTime = 0;
    int move1;
    int move2;
    int wait;
    int waist;
    int attack;
    int attackTime = 0;
    int mTeam;
    int state;

    public static final int RUN = 0;
    public static final int WAIT = 1;
    public static final int ATTACK = 2;

    public Cow(int team, String id) {
        isAlive = true;
        this.mTeam = team;
        this.id = id;
        state = RUN;
        width = 200;
        imgWidth = width;
        height = 150;
        maxHp = 80;
        hp = maxHp;
        if (mTeam == 0) {
            move1 = R.drawable.cow_move1_left;
            move2 = R.drawable.cow_move2_left;
            wait = R.drawable.cow_wait_left;
            attack = R.drawable.cow_attack_left;
            mp = 3;
            g_nX = 0;
            rp = 70;
        } else if (mTeam == 1) {
            move1 = R.drawable.cow_move1_right;
            move2 = R.drawable.cow_move2_right;
            wait = R.drawable.cow_wait_right;
            attack = R.drawable.cow_attack_right;
            mp = -3;
            g_nX = 1920 - width;
            rp = -70;
        }
    }

    public void run() {
        while (isAlive) {
            try {
                switch (state) {
                    case RUN:
                        g_nX += mp;
                        if (moveTime % 2 == 0) {
                            drawableId = move1;
                        } else {
                            drawableId = move2;
                        }
                        moveTime = (moveTime > 10000) ? 0 : moveTime + 1;
                        break;
                    case WAIT:
                        drawableId = wait;
                        attackTime = 0;
                        moveTime = 0;
                        break;
                    case ATTACK:
                        if (attackTime > 7) {
                            state = WAIT;
                            drawableId = wait;
                            attackTime = 0;
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
