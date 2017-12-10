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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class LobbyActivity extends Activity {
    private Messenger messenger = null;
    private Messenger reply = new Messenger(new ReplyHandler());
    SharedPreferences sp;
    String myId;
    String myName;
    Button bt_quick, bt_make, bt_search, bt_single, bt_multi, bt_back, bt_cancel, bt_friend;
    TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("me", 0);
        myId = sp.getString("id", "");
        myName = sp.getString("name", "");
        setContentView(R.layout.activity_lobby);
        bt_quick = (Button) findViewById(R.id.bt_quick_a_lobby);
        bt_make = (Button) findViewById(R.id.bt_make_a_lobby);
        bt_search = (Button) findViewById(R.id.bt_search_a_lobby);
        bt_single = (Button) findViewById(R.id.bt_single_a_lobby);
        bt_multi = (Button) findViewById(R.id.bt_multi_a_lobby);
        bt_back = (Button) findViewById(R.id.bt_back_a_lobby);
        bt_cancel = (Button) findViewById(R.id.bt_cancel_a_lobby);
        bt_friend = (Button) findViewById(R.id.bt_friend_a_lobby);
        tv_name = (TextView) findViewById(R.id.tv_name_a_lobby);
        tv_name.setText(myName);

        bt_quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_quick.setVisibility(View.GONE);
                bt_make.setVisibility(View.GONE);
                bt_search.setVisibility(View.GONE);

                bt_single.setVisibility(View.VISIBLE);
                bt_multi.setVisibility(View.VISIBLE);
                bt_back.setVisibility(View.VISIBLE);

                bt_cancel.setVisibility(View.GONE);
            }
        });
        bt_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.QUICK);
                    msg.arg1 = 2;
                    msg.replyTo = reply;
                    try {
                        messenger.send(msg);
                        bt_quick.setVisibility(View.GONE);
                        bt_make.setVisibility(View.GONE);
                        bt_search.setVisibility(View.GONE);

                        bt_single.setVisibility(View.GONE);
                        bt_multi.setVisibility(View.GONE);
                        bt_back.setVisibility(View.GONE);

                        bt_cancel.setVisibility(View.VISIBLE);
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
                    Message msg = Message.obtain(null, SocketService.QUICK);
                    msg.arg1 = 4;
                    msg.replyTo = reply;
                    try {
                        messenger.send(msg);
                        bt_quick.setVisibility(View.GONE);
                        bt_make.setVisibility(View.GONE);
                        bt_search.setVisibility(View.GONE);

                        bt_single.setVisibility(View.GONE);
                        bt_multi.setVisibility(View.GONE);
                        bt_back.setVisibility(View.GONE);

                        bt_cancel.setVisibility(View.VISIBLE);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_quick.setVisibility(View.VISIBLE);
                bt_make.setVisibility(View.VISIBLE);
                bt_search.setVisibility(View.VISIBLE);

                bt_single.setVisibility(View.GONE);
                bt_multi.setVisibility(View.GONE);
                bt_back.setVisibility(View.GONE);

                bt_cancel.setVisibility(View.GONE);
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "cancelWait");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    msg.obj = jsonObject;
                    try {
                        messenger.send(msg);
                        bt_quick.setVisibility(View.VISIBLE);
                        bt_make.setVisibility(View.VISIBLE);
                        bt_search.setVisibility(View.VISIBLE);

                        bt_single.setVisibility(View.GONE);
                        bt_multi.setVisibility(View.GONE);
                        bt_back.setVisibility(View.GONE);

                        bt_cancel.setVisibility(View.GONE);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        bt_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, MakeRoomActivity.class);
                startActivity(intent);
            }
        });
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "searchRoom");
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
        bt_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "getFriend");
                        jsonObject.put("userId", myId);
                        jsonObject.put("sect", "mine");
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
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(getApplicationContext(), SocketService.class);
        bindService(i, conn, Context.BIND_AUTO_CREATE);

//        if (messenger != null) {
//            Message msg = Message.obtain(null, SocketService.SETCOW);
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("order", "isWait");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            msg.obj = jsonObject;
//            try {
//                messenger.send(msg);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            Message msg = Message.obtain(null, SocketService.CONNECT);

            Bundle bundle = new Bundle();
            bundle.putString("id", myId);
            bundle.putString("name", myName);
            msg.setData(bundle);

            msg.arg1 = 1;
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
                    Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                    intent.putExtra("team", jsonObject.getInt("team"));
                    intent.putExtra("roomId", jsonObject.getString("roomId"));
                    intent.putExtra("players", jsonObject.getString("players"));
                    intent.putExtra("roomSect", jsonObject.getInt("roomSect"));
                    startActivity(intent);
                } else if (order.equals("searchRoom")) {
                    Intent intent = new Intent(LobbyActivity.this, SearchRoomActivity.class);
                    intent.putExtra("rooms", jsonObject.getString("rooms"));
                    startActivity(intent);
                } else if (order.equals("getFriend")) {
                    Intent intent = new Intent(LobbyActivity.this, FriendActivity.class);
                    intent.putExtra("friends", jsonObject.getString("friends"));
                    startActivity(intent);
                } else if (order.equals("isWait")) {
                    if (jsonObject.getBoolean("isWait")) {
                        bt_quick.setVisibility(View.GONE);
                        bt_make.setVisibility(View.GONE);
                        bt_search.setVisibility(View.GONE);

                        bt_single.setVisibility(View.GONE);
                        bt_multi.setVisibility(View.GONE);
                        bt_back.setVisibility(View.GONE);

                        bt_cancel.setVisibility(View.VISIBLE);
                    } else {
                        bt_quick.setVisibility(View.VISIBLE);
                        bt_make.setVisibility(View.VISIBLE);
                        bt_search.setVisibility(View.VISIBLE);

                        bt_single.setVisibility(View.GONE);
                        bt_multi.setVisibility(View.GONE);
                        bt_back.setVisibility(View.GONE);

                        bt_cancel.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
