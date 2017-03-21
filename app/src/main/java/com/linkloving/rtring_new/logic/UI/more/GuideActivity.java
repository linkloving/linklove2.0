package com.linkloving.rtring_new.logic.UI.more;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends ToolBarActivity {
    private ViewPager guideViewPager;
    private GuidePageAdapter guidePageAdapter;
    private List<View> views = new ArrayList<>();
    private View firstView, secondView;//需要滑动的页卡

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        UserEntity userEntity = MyApplication.getInstance(GuideActivity.this).getLocalUserInfoProvider();

        int device_type = userEntity.getDeviceEntity().getDevice_type();

        SetBarTitleText(getString(R.string.getting_started));
        guideViewPager = (ViewPager) findViewById(R.id.guideViewPager);
        LayoutInflater lf = getLayoutInflater().from(this);
        firstView = lf.inflate(R.layout.activity_guide_view, null);
        secondView = lf.inflate(R.layout.activity_guide_view, null);
        ImageView imageView1 = (ImageView) firstView.findViewById(R.id.imageView);
        ImageView imageView2 = (ImageView) secondView.findViewById(R.id.imageView);
        switch (device_type){
            case MyApplication.DEVICE_BAND:
                imageView1.setImageResource(R.mipmap.help_one_bund1);
                imageView2.setImageResource(R.mipmap.help_two_bund1);
                break;
            case MyApplication.DEVICE_WATCH:
                imageView1.setImageResource(R.mipmap.help_one_watch);
                imageView2.setImageResource(R.mipmap.help_two_watch);
                break;
            case MyApplication.DEVICE_BAND_VERSION3:
                imageView1.setImageResource(R.mipmap.help_one_bund3);
                imageView2.setImageResource(R.mipmap.help_two_bund3);
                break;
        }
        views.add(firstView);
        views.add(secondView);
        views.add(new View(this));
        guidePageAdapter = new GuidePageAdapter();
        guideViewPager.setAdapter(guidePageAdapter);
        guideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //position 第几页  positionOffsetPixels滑动偏移量
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==1 && positionOffsetPixels>5){
                    GuideActivity.this.finish();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initListeners() {

    }

    public class GuidePageAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return 3;
        }
        // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }
    }
}
