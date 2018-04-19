package com.fifthfloor.media;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.File;

public class Appcontroller extends Application
{
  public static final String TAG = Appcontroller.class.getSimpleName();
  private static boolean activityVisible;
  private static Appcontroller mInstance;
  private ImageLoader mImageLoader;
  private RequestQueue mRequestQueue;

  public void onCreate()
  {
    super.onCreate();
    mInstance = this;
  }

  public static void activityPaused()
  {
    activityVisible = false;
  }
  
  public static void activityResumed()
  {
    activityVisible = true;
  }
  
  public static Appcontroller getInstance()
  {
    return mInstance;
  }
  
  public static boolean isActivityVisible()
  {
    return activityVisible;
  }
  
  public <T> void addToRequestQueue(Request<T> paramRequest)
  {
    paramRequest.setTag(TAG);
    getRequestQueue().add(paramRequest);
  }
  
  public <T> void addToRequestQueue(Request<T> paramRequest, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      paramString = TAG;
    }
    paramRequest.setTag(paramString);
    getRequestQueue().add(paramRequest);
  }
  
  public void cancelPendingRequests(Object paramObject)
  {
    if (this.mRequestQueue != null) {
      this.mRequestQueue.cancelAll(paramObject);
    }
  }

  public RequestQueue getRequestQueue()
  {
    if (this.mRequestQueue == null) {
      this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }
    return this.mRequestQueue;
  }

  public void clearApplicationData() {
    File cache = getCacheDir();
    File appDir = new File(cache.getParent());
    if(appDir.exists()){
      String[] children = appDir.list();
      for(String s : children){
        if(!s.equals("lib")){
          deleteDir(new File(appDir, s));
          Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED");
        }
      }
    }
  }
  public static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    return dir.delete();
  }
}