package com.linkloving.rtring_new.logic.UI.HeartRate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import Trace.GreenDao.DaoMaster;
import Trace.GreenDao.DaoSession;
import Trace.GreenDao.heartrate;
import Trace.GreenDao.heartrateDao;
import de.greenrobot.dao.query.Query;


/**
 * Created by Daniel.Xu on 2016/10/17.
 */

public class GreendaoUtils {

    private SQLiteDatabase db;
    private Context context ;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
//    private final static long HALFFIVEMILLIONS=150 ;
private final static long HALFFIVEMILLIONS=600 ;
    public GreendaoUtils(Context context,SQLiteDatabase db){
        this.db = db ;
        this.context = context ;
//        dao = new DaoMaster(db).newSession().getHeartrateDao();
        init();
    }
    public GreendaoUtils(Context context){
        this.context = context ;
      DaoMaster.DevOpenHelper heartrateHelper = new DaoMaster.DevOpenHelper(context, "heartrate", null);
        this.db = heartrateHelper.getReadableDatabase();
        init();

    }
    private void init(){
        // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        setupDatabase(db);
        // 获取 NoteDao 对象
        getHeartrateDao();
    }
    private void  setupDatabase(SQLiteDatabase db){
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//            devOpenHelper = new DaoMaster.DevOpenHelper(context, "Note", null);
//            db = devOpenHelper.getReadableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
    private heartrateDao getHeartrateDao(){
        return daoSession.getHeartrateDao();
    }


    /**
     * time是秒数
     * @param starttime
     * @param max
     * @param avg
     */
    public void add(int starttime , int  max ,int avg){
        heartrate heartrate = new heartrate(null, starttime, max, avg);
        getHeartrateDao().insertOrReplace(heartrate);

    }

    /**
     * 根据开始时间来查数据
     * @param starttime
     * @return
     */
    public   List<heartrate>  search(int  starttime){
        Query<heartrate> list = getHeartrateDao().queryBuilder().where(heartrateDao.Properties.StartTime.eq(starttime)).orderAsc(heartrateDao.Properties.Id)
                .build();
        List<heartrate> heartrateList = list.list();
        return heartrateList ;
    }

    /**
     * 根据时间来查5分钟的数据
     */
    public List<heartrate> searchDurationFiveMinute(long starttime){
        Query<heartrate> list = getHeartrateDao().queryBuilder().where(heartrateDao.Properties.StartTime.between(starttime - HALFFIVEMILLIONS, starttime + HALFFIVEMILLIONS))
                .orderAsc(heartrateDao.Properties.Id).build();
        List<heartrate> heartrateList = list.list();
            return heartrateList ;
    }

    public List<heartrate> searchAllRecord(){
        Query<heartrate> build = getHeartrateDao().queryBuilder().where(heartrateDao.Properties.Id.isNotNull())
                .orderAsc(heartrateDao.Properties.Id).build();
        List<heartrate> list = build.list();
        return list ;
    }

    /**
     * 根据一天的开始时间和结束时间来找一天的数据
     * @param starttime
     * @param endtime
     * @return
     */
    public   List<heartrate>  searchOneDay(long  starttime, long endtime){
        Query<heartrate> list = getHeartrateDao().queryBuilder().where(heartrateDao.Properties.StartTime.between(starttime/1000,endtime/1000)).orderAsc(heartrateDao.Properties.Id)
                .build();
        List<heartrate> heartrateList = list.list();
        return heartrateList ;
    }

}
