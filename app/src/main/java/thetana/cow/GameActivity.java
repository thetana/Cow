package thetana.cow;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class GameActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;
    ImageView imageView;
    ImageHandler handler;

    //public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public static native long loadCascade(String cascadeFileName);

    public static native void detect(long cascadeClassifier_face,
                                     long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    private Messenger messenger = null;
    private Messenger reply = new Messenger(new GameActivity.ReplyHandler());
    public static GLView mGLView;
    public static GameThread mThread;
    SharedPreferences sp;
    String myId;
    String myName;
    String roomId;
    LinearLayout ll_game, ll_chat, ll_face;
    EditText et_text;
    Button bt_send, button5, bt_face;
    String[] ids;
    String[] names;
    Bitmap mB = null;
    ImageHelper ih = new ImageHelper();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("me", 0);
        myId = sp.getString("id", "");
        myName = sp.getString("name", "");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game);
        imageView = (ImageView)findViewById(R.id.iv_face_a_game);
        handler = new ImageHandler();

        ll_game = (LinearLayout) findViewById(R.id.ll_game_a_game);
        ll_chat = (LinearLayout) findViewById(R.id.ll_chat_a_game);
        ll_face = (LinearLayout) findViewById(R.id.ll_face_a_game);
        et_text = (EditText) findViewById(R.id.et_text_a_game);
        bt_send = (Button) findViewById(R.id.bt_send_a_game);
        button5 = (Button) findViewById(R.id.button5);
        bt_face = (Button) findViewById(R.id.bt_face_a_game);
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else read_cascade_file(); //추가
        } else read_cascade_file(); //추가

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.GONE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        //mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

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
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
                mOpenCvCameraView.setVisibility(View.VISIBLE);
                ll_face.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                bt_face.setVisibility(View.VISIBLE);
                mB = null;
            }
        });
        bt_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mB = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                setBitmap(mB);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                bt_face.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                ll_face.setVisibility(View.GONE);
                mOpenCvCameraView.setVisibility(View.GONE);
                mOpenCvCameraView.setVisibility(SurfaceView.GONE);
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

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();

        if (matResult != null) matResult.release();
        matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        //ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        Core.flip(matInput, matInput, 1);
        detect(cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(),
                matResult.getNativeObjAddr());
        Bitmap bm = null;
        bm = Bitmap.createBitmap(matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matResult, bm);
        Message msg = new Message();
        msg.obj = bm;
        handler.sendMessage(msg);
        return matResult;
    }

    private class ReplyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                if(msg.what == 2){
                    byte[] data = (byte[])msg.obj;
                    mB = BitmapFactory.decodeByteArray(data, 0, data.length);
                }else {
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
                    } else if (order.equals("setBitmap")) {
                        mB = ih.stringToBitMap(jsonObject.getString("what"));
                    }
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

    void setBitmap(Bitmap what) {
        if (messenger != null) {
            byte[] data = ih.getImageByte(what);
            Message msg = Message.obtain(null, SocketService.SETIMG);
            msg.obj = data;
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

    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }
        //모든 퍼미션이 허가되었음
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    } else {
                        read_cascade_file();
                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d(TAG, "copyFile :: 다음 경로로 파일복사 " + pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 " + e.toString());
        }
    }

    private void read_cascade_file() {
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.d(TAG, "read_cascade_file:");

        cascadeClassifier_face = loadCascade("haarcascade_frontalface_alt.xml");
        Log.d(TAG, "read_cascade_file:");

        cascadeClassifier_eye = loadCascade("haarcascade_eye_tree_eyeglasses.xml");
    }

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }
    class ImageHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            imageView.setImageBitmap((Bitmap) msg.obj);
        }
    }
}
