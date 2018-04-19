package com.fifthfloor.media;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
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
import java.util.Timer;
import java.util.TimerTask;

public class ReportofApp extends Service {
    Integer AppStatus;
    private String LOG_TAG = null;
    String code = "";
    private Context context = this;
    private Runnable mLaunchTask = new Runnable() {
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            Log.d("test", "started at " + calendar.get(Calendar.DAY_OF_YEAR) + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.DAY_OF_WEEK));

        }
    };
    SharedPreferences sp;
    private Timer timer = new Timer();

    public void onCreate() {
        super.onCreate();
        this.LOG_TAG = "app_name";
        Log.i(this.LOG_TAG, "service created");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i(ReportofApp.this.LOG_TAG, "In onStartCommand App Status:: " + Appcontroller.isActivityVisible());
                SendReport();
            }
        }, 0, 300000); // 5mins once updating to server
        return Service.START_REDELIVER_INTENT;
    }

    public IBinder onBind(Intent intent) {
        Log.i(this.LOG_TAG, "In onBind");
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(this.LOG_TAG, "In onDestroyed");
    }

    public void SendReport() {

        System.out.println("URL Sending Report " + Global_Data.SendAppStatus_url);
        StringRequest strReq = new StringRequest(1, Global_Data.SendAppStatus_url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getBoolean("status")) {
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
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                try {
                    ReportofApp.this.sp = ReportofApp.this.getSharedPreferences("5thfloormedias", 0);
                    String Url = ReportofApp.this.sp.getString("Url", null);
                    int pos = Url.lastIndexOf("=");
                    code = Url.substring(pos + 1, Url.length());
                    Log.d("URL", "URL : " + Url + " COdE " + ReportofApp.this.code);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (Appcontroller.isActivityVisible()) {
                    ReportofApp.this.AppStatus = Integer.valueOf(1);
                } else if (!Appcontroller.isActivityVisible()) {
                    ReportofApp.this.AppStatus = Integer.valueOf(2);
                }
                Map<String, String> params = new HashMap();
                params.put(SQLDB_Helper.FIELD_APPCODE, ReportofApp.this.code);
                params.put("status", AppStatus.toString());
                params.put("date_time", formattedDate);
                Log.d("ReportofApp", "Request" + params.toString());
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Appcontroller.getInstance().addToRequestQueue(strReq);
    }
}
