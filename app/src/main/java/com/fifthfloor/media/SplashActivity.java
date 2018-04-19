package com.fifthfloor.media;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fifthfloor.media.Util.Global_Data;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashActivity extends Activity {
    VideoView videoHolder;
    ConnectionDetector conn;
    private static int SPLASH_TIME_OUT = 3000;
    String statuscode, versionflag;
    Boolean versionfind = false;
    SharedPreferences sp;
    String firsttime_install_status = "",Url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        videoHolder = ((VideoView)findViewById(R.id.videoview));
        conn = new ConnectionDetector(this);

        try
        {
            Uri localUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
            this.videoHolder.setVideoURI(localUri);
            this.videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
                {
                    jump();
                }
            });
            this.videoHolder.start();
            return;
        }
        catch (Exception localException)
        {
            jump();
        }
    }
    private void CheckforConnection() {
        if (conn.isConnectingToInternet()) {
            Checkversion_Update();
        } else {
            CallDialog();
        }
    }
    private void CallDialog() {
        final Dialog dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.connection_check);
        dialog.show();

        ImageView imgview = (ImageView) dialog.findViewById(R.id.btnclose);
        TextView text_info = (TextView) dialog.findViewById(R.id.txtalertinfo);
        Button btn_retry = (Button) dialog.findViewById(R.id.btn_retry);

        text_info.setText("Oops! No internet. Check your connection and Re-try again");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                finish();
            }
        });
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckforConnection();
                dialog.dismiss();
            }
        });
    }
    // version update function..........

    private void Checkversion_Update() {

        Log.d("versionurl", Global_Data.forceupdate_url);
        JsonObjectRequest strreq = new JsonObjectRequest(Request.Method.GET, Global_Data.forceupdate_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject resobj) {
                // TODO Auto-generated method stub

                try {
                    Log.d("JSONObject", "" + resobj);

                    boolean status = resobj.getBoolean("status");
                    Log.d("Result ", "" + resobj.getBoolean("status"));

                    if (status) {
                        // statuscode = response.getString("statuscode");
                        JSONArray arraylist = resobj.getJSONArray("GetUserAppRelease");
                        for (int i = 0; i < arraylist.length(); i++) {
                            JSONObject json = null;
                            try {
                                json = arraylist.getJSONObject(i);
                                versionflag = json.optString("forceupdate");
                                Double newversion = Double.parseDouble(json.optString("version"));
                                Double currversion = Double.parseDouble(BuildConfig.VERSION_NAME);

                                Log.d("Newversion", "" + newversion);
                                Log.d("currentversion", "" + currversion);

                                if (newversion > currversion) {
                                    versionfind = true;
                                }
                                if (versionfind) {
                                    versionfind = false;

                                    final Dialog updatedialog = new Dialog(SplashActivity.this);
                                    updatedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    updatedialog.setContentView(R.layout.activity_forceupdate);
                                    updatedialog.setCancelable(false);
                                    updatedialog.show();

                                    TextView txtinfo = (TextView) updatedialog.findViewById(R.id.txtupdateinfo);
                                    Button updatebtn = (Button) updatedialog.findViewById(R.id.updatebtn);
                                    ImageView updatecolse = (ImageView) updatedialog.findViewById(R.id.updatecolse);

                                    updatebtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            updatedialog.dismiss();
                                            installApplication();
                                        }
                                    });

                                    updatecolse.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (versionflag.equalsIgnoreCase("1")) {
                                                finish();
                                            } else {
                                                updatedialog.dismiss();
                                                Login_Process();
                                            }
                                        }
                                    });
                                } else {
                                    Login_Process();
                                }
                            } catch (Exception e) {
                                Toast.makeText(SplashActivity.this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        // Error in Forceupdate. Get the error message
                        String errorMsg = resobj.optString("error");
                        Toast.makeText(SplashActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.d("Exception", ex.toString());
                    Toast.makeText(SplashActivity.this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show();
                    //Login_Process();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("Error_response", error.toString());

                if (error instanceof NetworkError) {
                    Toast.makeText(SplashActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(SplashActivity.this, "ServerError", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(SplashActivity.this, "NoConnectionError", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(SplashActivity.this, "TimeoutError", Toast.LENGTH_LONG).show();

                }
                //    Login_Process();
            }
        });

        strreq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Appcontroller.getInstance().addToRequestQueue(strreq);

        /*RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strreq);*/
    }

    private void Login_Process() {
        startActivity(new Intent(this, VideoViewActivity.class));
        finish();
    }

    private void installApplication() {
        try {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.fifthfloor.media"));
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(marketIntent);
            finish();
        } catch (Exception ex) {
            Log.d("Playstoreerror", ex.toString());
        }
    }
    private void jump()
    {
        if (isFinishing()) {
            return;
        }
        try{
            sp = getSharedPreferences("5thfloormedias", 0);
            firsttime_install_status = sp.getString("First_timeInstall", null);
            Log.d("SplashActivity","Install_Status :: "+firsttime_install_status);

            if(firsttime_install_status == null){
                SharedPreferences.Editor editor = SplashActivity.this.sp.edit();
                editor.putString("First_timeInstall", "true");
                editor.commit();

                sp = getSharedPreferences("5thfloormedias", 0);
                SharedPreferences.Editor edit = SplashActivity.this.sp.edit();
                edit.putString("Url", null);
                edit.commit();


                Url = sp.getString("Url", null);
                Log.d("SplashActivity","URL_STATUS :: "+Url);
                //Appcontroller.getInstance().clearApplicationData();
                //Log.d("SplashActivity","Install_Status :: "+firsttime_install_status);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        CheckforConnection();

    }
}
