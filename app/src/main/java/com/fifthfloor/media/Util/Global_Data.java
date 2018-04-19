package com.fifthfloor.media.Util;

import java.util.ArrayList;
import java.util.List;

public class Global_Data
{
  public static List<String> Bannerurllist = new ArrayList();
  public static ArrayList<String> BreakVideourllist;
  public static String breakvideo_duration;
  public static Boolean Booted;
  public static String Device_Id;
  public static int Lengthof_breakvurl;
  public static int Lengthof_imageurl;
  public static int Lengthof_url = 0;
  public static String StartTime;
  public static ArrayList<String> VideoCountList;
  public static String app_code;
  public static boolean internet_check;
  private static final Global_Data ourInstance;
  public static ArrayList<String> videourllist;

  static
  {
    Booted = Boolean.valueOf(false);
    Lengthof_imageurl = 0;
    internet_check = false;
    VideoCountList = new ArrayList();
    ourInstance = new Global_Data();
    videourllist = new ArrayList();
    BreakVideourllist = new ArrayList();
  }

  /*public static String forceupdate_url = "http://5thfloor.media/appdev/api.video/n1/Video/get_5th_app_version";
  public static String GetVideo_url = "http://5thfloor.media/appdev/api.video/n1/Video/get_video_url?password=";
  public static String SendAppStatus_url = "http://5thfloor.media/appdev/api.video/n1/Util/video_5th_overview";
  public static String SendVideoCount_url = "http://5thfloor.media/appdev/api.video/n1/Util/video_5th_records";*/


  public static String forceupdate_url = "http://5thfloor.media/app/api.video/n1/Video/get_5th_app_version";
  public static String GetVideo_url = "http://5thfloor.media/app/api.video/n1/Video/get_video_url?password=";
  public static String SendAppStatus_url = "http://5thfloor.media/app/api.video/n1/Util/video_5th_overview";
  public static String SendVideoCount_url = "http://5thfloor.media/app/api.video/n1/Util/video_5th_records";

}
