package com.fifthfloor.media;

public class VideoData
{
  public String Appcode;
  public String DeviceId;
  public String EndTime;
  public String Imageurl;
  public String StartTime;
  public String VideoLists;
  public String image_duration;
  public String videoPath;
  public String videoUrl;
  public String videoid;
  public String breakvideoUrl;
  public String breakvideoduration;

  public VideoData() {}

  public VideoData(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    this.StartTime = paramString1;
    this.Appcode = paramString2;
    this.DeviceId = paramString3;
    this.VideoLists = paramString4;
    this.EndTime = paramString5;
  }

  public String getAppcode()
  {
    return this.Appcode;
  }

  public String getDeviceId()
  {
    return this.DeviceId;
  }

  public String getEndTime()
  {
    return this.EndTime;
  }

  public String getImage_duration()
  {
    return this.image_duration;
  }

  public String getImageurl()
  {
    return this.Imageurl;
  }

  public String getStartTime()
  {
    return this.StartTime;
  }

  public String getVideoLists()
  {
    return this.VideoLists;
  }

  public String getVideoPath()
  {
    return this.videoPath;
  }

  public String getvideoUrl()
  {
    return this.videoUrl;
  }

  public String getvideoid()
  {
    return this.videoid;
  }

  public void setAppcode(String paramString)
  {
    this.Appcode = paramString;
  }

  public void setDeviceId(String paramString)
  {
    this.DeviceId = paramString;
  }

  public void setEndTime(String paramString)
  {
    this.EndTime = paramString;
  }

  public void setImage_duration(String paramString)
  {
    this.image_duration = paramString;
  }

  public void setImageurl(String paramString)
  {
    this.Imageurl = paramString;
  }

  public void setStartTime(String paramString)
  {
    this.StartTime = paramString;
  }

  public void setVideoLists(String paramString)
  {
    this.VideoLists = paramString;
  }

  public void setVideoPath(String paramString)
  {
    this.videoPath = paramString;
  }

  public void setvideoUrl(String paramString) {}

  public void setvideoid(String paramString) {}


  public String getBreakvideoUrl() {
    return breakvideoUrl;
  }

  public void setBreakvideoUrl(String breakvideoUrl) {
    this.breakvideoUrl = breakvideoUrl;
  }

  public String getBreakvideoduration() {
    return breakvideoduration;
  }

  public void setBreakvideoduration(String breakvideoduration) {
    this.breakvideoduration = breakvideoduration;
  }

}

