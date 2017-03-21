package com.linkloving.rtring_new.logic.UI.main.boundband;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.UI.main.boundwatch.BLEListActivity;
import com.linkloving.rtring_new.logic.UI.main.boundwatch.BoundActivity;
import com.linkloving.rtring_new.utils.LanguageHelper;
import com.linkloving.rtring_new.utils.ToolKits;

public class Bound3Activity_2 extends ToolBarActivity {
    private ImageView bound_step_img;
    private Button next;
    private Button skipBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_activity_3);
    }

    @Override
    protected void getIntentforActivity() {

    }
    @Override
    protected void initView()
    {
        HideButtonRight(false);
        SetBarTitleText(getString(R.string.bound_title_band));
        next = (Button) findViewById(R.id.scan);
        skipBtn = getRightButton();
        ViewGroup.LayoutParams layoutParams = skipBtn.getLayoutParams();
        layoutParams.width=100;
        layoutParams.height=200;
        skipBtn.setLayoutParams(layoutParams);
        skipBtn.setText(getString(R.string.bound_skip));
        skipBtn.setTextColor(getResources().getColor(R.color.white));
        bound_step_img = (ImageView) findViewById(R.id.bound_step_img);
        TextView changeText = (TextView) findViewById(R.id.change1text);
        changeText.setText(R.string.bound3_active_notice3);
        if(LanguageHelper.isChinese_SimplifiedChinese()){
            bound_step_img.setBackgroundResource(R.mipmap.bound_step_two_phone);
        }else{
            bound_step_img.setBackgroundResource(R.mipmap.bound_step_two_phone_en);

        }

    }

    @Override
    protected void initListeners() {
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // 网络畅通的情况下才能绑定（否则无法完成从服务端拿到utc时间等问题）
                if(ToolKits.isNetworkConnected(Bound3Activity_2.this))
                {
                    startActivityForResult(new Intent(Bound3Activity_2.this, Band3ListActivity.class),BoundActivity.REQUEST_CODE_BLE_LIST);
                }
                else
                {
                    AlertDialog dialog = new AlertDialog.Builder(Bound3Activity_2.this)
                            .setTitle(ToolKits.getStringbyId(Bound3Activity_2.this, R.string.bound_failed))
                            .setMessage(ToolKits.getStringbyId(Bound3Activity_2.this, R.string.bound_failed_msg))
                            .setPositiveButton(ToolKits.getStringbyId(Bound3Activity_2.this, R.string.general_ok),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BoundActivity.REQUEST_CODE_BLE_LIST){
            setResult(resultCode, data);
            finish();
        }
    }
}
