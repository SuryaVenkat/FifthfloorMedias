package com.fifthfloor.media;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

public class InputStreamVolleyRequest
  extends Request<byte[]>
{
  private final Response.Listener<byte[]> mListener;
  private Map<String, String> mParams;
  public Map<String, String> responseHeaders;

  public InputStreamVolleyRequest(int paramInt, String paramString, Response.Listener<byte[]> paramListener, Response.ErrorListener paramErrorListener, HashMap<String, String> paramHashMap)
  {
    super(0, paramString, paramErrorListener);
    setShouldCache(false);
    this.mListener = paramListener;
    this.mParams = paramHashMap;
  }
  
  protected void deliverResponse(byte[] paramArrayOfByte)
  {
    this.mListener.onResponse(paramArrayOfByte);
  }
  
  protected Map<String, String> getParams()
    throws AuthFailureError
  {
    return this.mParams;
  }
  
  protected Response<byte[]> parseNetworkResponse(NetworkResponse paramNetworkResponse)
  {
    this.responseHeaders = paramNetworkResponse.headers;
    return Response.success(paramNetworkResponse.data, HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
  }
}
