package com.linkloving.rtring_new.logic.UI.main.datachatactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.summary.DaySynopicTable;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.DateSwitcher;
import com.linkloving.rtring_new.utils.ScreenHotAsyncTask;
import com.linkloving.rtring_new.utils.SwitchUnit;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.UnitTookits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.manager.AsyncTaskManger;
import com.linkloving.rtring_new.utils.sportUtils.Datahelper;
import com.linkloving.rtring_new.utils.sportUtils.SportDataHelper;
import com.linkloving.utils.TimeZoneHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistanceDataActivity extends ToolBarActivity implements View.OnClickListener {
    private static final String TAG = DistanceDataActivity.class.getSimpleName();
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> dayDataAsync = null;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> weekDataAsync = null;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> monthDataAsync = null;
    private CombinedChart combbarChart;
    private UserEntity userEntity;
    private RadioButton dayButton,weekButton,monthButton,day1,day2,day3;
    private ArrayList<BarEntry> mentries;
    private ArrayList<DaySynopic> listData;
    Map<Integer,ArrayList<SportRecord>> data;//日数据查询结果
    Map<Integer,ArrayList<SportRecord>> timenowdata;//用来保存用户所选时间的数据,避免多次查询数据
    String timeNow;
    private Map<Integer,View> listView;//存放viewpager的view
    Date date;//刚刚进入页面的时间
    String startDateString;
    String endDateString;
    private ViewPager mViewPager;
    MyPageadapter dayadapter;
    MyPageadapter weekadapter;
    MyPageadapter monthadapter;
    private DateSwitcher weekSwitcher = null;
    private DateSwitcher monthSwitcher= null;
    public static final int WALKING = 1;
    public static final int RUNNING = 2;
    public final static int ACTIVE = 3;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_data);

        weekSwitcher = new DateSwitcher(DateSwitcher.PeriodSwitchType.week);
        monthSwitcher= new DateSwitcher(DateSwitcher.PeriodSwitchType.month);
        pd=new ProgressDialog(this);
        pd.setMessage(getString(R.string.summarizing_data));
        pd.setCanceledOnTouchOutside(false);
        listView=new HashMap<>();
        initTitle();//刚刚进页面的时候初始化那三个字段
    }
    @Override
    protected void getIntentforActivity() {
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        timeNow=getIntent().getStringExtra("time");
        date= ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
        MyLog.i(TAG,"开始时间======="+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
        timenowdata=data=new Datahelper(DistanceDataActivity.this, String.valueOf(userEntity.getUser_id()), timeNow,
                new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date),false).getMydata();
    }
    private void initchat(CombinedChart barChart) {
        //设置图表的一些属性
        barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER});
        Legend mLegend = barChart.getLegend();
        // 设置窗体样式
        mLegend.setForm(Legend.LegendForm.CIRCLE);
        mLegend.setEnabled(false);
        // 字体颜色
        mLegend.setTextColor(getResources().getColor(R.color.white));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //zhang 修改
//        xAxis.setGridColor(Color.WHITE);
//        xAxis.enableGridDashedLine(5f,5f,5f);
        //x坐标的颜色
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setLabelsToSkip(0);
        //画x坐标
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(0);
        //X坐标点描述的颜色
        xAxis.setTextColor(Color.WHITE);
        // 隐藏左Y坐标轴
        barChart.getAxisLeft().setEnabled(false);
        // 隐藏右Y坐标轴
        barChart.getAxisRight().setEnabled(false);
        // 显示表格颜色
        barChart.setGridBackgroundColor(getResources().getColor(R.color.yellow_title));
        // 打开或关闭绘制的图表边框。（环绕图表的线） 最外边环绕的线
        barChart.setDrawBorders(false);
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        // 是否可以拖拽
        barChart.setDragEnabled(false);
        // 是否可以缩放
        barChart.setScaleEnabled(false);
        // 集双指缩放
        barChart.setPinchZoom(false);
        //设置Y方向上动画animateY(int time);
        barChart.animateY(3000);
        //图表描述
        barChart.setDescription("");
        //去除了中间的字体
        barChart.setNoDataText(" ");
        //没有数据显示
        barChart.setNoDataTextDescription("No data(⊙o⊙)");
    }
    @Override
    protected void initView() {
        HideButtonRight(false);
        SetBarTitleText(getString(R.string.unit_mileage));

        Button btn = getRightButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行分享操作
                if (ToolKits.isNetworkConnected(DistanceDataActivity.this)) {
                    String filePath = "/sdcard/DistanceDataActivity.png";
                    new ScreenHotAsyncTask(filePath, DistanceDataActivity.this).execute(getWindow().getDecorView());
                } else {
                    new AlertDialog.Builder(DistanceDataActivity.this)
                            .setTitle(ToolKits.getStringbyId(DistanceDataActivity.this, R.string.share_content_fail))
                            .setMessage(ToolKits.getStringbyId(DistanceDataActivity.this, R.string.main_more_sycn_fail))
                            .setPositiveButton(R.string.general_ok, null).show();
                }


            }
        });
        btn.setBackground(ContextCompat.getDrawable(DistanceDataActivity.this, R.mipmap.btn_share));
        combbarChart =(CombinedChart )findViewById(R.id.barChart);
        dayButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_dayRb);
        weekButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_weekRb);
        monthButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_monthRb);
        day1=(RadioButton) findViewById(R.id.day01);
        day2=(RadioButton) findViewById(R.id.day02);
        day3=(RadioButton) findViewById(R.id.day03);
        mViewPager= (ViewPager) findViewById(R.id.my_ViewPager);
        initchat(combbarChart);
    }

    private void initTitle(){
        if(dayButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0), new Date());
            dayadapter=new MyPageadapter(days);
            mViewPager.setAdapter(dayadapter);
            int day=ToolKits.getBetweenDay(date, new Date());
            MyLog.i(TAG, "days=" + days + "  " + day);
            mViewPager.setCurrentItem(days-day-1);
        }

        //周数据
        else if(weekButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            }
            //计算传进来时间与1970/01/01差几周
            int day=ToolKits.getBetweenDay(new Date(0),date);
            int weeknow=day/7;//系统时间1970/01/01与总的周数
            int week2=day%7;
            if(week2>3){
                weeknow=weeknow+1;
            }

            weekadapter=new MyPageadapter(weekcount);
            mViewPager.setAdapter(weekadapter);
            MyLog.i(TAG,"相差几周="+(weekcount-weeknow));
            mViewPager.setCurrentItem(weeknow-1);
        }
        //月数据
        else if(monthButton.isChecked()){
            java.text.SimpleDateFormat sim1 = new java.text.SimpleDateFormat("MM");
            java.text.SimpleDateFormat sim2 = new java.text.SimpleDateFormat("yyyy-MM");
            java.text.SimpleDateFormat sim3 = new java.text.SimpleDateFormat("yyyy");
            int year=Integer.parseInt(sim3.format(new Date()));
            int nowyear=Integer.parseInt(sim3.format(date));
            int s1=Integer.parseInt( sim1.format(date));//传进来时间的月份
            int s2=Integer.parseInt(sim1.format(new Date()));//当前时间的月份
            int count=(year-1970)*12+s2+1;
            monthadapter=new MyPageadapter(count);
            mViewPager.setAdapter(monthadapter);
            mViewPager.setCurrentItem((nowyear-1970)*12+s1);//计算出当前时间应该是第几个月
        }
    }
    public class MyPageadapter extends PagerAdapter{
        int mcount;

        public void setMcount(int mcount) {
            this.mcount = mcount;
        }

        public MyPageadapter(int count){
            mcount=count;
        }

        @Override
        public int getCount() {
            return mcount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        //销毁Item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(listView.get(position));
        }
        //实例化Item
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //根据position区添加新的view
            View view=getView(position);
            view.setId(position);
            listView.put(position,view);
            container.addView(view);
            return view;
        }
        }

    private View getView(int position) {
        if(dayButton.isChecked()){
            LayoutInflater inflater=getLayoutInflater();
            View view = inflater.inflate(R.layout.diatance_data_view, null);
            return view;
        }else {
            //星期和月
            //选择了周和月
            LayoutInflater inflater=getLayoutInflater();
            View view = inflater.inflate(R.layout.step_data_view, null);
            return view;
        }
    }
    //查询到数据之后再来给viewpager的各个view赋值
    private void setDataToView(int position,List<DaySynopic> daySynopicList){
        if(dayButton.isChecked()){
            View view=mViewPager.findViewById(position);
            RelativeLayout shuju= (RelativeLayout) view.findViewById(R.id.shuju);//有数据的时候显示
            LinearLayout nulldata= (LinearLayout) view.findViewById(R.id.null_data_LL);//没数据的时候显示

            TextView data= (TextView) view.findViewById(R.id.data);
            TextView title= (TextView) view.findViewById(R.id.title);

            if(SwitchUnit.getLocalUnit(DistanceDataActivity.this)==ToolKits.UNIT_GONG){
                title.setText(getString(R.string.unit_mileage)+"("+getString(R.string.unit_m)+")");
            }else{
                title.setText(getString(R.string.unit_mileage)+"("+getString(R.string.unit_mile)+")");
            }
            if(daySynopicList==null || daySynopicList.size()<=0) {
                shuju.setVisibility(View.GONE);
                nulldata.setVisibility(View.VISIBLE);
            }else {
            int walkDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopicList.get(0).getWork_distance()), 0));
            //跑步 里程
            int runDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopicList.get(0).getRun_distance()), 0));
            int distance=walkDistance+runDistance;
                if(distance==0){
                    shuju.setVisibility(View.GONE);
                    nulldata.setVisibility(View.VISIBLE);
                }else {
                    shuju.setVisibility(View.VISIBLE);
                    nulldata.setVisibility(View.GONE);

                    if(SwitchUnit.getLocalUnit(DistanceDataActivity.this)==ToolKits.UNIT_GONG){
                        data.setText(distance+"");
                    }else{
                       // text_Distance.setText(UnitTookits.MChangetoMIRate(distance) + getResources().getString(R.string.unit_mile));
                        data.setText(UnitTookits.MChangetoMIRate(distance)+"");
                    }
                }
            }
        }
        else {
            View view=mViewPager.findViewById(position);

            LinearLayout shuju= (LinearLayout) view.findViewById(R.id.shuju);//有数据的时候显示
            LinearLayout nulldata= (LinearLayout) view.findViewById(R.id.null_data_LL);//没数据的时候显示

            TextView titleview= (TextView) view.findViewById(R.id.data_count_title);
            titleview.setText(getString(R.string.distance_count));//总里程
            TextView data= (TextView) view.findViewById(R.id.step_count);
            ListView listviewstep= (ListView) view.findViewById(R.id.listview_step);

            if(daySynopicList!=null&&daySynopicList.size() >0){
                    int count=0;
                for(DaySynopic mDaySynopic:daySynopicList) {
                    int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
                    //跑步 里程
                    int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
                    int distance = walkDistance + runDistance;
                    count = count + distance;
                }
                MyLog.e(TAG, "计算后的步数:" + count);
                if(count==0){
                    shuju.setVisibility(View.GONE);
                    nulldata.setVisibility(View.VISIBLE);
                }else {
                    shuju.setVisibility(View.VISIBLE);
                    nulldata.setVisibility(View.GONE);

                    if(SwitchUnit.getLocalUnit(DistanceDataActivity.this)==ToolKits.UNIT_GONG){
                        data.setText(count+"("+getString(R.string.unit_m)+")");
                    }else{
                        data.setText(UnitTookits.MChangetoMIRate(count)+"("+getResources().getString(R.string.unit_mile)+")");
                    }

                    MyDaitanceAdapter adapter=new MyDaitanceAdapter(DistanceDataActivity.this, (ArrayList<DaySynopic>) daySynopicList);
                    listviewstep.setAdapter(adapter);
                }
            }else {
                shuju.setVisibility(View.GONE);
                nulldata.setVisibility(View.VISIBLE);
            }
        }
    }
    private CombinedData generateDayBarData() {
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        //横坐标标签
        ArrayList<String> xVals = new ArrayList<String>();
        //查看天时
        for(int i = 0;i < 48 ;i++){
            switch (i){
                case 0:
                    xVals.add("12am");
                    break;
                case 12:
                    xVals.add("6am");
                    break;
                case 24:
                    xVals.add("12pm");
                    break;
                case 36:
                    xVals.add("6pm");
                    break;
                case 47:
                    xVals.add("12pm");
                    break;
                default: xVals.add("");
                    break;
            }
        }
        CombinedData data = new CombinedData(xVals);
        if(getDayBarData()!=null)
            data.setData(getDayBarData());
        return data;
    }
    private CombinedData generateWeekBarData() {
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        String s1=getString(R.string.sun);
        String s2=getString(R.string.mon);
        String s3=getString(R.string.tues);
        String s4=getString(R.string.wed);
        String s5=getString(R.string.thurs);
        String s6=getString(R.string.fri);
        String s7=getString(R.string.sat);
        //横坐标标签
        ArrayList<String> xVals = new ArrayList<String>();
        //查看天时
        for(int i = 0;i < 7 ;i++){
            switch (i){
                case 0:
                    xVals.add(s2);
                    break;
                case 1:
                    xVals.add(s3);
                    break;
                case 2:
                    xVals.add(s4);
                    break;
                case 3:
                    xVals.add(s5);
                    break;
                case 4:
                    xVals.add(s6);
                    break;
                case 5:
                    xVals.add(s7);
                    break;
                default: xVals.add(s1);
                    break;
            }
        }
        CombinedData data = new CombinedData(xVals);
        //data.setData(generateLineData());
        data.setData(getWeekBarData());
        return data;
    }
    //获取日数据
        private BarData getDayBarData() {
        if(data==null){
           return null;
        }
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        //一天有48条数据
        //查找当天的数据,然后判断最后一条取出来的最后时间,和最一开始取出来的时间
        for(int i=0;i<48;i++){
            entries.add(new BarEntry(getdaistace(data.get(i)),i));
        }
        mentries=entries;
        addline(R.id.report_page_activity_circleviews_dayRb);
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
            dataSet.setColor(getResources().getColor(R.color.yellow_title));
        // 柱形图顶端字是否显示
        dataSet.setDrawValues(false);
        // 柱形图顶端字体颜色
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        //  柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        bardata.addDataSet(dataSet);
            dataSet.setHighlightEnabled(false);//设置成t触摸的时候回变成灰色
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }
    private float getdaistace(ArrayList<SportRecord> arrayList ) {
        double i = 0;
        for (SportRecord s : arrayList) {
            MyLog.i("转换后的数据",s.toString());
            switch (Integer.parseInt(s.getState())) {
                case WALKING:
                    //运动数据里加了一条数据
                    i = i + Double.parseDouble(s.getDistance());
                    break;
                case RUNNING:
                    i = i + Double.parseDouble(s.getDistance());
                    break;
                case ACTIVE:
                    i = i +Double.parseDouble(s.getDistance());
                    break;
            }

        }
        MyLog.i("转换后的数据",">>>>>>>>>>>>>>>>>>>"+i);
        return (float) i;
    }
    //获取周数据
    private BarData getWeekBarData() {
        if(listData==null||listData.size()<=0){
            return new BarData();
        }
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        //一周7条数据
        for(int i=0;i<listData.size();i++){
            //float profit= random.nextFloat()*1000;
            MyLog.i(TAG, listData.get(i).toString());
            Calendar c = Calendar.getInstance();
            Date date=ToolKits.stringToDate(listData.get(i).getData_date(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
            c.setTime(date);

            //走路 里程
            int walkDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(listData.get(i).getWork_distance()), 0));
            //跑步 里程
            int runDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(listData.get(i).getRun_distance()), 0));
            int distance=walkDistance+runDistance;


            if((c.get(Calendar.DAY_OF_WEEK) - 1)==0){

                entries.add(new BarEntry(distance,6));
            }else {
                entries.add(new BarEntry(distance,c.get(c.DAY_OF_WEEK)-2));
            }

        }
        mentries=entries;
        addline(R.id.report_page_activity_circleviews_weekRb);
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
        dataSet.setColor(getResources().getColor(R.color.yellow_title));
        // 柱形图顶端字是否显示f
        dataSet.setDrawValues(false);
        // 柱形图顶端字体颜色
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        //  柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        bardata.addDataSet(dataSet);
        dataSet.setBarSpacePercent(60);
        dataSet.setHighlightEnabled(false);//设置成t触摸的时候回变成灰色
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }
    //获取月数据
    private BarData getMonthBarData() {
        if(listData==null||listData.size()<=0){
            return new BarData();
        }
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        //一周7条数据
        //   MyLog.i(TAG,"获得数据" +listData.size());
        for(int i=0;i<listData.size();i++){
            // float profit= random.nextFloat()*1000;
            // MyLog.i(TAG, listData.get(i).toString());
            Calendar c = Calendar.getInstance();
            Date date=ToolKits.stringToDate(listData.get(i).getData_date(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
            c.setTime(date);
            java.text.SimpleDateFormat sim1 = new java.text.SimpleDateFormat("dd");
            String date1 = sim1.format(c.getTime());
            int s=Integer.parseInt(date1);
            //走路 里程
            int walkDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(listData.get(i).getWork_distance()), 0));
            //跑步 里程
            int runDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(listData.get(i).getRun_distance()), 0));
            int distance=walkDistance+runDistance;
            entries.add(new BarEntry(distance,s-1));
        }
        mentries=entries;
        addline(R.id.report_page_activity_circleviews_weekRb);
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
        dataSet.setColor(getResources().getColor(R.color.yellow_title));
        // 柱形图顶端字是否显示
        dataSet.setDrawValues(false);
        // 柱形图顶端字体颜色
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        //  柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        bardata.addDataSet(dataSet);
        dataSet.setHighlightEnabled(false);//设置成t触摸的时候回变成灰色
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }

    private CombinedData generateMonthBarData(String timeNow) {
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        //横坐标标签
        Calendar c = Calendar.getInstance();
        Date date=ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
        c.setTime(date);
        int j=c.getActualMaximum(Calendar.DAY_OF_MONTH);
        MyLog.i(TAG, "这个月多少天" + j);
        ArrayList<String> xVals = new ArrayList<String>();
        //查看天时
        for(int i = 1;i<=j ;i++){
            switch (i){
                case 1:
                    xVals.add(i-1,i+"");
                    break;
                case 5:
                    xVals.add(i - 1, i+"");
                    break;
                case 10:
                    xVals.add(i-1,i+"");
                    break;
                case 15:
                    xVals.add(i-1,i+"");
                    break;
                case 20:
                    xVals.add(i-1,i+"");
                    break;
                case 25:
                    xVals.add(i-1,i+"");
                    break;
                case 30:
                    xVals.add(i-1,i+"");
                    break;
                default:
                    xVals.add(i-1,"");
                    break;
            }

        }
        CombinedData data = new CombinedData(xVals);
        //data.setData(generateLineData());
        data.setData(getMonthBarData());
        return data;
    }

    @Override
    protected void initListeners() {
        dayButton.setOnClickListener(this);
        weekButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        //为下面的页面.ViewPager设置页面滑动监听事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                MyLog.i(TAG, "onPageSelected=" + position);
                //页面滑动最后会显示当前的位置,根据这个位置去改变图表的数据,和上面的标题栏
                changeDate(position);
                changeChat(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    //切换图表设置
    private void changeChat(final int position) {
        if(dayButton.isChecked()){
            AsyncTask myDayAsync=new AsyncTask<Object,Object,List<DaySynopic>>(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(pd!=null){
                            pd.show();
                        }
                    }

                    @Override
                protected List<DaySynopic> doInBackground(Object... params) {
                    Date date1=ToolKits.getDayFromDate(new Date(0), position+1);
                    String time=new SimpleDateFormat(com.linkloving.rtring_new.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date1);
                    MyLog.i(TAG,"查询数据的时间="+time+"position="+position);
                    Map<Integer,ArrayList<SportRecord>> datatemp=new Datahelper(DistanceDataActivity.this, String.valueOf(userEntity.getUser_id()), time,time,false).getMydata();
                    data=datatemp;
                        //查询汇总表的数据
                        List<DaySynopic> mDaySynopicArrayList= DaySynopicTable.findDaySynopicRange(DistanceDataActivity.this, userEntity.getUser_id()+"", time, time, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
                        DaySynopic mDaySynopic = new DaySynopic();
                        ArrayList<DaySynopic> temp=new ArrayList<>();
                        if(mDaySynopicArrayList==null||mDaySynopicArrayList.size()<=0){
                            mDaySynopic=SportDataHelper.offlineReadMultiDaySleepDataToServer(DistanceDataActivity.this, time, time);
                            if(mDaySynopic.getTime_zone()==null){
                                return null;
                            }
                            temp.add(mDaySynopic);
                            DaySynopicTable.saveToSqliteAsync(DistanceDataActivity.this,temp,userEntity.getUser_id()+"");
                        }else {
                            return mDaySynopicArrayList;
                        }
                        MyLog.e(TAG, "temp=" + temp.toString());
                        MyLog.e(TAG, "temp=" +temp.size());
                        if(temp.size()>0){
                            return temp;
                        }else {
                            //可能还没去同步这一天的数据,
                            return null;
                        }
                }
                @Override
                protected void onPostExecute(List<DaySynopic> aVoid) {
                    MyLog.i(TAG,"重画图");
                    combbarChart.setData(generateDayBarData());
                    combbarChart.invalidate();

                    setDataToView(position, aVoid);

                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (dayDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(dayDataAsync, true);
                AsyncTaskManger.getAsyncTaskManger().addAsyncTask(dayDataAsync = myDayAsync);
                myDayAsync.execute();
        }


        if(weekButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            }
            weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount - position - 1) * 7)));//中间是当前日期
            AsyncTask myWeekAsync=new AsyncTask<Object,Object,Object>(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if(pd!=null){
                        pd.show();
                    }
                }
                @Override
                protected Object doInBackground(Object... params) {
                    MyLog.i(TAG, "开始时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    ArrayList<DaySynopic> mDaySynopicArrayList=ToolKits.getFindWeekData(DistanceDataActivity.this, weekSwitcher.getStartDate(), userEntity);
                    listData=mDaySynopicArrayList;
                    return null;
                }
                @Override
                protected void onPostExecute(Object aVoid) {
                    MyLog.i(TAG, "结束时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    combbarChart.setData(generateWeekBarData());
                    combbarChart.invalidate();
                    setDataToView(position, listData);
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (weekDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(weekDataAsync, true);
                AsyncTaskManger.getAsyncTaskManger().addAsyncTask(weekDataAsync = myWeekAsync);
            myWeekAsync.execute();
        }
        if(monthButton.isChecked()){
            java.text.SimpleDateFormat sim1 = new java.text.SimpleDateFormat("MM");
            java.text.SimpleDateFormat sim3 = new java.text.SimpleDateFormat("yyyy");
            int year=Integer.parseInt(sim3.format(new Date()));
            int s2=Integer.parseInt(sim1.format(new Date()));//当前时间的月份
            int month=(year-1970)*12+s2+1;
            GregorianCalendar base2 = new GregorianCalendar();
            base2.setTime(new Date());
            base2.add(GregorianCalendar.MONTH, -(month - position - 1));
            monthSwitcher.setBaseTime(base2.getTime());
            MyLog.i(TAG, "结束时间" + monthSwitcher.getEndDateStr());
            Date lastDate=monthSwitcher.getEndDate();
            startDateString=monthSwitcher.getStartDateStr();
            endDateString=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getDayFromDate(lastDate, -1));
            MyLog.i(TAG, monthSwitcher.getSQLBetweenAnd());
            MyLog.i(TAG, "startDateString=" + startDateString + "endDateString=" + endDateString);
            AsyncTask myMonthAsybc=new AsyncTask<Object,Object,Object>(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if(pd!=null){
                        pd.show();
                    }
                }
                @Override
                protected Object doInBackground(Object... params) {
                    MyLog.i(TAG, "开始时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    ArrayList<DaySynopic> mDaySynopicArrayList=ToolKits.getFindMonthData(DistanceDataActivity.this,monthSwitcher.getStartDate(),userEntity);
                    listData=mDaySynopicArrayList;
                    return null;
                }
                @Override
                protected void onPostExecute(Object aVoid) {
                    MyLog.i(TAG, "结束时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    combbarChart.setData(generateMonthBarData(monthSwitcher.getStartDateStr()));
                    combbarChart.invalidate();
                    setDataToView(position, listData);
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (monthDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(monthDataAsync, true);
            AsyncTaskManger.getAsyncTaskManger().addAsyncTask(monthDataAsync = myMonthAsybc);
            myMonthAsybc.execute();
        }

    }
       /* private ArrayList<DaySynopic> querydata(int position){
        if(dayButton.isChecked()){
            //计算出当前的时期
            Date date1=ToolKits.getDayFromDate(new Date(0), position+1);
            //查询这天的数据
            String time=new SimpleDateFormat(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date1);
            MyLog.i(TAG,"查询数据的时间="+time+"position="+position);
            DaySynopic mDaySynopic = new DaySynopic() ;
            ArrayList<DaySynopic> temp=new ArrayList<>();
            ArrayList<DaySynopic> mDaySynopicArrayList= DaySynopicTable.findDaySynopicRange(DistanceDataActivity.this, userEntity.getUser_id()+"", time, time, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
            //去汇总一遍
            if(mDaySynopicArrayList==null||mDaySynopicArrayList.size()<=0){
                mDaySynopic=SportDataHelper.offlineReadMultiDaySleepDataToServer(DistanceDataActivity.this, time, time);
                if(mDaySynopic.getTime_zone()==null){
                    return null;
                }
                temp.add(mDaySynopic);
                DaySynopicTable.saveToSqliteAsync(DistanceDataActivity.this,temp,userEntity.getUser_id()+"");
            }else {
                return mDaySynopicArrayList;
                //mDaySynopic=mDaySynopicArrayList.get(0);
            }

            if(temp.size()>0){
                return temp;
            }else {
                //可能还没去同步这一天的数据,
                return null;
            }

          *//*  int walkDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
            //跑步 里程
            int runDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
            int distance=walkDistance+runDistance;
            //int distance=Integer.parseInt(mDaySynopic.getWork_distance())+Integer.parseInt(mDaySynopic.getRun_distance());
            return distance;*//*
        }
        //周数据
        else if(weekButton.isChecked()){
            //传来的position是的几周//更当前时间比较
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            };
            weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount - position - 1) * 7)));//中间是当前日期
            ArrayList<DaySynopic> mDaySynopicArrayList=ToolKits.getFindWeekData(DistanceDataActivity.this,weekSwitcher.getStartDate(),userEntity);
            return mDaySynopicArrayList;
            *//*if(mDaySynopicArrayList==null||mDaySynopicArrayList.size()<=0){
                return 0;
            }
            int countDistance=0;
            for (DaySynopic mDaySynopic:mDaySynopicArrayList){
                int walkDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
                //跑步 里程
                int runDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
                int distance=walkDistance+runDistance;
                countDistance=countDistance+distance;
            }
            return countDistance;*//*
        }
        //月数据
        else if(monthButton.isChecked()){
            java.text.SimpleDateFormat sim1 = new java.text.SimpleDateFormat("MM");
            java.text.SimpleDateFormat sim3 = new java.text.SimpleDateFormat("yyyy");
            int year=Integer.parseInt(sim3.format(new Date()));
            int s2=Integer.parseInt(sim1.format(new Date()));//当前时间的月份
            int month=(year-1970)*12+s2+1;
            GregorianCalendar base2 = new GregorianCalendar();
            base2.setTime(new Date());
            base2.add(GregorianCalendar.MONTH, -(month - position-1));
            monthSwitcher.setBaseTime(base2.getTime());
            MyLog.i(TAG, "结束时间" + monthSwitcher.getEndDateStr());
            ArrayList<DaySynopic> mDaySynopicArrayList= ToolKits.getFindMonthData(DistanceDataActivity.this, monthSwitcher.getStartDate(), userEntity);

            return mDaySynopicArrayList;
           *//* if(mDaySynopicArrayList==null||mDaySynopicArrayList.size()<=0){
                return 0;
            }
            int countDistance=0;
            for (DaySynopic mDaySynopic:mDaySynopicArrayList){
                int walkDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
                //跑步 里程
                int runDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
                int distance=walkDistance+runDistance;
                countDistance=countDistance+distance;
            }
            return countDistance;*//*
        }
        return null;
    }*/

    //viewpage在切换过程中更改button的字样
        private void changeDate(int position){
            //思路就是根据传过来的位置,推算出当前的选定的时间,和系统当前时间的差值,
            //
            //日详情
        if(dayButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0), new Date());//计算当前时间和1970年的时间之间相差多少天
            int day=days-position-1;
            Date dataChange=ToolKits.getDayFromDate(new Date(),-(day));
            date=dataChange;
            MyLog.i(TAG, "days=" + days + "  " + position+"   day"+day);
            if(day==0){
                //传进来的是今天的数据
                day1.setText(getString(R.string.yesterday));
                day2.setText(getString(R.string.today));
                day2.setChecked(true);
                day3.setText("");
            } else if(day==1) {
                //计算传传进来的日期是几天
                Date d=ToolKits.getDayFromDate(new Date(),-2);
                day1.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(d));
                day2.setText(getString(R.string.yesterday));
                day2.setChecked(true);
                day3.setText(getString(R.string.today));
            }else if(day==2){
                Date date1=ToolKits.getDayFromDate(new Date(),-(day));
                Date date3=ToolKits.getDayFromDate(new Date(),-(day+1));
                day1.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date3));
                day2.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date1));
                day3.setText(getString(R.string.yesterday));
            }
            else{
                Date date1=ToolKits.getDayFromDate(new Date(),-(day+1));
                Date data2=ToolKits.getDayFromDate(new Date(),-(day));
                Date date3=ToolKits.getDayFromDate(new Date(),-(day-1));
                day1.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date1));
                day2.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(data2));
                day2.setChecked(true);
                day3.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date3));
            }

        }
        //周数据
        else if(weekButton.isChecked()){
            //传来的position是的几周//更当前时间比较

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD);
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            }
            MyLog.i(TAG,"相差几周="+(weekcount-position-1));
            if(weekcount-position-1==0){
                //传进来的是本周
                // 上周  本周
                day1.setText(getString(R.string.lastweek));
                day2.setText(getString(R.string.thisweek));
                day2.setChecked(true);
                day3.setText("");
            }
            else if(weekcount-position-1==1){
                // 日期  上周  本周
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*2))));
                day1.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(),-1)));
                day2.setText(getString(R.string.lastweek));
                day2.setChecked(true);
                day3.setText(getString(R.string.thisweek));
            }else if(weekcount-position-1==2){
                //日期 日期  上周
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*3))));//相差三周的日期
                day1.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*2))));//相差两周周的日期
                day2.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                day3.setText(getString(R.string.lastweek));

            }else {
                //日期 日期  日期
                GregorianCalendar base1 = new GregorianCalendar();
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position)*7)));//减一周的日期
                day1.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position-1)*7)));//中间是当前日期
                day2.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position-2)*7)));//加一周的日期
                day3.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
            }
        }

        //月数据
        else if(monthButton.isChecked()){
            //根据position当前是第几月  代表的是第几个月
            int month=monthadapter.getCount();//当前的总共的月
            MyLog.i(TAG,"month="+month+"position="+position);
            java.text.SimpleDateFormat sim1 = new java.text.SimpleDateFormat("MM");
            java.text.SimpleDateFormat sim2 = new java.text.SimpleDateFormat("yyyy-MM");
            java.text.SimpleDateFormat sim3 = new java.text.SimpleDateFormat("yyyy");
            if(position==month-1){
                //传进来的是本月的数据
                //当前是本月
                day1.setText(getString(R.string.lastmonth));
                day2.setText(getString(R.string.thismonth));
                day2.setChecked(true);
                day3.setText("");
            } else if(position==month-2) {
                //上个月
                GregorianCalendar base1= new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -2);
                String s=sim2.format(base1.getTime());
                day1.setText(s);
                day2.setText(getString(R.string.lastmonth));
                day2.setChecked(true);
                day3.setText(getString(R.string.thismonth));
            }else if(position==month-3){
                GregorianCalendar base1 = new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -3);
                String days1=sim2.format(base1.getTime()) ;//相差三个月
                GregorianCalendar base2 = new GregorianCalendar();
                base2.setTime(new Date());
                base2.add(GregorianCalendar.MONTH, -2);
                String days2=sim2.format(base2.getTime()) ;//相差两个个月
                day1.setText(days1);
                day2.setText(days2);//相差两个月
                day2.setChecked(true);
                day3.setText(getString(R.string.lastmonth));
            }else{
                //相差了month-position-1
                GregorianCalendar base1= new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -(month-position));
                GregorianCalendar base2 = new GregorianCalendar();
                base2.setTime(new Date());
                base2.add(GregorianCalendar.MONTH,-(month-position-1));
                GregorianCalendar base3 = new GregorianCalendar();
                base3.setTime(new Date());
                base3.add(GregorianCalendar.MONTH, -(month-position-2));
                String days1=sim2.format(base1.getTime()) ;//前一个月
                String days2=sim2.format(base2.getTime()) ;//本月
                String days3=sim2.format(base3.getTime());//后一个月
                day1.setText(days1);
                day2.setText(days2);
                day2.setChecked(true);
                day3.setText(days3);
            }
        }
    }
    private void addline(int i){
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        float f=0;
        for(BarEntry barEntry:mentries){
            if(barEntry.getVal()>f){
                f=barEntry.getVal();
            }
        }
        //没有数据就不画了
        if(f==0){
            return;
        }

        switch (i){
            case R.id.report_page_activity_circleviews_dayRb:
                if((int)f<1500){
                    MyLog.i("重新设置");
                    yAxis.setAxisMaxValue(1500);
                }else {
                    yAxis.setAxisMaxValue(f+500);
                }
                LimitLine limitLine1 = new LimitLine(1000,1000+"");
                limitLine1.setLineColor(Color.WHITE);
                limitLine1.setLineWidth(1f);
                limitLine1.setTextColor(Color.WHITE);
                limitLine1.setTextSize(12f);
                limitLine1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                LimitLine limitLine2 = new LimitLine(500,500+"");
                limitLine2.setLineColor(Color.WHITE);
                limitLine2.setLineWidth(1f);
                limitLine2.setTextColor(Color.WHITE);
                limitLine2.setTextSize(12f);
                limitLine2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                yAxis.addLimitLine(limitLine2);
                yAxis.addLimitLine(limitLine1);
                break;
            case R.id.report_page_activity_circleviews_weekRb:
                //紧界线
                if((int)f<15000){
                    yAxis.setAxisMaxValue(15000);
                }else {
                    yAxis.setAxisMaxValue(f+500);
                }
                MyLog.i(TAG, yAxis.getAxisMaxValue() + "最大值");
                LimitLine limitLine = new LimitLine(10000,10000+"");
                limitLine.setLineColor(Color.WHITE);
                limitLine.setLineWidth(1f);
                limitLine.setTextColor(Color.WHITE);
                limitLine.setTextSize(12f);
                limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                yAxis.addLimitLine(limitLine);
                break;
            case R.id.report_page_activity_circleviews_monthRb:
                break;
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.report_page_activity_circleviews_dayRb:
                if(dayButton.isChecked())
                    initTitle();
                break;
            case R.id.report_page_activity_circleviews_weekRb:
                if(weekButton.isChecked())
                    initTitle();
                break;
            case R.id.report_page_activity_circleviews_monthRb:
                if(monthButton.isChecked())
                    initTitle();
                break;
        }

    }

    public class MyDaitanceAdapter extends BaseAdapter {
        String s1=getString(R.string.sunday);
        String s2=getString(R.string.monday);
        String s3=getString(R.string.tuesday);
        String s4=getString(R.string.wednesday);
        String s5=getString(R.string.thursday);
        String s6=getString(R.string.friday);
        String s7=getString(R.string.saturday);
        String[] weekDays = { s1, s2, s3, s4, s5, s6, s7 };
        private Context mcontext;
        private ArrayList<DaySynopic> list;
        private ArrayList<DaySynopic> mlist=new ArrayList<>();
        public MyDaitanceAdapter(Context context,ArrayList<DaySynopic> list){
            this.list=list;
            this.mcontext=context;
            initlist();
        }
        private void initlist  (){
            for(DaySynopic d:list){
               //int i=Integer.parseInt(d.getRun_step()) + Integer.parseInt(d.getWork_step());
                int walkDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(d.getWork_step()), 0));
                //跑步 里程
                int runDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(d.getRun_step()), 0));
                int distance=walkDistance+runDistance;
                if(distance!=0){
                    mlist.add(d);
                }
            }
        }
        @Override
        public int getCount() {
            return mlist.size();
        }
        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mcontext).inflate(R.layout.step_listview_item, null);
                vh = new ViewHolder();
                vh.chatItem= (LinearLayout) convertView.findViewById(R.id.chart_item);
                vh.textViewcount= (TextView) convertView.findViewById(R.id.count);
                vh.textViewdate=(TextView) convertView.findViewById(R.id.date);
                vh.textViewpercent=(TextView) convertView.findViewById(R.id.percent);
                vh.textViewweek=(TextView) convertView.findViewById(R.id.week);
                vh.unit_step= (TextView)convertView.findViewById(R.id.unit_step);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            vh.chatItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //切换图表到当前的日期
                    if(!CommonUtils.isStringEmpty(mlist.get(position).getData_date())){
                     /* int days=ToolKits.getBetweenDay(new Date(0),new Date());
                      int day=ToolKits.getBetweenDay(ToolKits.stringToDate(list.get(position).getDate(),ToolKits.DATE_FORMAT_YYYY_MM_DD),new Date());
                      dayposition=days - day - 1;*/
                        Date dateChage=ToolKits.stringToDate(mlist.get(position).getData_date(),ToolKits.DATE_FORMAT_YYYY_MM_DD);
                        date=dateChage;
                        dayButton.setChecked(true);
                        initTitle();
                    }else {
                        //得到的日期是空的
                        MyLog.i(TAG,"点击后,获得的时间是空的");
                    }

                }
            });


            vh.textViewdate.setText(mlist.get(position).getData_date());
            //星期,目标
            //获取本地目标
            Calendar cal=Calendar.getInstance();
            cal.setTime(ToolKits.stringToDate(mlist.get(position).getData_date(),ToolKits.DATE_FORMAT_YYYY_MM_DD));
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0) {
                w = 0;
            }
            vh.textViewweek.setText(weekDays[w]);
            DaySynopic mDaySynopic=mlist.get(position);
            int walkDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
            //跑步里程
            int runDistance = (int)(CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
            int distance=walkDistance+runDistance;
            // int i=Integer.parseInt(mlist.get(position).getRun_step()) + Integer.parseInt(mlist.get(position).getWork_step());


            if(SwitchUnit.getLocalUnit(DistanceDataActivity.this)==ToolKits.UNIT_GONG){
                vh.textViewcount.setText(distance+"");
                vh.unit_step.setText(getString(R.string.unit_m));

            }else{
                // text_Distance.setText(UnitTookits.MChangetoMIRate(distance) + getResources().getString(R.string.unit_mile));
                vh.textViewcount.setText(UnitTookits.MChangetoMIRate(distance)+"");
                vh.unit_step.setText(getString(R.string.unit_mile));
            }

            //vh.textViewcount.setText(distance+"");
            int  distace_goal = (int)(Float.parseFloat(PreferencesToolkits.getGoalInfo(DistanceDataActivity.this, PreferencesToolkits.KEY_GOAL_DISTANCE)));
            if(distace_goal==0){
            }else {
                //Math.ceil(Float.parseFloat(money) * 100*1.0f/ money_goal))
                vh.textViewpercent.setText(Math.ceil(distance*100*1.0f/ distace_goal)+"%");
            }
            return convertView;
        }
        public class ViewHolder {
            public TextView textViewdate, textViewweek,textViewcount,textViewpercent,unit_step;
            public LinearLayout chatItem;
        }


    }

}
