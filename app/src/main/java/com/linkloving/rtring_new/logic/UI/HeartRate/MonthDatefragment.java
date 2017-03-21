package com.linkloving.rtring_new.logic.UI.HeartRate;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.utils.Viewutils.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Daniel.Xu on 2016/11/3.
 */

public class MonthDatefragment extends Fragment {
    private static final String TAG = MonthDatefragment.class.getSimpleName();
    private IDataChangeListener dataChangeListener;
    private WheelView wva;

    public void setDataChangeListener(IDataChangeListener dataChangeListener){
        this.dataChangeListener = dataChangeListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_month_view_fragment, container, false);
        LinearLayout totalView = (LinearLayout) view.findViewById(R.id.step_linear_day);
        totalView.setPadding(0,200,0,0);
        ButterKnife.inject(this, view);
        ImageView step_week_next = (ImageView) view.findViewById(R.id.step_month_next);

        wva = (WheelView) view.findViewById(R.id.step_month_wheelView);
        wva.setOffset(1);
        List<String> monthList = getMonth();
        wva.setSeletion(monthList.size()-1);
        wva.setItems(monthList);
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String month) {
                dataChangeListener.onDataChange(month);
                Log.e("MonthViewFragment","选中的月:"+month);
            }
        });
        step_week_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seletedItem = wva.getSeletedItem();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                MonthFragment monthFragment = new MonthFragment();
                Bundle bundle = new Bundle();
                bundle.putString("monthDate", seletedItem);
                monthFragment.setArguments(bundle);
                transaction.replace(R.id.middle_framelayout, monthFragment);
                transaction.commit();
            }
        });

        return view ;
    }
    //    获取周数集合
    public List<String> getMonth(){
        List<String> month =  new ArrayList<String>();
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM");
        wva.setType(WheelView.TYPE_MONTH);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
//        rightNow.add(Calendar.DATE,-49);

        for (int i=0;i<40;i++){

            String date = sim.format(rightNow.getTime());
            month.add(date);
            rightNow.add(Calendar.MONTH,-1);
        }
        Collections.reverse(month);
        return  month ;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
