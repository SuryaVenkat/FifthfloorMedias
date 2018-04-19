package com.fifthfloor.media;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class ConnectionDetector
{
  public Context context;
  
  public ConnectionDetector(Context paramContext)
  {
    this.context = paramContext;
  }
  
  public boolean isConnectingToInternet()
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (localConnectivityManager != null)
    {
      NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager.getAllNetworkInfo();
      if (arrayOfNetworkInfo != null) {
        for (int i = 0; i < arrayOfNetworkInfo.length; i++) {
          if (arrayOfNetworkInfo[i].getState() == State.CONNECTED) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
