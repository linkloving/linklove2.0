package com.linkloving.rtring_new.logic.UI.HeartRate;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.logic.UI.HeartRate.MonthView.BarChartView;
import com.linkloving.rtring_new.logic.UI.HeartRate.MonthView.DetailChartControl;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.ToolKits;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Trace.GreenDao.heartrate;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/10/19.
 */

public class MonthFragment extends Fragment {
    private final static String TAG = MonthFragment.class.getSimpleName();
    @InjectView(R.id.Heartrate_day_barchartview)
    BarChartView HeartrateDayBarchartview;
    @InjectView(R.id.activity_detailChartView1)
    DetailChartControl activityDetailChartView1;
    private GreendaoUtils greendaoUtils;
    private final static long ONEDAYMILLIONS = 86400000;
    private ArrayList<BarChartView.BarChartItemBean> beanArrayList;

    private RestingBpm restingBpm ;
    private Date getfirstDayOfMonth;
    private SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
    private Date parse;

    public  void setRestingBpmListener(RestingBpm restingBpm){
        this.restingBpm = restingBpm ;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_heartrate_month, container, false);
        ButterKnife.inject(this, view);
        String monthDate = getArguments().getString("monthDate");
        String[] split = monthDate.split("-");
        String year = split[0];
        String month = split[1];
        String firstDayOfMonth = CommonUtils.getFirstDayOfMonth(Integer.parseInt(year), Integer.parseInt(month));
        try {
            parse = sim.parse(firstDayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        greendaoUtils = new GreendaoUtils(getActivity());
        beanArrayList = getOneMonthRecord();
        HeartrateDayBarchartview.setItems(beanArrayList);
        activityDetailChartView1.initDayIndex(getfirstDayOfMonth);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private ArrayList<BarChartView.BarChartItemBean> getOneMonthRecord() {
        beanArrayList = new ArrayList<>();

        int monthMount = ToolKits.getMonthMount(parse);
      /**每月的天数*/
        getfirstDayOfMonth = ToolKits.getFirstofMonth(parse);
        long time = getfirstDayOfMonth.getTime();
        int restmonth = 0 ;
        int avgmonth = 0 ;
        int totalAvg = 0 ;
        int times = 0 ;
        for (int i = 0; i < monthMount; i++) {
            long Daystarttime =time +ONEDAYMILLIONS*i;
            long timeEnd = Daystarttime + ONEDAYMILLIONS;
            List<heartrate> heartrates =  greendaoUtils.searchOneDay(Daystarttime, timeEnd);
            int onedayAvg = 0;
            int onedayMax = 0;
            for (heartrate record : heartrates) {
                Integer avg = record.getAvg();
                Integer max = record.getMax();
                onedayAvg = avg + onedayAvg;
                onedayMax = max + onedayMax;
                totalAvg = record.getAvg()+totalAvg ;
                times = times++ ;
            }
            if (heartrates.size() == 0) {
                onedayAvg = 0;
                onedayMax = 0;
            } else {
                onedayAvg = onedayAvg / heartrates.size();
                onedayMax = onedayMax / heartrates.size();
            }
            restmonth = onedayMax+restmonth ;
            avgmonth = onedayAvg+avgmonth ;
            BarChartView.BarChartItemBean barChartItemBean = new BarChartView.BarChartItemBean(0, onedayMax, onedayAvg);
            beanArrayList.add(barChartItemBean);
        }
           int  resting = restmonth/monthMount ;
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
