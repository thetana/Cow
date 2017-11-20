package thetana.cow;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketService extends Service {
    public static final int CONNECT = 0;
    public static final int DISCONNECT = -1;
    public static final int QUICK = 1;
    public static final int SETCOW = 2;
    public static final int GAMESTART = 7;
    private Messenger reply = null;
    final Messenger messenger = new Messenger(new ReplyHandler());

    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    String id;
    String name;
    String host = "13.125.1.244";
    int port = 31;

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    void setSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(host, port);
                    out = new DataOutputStream(socket.getOutputStream());
                    in = new DataInputStream(socket.getInputStream());
                    out.writeUTF(id);
                    out.writeUTF(name);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (in != null) {
                                try {
                                    String str = in.readUTF();
                                    Message msg = new Message();
                                    msg.obj = str;
                                    reply.send(msg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                    reply = null;
                                }
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void out(final String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.writeUTF(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class ReplyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            switch (msg.what) {
                case CONNECT:
                    reply = msg.replyTo;
                    if (msg.arg1 == 1) {
                        id = msg.getData().getString("id");
                        name = msg.getData().getString("name");
                        setSocket();
                    }
                    break;
                case DISCONNECT:
                    reply = null;
                    break;
                case QUICK:
                    try {
                        jsonObject.put("order", "setWait");
                        jsonObject.put("roomSect", String.valueOf(msg.arg1));
                        out(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SETCOW:
                    out(msg.obj.toString());
                    break;
            }
        }
    }
}
