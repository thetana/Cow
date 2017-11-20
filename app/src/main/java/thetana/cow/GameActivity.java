package thetana.cow;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends Activity {
    private Messenger messenger = null;
    private Messenger reply = new Messenger(new GameActivity.ReplyHandler());
    public static GLView mGLView;
    public static GameThread mThread;
    SharedPreferences sp;
    String myId;
    String myName;
    String roomId;
    LinearLayout ll_game;
    EditText et_text;
    Button bt_send;
    String[] ids;
    String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("me", 0);
        myId = sp.getString("id", "");
        myName = sp.getString("name", "");
        setContentView(R.layout.activity_game);
        ll_game = (LinearLayout) findViewById(R.id.ll_game_a_game);
        et_text = (EditText) findViewById(R.id.et_text_a_game);
        bt_send = (Button) findViewById(R.id.bt_send_a_game);
        try {
            ids = new String[getIntent().getIntExtra("roomSect", 4)];
            names = new String[getIntent().getIntExtra("roomSect", 4)];
            JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("players"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = new JSONObject(jsonArray.getString(i));
                ids[object.getInt("userTeam")] = object.getString("userId");
                names[object.getInt("userTeam")] = object.getString("userName");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGLView = new GLView(GameActivity.this, getIntent().getIntExtra("team", -1));
        roomId = getIntent().getStringExtra("roomId");
        mThread = new GameThread(GameActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mGLView.setLayoutParams(params);
        ll_game.addView(mGLView);
        mThread.start();

        Intent i = new Intent(getApplicationContext(), SocketService.class);
        bindService(i, conn, Context.BIND_AUTO_CREATE);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "setText");
                        jsonObject.put("roomId", roomId);
                        jsonObject.put("userId", myId);
                        jsonObject.put("userName", myName);
                        jsonObject.put("text", et_text.getText());
                        et_text.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    msg.obj = jsonObject;
                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            Message msg = Message.obtain(null, SocketService.CONNECT);

            msg.arg1 = 0;
            msg.replyTo = reply;
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
        }
    };

    private class ReplyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg.obj.toString());
                String order = jsonObject.getString("order");
                if (order.equals("setCow")) {
                    mGLView.setCow(jsonObject.getInt("team"), jsonObject.getString("cowId"), jsonObject.getString("what"));
                } else if (order.equals("attackCow")) {
                    mGLView.cowMap.get(jsonObject.getString("from")).state = Cow.ATTACK;
                    mGLView.cowMap.get(jsonObject.getString("to")).hp = jsonObject.getInt("hp");
                    if (jsonObject.getInt("hp") <= 0) {
                        mGLView.cowMap.get(jsonObject.getString("to")).isAlive = false;
                    }
                } else if (order.equals("setText")) {
                    mGLView.setTextBitmap(jsonObject.getString("userName") + " : " + jsonObject.getString("text"), 60);
                } else if (order.equals("endGame")) {
                    mGLView.endGame(jsonObject.getInt("loser"));
                } else if (order.equals("outRoom")) {
                    Intent intent = new Intent(GameActivity.this, LobbyActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void setCow(String what) {
        if (messenger != null) {
            Message msg = Message.obtain(null, SocketService.SETCOW);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("order", "setCow");
                jsonObject.put("roomId", roomId);
                jsonObject.put("userId", myId);
                jsonObject.put("what", what);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg.obj = jsonObject;
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    void goLobby() {
        if (messenger != null) {
            Message msg = Message.obtain(null, SocketService.SETCOW);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("order", "outRoom");
                jsonObject.put("roomId", roomId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg.obj = jsonObject;
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    static {
        System.loadLibrary("native-lib");
    }
}
