package com.handsome.qhb.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handsome.qhb.application.MyApplication;
import com.handsome.qhb.bean.Product;
import com.handsome.qhb.bean.Room;
import com.handsome.qhb.bean.User;
import com.handsome.qhb.config.Config;
import com.handsome.qhb.ui.activity.ChatActivity;
import com.handsome.qhb.ui.activity.MainActivity;
import com.handsome.qhb.ui.activity.OrderDetailActivity;
import com.handsome.qhb.utils.LogUtils;
import com.handsome.qhb.utils.UserInfo;
import com.handsome.qhb.utils.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tab.com.handsome.handsome.R;


/**
 * Created by zhang on 2016/2/22.
 */
public class RoomAdapter extends CommonAdapter<Room> {

    public RoomAdapter(Context context, List<Room> datas, int layoutId, RequestQueue mQueue){
        super(context,datas,layoutId,mQueue);
    }
    @Override
    public void convert(int position,ViewHolder holder,ListView listView ,Room room) {
        holder.setText(R.id.id_tv_roomName,room.getRoomName());
        holder.setText(R.id.id_tv_time, room.getRoomEndTime());
        holder.getView(R.id.room_list_items).setOnClickListener(new RoomItemOnclick(position));
    }

    class RoomItemOnclick implements View.OnClickListener{
        private int position;
        public RoomItemOnclick(int position){
            this.position = position;
        }
        @Override
        public void onClick(View view) {

            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("请求中");
            progressDialog.setCancelable(true);
            progressDialog.show();
            Intent i = new Intent(mContext, ChatActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("room", mDatas.get(position));
            i.putExtras(b);
            mContext.startActivity(i);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.BASE_URL+"Room/enterRoom",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressDialog.dismiss();
                                LogUtils.e("response", response);
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                if(status == "0"){
                                    Toast.makeText(mContext, jsonObject.getString("info"), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                //设置房间打开转态,消息直接呈现，无通知功能;
                                mDatas.get(position).setFlag("1");
//                                Intent i = new Intent(mContext, ChatActivity.class);
//                                Bundle b = new Bundle();
//                                b.putSerializable("room", mDatas.get(position));
//                                i.putExtras(b);
//                                mContext.startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                    Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("uid", String.valueOf(UserInfo.getInstance().getUid()));
                    map.put("rid",String.valueOf(mDatas.get(position).getRid()));
                    return map;
                }
            };
            MyApplication.getmQueue().add(stringRequest);
        }
    }

}
