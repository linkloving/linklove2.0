package com.linkloving.rtring_new.utils.sportUtils;

import android.content.Context;

import com.linkloving.rtring_new.utils.APPMD5;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.lnt.rechargelibrary.impl.AppRegisterCallbackInterface;
import com.lnt.rechargelibrary.impl.RegisterAppUtil;
import com.lnt.rechargelibrary.util.XXTea;

/**
 * Created by Daniel.Xu on 2016/12/12.
 */

public class LntUtils {
    public  static void  registApp(Context context){
        String packagename = context.getPackageName();  //需要替换
        String appmd5 = APPMD5.getSignMd5Str(context);       //需要替换
        String sign = "";
        String str = packagename + appmd5;
//        String appkey = "1a7290dc34939147c6cad652b91fa005";   //需要替换
        String appkey = "47fa1aee1eea546fcc6e3265bf6e75b5";   //需要替换
        str = RegisterAppUtil.sort(str);
        try {
            String encryptStr = XXTea.encrypt(str, "UTF-8", appkey);
            byte[] bymd5 = RegisterAppUtil.encryptMD5(encryptStr.getBytes());
            String md5Str = RegisterAppUtil.toHex(bymd5);
            sign = md5Str.substring(md5Str.length() - 8, md5Str.length());
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        RegisterAppUtil.registerApp(context, packagename, appmd5, sign, new AppRegisterCallbackInterface() {
            @Override
            public void onSuccess() {
                MyLog.e("registerapp","onSuccess");
            }

            @Override
            public void onFail(String s) {
                MyLog.e("registerapp","onfail"+s);
            }
        });
    }
}
