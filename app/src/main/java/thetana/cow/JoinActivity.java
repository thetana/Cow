package thetana.cow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class JoinActivity extends Activity {
    private EditText et_password;
    private EditText et_rePassword;
    private EditText et_id;
    private EditText et_name;
    private Button bt_save;
    URLThread urlThread;
    JoinHandler handler = new JoinHandler();
    HashMap<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        et_id = (EditText) findViewById(R.id.et_id_a_join);
        et_password = (EditText) findViewById(R.id.et_password_a_join);
        et_rePassword = (EditText) findViewById(R.id.et_repassword_a_join);
        et_name = (EditText) findViewById(R.id.et_name_a_join);
        bt_save = (Button) findViewById(R.id.bt_save_a_join);

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_id.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(et_password.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(et_rePassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 재입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(et_name.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                map.put("userId", et_id.getText().toString());
                map.put("userPassword", et_password.getText().toString());
                map.put("rePassword", et_rePassword.getText().toString());
                map.put("userName", et_name.getText().toString());
                urlThread = new URLThread(JoinActivity.this, handler, "join.php", map);
                urlThread.start();
            }
        });
    }
    class JoinHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
            if (msg.obj.toString().equals("환영합니다.")) {
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                intent.putExtra("killed", "");
                startActivity(intent);
            }
        }
    }
}
