package thetana.cow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends Activity {
    private EditText et_id, et_password;
    private Button bt_login, bt_join;

    SharedPreferences sp;
    URLThread urlThread;
    LoginHandler handler = new LoginHandler();
    HashMap<String, String> map = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = (EditText) findViewById(R.id.et_id_a_login);
        et_password = (EditText) findViewById(R.id.et_password_a_login);
        bt_login = (Button) findViewById(R.id.bt_login_a_login);
        bt_join = (Button) findViewById(R.id.bt_join_a_login);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_id.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(et_password.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                map.put("userId", et_id.getText().toString());
                map.put("userPassword", et_password.getText().toString());
                urlThread = new URLThread(LoginActivity.this, handler, "login.php", map);
                urlThread.start();
            }
        });
        bt_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });
    }
    class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg.obj.toString());
                String chk = jsonObject.getString("chk");
                if (chk.equals("success")) {
                    sp = getSharedPreferences("me", 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("id", jsonObject.getString("id"));
                    editor.putString("name", jsonObject.getString("name"));
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), chk, Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



}
