package thetana.cow;

import android.content.Context;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class URLThread extends Thread {
    Context context;
    Handler handler;
    String file;
    HashMap<String, String> map;

    public URLThread(Context context, Handler handler, String file, HashMap<String, String> map) {
        this.context = context;
        this.handler = handler;
        this.file = file;
        this.map = map;
    }

    @Override
    public void run() {
        String spec = "http://" + context.getString(R.string.host) + "/" + file;
        StringBuilder sb = new StringBuilder();
        try {
            Set<String> set = map.keySet();
            Iterator<String> iterator = set.iterator();
            String str = "";
            while (iterator.hasNext()){
                String key = iterator.next();
                if(!str.equals("")) str += "&";
                str += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(map.get(key), "UTF-8");
             }

            URL url = new URL(spec);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(str);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String json;
            while ((json = reader.readLine()) != null) {
                sb.append(json + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.os.Message message = new android.os.Message();
        message.obj = sb.toString().trim();
        handler.sendMessage(message);
    }
}
