package com.fifthfloor.media;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class GIFView
  extends View
{
  private int gifId;
  public Movie mMovie;
  public long movieStart;
  
  public GIFView(Context paramContext)
  {
    super(paramContext);
    initializeView();
  }
  
  public GIFView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initializeView();
  }
  
  public GIFView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initializeView();
  }
  
  private void initializeView()
  {
    this.mMovie = Movie.decodeStream(getContext().getResources().openRawResource(R.raw.splash));
  }
  
  public int getGIFResource()
  {
    return this.gifId;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawColor(0);
    super.onDraw(paramCanvas);
    long l = SystemClock.uptimeMillis();
    if (this.movieStart == 0L) {
      this.movieStart = l;
    }
    if (this.mMovie != null)
    {
      int i = (int)((l - this.movieStart) % this.mMovie.duration());
      this.mMovie.setTime(i);
      this.mMovie.draw(paramCanvas, getWidth() - this.mMovie.width(), getHeight() - this.mMovie.height());
      invalidate();
    }
  }
  
  public void setGIFResource(int paramInt)
  {
    this.gifId = paramInt;
    initializeView();
  }
}