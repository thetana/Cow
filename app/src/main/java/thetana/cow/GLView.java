package thetana.cow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private Context mContext;
    ArrayList<Cow> cows = new ArrayList<Cow>();
    ArrayList<TextBitmap> texts = new ArrayList<TextBitmap>();
    HashMap<String, Cow> cowMap = new HashMap<String, Cow>();
    ArrayList<Cow> leftCows = new ArrayList<Cow>();
    ArrayList<Cow> rightCows = new ArrayList<Cow>();
    int id = 0;
    int g_nX = 0;
    int mTeam = 0;
    public static final int COOLTIME_COW = 1;
    public static final int COST_COW = 10;
    public static final int COST_TAU = 30;
    public static final int COST_RHINO = 50;
    int coolTime_cow = 0;
    ImageHelper ih = new ImageHelper();
    int cost = 0;
    int costCo = 0;
    int maxCost = 200;
    Fortress fortress1;
    Fortress fortress2;
    boolean isEnd = false;
    String endText;
    public static final int COW = 0;

    private static native void nativeCreated();

    private static native void nativeChanged(int w, int h);

    private static native void nativeUpdateGame();

    private static native void nativeOnTouchEvent(int x, int y, int touchFlag);

    private static native void nativeSetTextureData(int[] pixels, int width, int height, int w, int h, int x, int y);

    public GLView(Context context, int team) {
        super(context);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setRenderer(this);
        this.requestFocus();
        this.setRenderMode(RENDERMODE_WHEN_DIRTY);
        this.setFocusableInTouchMode(true);
        mContext = context;
        this.mTeam = team;
        Collections.synchronizedMap(cowMap); //해쉬맵 동기화 설정.
        isEnd = false;
        fortress1 = new Fortress(0, "0");
        fortress2 = new Fortress(1, "1");
        fortress1.run = (fortress1.mTeam == mTeam) ? R.drawable.fortress1 : R.drawable.fortress2;
        fortress2.run = (fortress2.mTeam == mTeam) ? R.drawable.fortress1 : R.drawable.fortress2;
        fortress1.drawableId = fortress1.run;
        fortress2.drawableId = fortress2.run;
        cowMap.put("0", fortress1);
        cowMap.put("1", fortress2);
        leftCows.add(fortress1);
        rightCows.add(fortress2);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        nativeUpdateGame();
        if (costCo > 1) cost += (cost < maxCost) ? 1 : 0;
        coolTime_cow -= (coolTime_cow > 0) ? 1 : 0;
        costCo = (costCo < 2) ? costCo + 1 : 0;

        for (int j = 0; j < ((GameActivity) mContext).names.length; j++) {
            TextView tv = new TextView(mContext);
            tv.setText(String.valueOf(j + 1) + "P : " + ((GameActivity) mContext).names[j]);
            tv.setTextColor(Color.argb(255, 0, 0, 0));
            tv.setTextSize(18);
            if (j % 2 == 1) tv.setGravity(Gravity.RIGHT);
            tv.layout(0, 0, 900, 50);
            tv.setDrawingCacheEnabled(true);
            tv.buildDrawingCache();
            Bitmap b = Bitmap.createBitmap(tv.getDrawingCache(false));
            tv.setDrawingCacheEnabled(false);
            tv.destroyDrawingCache();
            int[] pixels1 = new int[b.getWidth() * b.getHeight()];
            b.getPixels(pixels1, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
            for (int i = 0; i < pixels1.length; ++i) {
                pixels1[i] = ((pixels1[i] & 0xff00ff00)) | ((pixels1[i] & 0x000000ff) << 16) | ((pixels1[i] & 0x00ff0000) >> 16);
            }
            int x = 10;
            if (j % 2 == 1) x = 1020;
            nativeSetTextureData(pixels1, b.getWidth(), b.getHeight(), b.getWidth(), b.getHeight(), x, 10 + (50 * j / 2));
        }

        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bt_cow);
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = ((pixels[i] & 0xff00ff00)) | ((pixels[i] & 0x000000ff) << 16) | ((pixels[i] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), 150, 150, 10, 660);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bt_tau);
        pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = ((pixels[i] & 0xff00ff00)) | ((pixels[i] & 0x000000ff) << 16) | ((pixels[i] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), 150, 150, 160, 660);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bt_rhino);
        pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = ((pixels[i] & 0xff00ff00)) | ((pixels[i] & 0x000000ff) << 16) | ((pixels[i] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), 150, 150, 310, 660);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), fortress1.drawableId);
        pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int j = 0; j < pixels.length; ++j) {
            pixels[j] = ((pixels[j] & 0xff00ff00)) | ((pixels[j] & 0x000000ff) << 16) | ((pixels[j] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), fortress1.width, fortress1.height, fortress1.g_nX, 650 - fortress1.height);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hp);
        pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int j = 0; j < pixels.length; ++j) {
            pixels[j] = ((pixels[j] & 0xff00ff00)) | ((pixels[j] & 0x000000ff) << 16) | ((pixels[j] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), fortress1.width * fortress1.hp / fortress1.maxHp, 50, fortress1.g_nX, 600 - fortress1.height);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hpbar);
        pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int j = 0; j < pixels.length; ++j) {
            pixels[j] = ((pixels[j] & 0xff00ff00)) | ((pixels[j] & 0x000000ff) << 16) | ((pixels[j] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), fortress1.width, 50, fortress1.g_nX, 600 - fortress1.height);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), fortress2.drawableId);
        pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int j = 0; j < pixels.length; ++j) {
            pixels[j] = ((pixels[j] & 0xff00ff00)) | ((pixels[j] & 0x000000ff) << 16) | ((pixels[j] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), fortress2.width, fortress2.height, fortress2.g_nX, 650 - fortress2.height);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hp);
        pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int j = 0; j < pixels.length; ++j) {
            pixels[j] = ((pixels[j] & 0xff00ff00)) | ((pixels[j] & 0x000000ff) << 16) | ((pixels[j] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), fortress2.width * fortress2.hp / fortress2.maxHp, 50, fortress2.g_nX, 600 - fortress2.height);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hpbar);
        pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int j = 0; j < pixels.length; ++j) {
            pixels[j] = ((pixels[j] & 0xff00ff00)) | ((pixels[j] & 0x000000ff) << 16) | ((pixels[j] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), fortress2.width, 50, fortress2.g_nX, 600 - fortress2.height);

        for (int i = 0; cows.size() > i; i++) {
            if (cows.get(i) != null && cows.get(i).isAlive) {

                if (cows.get(i).state != Cow.ATTACK) {
                    if (cows.get(i).mTeam == 0) {
                        for (int j = 0; j < rightCows.size(); j++) {
                            Cow enemy = rightCows.get(j);
                            if (enemy.isAlive && cows.get(i).g_nX + cows.get(i).width < enemy.g_nX + enemy.width
                                    && cows.get(i).g_nX + cows.get(i).width + cows.get(i).rp > enemy.g_nX) {
                                cows.get(i).state = Cow.WAIT;
                                break;
                            } else {
                                cows.get(i).state = Cow.RUN;
                            }
                        }
                    } else if (cows.get(i).mTeam == 1) {
                        for (int j = 0; j < leftCows.size(); j++) {
                            Cow enemy = leftCows.get(j);
                            if (enemy.isAlive && cows.get(i).g_nX > enemy.g_nX
                                    && cows.get(i).g_nX + cows.get(i).rp < enemy.g_nX + enemy.width) {
                                cows.get(i).state = Cow.WAIT;
                                break;
                            } else {
                                cows.get(i).state = Cow.RUN;
                            }
                        }
                    }
                }
                id = cows.get(i).drawableId;
                g_nX = (cows.get(i).state == Cow.ATTACK) ? (int) (cows.get(i).g_nX + (cows.get(i).rp * 0.9)) : cows.get(i).g_nX;
                bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
                pixels = new int[bmp.getWidth() * bmp.getHeight()];
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
                for (int j = 0; j < pixels.length; ++j) {
                    pixels[j] = ((pixels[j] & 0xff00ff00)) | ((pixels[j] & 0x000000ff) << 16) | ((pixels[j] & 0x00ff0000) >> 16);
                }
                nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), cows.get(i).imgWidth, cows.get(i).height, g_nX, 650 - cows.get(i).height);
            }
        }

        TextView tv_cost = new TextView(mContext);
        tv_cost.setText("COST : " + cost + " / " + maxCost);
        tv_cost.setTextColor(Color.argb(255, 0, 0, 0));
        tv_cost.setGravity(Gravity.RIGHT);
        tv_cost.setTextSize(14);
        tv_cost.layout(0, 0, 300, 50);
        tv_cost.setDrawingCacheEnabled(true);
        tv_cost.buildDrawingCache();
        Bitmap bb = Bitmap.createBitmap(tv_cost.getDrawingCache(false));
        tv_cost.setDrawingCacheEnabled(false);
        tv_cost.destroyDrawingCache();
        int[] pixels12 = new int[bb.getWidth() * bb.getHeight()];
        bb.getPixels(pixels12, 0, bb.getWidth(), 0, 0, bb.getWidth(), bb.getHeight());
        for (int i = 0; i < pixels12.length; ++i) {
            pixels12[i] = ((pixels12[i] & 0xff00ff00)) | ((pixels12[i] & 0x000000ff) << 16) | ((pixels12[i] & 0x00ff0000) >> 16);
        }
        nativeSetTextureData(pixels12, bb.getWidth(), bb.getHeight(), bb.getWidth(), bb.getHeight(), 1600, 700);

        ArrayList<TextBitmap> deadTexts = new ArrayList<TextBitmap>();
        for (int j = 0; texts.size() > j; j++) {
            TextView tv = new TextView(mContext);
            tv.setText(texts.get(j).text);
            int a = (texts.get(j).life < 31) ? texts.get(j).life + 100 : 255;
            tv.setTextColor(Color.argb(a, 0, 0, 0));
            tv.setTextSize(18);
            tv.layout(0, 0, 900, 50);
            tv.setDrawingCacheEnabled(true);
            tv.buildDrawingCache();
            Bitmap b = Bitmap.createBitmap(tv.getDrawingCache(false));
            tv.setDrawingCacheEnabled(false);
            tv.destroyDrawingCache();
            int[] pixels1 = new int[b.getWidth() * b.getHeight()];
            b.getPixels(pixels1, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
            for (int i = 0; i < pixels1.length; ++i) {
                pixels1[i] = ((pixels1[i] & 0xff00ff00)) | ((pixels1[i] & 0x000000ff) << 16) | ((pixels1[i] & 0x00ff0000) >> 16);
            }
            nativeSetTextureData(pixels1, b.getWidth(), b.getHeight(), b.getWidth(), b.getHeight(), 30, 100 + (50 * j));
            texts.get(j).life--;
            if (texts.get(j).life <= 0) {
                texts.get(j).isAlive = false;
                deadTexts.add(texts.get(j));
            }
        }
        for (int j = 0; deadTexts.size() > j; j++) {
            if (!deadTexts.get(j).isAlive) {
                texts.remove(deadTexts.get(j));
            }
        }

        if(((GameActivity) mContext).mB != null) {
            bmp = ih.getRoundedCornerBitmap(((GameActivity) mContext).mB);
            pixels = new int[bmp.getWidth() * bmp.getHeight()];
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
            for (int j = 0; j < pixels.length; ++j) {
                pixels[j] = ((pixels[j] & 0xff00ff00)) | ((pixels[j] & 0x000000ff) << 16) | ((pixels[j] & 0x00ff0000) >> 16);
            }
            nativeSetTextureData(pixels, bmp.getWidth(), bmp.getHeight(), 300, 300, 700, 200);
        }

        if (isEnd) {
            TextView tv = new TextView(mContext);
            tv.setText(endText);
            tv.setTextColor(Color.argb(255, 0, 0, 0));
            tv.setTextSize(36);
            tv.layout(0, 0, 200, 100);
            tv.setDrawingCacheEnabled(true);
            tv.buildDrawingCache();
            Bitmap b = Bitmap.createBitmap(tv.getDrawingCache(false));
            tv.setDrawingCacheEnabled(false);
            tv.destroyDrawingCache();
            int[] pixels1 = new int[b.getWidth() * b.getHeight()];
            b.getPixels(pixels1, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
            for (int i = 0; i < pixels1.length; ++i) {
                pixels1[i] = ((pixels1[i] & 0xff00ff00)) | ((pixels1[i] & 0x000000ff) << 16) | ((pixels1[i] & 0x00ff0000) >> 16);
            }
            nativeSetTextureData(pixels1, b.getWidth(), b.getHeight(), b.getWidth(), b.getHeight(), 860, 300);

            tv = new TextView(mContext);
            tv.setText("로비로 이동");
            tv.setTextColor(Color.argb(255, 0, 0, 0));
            tv.setTextSize(30);
            tv.layout(0, 0, 300, 100);
            tv.setDrawingCacheEnabled(true);
            tv.buildDrawingCache();
            b = Bitmap.createBitmap(tv.getDrawingCache(false));
            tv.setDrawingCacheEnabled(false);
            tv.destroyDrawingCache();
            pixels1 = new int[b.getWidth() * b.getHeight()];
            b.getPixels(pixels1, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
            for (int i = 0; i < pixels1.length; ++i) {
                pixels1[i] = ((pixels1[i] & 0xff00ff00)) | ((pixels1[i] & 0x000000ff) << 16) | ((pixels1[i] & 0x00ff0000) >> 16);
            }
            nativeSetTextureData(pixels1, b.getWidth(), b.getHeight(), b.getWidth(), b.getHeight(), 800, 400);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        nativeChanged(w, h);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        nativeCreated();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() > 10 && event.getX() < 10 + 150 && event.getY() > 660 && event.getY() < 660 + 150 && COST_COW <= cost && coolTime_cow <= 0) {
                ((GameActivity) mContext).setCow("cow");
                coolTime_cow = COOLTIME_COW;
                cost = cost - COST_COW;
            } else if (event.getX() > 160 && event.getX() < 160 + 150 && event.getY() > 660 && event.getY() < 660 + 150 && COST_TAU <= cost) {
                ((GameActivity) mContext).setCow("tau");
                cost = cost - COST_TAU;
            } else if (event.getX() > 310 && event.getX() < 310 + 150 && event.getY() > 660 && event.getY() < 660 + 150 && COST_RHINO <= cost) {
                ((GameActivity) mContext).setCow("rhino");
                cost = cost - COST_RHINO;
            } else if (isEnd && event.getX() > 800 && event.getX() < 800 + 300 && event.getY() > 400 && event.getY() < 400 + 100) {
                ((GameActivity) mContext).goLobby();
            }
        }
        return true;
    }

    void setCow(int team, String id, String what) {
        if (!isEnd) {
            Cow cow = null;
            if (what.equals("cow")) {
                cow = new Cow(team, id);
            } else if (what.equals("tau")) {
                cow = new Tau(team, id);
            } else if (what.equals("rhino")) {
                cow = new Rhino(team, id);
            }
            cowMap.put(id, cow);
            cows.add(cow);
            if (team == 0) {
                leftCows.add(cow);
            } else if (team == 1) {
                rightCows.add(cow);
            }
            cow.start();
        }
    }

    void setTextBitmap(String text, int life) {
        TextBitmap textBitmap = new TextBitmap(text, life);
        texts.add(textBitmap);
    }

    void endGame(int loser) {
        if (loser == 0) {
            fortress1.broken = (fortress1.mTeam == mTeam) ? R.drawable.fortress1_broken : R.drawable.fortress2_broken;
            fortress1.drawableId = fortress1.broken;
        } else if (loser == 1) {
            fortress2.broken = (fortress2.mTeam == mTeam) ? R.drawable.fortress1_broken : R.drawable.fortress2_broken;
            fortress2.drawableId = fortress2.broken;
        }
        endText = (loser == mTeam) ? "패배" : "승리";
        isEnd = true;
        for (int i = 0; cows.size() > i; i++) {
            cows.get(i).mp = 0;
        }
    }

}
