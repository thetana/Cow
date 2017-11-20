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
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class MakeRoomActivity extends Activity {
    private Messenger messenger = null;
    private Messenger reply = new Messenger(new ReplyHandler());
    SharedPreferences sp;
    String myId;
    String myName;
    Button bt_multi, bt_single, bt_cancel;
    EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("me", 0);
        myId = sp.getString("id", "");
        myName = sp.getString("name", "");
        setContentView(R.layout.activity_make_room);
        bt_single = (Button) findViewById(R.id.bt_single_a_make_room);
        bt_multi = (Button) findViewById(R.id.bt_multi_a_make_room);
        et_name = (EditText) findViewById(R.id.et_name_a_make_room);

        Intent i = new Intent(getApplicationContext(), SocketService.class);
        bindService(i, conn, Context.BIND_AUTO_CREATE);

        bt_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "addRoom");
                        jsonObject.put("roomSect", 2);
                        jsonObject.put("roomName", et_name.getText().toString());
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
        bt_multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "addRoom");
                        jsonObject.put("roomSect", 4);
                        jsonObject.put("roomName", et_name.getText().toString());
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
                if (order.equals("gameStart")) {
                    Intent intent = new Intent(MakeRoomActivity.this, GameActivity.class);
                    intent.putExtra("team", jsonObject.getInt("team"));
                    intent.putExtra("roomId", jsonObject.getString("roomId"));
                    intent.putExtra("players", jsonObject.getString("players"));
                    intent.putExtra("roomSect", jsonObject.getInt("roomSect"));
                    startActivity(intent);
                } else if (order.equals("getRoomInfo")) {
                    Intent intent = new Intent(MakeRoomActivity.this, RoomActivity.class);
                    intent.putExtra("roomId", jsonObject.getString("roomId"));
                    intent.putExtra("roomSect", jsonObject.getInt("roomSect"));
                    intent.putExtra("roomName", jsonObject.getString("roomName"));
                    intent.putExtra("master", jsonObject.getString("master"));
                    intent.putExtra("players", jsonObject.getString("players"));
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
