package thetana.cow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kc on 2017-10-29.
 */

public class SearchRoomAdapter extends BaseAdapter {
    private ArrayList<RoomItem> itmes = new ArrayList<RoomItem>();
    private HashMap<String, RoomItem> msgMap = new HashMap<String, RoomItem>();


    void clearItem() {
        itmes.clear();
    }

    void addItem(RoomItem itme) {
        itmes.add(itme);
        msgMap.put(itme.roomId, itme);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itmes.size();
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
        final SearchRoomActivity context = (SearchRoomActivity) parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv_p1, iv_p2, iv_p3, iv_p4;
        TextView tv_p1, tv_p2, tv_p3, tv_p4, tv_name1, tv_name2, tv_name3, tv_name4, tv_sect, tv_state;
        final Button bt_join;
        LinearLayout ll_p1, ll_p2, ll_p3, ll_p4;
        convertView = inflater.inflate(R.layout.item_search_room, parent, false);
        ll_p1 = (LinearLayout) convertView.findViewById(R.id.ll_p1_i_search_room);
        ll_p2 = (LinearLayout) convertView.findViewById(R.id.ll_p2_i_search_room);
        ll_p3 = (LinearLayout) convertView.findViewById(R.id.ll_p3_i_search_room);
        ll_p4 = (LinearLayout) convertView.findViewById(R.id.ll_p4_i_search_room);

        tv_p1 = (TextView) convertView.findViewById(R.id.tv_p1_i_search_room);
        tv_p2 = (TextView) convertView.findViewById(R.id.tv_p2_i_search_room);
        tv_p3 = (TextView) convertView.findViewById(R.id.tv_p3_i_search_room);
        tv_p4 = (TextView) convertView.findViewById(R.id.tv_p4_i_search_room);
        iv_p1 = (ImageView) convertView.findViewById(R.id.iv_p1_i_search_room);
        iv_p2 = (ImageView) convertView.findViewById(R.id.iv_p2_i_search_room);
        iv_p3 = (ImageView) convertView.findViewById(R.id.iv_p3_i_search_room);
        iv_p4 = (ImageView) convertView.findViewById(R.id.iv_p4_i_search_room);
        tv_name1 = (TextView) convertView.findViewById(R.id.tv_name1_i_search_room);
        tv_name2 = (TextView) convertView.findViewById(R.id.tv_name2_i_search_room);
        tv_name3 = (TextView) convertView.findViewById(R.id.tv_name3_i_search_room);
        tv_name4 = (TextView) convertView.findViewById(R.id.tv_name4_i_search_room);
        tv_sect = (TextView) convertView.findViewById(R.id.tv_sect_i_search_room);
        tv_state = (TextView) convertView.findViewById(R.id.tv_state_i_search_room);
        bt_join = (Button) convertView.findViewById(R.id.bt_join_i_search_room);

        if (itmes.get(position).name1 != null && !itmes.get(position).name1.isEmpty()) {
            ll_p1.setVisibility(View.VISIBLE);
            tv_name1.setText(itmes.get(position).name1);
        } else {
            ll_p1.setVisibility(View.INVISIBLE);
        }
        if (itmes.get(position).name2 != null && !itmes.get(position).name2.isEmpty()) {
            ll_p2.setVisibility(View.VISIBLE);
            tv_name2.setText(itmes.get(position).name2);
        } else {
            ll_p2.setVisibility(View.INVISIBLE);
        }
        if (itmes.get(position).name3 != null && !itmes.get(position).name3.isEmpty()) {
            ll_p3.setVisibility(View.VISIBLE);
            tv_name3.setText(itmes.get(position).name3);
        } else {
            ll_p3.setVisibility(View.INVISIBLE);
        }
        if (itmes.get(position).name4 != null && !itmes.get(position).name4.isEmpty()) {
            ll_p4.setVisibility(View.VISIBLE);
            tv_name4.setText(itmes.get(position).name4);
        } else {
            ll_p4.setVisibility(View.INVISIBLE);
        }
        if (itmes.get(position).sect == 2) {
            tv_sect.setText("1대1");
            if (itmes.get(position).name1 != null && !itmes.get(position).name1.isEmpty()
                    && itmes.get(position).name2 != null && !itmes.get(position).name2.isEmpty()) {
                bt_join.setEnabled(false);
            } else {
                bt_join.setEnabled(true);
            }
        } else if (itmes.get(position).sect == 4) {
            tv_sect.setText("2대2");
            if (itmes.get(position).name1 != null && !itmes.get(position).name1.isEmpty()
                    && itmes.get(position).name2 != null && !itmes.get(position).name2.isEmpty()
                    && itmes.get(position).name3 != null && !itmes.get(position).name3.isEmpty()
                    && itmes.get(position).name4 != null && !itmes.get(position).name4.isEmpty()) {
                bt_join.setEnabled(false);
            } else {
                bt_join.setEnabled(true);
            }
        }
        tv_state.setText(itmes.get(position).state);

        bt_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.joinRoom(itmes.get(position).roomId);
            }
        });
        return convertView;
    }
}
