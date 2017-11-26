package thetana.cow;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketService extends Service {
    public static final int CONNECT = 0;
    public static final int DISCONNECT = -1;
    public static final int QUICK = 1;
    public static final int SETCOW = 2;
    public static final int SETIMG = 3;
    public static final int GAMESTART = 7;
    private Messenger reply = null;
    final Messenger messenger = new Messenger(new ReplyHandler());

    ImageHelper ih = new ImageHelper();
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
                                    if(str.equals("2")){
                                        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                                        byte[] imagebuffer = null;
                                        int size = 0;
                                        byte[] buffer = new byte[1024];
                                        int read;
                                        boolean mRun = true;
                                        while((read = bis.read(buffer)) != -1 && mRun) {
                                            if (imagebuffer == null) {
                                                //처음 4byte에서 비트맵이미지의 총크기를 추출해 따로 저장한다
                                                byte[] sizebuffer = new byte[4];
                                                System.arraycopy(buffer, 0, sizebuffer, 0, sizebuffer.length);
                                                size = getInt(sizebuffer);
                                                read -= sizebuffer.length;

                                                //나머지는 이미지버퍼 배열에 저장한다
                                                imagebuffer = new byte[read];
                                                System.arraycopy(buffer, sizebuffer.length, imagebuffer, 0, read);
                                            }
                                            else {
                                                //이미지버퍼 배열에 계속 이어서 저장한다
                                                byte[] preimagebuffer = imagebuffer.clone();
                                                imagebuffer = new byte[read + preimagebuffer.length];
                                                System.arraycopy(preimagebuffer, 0, imagebuffer, 0, preimagebuffer.length);
                                                System.arraycopy(buffer, 0, imagebuffer, imagebuffer.length - read, read);
                                            }
                                            //이미지버퍼 배열에 총크기만큼 다 받아졌다면 이미지를 저장하고 끝낸다
                                            if(imagebuffer.length >= size) {
                                                Message msg = Message.obtain(null, 2);
                                                msg.obj = imagebuffer;
                                                reply.send(msg);
                                                imagebuffer = null;
                                                size = 0;
                                                mRun= false;
                                                break;
                                            }
                                        }
                                    }else {
                                        Message msg = new Message();
                                        msg.obj = str;
                                        reply.send(msg);
                                    }
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
                    out.writeUTF("1");
                    out.writeUTF(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void out(final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.writeUTF("2");

                    byte[] size = ih.getByte(data.length);
                    out.write(size, 0, size.length);
                    out.flush();

                    out.write(data, 0, data.length);
                    out.flush();
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
                case SETIMG:
                    out((byte[])msg.obj);
                    break;
            }
        }
    }
    private int getInt(byte[] data) {
        int s1 = data[0] & 0xFF;
        int s2 = data[1] & 0xFF;
        int s3 = data[2] & 0xFF;
        int s4 = data[3] & 0xFF;

        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }
}
