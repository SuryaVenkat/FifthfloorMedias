package com.fifthfloor.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fifthfloor.media.DBHelper.SQLDB_Helper;
import com.fifthfloor.media.Util.Global_Data;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BootReciever extends BroadcastReceiver {
  public static boolean shutdown = false;
  String Appcode;
  String DeviceId;
  String EndTime;
  String StartTime;
  String VideoList;
  Calendar cal;
  SimpleDateFormat df;

  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
      Log.d("BootReceiver", "Boot_ON");
      Global_Data.Booted = Boolean.valueOf(true);
      try {
        SQLDB_Helper sqldb_helper = new SQLDB_Helper(context);
        Log.d("Reading: ", "Reading all contacts..");
        for (VideoData vd : sqldb_helper.getLastRecord()) {
          this.StartTime = vd.getStartTime();
          this.Appcode = vd.getAppcode();
          this.DeviceId = vd.getDeviceId();
          this.VideoList = vd.getVideoLists();
          this.EndTime = vd.getEndTime();
          Log.d("SQLITE_DB: ", "StartTime: " + vd.getStartTime() + " ,AppCode: " + vd.getAppcode() + " ,DeviceId: " + vd.getDeviceId() + " ,DeviceList: " + vd.getVideoLists() + " ,EndTime: " + vd.getEndTime());
        }
        SendVideoCount();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      Intent lockscreen_intent = new Intent(context, SplashActivity.class);
      lockscreen_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(lockscreen_intent);
      shutdown = false;
    }
  }

  private void SendVideoCount() {

    System.out.println("URL Sending VideoCount " + Global_Data.SendVideoCount_url);
    StringRequest strReq = new StringRequest(1, Global_Data.SendVideoCount_url, new Listener<String>() {
      @Override
      public void onResponse(String response) {
        try {
          JSONObject jObj = new JSONObject(response);
          boolean status = jObj.getBoolean("status");
          Global_Data.VideoCountList.clear();
          if (status) {
            Log.d("SucessMsg", " " + jObj.getString("success"));
            return;
          }
          Log.d("ErrorMsg", " " + jObj.getString("error"));
        } catch (JSONException e) {
          e.printStackTrace();
        } catch (NegativeArraySizeException e2) {
          e2.printStackTrace();
        }
      }
    }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
      }
    }) {
      protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap();
        params.put(SQLDB_Helper.FIELD_STARTTIME, BootReciever.this.StartTime);
        params.put(SQLDB_Helper.FIELD_ENDTIME, BootReciever.this.EndTime);
        params.put("device_code", BootReciever.this.DeviceId);
        params.put(SQLDB_Helper.FIELD_APPCODE, BootReciever.this.Appcode);
        params.put("video_details", BootReciever.this.VideoList);
        Log.d("ReportofAppFromBoot", "Request" + params.toString());
        return params;
      }
    };
    strReq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    Appcontroller.getInstance().addToRequestQueue(strReq);
  }
}
