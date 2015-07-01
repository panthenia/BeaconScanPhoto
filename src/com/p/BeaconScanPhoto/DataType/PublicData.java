package com.p.BeaconScanPhoto.DataType;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.p.BeaconScanPhoto.R;
import com.p.BeaconScanPhoto.Utils.DataUtil;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by p on 2015/3/3.
 *
 */
public class PublicData extends Application {

    public static final boolean LOCATE_GPS = true;
    public static final boolean LOCATE_LOCAL = false;


    private static PublicData self;
    public ArrayList<DBIbeancon> beacons = new ArrayList<>();
    public HashSet<String> checkBeaconSet = new HashSet<String>();
    public HashSet<String> uploadBeaconSet = new HashSet<String>();
    public HashMap<String,DBIbeancon> configedBeacons = new HashMap<>();

    public ArrayList<String> beaconDetails = new ArrayList<>();
    public ArrayList<String> beaconSumury  = new ArrayList<>();
    public DataUtil du;
    private String ip;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-hh-mm-ss");
    public MessageDigest md5_encriptor = null;
    public boolean isHas_save_ip() {
        return has_save_ip;
    }

    public void setHas_save_ip(boolean has_save_ip) {
        this.has_save_ip = has_save_ip;
    }

    private boolean has_save_ip;

    public boolean isHas_save_user() {
        return has_save_user;
    }

    public void setHas_save_user(boolean has_save_user) {
        this.has_save_user = has_save_user;
    }

    private boolean has_save_user;
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    private String user,psw;
    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getUser() {
        return user;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    boolean login = false;
    public void setUser(String user) {
        this.user = user;
    }
    private ConcurrentHashMap<String, Handler> handlerHashMap = new ConcurrentHashMap<String, Handler>();

    public ConcurrentHashMap<String, Handler> getHandlerHashMap() {
        return handlerHashMap;
    }
    public boolean locatingType;
    public int beaconScanPeriod = 1000,beaconExpirationPeriod = 2000,gpsScanPeriod = 1000;
    public String defaultSumury;
    private String port;
    public double latitude,longitude;
    public LocationClient mLocationClient = null;
    public LocationClientOption locationClientOption = null;
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        du = new DataUtil(this, this.getString(R.string.db_name), null, 1);
        try {
            md5_encriptor = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        beaconSumury.add("hello");
        getCheckedBeaconInDb();
        initBaiduLocating();
        initWithConfig();
    }
    public static PublicData getInstance(){
        return self;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;
    public String getMd5(String data){

        md5_encriptor.reset();
        byte[] data_byte;
        data_byte = data.getBytes();

        byte[] hash_data = md5_encriptor.digest(data_byte);
        StringBuilder md5StrBuff = new StringBuilder();

        for (byte aHash_data : hash_data) {
            if (Integer.toHexString(0xFF & aHash_data).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & aHash_data));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & aHash_data));
        }

        return md5StrBuff.toString();
    }
    public String saveBeaconImg2File(String bmac, Bitmap bitmap){
        String fname = bmac+ "-" + java.util.UUID.randomUUID().toString()+".png";
        try {
            FileOutputStream fileOutputStream = openFileOutput(fname, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fname;
    }
    public Bitmap getBeaconImgFromFile(String name){
        try {
            FileInputStream inputStream = openFileInput(name);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public String getImei() {
        return ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
    }
    public void initBaiduLocating(){
        SDKInitializer.initialize(getApplicationContext());
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        locationClientOption = new LocationClientOption();
        locationClientOption.setOpenGps(true);
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(locationClientOption);
    }
    public void initWithConfig(){
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
        String loc = pre.getString("beacon_location","local");
        Log.d("prefer",loc);
        if (loc.equals("gps")){
            setLocationType(LOCATE_GPS);
        }
        else setLocationType(LOCATE_LOCAL);

        defaultSumury = pre.getString("beacon_sumury","");
        String s = pre.getString("beacon_scan_period","1000");
        try {
            beaconScanPeriod = Integer.valueOf(s);
        }catch (NumberFormatException e){
            beaconScanPeriod = 1000;
        }
        s = pre.getString("beacon_expiration_period","2000");
        try {
            beaconScanPeriod = Integer.valueOf(s);
        }catch (NumberFormatException e){
            beaconExpirationPeriod = 2000;
        }
    }
    public void setLocationType(boolean type){
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
        if (type == LOCATE_GPS){
            locatingType = LOCATE_GPS;
            String loc = pre.getString("gps_scan_period","1000");
            try {
                gpsScanPeriod = Integer.valueOf(loc);
            }catch (NumberFormatException e){
                gpsScanPeriod = 1000;
            }
            locationClientOption.setScanSpan(gpsScanPeriod);
            mLocationClient.start();
            mLocationClient.requestLocation();
        }else{
            locatingType = LOCATE_LOCAL;
            if (mLocationClient.isStarted())
                mLocationClient.stop();
        }

    }
    public boolean saveCheckBeacon2Db(DBIbeancon ibeancon) {
        long result = 0;
        SQLiteDatabase db = du.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("mac_id",ibeancon.getMac());
        cv.put("uuid",ibeancon.getUuid());
        cv.put("name",ibeancon.getName());
        cv.put("beizhu",ibeancon.getDetail());
        cv.put("gaiyao",ibeancon.getSumury());
        cv.put("rssi",String.valueOf(ibeancon.getRssi()));
        cv.put("major",String.valueOf(ibeancon.getMajor()));
        cv.put("minor",String.valueOf(ibeancon.getMinor()));
        cv.put("img",ibeancon.getImgNames());
        cv.put("bd",ibeancon.getBuilding());
        cv.put("fl",ibeancon.getFloor());
        cv.put("lctype",String.valueOf(ibeancon.getLocationType()));
        cv.put("longitude",String.valueOf(ibeancon.getLon()));
        cv.put("latitude",String.valueOf(ibeancon.getLat()));
        if (!beaconDetails.contains(ibeancon.getDetail())){
            beaconDetails.add(ibeancon.getDetail());
        }
        checkBeaconSet.add(ibeancon.getBluetoothAddress());
        // area text,type text,time text,val text
        try {
            result = db.insert("beacon_info",null,cv);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        Log.d("save beacon",String.valueOf(result));
        return true;
    }

    public void removeCheckedBeaconInDb(){
        SQLiteDatabase db = du.getReadableDatabase();
        String sql = "delete from unupbeacon";
        String sql1 = "delete from beacon_location";
        try {
            if(db.isOpen()){
                db.execSQL(sql);
                db.execSQL(sql1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }
    public void removeUploadCheckedBeaconInDb(){
        SQLiteDatabase db = du.getReadableDatabase();

        String sql = "delete from unupbeacon where mac_id = '";
        try {
            for(String mac:uploadBeaconSet){
                sql += mac+"';";
                db.execSQL(sql);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }
    public void getCheckedBeaconInDb() {
        SQLiteDatabase db = du.getReadableDatabase();
        String sql;
        Cursor cursor;
        // area text,type text,time text,val text
        sql = "select * from beacon_info";
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {//直到返回false说明表中到了数据末尾
                    DBIbeancon ibeacon = new DBIbeancon();

                    ibeacon.setMajor(Integer.valueOf(cursor.getString(cursor.getColumnIndex("major"))));
                    ibeacon.setMac(cursor.getString(cursor.getColumnIndex("mac_id")));
                    ibeacon.setMinor(Integer.valueOf(cursor.getString(cursor.getColumnIndex("minor"))));
                    ibeacon.setRssi(Integer.valueOf(cursor.getString(cursor.getColumnIndex("rssi"))));
                    ibeacon.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    ibeacon.setName(cursor.getString(cursor.getColumnIndex("name")));
                    ibeacon.setDetail(cursor.getString(cursor.getColumnIndex("beizhu")));
                    ibeacon.setSumury(cursor.getString(cursor.getColumnIndex("gaiyao")));
                    ibeacon.setLocationType(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("lctype"))));
                    String imgs = cursor.getString(cursor.getColumnIndex("img"));
                    ibeacon.setBuilding(cursor.getString(cursor.getColumnIndex("bd")));
                    ibeacon.setBuilding(cursor.getString(cursor.getColumnIndex("fl")));
                    ibeacon.setLon(Double.valueOf(cursor.getString(cursor.getColumnIndex("longitude"))));
                    ibeacon.setLat(Double.valueOf(cursor.getString(cursor.getColumnIndex("latitude"))));
                    if (imgs.length() > 0){
                        String[] ar = imgs.split("|");
                        for (int i=0;i<ar.length;++i)
                            ibeacon.getImgs().add(ar[i]);
                    }
                    configedBeacons.put(ibeacon.getMac(),ibeacon);
                    checkBeaconSet.add(ibeacon.getMac());
                    //ibeacon.setLat(Double.valueOf(cursor.getString(cursor.getColumnIndex("latitude"))));
                    //ibeacon.setLon(Double.valueOf(cursor.getString(cursor.getColumnIndex("longitude"))));
                }
            }
        } catch (SQLException e) {
        } finally {
            //db.close();
        }
    }
}
