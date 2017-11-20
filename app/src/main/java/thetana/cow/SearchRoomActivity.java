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
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchRoomActivity extends Activity {
    private Messenger messenger = null;
    private Messenger reply = new Messenger(new ReplyHandler());
    SharedPreferences sp;
    String myId;
    String myName;

    SearchRoomAdapter adapter = new SearchRoomAdapter();
    ListView lv_games;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);
        sp = getSharedPreferences("me", 0);
        myId = sp.getString("id", "");
        myName = sp.getString("name", "");
        lv_games = (ListView) findViewById(R.id.lv_games_a_search_room);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        lv_games.setAdapter(adapter);
        adapter.clearItem();
        try {
            JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("rooms"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = new JSONObject(jsonArray.getString(i));
                RoomItem item = new RoomItem(object.getInt("roomSect"));
                item.state = object.getString("state");
                item.roomId = object.getString("roomId");
                JSONArray array = new JSONArray(object.getString("players"));
                for(int j = 0; array.length() > j; j++ ){
                    JSONObject jsonObject = new JSONObject(array.getString(j));
                    if(jsonObject.getInt("userTeam") == 0){
                        item.name1 = jsonObject.getString("userName");
                    }else if(jsonObject.getInt("userTeam") == 1){
                        item.name2 = jsonObject.getString("userName");
                    }else if(jsonObject.getInt("userTeam") == 2){
                        item.name3 = jsonObject.getString("userName");
                    }else if(jsonObject.getInt("userTeam") == 3){
                        item.name4 = jsonObject.getString("userName");
                    }
                }
                adapter.addItem(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                if (order.equals("gameStart")) {
                    Intent intent = new Intent(SearchRoomActivity.this, GameActivity.class);
                    intent.putExtra("team", jsonObject.getInt("team"));
                    intent.putExtra("roomId", jsonObject.getString("roomId"));
                    intent.putExtra("players", jsonObject.getString("players"));
                    intent.putExtra("roomSect", jsonObject.getInt("roomSect"));
                    startActivity(intent);
                }else if (order.equals("getRoomInfo")) {
                    Intent intent = new Intent(SearchRoomActivity.this, RoomActivity.class);
                    intent.putExtra("roomId", jsonObject.getString("roomId"));
                    intent.putExtra("roomSect", jsonObject.getInt("roomSect"));
                    intent.putExtra("roomName", jsonObject.getString("roomName"));
                    intent.putExtra("master", jsonObject.getString("master"));
                    intent.putExtra("players", jsonObject.getString("players"));
                    startActivity(intent);
                } else if (order.equals("")) {
                    adapter.clearItem();
                    for (int i = 0; i < 4; i++) {
                        RoomItem item = new RoomItem(2);

                        adapter.addItem(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void joinRoom(String roomId){
        if (messenger != null) {
            Message msg = Message.obtain(null, SocketService.SETCOW);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("order", "joinRoom");
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

}
