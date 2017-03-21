package com.linkloving.rtring_new.logic.UI.HeartRate;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.ToolKits;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/10/15.
 */

public class HeartRateActivity extends ToolBarActivity implements View.OnClickListener{
    private static final String TAG = HeartRateActivity.class.getSimpleName();
    @InjectView(R.id.groups_time)
    TextView groupsTime;
    @InjectView(R.id.Heartrate_day)
    RadioButton HeartrateDay;
    @InjectView(R.id.Heartrate_week)
    RadioButton HeartrateWeek;
    @InjectView(R.id.Heartrate_month)
    RadioButton HeartrateMonth;
    @InjectView(R.id.tw_Heartrate_buttom)
    RadioGroup twHeartrateButtom;
    @InjectView(R.id.resting)
    TextView restingText;
    @InjectView(R.id.avgerage)
    TextView avgerageText;
    @InjectView(R.id.middle_framelayout)
    FrameLayout middleFramelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_heartrate);
        ButterKnife.inject(this);
        HeartrateDay.setOnClickListener(this);
        HeartrateMonth.setOnClickListener(this);
        HeartrateWeek.setOnClickListener(this);
        initDay();
        UserEntity localUserInfoProvider = MyApplication.getInstance(HeartRateActivity.this).getLocalUserInfoProvider();
        String birthdate = localUserInfoProvider.getUserBase().getBirthdate();
        String[] split = birthdate.split("-");
        int i = Integer.parseInt(split[0]);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int resting = (int) ((220-(year-i))*0.4);
        restingText.setText(resting+"");
        HeartrateDay.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.d_on_72px, 0, 0);
        //中间的时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd a HH:mm",Locale.US);
        String format = simpleDateFormat.format(date);
        groupsTime.setText(format);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Heartrate_day:
                restartBotton();
                HeartrateDay.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.d_on_72px, 0, 0);
                initDay();
                break;
            case R.id.Heartrate_week:
                restartBotton();
                HeartrateWeek.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.w_on_72px, 0, 0);
                initWeek();
                break;
            case R.id.Heartrate_month:
                restartBotton();
                HeartrateMonth.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.m_on_72px, 0, 0);
                initMonth();
                break;
        }

    }

    private void initMonth() {
        MonthDatefragment monthDatefragment = new MonthDatefragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.middle_framelayout, monthDatefragment);
        fragmentTransaction.commit();
   /*     MonthFragment monthFragment = new MonthFragment();
        monthFragment.setRestingBpmListener(new RestingBpm() {
            @Override
            public void setAvg(int average) {
                avgerageText.setText(average+"");
            }
        });*/
        monthDatefragment.setDataChangeListener(new IDataChangeListener() {
            @Override
            public void onDataChange(String data) {
                groupsTime.setText(data);
            }
        });

    }

    private void initWeek() {
        WeekDatefragment weekDatefragment = new WeekDatefragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.middle_framelayout, weekDatefragment);
        fragmentTransaction.commit();
 /*       WeekFragment weekFragment = new WeekFragment();
    weekFragment.setRestingBpmListener(new RestingBpm() {
        @Override
        public void setAvg(int average) {
            avgerageText.setText(average+"");
        }
    });*/
        weekDatefragment.setDataChangeListener(new IDataChangeListener() {
            @Override
            public void onDataChange(String data) {
                Log.e(TAG, "dateStr-------" + data);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                Date mondayOfThisWeek = null;
                Date sundayofThisWeek = null;
                try {
                    mondayOfThisWeek= ToolKits.getFirstSundayOfThisWeek(sdf.parse(data));
                    sundayofThisWeek = ToolKits.getStaurdayofThisWeek(sdf.parse(data));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat simYearMonth = new SimpleDateFormat("MM/dd");
             String   startData = sdf.format(mondayOfThisWeek);
              String  endData = simYearMonth.format(sundayofThisWeek);
                groupsTime.setText(startData+" - "+endData);
            }
        });
    }

    private void initDay() {
        DayDatefragment dayDatefragment = new DayDatefragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.middle_framelayout, dayDatefragment);
        fragmentTransaction.commit();
        dayDatefragment.setDataChangeListener(new IDataChangeListener() {
            @Override
            public void onDataChange(String data) {
                groupsTime.setText(data);

            }
        });
      /*  DayFragment dayFragment = new DayFragment();
        dayFragment.setRestingBpmListener(new RestingBpm() {
            @Override
            public void setAvg(int average) {
                avgerageText.setText(average+"");
            }
        });*/

    }


    /**
     * 切换Fragment时候 重置按钮图片
     */
    private void restartBotton() {
        HeartrateDay.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.d_off_72px, 0, 0);
        HeartrateWeek.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.w_off_72px, 0, 0);
        HeartrateMonth.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.m_off_72px, 0, 0);
    }

    @Override
    protected void getIntentforActivity() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListeners() {
    }

    public void setAvgText(int avging) {
        avgerageText.setText(avging+"");
    }
}
