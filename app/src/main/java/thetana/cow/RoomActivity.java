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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomActivity extends Activity {
    private Messenger messenger = null;
    private Messenger reply = new Messenger(new ReplyHandler());
    SharedPreferences sp;
    String myId;
    String myName;
    String roomId;
    String master;
    int roomSect;
    FriendAdapter adapter = new FriendAdapter();
    ListView lv_list;
    Button bt_p1, bt_p2, bt_p3, bt_p4, bt_start, bt_out, bt_invite;
    EditText et_name;
    TextView tv_m1, tv_m2, tv_m3, tv_m4, tv_name1, tv_name2, tv_name3, tv_name4;
    ImageView iv_p1, iv_p2, iv_p3, iv_p4;
    LinearLayout ll_p1, ll_p2, ll_p3, ll_p4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        sp = getSharedPreferences("me", 0);
        myId = sp.getString("id", "");
        myName = sp.getString("name", "");
        lv_list = (ListView) findViewById(R.id.lv_friend_a_room);
        et_name = (EditText) findViewById(R.id.et_name_a_room);
        bt_start = (Button) findViewById(R.id.bt_start_a_room);
        bt_out = (Button) findViewById(R.id.bt_out_a_room);
        ll_p1 = (LinearLayout) findViewById(R.id.ll_p1_a_room);
        ll_p2 = (LinearLayout) findViewById(R.id.ll_p2_a_room);
        ll_p3 = (LinearLayout) findViewById(R.id.ll_p3_a_room);
        ll_p4 = (LinearLayout) findViewById(R.id.ll_p4_a_room);
        iv_p1 = (ImageView) findViewById(R.id.iv_p1_a_room);
        iv_p2 = (ImageView) findViewById(R.id.iv_p2_a_room);
        iv_p3 = (ImageView) findViewById(R.id.iv_p3_a_room);
        iv_p4 = (ImageView) findViewById(R.id.iv_p4_a_room);
        tv_name1 = (TextView) findViewById(R.id.tv_name1_a_room);
        tv_name2 = (TextView) findViewById(R.id.tv_name2_a_room);
        tv_name3 = (TextView) findViewById(R.id.tv_name3_a_room);
        tv_name4 = (TextView) findViewById(R.id.tv_name4_a_room);
        tv_m1 = (TextView) findViewById(R.id.tv_m1_a_room);
        tv_m2 = (TextView) findViewById(R.id.tv_m2_a_room);
        tv_m3 = (TextView) findViewById(R.id.tv_m3_a_room);
        tv_m4 = (TextView) findViewById(R.id.tv_m4_a_room);
        bt_p1 = (Button) findViewById(R.id.bt_p1_a_room);
        bt_p2 = (Button) findViewById(R.id.bt_p2_a_room);
        bt_p3 = (Button) findViewById(R.id.bt_p3_a_room);
        bt_p4 = (Button) findViewById(R.id.bt_p4_a_room);
        bt_invite = (Button) findViewById(R.id.bt_invite_a_room);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        lv_list.setAdapter(adapter);

        iv_p1.setVisibility(View.INVISIBLE);
        iv_p2.setVisibility(View.INVISIBLE);
        iv_p3.setVisibility(View.INVISIBLE);
        iv_p4.setVisibility(View.INVISIBLE);
        tv_name1.setVisibility(View.INVISIBLE);
        tv_name2.setVisibility(View.INVISIBLE);
        tv_name3.setVisibility(View.INVISIBLE);
        tv_name4.setVisibility(View.INVISIBLE);
        tv_m1.setVisibility(View.GONE);
        tv_m2.setVisibility(View.GONE);
        tv_m3.setVisibility(View.GONE);
        tv_m4.setVisibility(View.GONE);
        bt_p1.setVisibility(View.GONE);
        bt_p2.setVisibility(View.GONE);
        bt_p3.setVisibility(View.GONE);
        bt_p4.setVisibility(View.GONE);

        roomId = getIntent().getStringExtra("roomId");
        roomSect = getIntent().getIntExtra("roomSect", 4);
        et_name.setText(getIntent().getStringExtra("roomName"));
        master = getIntent().getStringExtra("master");
        if (roomSect == 2) {
            ll_p3.setVisibility(View.INVISIBLE);
            ll_p4.setVisibility(View.INVISIBLE);
        }

        bt_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "getFriend");
                        jsonObject.put("userId", myId);
                        jsonObject.put("sect", "invite");
                        jsonObject.put("search", "");
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
                //bt_invite.setVisibility(View.GONE);
                lv_list.setVisibility(View.VISIBLE);
            }
        });

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "gameStart");
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
        });

        bt_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        try {
            JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("players"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = new JSONObject(jsonArray.getString(i));
                if (object.getInt("userTeam") == 0) {
                    iv_p1.setVisibility(View.VISIBLE);
                    tv_name1.setVisibility(View.VISIBLE);
                    tv_name1.setText(object.getString("userName"));
                    if (master.equals(object.getString("userId"))) {
                        tv_m1.setVisibility(View.VISIBLE);
                    }
                    if (myId.equals(master) && !myId.equals(object.getString("userId"))) {
                        bt_p1.setVisibility(View.VISIBLE);

                    }
                } else if (object.getInt("userTeam") == 1) {
                    iv_p2.setVisibility(View.VISIBLE);
                    tv_name2.setVisibility(View.VISIBLE);
                    tv_name2.setText(object.getString("userName"));
                    if (master.equals(object.getString("userId"))) {
                        tv_m2.setVisibility(View.VISIBLE);
                    }
                    if (myId.equals(master) && !myId.equals(object.getString("userId"))) {
                        bt_p2.setVisibility(View.VISIBLE);

                    }
                } else if (object.getInt("userTeam") == 2) {
                    iv_p3.setVisibility(View.VISIBLE);
                    tv_name3.setVisibility(View.VISIBLE);
                    tv_name3.setText(object.getString("userName"));
                    if (master.equals(object.getString("userId"))) {
                        tv_m3.setVisibility(View.VISIBLE);
                    }
                    if (myId.equals(master) && !myId.equals(object.getString("userId"))) {
                        bt_p3.setVisibility(View.VISIBLE);

                    }
                } else if (object.getInt("userTeam") == 3) {
                    iv_p4.setVisibility(View.VISIBLE);
                    tv_name4.setVisibility(View.VISIBLE);
                    tv_name4.setText(object.getString("userName"));
                    if (master.equals(object.getString("userId"))) {
                        tv_m4.setVisibility(View.VISIBLE);
                    }
                    if (myId.equals(master) && !myId.equals(object.getString("userId"))) {
                        bt_p4.setVisibility(View.VISIBLE);

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (messenger != null) {
            Message msg = Message.obtain(null, SocketService.SETCOW);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("order", "getRoomInfo");
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

                if (order.equals("getRoomInfo")) {
                    iv_p1.setVisibility(View.INVISIBLE);
                    iv_p2.setVisibility(View.INVISIBLE);
                    iv_p3.setVisibility(View.INVISIBLE);
                    iv_p4.setVisibility(View.INVISIBLE);
                    tv_name1.setVisibility(View.INVISIBLE);
                    tv_name2.setVisibility(View.INVISIBLE);
                    tv_name3.setVisibility(View.INVISIBLE);
                    tv_name4.setVisibility(View.INVISIBLE);
                    tv_m1.setVisibility(View.INVISIBLE);
                    tv_m2.setVisibility(View.INVISIBLE);
                    tv_m3.setVisibility(View.INVISIBLE);
                    tv_m4.setVisibility(View.INVISIBLE);
                    bt_p1.setVisibility(View.INVISIBLE);
                    bt_p2.setVisibility(View.INVISIBLE);
                    bt_p3.setVisibility(View.INVISIBLE);
                    bt_p4.setVisibility(View.INVISIBLE);
                    roomId = jsonObject.getString("roomId");
                    roomSect = jsonObject.getInt("roomSect");
                    et_name.setText(jsonObject.getString("roomName"));
                    master = jsonObject.getString("master");
                    int count = 0;
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("players"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = new JSONObject(jsonArray.getString(i));
                        if (adapter.containsItem(object.getString("userId")))
                            adapter.removeItem(object.getString("userId"));
                        if (object.getInt("userTeam") == 0) {
                            iv_p1.setVisibility(View.VISIBLE);
                            tv_name1.setVisibility(View.VISIBLE);
                            tv_name1.setText(object.getString("userName"));
                            if (master.equals(object.getString("userId"))) {
                                tv_m1.setVisibility(View.VISIBLE);
                            }
                            if (myId.equals(master) && !myId.equals(object.getString("userId"))) {
                                bt_p1.setVisibility(View.VISIBLE);
                            }
                            count++;
                        } else if (object.getInt("userTeam") == 1) {
                            iv_p2.setVisibility(View.VISIBLE);
                            tv_name2.setVisibility(View.VISIBLE);
                            tv_name2.setText(object.getString("userName"));
                            if (master.equals(object.getString("userId"))) {
                                tv_m2.setVisibility(View.VISIBLE);
                            }
                            if (myId.equals(master) && !myId.equals(object.getString("userId"))) {
                                bt_p2.setVisibility(View.VISIBLE);
                            }
                            count++;
                        } else if (object.getInt("userTeam") == 2) {
                            iv_p3.setVisibility(View.VISIBLE);
                            tv_name3.setVisibility(View.VISIBLE);
                            tv_name3.setText(object.getString("userName"));
                            if (master.equals(object.getString("userId"))) {
                                tv_m3.setVisibility(View.VISIBLE);
                            }
                            if (myId.equals(master) && !myId.equals(object.getString("userId"))) {
                                bt_p3.setVisibility(View.VISIBLE);
                            }
                            count++;
                        } else if (object.getInt("userTeam") == 3) {
                            iv_p4.setVisibility(View.VISIBLE);
                            tv_name4.setVisibility(View.VISIBLE);
                            tv_name4.setText(object.getString("userName"));
                            if (master.equals(object.getString("userId"))) {
                                tv_m4.setVisibility(View.VISIBLE);
                            }
                            if (myId.equals(master) && !myId.equals(object.getString("userId"))) {
                                bt_p4.setVisibility(View.VISIBLE);
                            }
                            count++;
                        }
                    }
                    if (myId.equals(master) && roomSect == count) {
                        bt_start.setEnabled(true);
                    }
                } else if (order.equals("gameStart")) {
                    Intent intent = new Intent(RoomActivity.this, GameActivity.class);
                    intent.putExtra("team", jsonObject.getInt("team"));
                    intent.putExtra("roomId", jsonObject.getString("roomId"));
                    intent.putExtra("players", jsonObject.getString("players"));
                    intent.putExtra("roomSect", jsonObject.getInt("roomSect"));
                    startActivity(intent);
                } else if (order.equals("outRoom")) {
                    Intent intent = new Intent(RoomActivity.this, LobbyActivity.class);
                    startActivity(intent);
                } else if (order.equals("getFriend")) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("friends"));
                    adapter.clearItem();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = new JSONObject(jsonArray.getString(i));
                        FriendItem item = new FriendItem(object.getString("sect"));
                        item.userId = object.getString("userId");
                        item.name = object.getString("name");
                        item.pop = object.getString("pop");
                        adapter.addItem(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void invite(String userId) {
        if (messenger != null) {
            Message msg = Message.obtain(null, SocketService.SETCOW);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("order", "invite");
                jsonObject.put("roomId", roomId);
                jsonObject.put("userId", userId);
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
}
