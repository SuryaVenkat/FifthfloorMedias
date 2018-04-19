package com.fifthfloor.media.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fifthfloor.media.VideoData;

import java.util.ArrayList;
import java.util.List;

public class SQLDB_Helper
  extends SQLiteOpenHelper
{
  private static final String DATABASE_NAME = "VideoDB";
  private static final int DATABASE_VERSION = 1;
  public static final String FIELD_APPCODE = "appcode";
  public static final String FIELD_DEVICEID = "device_id";
  public static final String FIELD_ENDTIME = "end_time";
  public static final String FIELD_ID = "_id";
  public static final String FIELD_STARTTIME = "start_time";
  public static final String FIELD_VIDEOLIST = "video_list";
  private static final String TABLE_NAME = "VideoList";
  SQLiteDatabase fdb;
  
  public SQLDB_Helper(Context paramContext)
  {
    super(paramContext, "VideoDB", null, 1);
  }
  
  public Cursor Check_RecordExist(String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    Cursor localCursor1 = localSQLiteDatabase.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] { "table", "VideoList" });
    int i = localCursor1.getCount();
    Log.d("tablecount", "" + i);
    localSQLiteDatabase.close();
    if (i > 0)
    {
      Cursor localCursor2 = getReadableDatabase().rawQuery("SELECT * FROM VideoList WHERE _id=?", new String[] { paramString });
      Log.d("Select query", "" + localCursor2);
      int j = localCursor1.getCount();
      Log.d("tablecount", "" + j);
      return localCursor2;
    }
    return localCursor1;
  }
  
  public void DeleteAll()
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.execSQL("DELETE  FROM VideoList");
    localSQLiteDatabase.close();
    Log.d("Record", "Deleted");
  }
  
  public void GetAll()
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.execSQL("SELECT  * FROM VideoList");
    localSQLiteDatabase.close();
    Log.d("Record", "GetAll");
  }
  
  public void InsertData(VideoData paramVideoData)
  {
    try
    {
      this.fdb = getWritableDatabase();
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("start_time", paramVideoData.getStartTime());
      localContentValues.put("appcode", paramVideoData.getAppcode());
      localContentValues.put("device_id", paramVideoData.getDeviceId());
      localContentValues.put("video_list", paramVideoData.getVideoLists());
      localContentValues.put("end_time", paramVideoData.getEndTime());
      this.fdb.insert("VideoList", null, localContentValues);
      this.fdb.close();
      Log.e("Insert success", "Records has been saved :VideoList");
      return;
    }
    catch (Exception localException)
    {
      Log.d("TableError", localException.toString());
    }
  }
  
  public void InsertionData(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    try
    {
      this.fdb = getWritableDatabase();
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("start_time", paramString1);
      localContentValues.put("appcode", paramString2);
      localContentValues.put("device_id", paramString3);
      localContentValues.put("video_list", paramString4);
      localContentValues.put("end_time", paramString5);
      this.fdb.insert("VideoList", null, localContentValues);
      this.fdb.close();
      return;
    }
    catch (Exception localException)
    {
      Log.d("TableError", localException.toString());
    }
  }
  
  public void UnFavour(String paramString)
  {
    this.fdb = getWritableDatabase();
    String str = "DELETE FROM VideoList WHERE _id=" + paramString;
    this.fdb.execSQL(str);
    this.fdb.close();
  }
  
  public List<VideoData> getAllList()
  {
    ArrayList localArrayList = new ArrayList();
    Cursor localCursor = getWritableDatabase().rawQuery("SELECT  * FROM VideoList", null);
    if (localCursor.moveToFirst()) {
      do
      {
        VideoData localVideoData = new VideoData();
        localVideoData.setStartTime(localCursor.getString(1));
        localVideoData.setAppcode(localCursor.getString(2));
        localVideoData.setDeviceId(localCursor.getString(3));
        localVideoData.setVideoLists(localCursor.getString(4));
        localVideoData.setEndTime(localCursor.getString(5));
        localArrayList.add(localVideoData);
      } while (localCursor.moveToNext());
    }
    return localArrayList;
  }
  
  public Cursor getDataList(String paramString)
  {
    return getReadableDatabase().rawQuery("SELECT * FROM VideoList WHERE _id=?", new String[] { paramString });
  }
  
  public List<VideoData> getLastRecord()
  {
    ArrayList localArrayList = new ArrayList();
    Cursor localCursor = getReadableDatabase().rawQuery("SELECT  * FROM VideoList", null);
    if (localCursor.moveToLast()) {
      do
      {
        VideoData localVideoData = new VideoData();
        localVideoData.setStartTime(localCursor.getString(1));
        localVideoData.setAppcode(localCursor.getString(2));
        localVideoData.setDeviceId(localCursor.getString(3));
        localVideoData.setVideoLists(localCursor.getString(4));
        localVideoData.setEndTime(localCursor.getString(5));
        localArrayList.add(localVideoData);
      } while (localCursor.isAfterLast());
    }
    return localArrayList;
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS VideoList(_id INTEGER PRIMARY KEY AUTOINCREMENT,start_time TEXT,appcode TEXT,device_id TEXT,video_list TEXT,end_time TEXT)");
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  
  public void updateRecord(String paramString, VideoData paramVideoData)
  {
    this.fdb = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("appcode", paramVideoData.getVideoPath());
    this.fdb.update("VideoList", localContentValues, "appcode= '" + paramString + "'", null);
    this.fdb.close();
  }
}
