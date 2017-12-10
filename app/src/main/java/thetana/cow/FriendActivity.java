package thetana.cow;

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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendActivity extends AppCompatActivity {
    private Messenger messenger = null;
    private Messenger reply = new Messenger(new FriendActivity.ReplyHandler());
    SharedPreferences sp;
    String myId;
    String myName;
    FriendAdapter adapter = new FriendAdapter();
    ListView lv_list;
    Button bt_new, bt_mine, bt_search, bt_back;
    EditText et_search;
    LinearLayout ll_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        sp = getSharedPreferences("me", 0);
        myId = sp.getString("id", "");
        myName = sp.getString("name", "");
        lv_list = (ListView) findViewById(R.id.lv_list_a_friend);
        ll_search = (LinearLayout) findViewById(R.id.ll_search_a_friend);
        bt_mine = (Button) findViewById(R.id.bt_mine_a_friend);
        bt_new = (Button) findViewById(R.id.bt_new_a_friend);
        bt_search = (Button) findViewById(R.id.bt_search_a_friend);
        bt_back = (Button) findViewById(R.id.bt_back_a_friend);

        et_search = (EditText) findViewById(R.id.et_search_a_friend);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        lv_list.setAdapter(adapter);
        adapter.clearItem();
        try {
            JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("friends"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = new JSONObject(jsonArray.getString(i));
                FriendItem item = new FriendItem(object.getString("sect"));
                item.userId = object.getString("userId");
                item.name = object.getString("name");
                item.pop = object.getString("pop");
                adapter.addItem(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                startActivity(intent);
            }
        });
        bt_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "getFriend");
                        jsonObject.put("userId", myId);
                        jsonObject.put("sect", "new");
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
                bt_new.setVisibility(View.GONE);
                bt_mine.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.VISIBLE);
            }
        });
        bt_mine.setOnClickListener(new View.OnClickListener() {
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
                bt_mine.setVisibility(View.GONE);
                bt_new.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
            }
        });
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messenger != null) {
                    Message msg = Message.obtain(null, SocketService.SETCOW);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("order", "getFriend");
                        jsonObject.put("sect", "search");
                        jsonObject.put("search", et_search.getText().toString());
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
                    Intent intent = new Intent(FriendActivity.this, GameActivity.class);
                    intent.putExtra("team", jsonObject.getInt("team"));
                    intent.putExtra("roomId", jsonObject.getString("roomId"));
                    intent.putExtra("players", jsonObject.getString("players"));
                    intent.putExtra("roomSect", jsonObject.getInt("roomSect"));
                    startActivity(intent);
                }  else if (order.equals("getFriend")) {
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
                } else if (order.equals("upPop")) {
                    adapter.getItem(jsonObject.getString("userId")).pop = jsonObject.getString("pop");
                    adapter.notifyDataSetChanged();
                } else if (order.equals("getMine")) {
                    adapter.removeItem(jsonObject.getString("userId"));
                } else if (order.equals("msg")) {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void upPop(String userId) {
        if (messenger != null) {
            Message msg = Message.obtain(null, SocketService.SETCOW);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("order", "upPop");
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

    void getMine(String userId) {
        if (messenger != null) {
            Message msg = Message.obtain(null, SocketService.SETCOW);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("order", "getMine");
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