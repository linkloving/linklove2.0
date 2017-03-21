package com.linkloving.rtring_new.logic.UI.HeartRate.MonthView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


import com.linkloving.rtring_new.utils.Viewutils.ScreenUtils;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义组件：条形统计图
 */
public class BarChartView extends View {
    public static final String TAG = BarChartView.class.getSimpleName();
    private int screenW, screenH;
    private List<BarChartItemBean> mItems = new ArrayList<BarChartItemBean>();
    //第一个是深色,第二个是total色,第三是画横线
    private int[] mBarColors = new int[]{Color.rgb(26, 43, 95), Color.rgb(38, 174, 222), Color.rgb(217, 173, 18)};
    private Paint barPaint, linePaint, textPaint;
    private Rect barRect;
    private int lineStrokeWidth;
    private double barItemWidth, barSpace, oneHourHight;
    private Rect deepSleepRect;
    private Paint deepSleepPaint;
    private SimpleDateFormat simpleDateFormat;
    private final static long ONEDAYMILLISECOND = 86400 ;

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        screenW = ScreenUtils.getScreenW(context);
        screenH = ScreenUtils.getScreenH(context);
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        barPaint = new Paint();
        deepSleepPaint = new Paint();
        barPaint.setColor(mBarColors[0]);
        linePaint = new Paint();
        lineStrokeWidth = ScreenUtils.dp2px(context, 1);
        linePaint.setStrokeWidth(lineStrokeWidth);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        barRect = new Rect(0, 0, 0, 0);
        deepSleepRect = new Rect(0, 0, 0, 0);
        barItemWidth = screenW * 0.09;
        barSpace = screenW * 0.035;
//        设置一个一小时的高度
        oneHourHight = screenH * 0.017;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(26, 43, 95));
//        画0-200 线
        linePaint.setColor(mBarColors[2]);
        for (int i = 0 ; i<11;i++){
            canvas.drawLine( (float) (screenW * 0.1), (float) (oneHourHight * (4+6*i)), (float) (screenW ), (float) (oneHourHight * (4+6*i)), linePaint);
            MyLog.e(TAG,"2468横线"+(float) (oneHourHight * (4+6*i)));
//            画竖的小线.
            if(i%2!=0){
                canvas.drawLine((float) (screenW * (0.2 + i * 0.072)), (float) (oneHourHight * 28),(float) (screenW * (0.2 + i * 0.072)),(float) (oneHourHight * 27.5) , linePaint);
            }else{
                canvas.drawLine((float) (screenW * (0.2 + i * 0.072)), (float) (oneHourHight * 28),(float) (screenW * (0.2 + i * 0.072)),(float) (oneHourHight * 27) , linePaint);
            }
        }
        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 16));
        /** 先调用一下下面这方法将开始时间装换成相应的百分比*/
        transformToPoint();
        for (int i = 0; i < mItems.size(); i++) {
            if (i<mItems.size()-1){
                    canvas.drawLine( (float) (screenW * (0.2 + 0.023+i * 0.023)),
                            (float)(oneHourHight*28- (mItems.get(i).itemDeepValue*1000/200*oneHourHight * 24)/1000),
                            (float) (screenW * (0.2 + (i+2) * 0.023)),
//                            (float) ((mItems.get(i+1).starttime *1000/288)*(screenW * 0.72)/1000+0.2*screenW),
                            (float)(oneHourHight*28-(mItems.get(i+1).itemDeepValue*1000/200 *oneHourHight * 24)/1000),
                            linePaint);
            }
        }

//        画 2 4 6 8 10H 文字
        String[] twoHour = {"200", "150", "100", "50", "0"};
        textPaint.setColor(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            String typeText = twoHour[i];
            float texttypeStartx = (float) 0.02*screenW;
            float texttypeStarty = (float) (oneHourHight * (4.5+6*i));
            canvas.drawText(typeText, texttypeStartx, texttypeStarty, textPaint);
        }

        String[] week = {"1", "4", "7", "10","13", "16", "19", "22","25", "28", "31"};
        for (int i = 0; i < 11; i++) {
            String typeText = week[i];
            float texttypeStartx = (float) (screenW * (0.18 + i * 0.072));
            float texttypeStarty = (float) (oneHourHight * 31);
            MyLog.e(TAG,texttypeStartx+"------"+texttypeStarty);
            canvas.drawText(typeText, texttypeStartx, texttypeStarty, textPaint);
        }
    }

    /**
     * 将时间转化成点
     */
    public  void  transformToPoint(){
        MyLog.e(TAG,"transform执行了");
        for (BarChartItemBean record :mItems) {
            long l = record.starttime % ONEDAYMILLISECOND;
             float per= (float)(l+28800) / ONEDAYMILLISECOND;
            MyLog.e(TAG,"per是.........."+per);
            record.starttime=l ;
        }
    }

    public List<BarChartItemBean> getItems() {
        return mItems;
    }

    public void setItems(List<BarChartItemBean> items) {

        this.mItems = items;
        invalidate();
    }

    /**
     * A model class to keep the bar item info.
     */
    public static class BarChartItemBean {
        public long starttime;
        public float itemDeepValue;
        public float itemLightValue;


        public BarChartItemBean(long time, float maxHeartrate, float minHeartrate) {
            this.starttime = time;
            this.itemDeepValue = maxHeartrate;
            this.itemLightValue = minHeartrate;
        }
    }
}