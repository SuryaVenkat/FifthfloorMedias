package com.fifthfloor.media;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fifthfloor.media.DBHelper.SQLDB_Helper;
import com.fifthfloor.media.Util.Global_Data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoViewActivity extends Activity {
    public static int count = 0;
    public static int count_video = 0;
    private final int COUNT = 3;
    String DB_START_TIME;
    String EndTime;
    String PATH = "";
    String StartTime;
    String Url = "",firsttime_install_status ="";
    ImageView ad_image;

    Calendar calender;
    String code;
    ConnectionDetector conn;
    SimpleDateFormat df;

    String[] imagefileList = null;
    private int index = 0;
    private int tempindex = 0;
    String onPause_Appcode;
    String onPause_DeviceId;
    String onPause_EndTime;
    String onPause_StartTime;
    String onPause_VideoList;
    File outputFile;
    AnimatorSet set;
    SharedPreferences sp;
    SQLDB_Helper sqldb_helper;
    private Timer timer = new Timer();
    TextView txt_error;
    VideoView videoHolder;
    int video_count = 1;
    VideoData video_data;
    String videocode;
    String[] videofileList = null;
    String[] breakfileList = null;
    String[] TempfileList = null;
    int temp_count = 1;
    Boolean break_video_played = false;
    // Progress Dialog
    private ProgressDialog pDialog;
    int total_videocount;
    ImageView my_image;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        videoHolder = (VideoView) findViewById(R.id.videoview);
        ad_image = (ImageView) findViewById(R.id.img_adstrip);
        txt_error = (TextView) findViewById(R.id.textview);
        conn = new ConnectionDetector(this);
        video_data = new VideoData();
        sqldb_helper = new SQLDB_Helper(this);
        try {
            for (VideoData vd : this.sqldb_helper.getLastRecord()) {
                this.DB_START_TIME = vd.getStartTime().trim();
            }
            Log.d("VideoView", "DB_START_TIME : " + this.DB_START_TIME);
            if (this.DB_START_TIME == null) {
                this.calender = Calendar.getInstance();
                this.df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                this.StartTime = this.df.format(this.calender.getTime());
                Global_Data.StartTime = this.StartTime;
            } else if (Global_Data.Booted.booleanValue()) {
                this.calender = Calendar.getInstance();
                this.df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                this.StartTime = this.df.format(this.calender.getTime());
                Global_Data.StartTime = this.StartTime;
            } else {
                this.calender = Calendar.getInstance();
                this.df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                this.StartTime = this.df.format(this.calender.getTime());
                Global_Data.StartTime = this.StartTime;
            }
            Log.d("VideoView", "StartTime : " + this.StartTime);
            Global_Data.Device_Id = Secure.getString(getContentResolver(), "android_id");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ad_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoHolder.stopPlayback();
                final Dialog dialog = new Dialog(VideoViewActivity.this);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.connection_info);
                dialog.show();
                ImageView imgview = (ImageView) dialog.findViewById(R.id.btnclose);
                final EditText text_info = (EditText) dialog.findViewById(R.id.txtalertinfo);
                final EditText text_pass = (EditText) dialog.findViewById(R.id.txtalertpass);
                Button btn_retry = (Button) dialog.findViewById(R.id.btn_retry);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                imgview.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        videoHolder.resume();
                        dialog.dismiss();
                        //finish();
                    }
                });
                btn_retry.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (text_info.getText().toString().trim().equalsIgnoreCase("")) {
                            Toast.makeText(VideoViewActivity.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();
                        } else {
                            Editor editor = VideoViewActivity.this.sp.edit();
                            //editor.putString("Url", "http://192.168.1.120/5thfloor/app/api.video/n1/Video/get_video_category?app_id=" + text_info.getText().toString().trim());
                            editor.putString("Url", Global_Data.GetVideo_url + text_pass.getText().toString().trim()+"&app_id="+text_info.getText().toString().trim());
                            editor.commit();
                            Url = VideoViewActivity.this.sp.getString("Url", null);
                            for (VideoData vd : sqldb_helper.getLastRecord()) {
                                onPause_StartTime = vd.getStartTime();
                                onPause_Appcode = vd.getAppcode();
                                onPause_DeviceId = vd.getDeviceId();
                                onPause_VideoList = vd.getVideoLists();
                                onPause_EndTime = vd.getEndTime();
                                Log.d("onPause_SQLITE_DB: ", "StartTime: " + vd.getStartTime() + " ,AppCode: " + vd.getAppcode() + " ,DeviceId: " + vd.getDeviceId() + " ,DeviceList: " + vd.getVideoLists() + " ,EndTime: " + vd.getEndTime());
                            }
                            SendVideoCount();
                            Global_Data.videourllist.clear();
                            Global_Data.Bannerurllist.clear();
                            Global_Data.BreakVideourllist.clear();
                            index = 0;
                            dialog.dismiss();
                            CheckforConnection();

                        }

                    }
                });
            }
        });
    }

    protected void onResume() {
        super.onResume();
        Appcontroller.activityResumed();
        try {
            sp = getSharedPreferences("5thfloormedias", 0);
            Url = sp.getString("Url", null);
            firsttime_install_status = sp.getString("First_timeInstall",null);
            Log.d("VideoviewActivity","URL_STATUS :: "+Url);

            if(firsttime_install_status.equalsIgnoreCase("true")){
                Editor editor = VideoViewActivity.this.sp.edit();
                editor.putString("First_timeInstall", "false");
                editor.putString("Url", null);
                editor.commit();

                sp = getSharedPreferences("5thfloormedias", 0);
                Url = sp.getString("Url", null);
                Log.d("VideoviewActivity","AFTERCHECKING :: "+Url);
            }
            if (Url == null || firsttime_install_status.equalsIgnoreCase("true")) {
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.connection_info);
                dialog.show();
                ImageView imgview = (ImageView) dialog.findViewById(R.id.btnclose);
                final EditText text_info = (EditText) dialog.findViewById(R.id.txtalertinfo);
                final EditText text_pass = (EditText) dialog.findViewById(R.id.txtalertpass);
                Button btn_retry = (Button) dialog.findViewById(R.id.btn_retry);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                imgview.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
                btn_retry.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (text_info.getText().toString().trim().equalsIgnoreCase("")) {
                            Toast.makeText(VideoViewActivity.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();
                        } else {
                            Editor editor = VideoViewActivity.this.sp.edit();
                            //editor.putString("Url", "http://192.168.1.120/5thfloor/app/api.video/n1/Video/get_video_category?app_id=" + text_info.getText().toString().trim());
                            editor.putString("Url", Global_Data.GetVideo_url + text_pass.getText().toString().trim()+"&app_id="+text_info.getText().toString().trim());
                            editor.commit();
                            Url = VideoViewActivity.this.sp.getString("Url", null);
                            dialog.dismiss();
                            CheckforConnection();
                        }

                    }
                });
            } else if (Global_Data.Booted.booleanValue()) {
                CheckforConnection();
                Global_Data.Booted = Boolean.valueOf(false);
            } else if (Global_Data.internet_check) {
                Log.d("internetcheck", "trigrred in internet check");
                CheckforConnection();
            } else {
                File videoFiles = new File("/storage/emulated/0/Download/VideoMedia");
                if (videoFiles.isDirectory()) {
                    videofileList = videoFiles.list();
                }
                File imageFiles = new File("/storage/emulated/0/Download/BannerMedia");
                if (imageFiles.isDirectory()) {
                    this.imagefileList = imageFiles.list();
                }
                File breakFiles = new File("/storage/emulated/0/Download/BreakMedia");
                if (breakFiles.isDirectory()) {
                    this.breakfileList = breakFiles.list();
                }
                try {
                    sp = getSharedPreferences("5thfloormedias", 0);
                    Global_Data.Lengthof_url = Integer.valueOf(sp.getString("videourl_length", null));
                    Global_Data.Lengthof_imageurl = Integer.valueOf(sp.getString("bannerurl_length", null));
                    Global_Data.Lengthof_breakvurl = Integer.valueOf(sp.getString("breakVurl_length", null));
                    Global_Data.breakvideo_duration = String.valueOf(sp.getString("Breakvideo_Duration", null));
                    Log.e("Countcondition_resume", "Count of Video : " + Global_Data.Lengthof_url + " Count of Path : " + videofileList.length);
//                    Log.e("Countcondition", "Count of Image : " + Global_Data.Lengthof_imageurl + " Count of Path : " + imagefileList.length);
//                    Log.e("Countcondition", "Count of BreakVideo : " + Global_Data.Lengthof_breakvurl + " Count of Path : " + this.breakfileList.length);
                    if (videofileList.length == Global_Data.Lengthof_url ) {
                        Playvideo();
                        /*final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Playvideo();
                            }
                        }, 2000);*/
                    } else {
                        CheckforConnection();
                        //DownloadVideoandBanner();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void onPause() {
        Appcontroller.activityPaused();
        Global_Data.VideoCountList.size();
        for (VideoData vd : this.sqldb_helper.getLastRecord()) {
            this.onPause_StartTime = vd.getStartTime();
            this.onPause_Appcode = vd.getAppcode();
            this.onPause_DeviceId = vd.getDeviceId();
            this.onPause_VideoList = vd.getVideoLists();
            this.onPause_EndTime = vd.getEndTime();
            Log.d("onPause_SQLITE_DB: ", "StartTime: " + vd.getStartTime() + " ,AppCode: " + vd.getAppcode() + " ,DeviceId: " + vd.getDeviceId() + " ,DeviceList: " + vd.getVideoLists() + " ,EndTime: " + vd.getEndTime());
        }
        SendVideoCount();
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("OnDestroy", "OnDestroy");
    }
    private void CheckforConnection() {
        if (!this.conn.isConnectingToInternet()) {
            CallDialog();
        } else if (VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList();
            List<String> permissionsList = new ArrayList();
            if (!addPermission(permissionsList, "android.permission.READ_EXTERNAL_STORAGE")) {
                permissionsNeeded.add("Read external storage");
            }
            if (!addPermission(permissionsList, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                permissionsNeeded.add("Write external storage");
            }
            if (permissionsList.size() > 0) {
                requestPermissions((String[]) permissionsList.toArray(new String[permissionsList.size()]), 1);
            } else {
                GetURLfromAPI();
            }
        } else {
            GetURLfromAPI();
        }
    }

    private void CallDialog() {
        this.txt_error.setVisibility(View.VISIBLE);
        this.videoHolder.setVisibility(View.GONE);
        this.txt_error.setText("Connecting to internet, please wait..");
        this.txt_error.setTextColor(Color.parseColor("#ff0000"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VideoViewActivity.this.finish();
                VideoViewActivity.this.overridePendingTransition(0, 0);
                VideoViewActivity.this.startActivity(VideoViewActivity.this.getIntent());
                Global_Data.internet_check = true;
                VideoViewActivity.this.overridePendingTransition(0, 0);
            }
        }, 30000);
    }

    public class Download_Image extends AsyncTask<Void, Void, String> {
        Context context;
        int fileLength = 0;
        ProgressDialog mProgressDialog;
        String murl = "";

        public Download_Image(Context context, String url) {
            this.context = context;
            this.murl = url;
        }

        protected void onPreExecute() {
            this.mProgressDialog = ProgressDialog.show(this.context, "", "Please wait, Downloading…");
        }

        protected String doInBackground(Void... params) {
            try {
                Log.v("Image URL", "URL ::: : " + this.murl);
                URL url = new URL(this.murl);
                URLConnection connection = url.openConnection();
                connection.connect();
                fileLength = connection.getContentLength();
                String[] path = url.getPath().split("/");
                String mp3 = path[path.length - 1];
                PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/BannerMedia/";
                Log.v("ImagePath", "PATH: " + PATH);
                File file = new File(PATH);
                Log.v("ImagePath", "file: " + file);
                file.mkdirs();
                String fileName = mp3;
                outputFile = new File(file, fileName);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                outputFile.createNewFile();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(outputFile);
                byte[] data = new byte[1024];
                long total = 0;
                Log.v("ImagePath", "PATH: " + outputFile);
                while (true) {
                    int count = input.read(data);
                    if (count == -1) {
                        break;
                    }
                    total += (long) count;
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "done";
        }

        protected void onPostExecute(String result) {
            this.mProgressDialog.dismiss();
        }
    }

    private void SendVideoCount() {
        String url = Global_Data.SendVideoCount_url;
        System.out.println("URL Sending VideoCount " + url);
        StringRequest strReq = new StringRequest(1, url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");
                    Global_Data.VideoCountList.clear();
                    if (status) {
                        Log.d("SucessMsg", " " + jObj.getString("success"));
                        sqldb_helper.DeleteAll();
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
                params.put(SQLDB_Helper.FIELD_STARTTIME, onPause_StartTime);
                params.put(SQLDB_Helper.FIELD_ENDTIME, onPause_EndTime);
                params.put("device_code", onPause_DeviceId);
                params.put(SQLDB_Helper.FIELD_APPCODE, onPause_Appcode);
                params.put("video_details", onPause_VideoList);
                Log.d("ParamsFromOnPause", "Request" + params.toString());
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Appcontroller.getInstance().addToRequestQueue(strReq);
    }

    public void GetURLfromAPI() {
        txt_error.setVisibility(View.GONE);
        videoHolder.setVisibility(View.VISIBLE);
        Log.d("GetURLfromAPI", "" + this.Url);
        Global_Data.videourllist.clear();
        Global_Data.Bannerurllist.clear();
        Global_Data.BreakVideourllist.clear();
        JsonObjectRequest strreq = new JsonObjectRequest(0, this.Url, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Success Response", "" + response);
                    boolean status = response.getBoolean("status");
                    Log.d("Result ", "" + response.getBoolean("status"));
                    if (status) {
                        int i;
                        JSONObject json;
                        JSONArray videolist = response.getJSONArray("data");
                        JSONArray bannerlist = response.getJSONArray("banner_link");
                        JSONArray breakvideolist = response.optJSONArray("break_video");

                        Editor editor = VideoViewActivity.this.sp.edit();
                        editor.putString("videourl_length", String.valueOf(videolist.length()));
                        editor.putString("bannerurl_length", String.valueOf(bannerlist.length()));
                        editor.putString("breakVurl_length", String.valueOf(breakvideolist.length()));
                        editor.commit();

                        Global_Data.Lengthof_url = videolist.length();
                        Global_Data.Lengthof_imageurl = bannerlist.length();
                        Global_Data.Lengthof_breakvurl = breakvideolist.length();

                        Log.d("length Video", ": " + Global_Data.Lengthof_url);
                        Log.d("length Image", ": " + Global_Data.Lengthof_imageurl);
                        Log.d("length BreakVideo", ": " + Global_Data.Lengthof_breakvurl);

                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/VideoMedia/");
                        File banner_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/BannerMedia/");
                        File break_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/BreakMedia/");

                         try {
                             FileUtils.deleteDirectory(file);
                             FileUtils.deleteDirectory(banner_file);
                             FileUtils.deleteDirectory(break_file);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        for (i = 0; i < videolist.length(); i++) {
                            try {
                                json = videolist.getJSONObject(i);
                                video_data.setvideoUrl(json.getString("v_url"));
                                Global_Data.videourllist.add(json.getString("v_url"));
                                Log.d("Video_URL", json.getString("v_url"));
                            } catch (Exception ex) {
                                try {
                                    Log.d("VideoDownload Exception", ex.toString());
                                } catch (Exception ex2) {
                                    Log.d("VideoDownload Exception", ex2.toString());
                                    return;
                                }
                            }
                        }
                        for (i = 0; i < bannerlist.length(); i++) {
                            try {
                                json = bannerlist.getJSONObject(i);
                                video_data.setImageurl(json.getString("img_path"));
                                video_data.setImage_duration(json.getString("hold_time"));
                                Global_Data.Bannerurllist.add(json.getString("img_path"));
                                Log.d("Image_URL", json.getString("img_path"));

                            } catch (Exception ex22) {
                                Log.d("ImageDownload Exception", ex22.toString());
                            }
                        }
                        for (i = 0; i < breakvideolist.length(); i++) {
                            try {
                                json = breakvideolist.getJSONObject(i);
                                video_data.setBreakvideoUrl(json.getString("v_url"));
                                video_data.setBreakvideoduration(json.getString("count"));
                                Global_Data.breakvideo_duration = json.getString("count");
                                Global_Data.BreakVideourllist.add(json.getString("v_url"));
                                Log.d("BreakVideo_URL", json.getString("v_url"));

                                Editor edit = VideoViewActivity.this.sp.edit();
                                edit.putString("Breakvideo_Duration", String.valueOf(Global_Data.breakvideo_duration));
                                edit.commit();

                            } catch (Exception ex22) {
                                Log.d("ImageDownload Exception", ex22.toString());
                            }
                        }
                        // Global_Data.videourllist.addAll(Global_Data.Bannerurllist);

                        Global_Data.internet_check = false;
                        DownloadVideoandBanner();
                        return;
                    }
                    String errorMsg = response.optString("error");
                    Toast.makeText(VideoViewActivity.this, "Invalid code! Enter the valid code.", Toast.LENGTH_SHORT).show();
                    Editor editor = VideoViewActivity.this.sp.edit();
                    editor.putString("Url", null);
                    editor.commit();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(VideoViewActivity.this.getIntent());
                            overridePendingTransition(0, 0);
                        }
                    }, 3000);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NegativeArraySizeException e2) {
                    e2.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("ServerError","Error :: "+obj);
                        String errorMsg = obj.optString("error");
                        Toast.makeText(VideoViewActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Editor editor = VideoViewActivity.this.sp.edit();
                        editor.putString("Url", null);
                        editor.commit();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(VideoViewActivity.this.getIntent());
                                overridePendingTransition(0, 0);
                            }
                        }, 3000);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        });
        strreq.setRetryPolicy(new DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Appcontroller.getInstance().addToRequestQueue(strreq);
    }

    private void DownloadVideoandBanner() {
        if(Global_Data.Bannerurllist.size() > 0){
            for (int i = 0; i < Global_Data.Bannerurllist.size(); i++) {
                Log.d("BannerImageUrl", "BannerImageUrl" + (Global_Data.Bannerurllist.get(i)));
                new Download_Image(this,Global_Data.Bannerurllist.get(i)).execute();
            }
        }

      /*  for (int j = 0; j < Global_Data.videourllist.size(); j++) {
            Log.d("VideoUrl", "VideoUrl" + (Global_Data.videourllist.get(j)));
            new Download(this, Global_Data.videourllist.get(j)).execute();
        }*/

        total_videocount = Global_Data.Lengthof_url+Global_Data.Lengthof_breakvurl;
        new Download(this,Global_Data.videourllist).execute();

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait..."+ "\n\n" + "Remaing videos : " +total_videocount);
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
    public class Download extends AsyncTask<String, Integer, String> {
        Context context;
        int fileLength = 0;
        // int i;
        ProgressDialog mProgressDialog;
        //  List<String> murl = new ArrayList<>();
        ArrayList<String> murl = new ArrayList<>();

        public Download(Context context, ArrayList<String> url) {
            context = context;
            murl = url;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                for (int i = 0; i < murl.size(); i++) {
                    Log.v("video URL", "URL ::: : " + murl.get(i));
                    URL url = new URL(murl.get(i));

                    URLConnection connection = url.openConnection();
                    connection.connect();
                    int fileLength = connection.getContentLength();
                    String[] path = url.getPath().split("/");
                    String mp3 = i+"~"+path[path.length - 1];
                    VideoViewActivity.this.PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/VideoMedia/";
                    Log.v("VideoPath", "PATH: " + PATH);
                    File file = new File(PATH);
                    Log.v("VideoPath", "file: " + file);
                    file.mkdirs();
                    String fileName = mp3;
                    outputFile = new File(file, fileName);
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    outputFile.createNewFile();
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(outputFile);
                    byte[] data = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) ((total * 100)/fileLength));

                        // writing data to file
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    total_videocount = total_videocount -1;

                    if(i == murl.size()-1){
                        // dismissDialog(progress_bar_type);
                        // DownloadBreakvideo();
                        if(Global_Data.BreakVideourllist.size() > 0){
                            DownloadBreakvideo();
                        }else{
                            dismissDialog(progress_bar_type);
                            CheckForDownloadCompletion();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "done";
        }

        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage

            pDialog.setProgress(progress[0]);
            pDialog.setMessage("Downloading file. Please wait..."+ "\n\n" + "Remaing videos : " +total_videocount);
        }
        @Override
        protected void onPostExecute(String result) {
            // dismissDialog(progress_bar_type);

        }
    }

    private void DownloadBreakvideo() {
        new DownloadBreakVideo(this,Global_Data.BreakVideourllist).execute();
    }
    public class DownloadBreakVideo extends AsyncTask<String, Integer, String> {
        Context context;
        int fileLength = 0;
        ProgressDialog mProgressDialog;
        //  List<String> murl = new ArrayList<>();
        ArrayList<String> murl = new ArrayList<>();

        public DownloadBreakVideo(Context context, ArrayList<String> url) {
            context = context;
            murl = url;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                for (int i = 0; i < murl.size(); i++) {
                    Log.v("BreakVideo URL", "URL ::: : " + murl.get(i));
                    URL url = new URL(murl.get(i));

                    URLConnection connection = url.openConnection();
                    connection.connect();
                    int fileLength = connection.getContentLength();
                    String[] path = url.getPath().split("/");
                    String mp3 = i+"~"+path[path.length - 1];
                    VideoViewActivity.this.PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/BreakMedia/";
                    Log.v("BreakVideoPath", "PATH: " + PATH);
                    File file = new File(PATH);
                    Log.v("BreakVideoPath", "file: " + file);
                    file.mkdirs();
                    String fileName = mp3;
                    outputFile = new File(file, fileName);
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    outputFile.createNewFile();
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(outputFile);
                    byte[] data = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) ((total * 100)/fileLength));

                        // writing data to file
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    total_videocount = total_videocount -1;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "done";
        }

        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage

            pDialog.setProgress(progress[0]);
            pDialog.setMessage("Downloading file. Please wait..."+ "\n\n" + "Remaing videos : " +total_videocount);
        }
        @Override
        protected void onPostExecute(String result) {

            dismissDialog(progress_bar_type);
            CheckForDownloadCompletion();
        }
    }
    private void CheckForDownloadCompletion() {

        try{
            File videoFiles = new File("/storage/emulated/0/Download/VideoMedia");
            if (videoFiles.isDirectory()) {
                this.videofileList = videoFiles.list();
            }
            File imageFiles = new File("/storage/emulated/0/Download/BannerMedia");
            if (imageFiles.isDirectory()) {
                this.imagefileList = imageFiles.list();
            }
            File BreakvideoFiles = new File("/storage/emulated/0/Download/BreakMedia");
            if (BreakvideoFiles.isDirectory() && BreakvideoFiles.length() >0) {
                this.breakfileList = BreakvideoFiles.list();
            }

            sp = getSharedPreferences("5thfloormedias", 0);
            Global_Data.Lengthof_url = Integer.valueOf(sp.getString("videourl_length", null));
            Global_Data.Lengthof_imageurl = Integer.valueOf(sp.getString("bannerurl_length", null));
            Global_Data.Lengthof_breakvurl = Integer.valueOf(sp.getString("breakVurl_length", null));
            Log.d("Countcondition_download", "Count of Video : " + Global_Data.Lengthof_url + " Count of Path : " + this.videofileList.length);
            Log.d("Countcondition_download", "Count of Image : " + Global_Data.Lengthof_imageurl );
            Log.d("Countcondition_download", "Count of Breakvideo : " + Global_Data.Lengthof_breakvurl );

            if (videofileList.length == Global_Data.Lengthof_url /* && this.imagefileList.length == Global_Data.Lengthof_imageurl &&
                    breakfileList.length == Global_Data.Lengthof_breakvurl*/) {
                Playvideo();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void Playvideo() {

        try {

            if (isMyServiceRunning(ReportofApp.class)) {
                Log.e("Reportservice", "ReportService is running");
                stopService(new Intent(this, ReportofApp.class));
                startService(new Intent(this, ReportofApp.class));
            } else {
                startService(new Intent(this, ReportofApp.class));
            }
            if(Global_Data.Lengthof_breakvurl !=0){

                final List<String> uris = new ArrayList();
                final List<String> uris_break = new ArrayList();
                File videoFiles = new File("/storage/emulated/0/Download/VideoMedia");
                File breakvideoFiles = new File("/storage/emulated/0/Download/BreakMedia");
                if (videoFiles.isDirectory()) {
                    videofileList = videoFiles.list();
                }
                if (breakvideoFiles.isDirectory()) {
                    breakfileList = breakvideoFiles.list();
                }

                for (String str : videofileList) {
                    uris.add("/storage/emulated/0/Download/VideoMedia/" + str);
                    System.out.print(uris);
                }
                for (String str1 : breakfileList) {
                    uris_break.add("/storage/emulated/0/Download/BreakMedia/" + str1);
                    System.out.print(uris_break);
                }
                Uri uri = Uri.parse(uris.get(0));
                videoHolder.setVideoURI(uri);
                videoHolder.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoHolder.start();
                    }
                });
                videoHolder.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.d("video", "setOnErrorListener ");
                        return true;
                    }
                });
                videoHolder.setOnCompletionListener(new OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        try {
                            if(break_video_played){
                                break_video_played = false;
                                Log.d("VideoCount", " Video tempindex  ::" + tempindex + " path :" + (uris_break.get(tempindex)));
                                Log.d("VideoCount", " No of Counts  ::" + video_count);
                                int pos = (uris_break.get(tempindex)).lastIndexOf("/");
                                videocode = (uris_break.get(tempindex)).substring(pos + 1, (uris_break.get(tempindex)).length()).trim();
                                try{
                                    videocode = videocode.substring(videocode.indexOf("~")+1);
                                    videocode.trim();
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }

                                Log.d("VideoCode", " VideoCode ::" + videocode);
                                Global_Data.VideoCountList.add(videocode);

                                Calendar cal = Calendar.getInstance();
                                df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                EndTime = df.format(cal.getTime());
                                String Url = sp.getString("Url", null);
                                Global_Data.app_code = Url.substring(Url.lastIndexOf("=") + 1, Url.length());
                                Log.d("VideoViewActivity", "Device_ID" + Global_Data.Device_Id + "  StartDate " + Global_Data.StartTime + " Appcode " + Global_Data.app_code);
                                sqldb_helper.InsertData(new VideoData(StartTime, Global_Data.app_code, Global_Data.Device_Id, Arrays.toString(Global_Data.VideoCountList.toArray()).replace("[", "").replace("]", ""), VideoViewActivity.this.EndTime));
                                for (VideoData vd : sqldb_helper.getLastRecord()) {
                                    Log.e("FromPlay: ", "StartTime: " + vd.getStartTime() + " ,AppCode: " + vd.getAppcode() + " ,DeviceId: " + vd.getDeviceId() + " ,DeviceList: " + vd.getVideoLists() + " ,EndTime: " + vd.getEndTime());
                                }
                                tempindex = (tempindex + 1) % uris_break.size();
                                temp_count = 0;
                            }else{
                                Log.d("VideoCount", " Video index  ::" + index + " path :" + (uris.get(index)));
                                Log.d("VideoCount", " No of Counts  ::" + video_count);
                                int pos = (uris.get(index)).lastIndexOf("/");
                                videocode = (uris.get(index)).substring(pos + 1, (uris.get(index)).length()).trim();
                                try{
                                    videocode = videocode.substring(videocode.indexOf("~")+1);
                                    videocode.trim();
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }

                                Log.d("VideoCode", " VideoCode ::" + videocode);
                                Global_Data.VideoCountList.add(videocode);
                                Calendar cal = Calendar.getInstance();
                                df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                EndTime = df.format(cal.getTime());
                                String Url = sp.getString("Url", null);
                                Global_Data.app_code = Url.substring(Url.lastIndexOf("=") + 1, Url.length());
                                Log.d("VideoViewActivity", "Device_ID" + Global_Data.Device_Id + "  StartDate " + Global_Data.StartTime + " Appcode " + Global_Data.app_code);
                                sqldb_helper.InsertData(new VideoData(StartTime, Global_Data.app_code, Global_Data.Device_Id, Arrays.toString(Global_Data.VideoCountList.toArray()).replace("[", "").replace("]", ""), VideoViewActivity.this.EndTime));
                                for (VideoData vd : sqldb_helper.getLastRecord()) {
                                    Log.e("FromPlay: ", "StartTime: " + vd.getStartTime() + " ,AppCode: " + vd.getAppcode() + " ,DeviceId: " + vd.getDeviceId() + " ,DeviceList: " + vd.getVideoLists() + " ,EndTime: " + vd.getEndTime());
                                }
                                index = (index + 1) % uris.size();
                            }
                            Log.e("FromPlay: ", "Tempcount: " + Global_Data.breakvideo_duration);

                            if(temp_count == Integer.valueOf(Global_Data.breakvideo_duration)){
                                break_video_played = true;

                                videoHolder.setVideoPath(uris_break.get(tempindex));
                            }else{
                                videoHolder.setVideoPath(uris.get(index));
                            }
                            video_count++;
                            temp_count++;


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                try{

                    File imageFiles = new File("/storage/emulated/0/Download/BannerMedia");
                    if (imageFiles.isDirectory()) {
                        imagefileList = imageFiles.list();
                    }
                    if(imagefileList.length > 0){
                        animate(ad_image, imagefileList, 0, true);
                    }


                }catch (Exception ex){
                    ex.printStackTrace();
                }


            }else {

                Log.d("VideoViewActivity","Else PArt");
                final List<String> uris = new ArrayList();
                final List<String> uris_break = new ArrayList();
                File videoFiles = new File("/storage/emulated/0/Download/VideoMedia");
                if (videoFiles.isDirectory()) {
                    videofileList = videoFiles.list();
                }


                for (String str : videofileList) {
                    uris.add("/storage/emulated/0/Download/VideoMedia/" + str);
                    System.out.print(uris);
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        Uri uri = Uri.parse(uris.get(0));
                        videoHolder.setVideoURI(uri);
                    }
                });

                videoHolder.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoHolder.start();
                    }
                });
                this.videoHolder.setOnCompletionListener(new OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        try {
                            Log.d("VideoCount", " Video index  ::" + index + " path :" + (uris.get(index)));
                            Log.d("VideoCount", " No of Counts  ::" + video_count);
                            int pos = (uris.get(index)).lastIndexOf("/");
                            videocode = (uris.get(index)).substring(pos + 1, (uris.get(index)).length()).trim();
                            try{
                                videocode = videocode.substring(videocode.indexOf("~")+1);
                                videocode.trim();
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                            Global_Data.VideoCountList.add(videocode);
                            Calendar cal = Calendar.getInstance();
                            df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                            EndTime = df.format(cal.getTime());
                            String Url = sp.getString("Url", null);
                            Global_Data.app_code = Url.substring(Url.lastIndexOf("=") + 1, Url.length());
                            Log.d("VideoViewActivity", "Device_ID" + Global_Data.Device_Id + "  StartDate " + Global_Data.StartTime + " Appcode " + Global_Data.app_code);
                            sqldb_helper.InsertData(new VideoData(StartTime, Global_Data.app_code, Global_Data.Device_Id, Arrays.toString(Global_Data.VideoCountList.toArray()).replace("[", "").replace("]", ""), VideoViewActivity.this.EndTime));
                            for (VideoData vd : sqldb_helper.getLastRecord()) {
                                Log.e("FromPlay: ", "StartTime: " + vd.getStartTime() + " ,AppCode: " + vd.getAppcode() + " ,DeviceId: " + vd.getDeviceId() + " ,DeviceList: " + vd.getVideoLists() + " ,EndTime: " + vd.getEndTime());
                            }
                            index = (index + 1) % uris.size();
                            videoHolder.setVideoPath(uris.get(index));
                            video_count++;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                try{

                    File imageFiles = new File("/storage/emulated/0/Download/BannerMedia");
                    if (imageFiles.isDirectory()) {
                        imagefileList = imageFiles.list();
                    }
                    if(imagefileList.length > 0){
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                animate(ad_image, imagefileList, 0, true);
                            }
                        });

                    }


                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void animate(ImageView imageView, String[] imagefileList, int imageIndex, boolean forever) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setBackground(Drawable.createFromPath(new File("/storage/emulated/0/Download/BannerMedia/" + imagefileList[imageIndex]).getAbsolutePath()));
        Animation fadeIn = new AlphaAnimation(0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration((long) 2000);
        Animation fadeOut = new AlphaAnimation(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset((long) 12000);
        fadeOut.setDuration((long) 1000);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);
        final String[] strArr = imagefileList;
        final int i = imageIndex;
        final ImageView imageView2 = imageView;
        final boolean z = forever;
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (strArr.length - 1 > i) {
                    VideoViewActivity.this.animate(imageView2, strArr, i + 1, z);
                } else if (z) {
                    VideoViewActivity.this.animate(imageView2, strArr, 0, z);
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (VERSION.SDK_INT >= 23 && checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                Map<String, Integer> perms = new HashMap();
                perms.put("android.permission.READ_EXTERNAL_STORAGE", Integer.valueOf(0));
                perms.put("android.permission.WRITE_EXTERNAL_STORAGE", Integer.valueOf(0));
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], Integer.valueOf(grantResults[i]));
                }
                if (((Integer) perms.get("android.permission.READ_EXTERNAL_STORAGE")).intValue() == 0 && ((Integer) perms.get("android.permission.WRITE_EXTERNAL_STORAGE")).intValue() == 0) {
                    GetURLfromAPI();
                    return;
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        for (RunningServiceInfo service : ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
