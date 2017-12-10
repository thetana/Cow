package thetana.cow;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kc on 2017-12-09.
 */

public class FriendAdapter extends BaseAdapter {
    private ArrayList<FriendItem> itmes = new ArrayList<FriendItem>();
    private HashMap<String, FriendItem> itemMap = new HashMap<String, FriendItem>();

    void clearItem() {
        itmes.clear();
    }

    void addItem(FriendItem itme) {
        itmes.add(itme);
        itemMap.put(itme.userId, itme);
        notifyDataSetChanged();
    }

    void removeItem(String userId) {
        itmes.remove(itemMap.get(userId));
        itemMap.remove(userId);
        notifyDataSetChanged();
    }

    boolean containsItem(String userId) {
        return itemMap.containsKey(userId);
    }

    @Override
    public int getCount() {
        return itmes.size();
    }

    public FriendItem getItem(String userId) {
        return itemMap.get(userId);
    }

    @Override
    public Object getItem(int position) {
        return itmes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Activity context = (Activity) parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView tv_name, tv_pop;
        final Button bt_up, bt_get, bt_invite;
        convertView = inflater.inflate(R.layout.item_friend, parent, false);

        tv_name = (TextView) convertView.findViewById(R.id.tv_name_i_friend);
        tv_pop = (TextView) convertView.findViewById(R.id.tv_pop_i_friend);
        bt_up = (Button) convertView.findViewById(R.id.bt_up_i_friend);
        bt_get = (Button) convertView.findViewById(R.id.bt_get_i_friend);
        bt_invite = (Button) convertView.findViewById(R.id.bt_invite_i_friend);

        tv_name.setText(itmes.get(position).name);
        tv_pop.setText(itmes.get(position).pop);
        tv_name.setText(itmes.get(position).name);

        if (itmes.get(position).sect.equals("mine")) {
            bt_up.setVisibility(View.VISIBLE);
            bt_get.setVisibility(View.GONE);
            bt_invite.setVisibility(View.GONE);
        } else if (itmes.get(position).sect.equals("invite")) {
            bt_up.setVisibility(View.GONE);
            bt_get.setVisibility(View.GONE);
            bt_invite.setVisibility(View.VISIBLE);
        } else {
            bt_up.setVisibility(View.GONE);
            bt_get.setVisibility(View.VISIBLE);
            bt_invite.setVisibility(View.GONE);
        }

        bt_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof FriendActivity) ((FriendActivity)context).upPop(itmes.get(position).userId);
            }
        });
        bt_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof FriendActivity) ((FriendActivity)context).getMine(itmes.get(position).userId);
            }
        });
        bt_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof RoomActivity) ((RoomActivity)context).invite(itmes.get(position).userId);
            }
        });
        return convertView;
    }
}
