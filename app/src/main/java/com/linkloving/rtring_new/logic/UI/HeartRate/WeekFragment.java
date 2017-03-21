package com.linkloving.rtring_new.logic.UI.HeartRate;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.logic.UI.HeartRate.WeekView.BarChartView;
import com.linkloving.rtring_new.logic.UI.HeartRate.WeekView.DetailChartControl;
import com.linkloving.rtring_new.utils.ToolKits;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Trace.GreenDao.heartrate;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/10/19.
 */

public class WeekFragment extends Fragment {
    private final static String TAG = WeekFragment.class.getSimpleName();
    @InjectView(R.id.Heartrate_day_barchartview)
    BarChartView HeartrateDayBarchartview;
    @InjectView(R.id.activity_detailChartView1)
    DetailChartControl activityDetailChartView1;
    private GreendaoUtils greendaoUtils;
    private final static long ONEDAYMILLIONS = 86400000;
    private ArrayList<BarChartView.BarChartItemBean> beanArrayList;
    private RestingBpm restingBpm ;
    private Date firstSundayOfThisWeek;
    Date mondayOfThisWeek =null;
    Date sundayofThisWeek=null ;
    private Date time;

    public  void setRestingBpmListener(RestingBpm restingBpm){
        this.restingBpm = restingBpm ;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_heartrate_week, container, false);
        ButterKnife.inject(this, view);
        String date = getArguments().getString("date");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//            SimpleDateFormat sdfWithSlashYear = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
//            SimpleDateFormat sdfWithSlashNoYear = new SimpleDateFormat("MM/dd", Locale.getDefault());
//            取出来的天数加7天
        try {
            Date parse = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            time = calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        greendaoUtils = new GreendaoUtils(getActivity());
        beanArrayList = getOneWeekRecord();
        HeartrateDayBarchartview.setItems(beanArrayList);
        activityDetailChartView1.initDayIndex(firstSundayOfThisWeek);
        return view;
    }

    private ArrayList<BarChartView.BarChartItemBean> getOneWeekRecord() {
        beanArrayList = new ArrayList<>();
        /**先取出每周的开始时间,for7次*/
        firstSundayOfThisWeek = ToolKits.getFirstSundayOfThisWeek(time);
        long time = firstSundayOfThisWeek.getTime();
        int restmonth = 0 ;
        int avgmonth = 0 ;
        int totalAvg = 0 ;
        int times = 0 ;
        for (int i = 0; i < 7; i++) {
            long Daystarttime =time +ONEDAYMILLIONS*i;
        long timeEnd = Daystarttime + ONEDAYMILLIONS;
        List<heartrate> heartrates = greendaoUtils.searchOneDay(Daystarttime, timeEnd);
        int onedayAvg = 0;
        int onedayMax = 0;
        for (heartrate record : heartrates) {
            Integer avg = record.getAvg();
            Integer max = record.getMax();
            totalAvg = record.getAvg()+totalAvg ;
            times = times++ ;
            onedayAvg = avg + onedayAvg;
            onedayMax = max + onedayMax;
        }
        if (heartrates.size() == 0) {
            onedayAvg = 0;
            onedayMax = 0;
        } else {
            onedayAvg = onedayAvg / heartrates.size();
            onedayMax = onedayMax / heartrates.size();
        }
        BarChartView.BarChartItemBean barChartItemBean = new BarChartView.BarChartItemBean(0, onedayMax, onedayAvg);
        beanArrayList.add(barChartItemBean);
            restmonth = onedayMax+restmonth ;
            avgmonth = onedayAvg+avgmonth ;
    }
        int  resting = restmonth/7 ;
        int avging = 0 ;
        if (times !=0){
            avging = totalAvg/times ;
        }
        HeartRateActivity activity = (HeartRateActivity) getActivity();
        activity.setAvgText( avging) ;
//        restingBpm.setAvg(avging);
        return beanArrayList ;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
