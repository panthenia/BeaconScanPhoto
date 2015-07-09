package com.p.BeaconScanPhoto.Activitis;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.p.BeaconScanPhoto.DataType.DBIbeancon;
import com.p.BeaconScanPhoto.DataType.PublicData;
import com.p.BeaconScanPhoto.R;
import com.p.BeaconScanPhoto.Utils.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by p on 2015/6/27.
 * Beacon详情界面
 *
 */
public class MoreBeaconInfo extends Activity {
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_LOCAL_LOCATION = 2;
    public static final boolean SHOW_TYPE_NEW = true;
    public static final boolean SHOW_TYPE_OLD = false;

    private String bd = "-1",fl = "-1";
    private double x = 0,y = 0;
    private LinearLayout beacon_img_list = null;
    private ImageView locateChoose = null;
    private TextView locationVal = null;
    private TextView beacon_loc_text = null;
    private AutoCompleteTextView beacon_detail = null;
    private DBIbeancon dbIbeancon = null;
    private Spinner beacon_sumury = null;
    private String mac="",uuid="",name="";
    private int rssi =0,major = 0,minor = 0;
    private boolean showtype = false;
    private HashMap<Integer,String> imgId2Name = new HashMap<>();
    private HashMap<Integer,Bitmap> beacon_imgs = new HashMap<>() ;
    private LinearLayout.LayoutParams newImgLayout = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_beacon_info);
        initIntentData();
        initUI();
    }
    private void initIntentData(){
        Intent intent = getIntent();
        if (intent.hasExtra("new")){
            showtype = SHOW_TYPE_NEW;
            mac = intent.getStringExtra("mac");
            uuid = intent.getStringExtra("uuid");
            name = intent.getStringExtra("name");
            rssi = intent.getIntExtra("rssi", 0);
            major = intent.getIntExtra("major", 0);
            minor = intent.getIntExtra("minor", 0);
            dbIbeancon = new DBIbeancon();
            dbIbeancon.setMac(mac);
            dbIbeancon.setMajor(major);
            dbIbeancon.setMinor(minor);
            dbIbeancon.setRssi(rssi);
            dbIbeancon.setUuid(uuid);
            dbIbeancon.setName(name);
        }else{
            showtype = SHOW_TYPE_OLD;
            mac = intent.getStringExtra("mac");
            dbIbeancon = PublicData.getInstance().configedBeacons.get(mac);
        }
    }
    private void initUI(){
        ActionBar bar = getActionBar();
        if (bar != null)
            bar.setTitle("Beacon:"+mac);
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor_norock));
        beacon_detail = (AutoCompleteTextView)findViewById(R.id.beacon_detail);
        beacon_sumury = (Spinner)findViewById(R.id.beacon_sumury);
        beacon_img_list = (LinearLayout)findViewById(R.id.img_list);
        locateChoose = (ImageView)findViewById(R.id.bt_chooseloc);
        locationVal = (TextView)findViewById(R.id.location_val);
        beacon_loc_text = (TextView)findViewById(R.id.choose_loc_text);
        if (PublicData.getInstance().locatingType == PublicData.LOCATE_GPS){
            beacon_loc_text.setText("GPS(百度)模式，点击获取位置->");
            if (showtype == SHOW_TYPE_OLD){
                locationVal.setText(String.format("Lat:%s-Lon:%s",dbIbeancon.getLat(),dbIbeancon.getLon()));
            }
        }else{
            beacon_loc_text.setText("室内点选模式，点击获取位置->");
            if (showtype == SHOW_TYPE_OLD){
                locationVal.setText(String.format("Floor:%s-X:%s-Y:%s",dbIbeancon.getFloor(),dbIbeancon.getLat(),dbIbeancon.getLon()));            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, PublicData.getInstance().beaconDetails);
        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_list_item_1, PublicData.getInstance().beaconSumury);
        beacon_detail.setAdapter(adapter);
        beacon_sumury.setAdapter(adapter1);
        int si = PublicData.getInstance().beaconSumury.indexOf(PublicData.getInstance().defaultSumury);
        beacon_sumury.setSelection(si==-1?0:si);
        newImgLayout.height = DensityUtil.dip2px(this,150);
        newImgLayout.weight = DensityUtil.dip2px(this,120);
        newImgLayout.rightMargin = DensityUtil.dip2px(this,10);
        newImgLayout.leftMargin = DensityUtil.dip2px(this,10);
        if (showtype == SHOW_TYPE_OLD){
            ArrayList<String> imgs = dbIbeancon.getImgs();
            for (String img : imgs) {
                ImageView view = new ImageView(this);
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                int id = View.generateViewId();
                Bitmap bitmap = PublicData.getInstance().getBeaconImgFromFile(img);
                if (bitmap != null) {
                    view.setId(id);
                    view.setLayoutParams(newImgLayout);
                    view.setImageBitmap(bitmap);
                    beacon_imgs.put(id, bitmap);
                    imgId2Name.put(id, img);
                    beacon_img_list.addView(view);
                }

            }
            if (dbIbeancon.getDetail().length() > 0)
                beacon_detail.setText(dbIbeancon.getDetail());
        }

    }
    public void onUIClicked(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.beacon_img:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
            case R.id.save_bt:
                String dtl = beacon_detail.getText().toString();
                String smr = beacon_sumury.getSelectedItem().toString();
                dbIbeancon.setDetail(dtl);
                dbIbeancon.setSumury(smr);
                if (PublicData.getInstance().locatingType == PublicData.LOCATE_LOCAL){
                    dbIbeancon.setLocationType(PublicData.LOCATE_LOCAL);
                    dbIbeancon.setBuilding(bd);
                    dbIbeancon.setFloor(fl);
                    dbIbeancon.setLat(x);
                    dbIbeancon.setLon(y);
                }else {
                    dbIbeancon.setLat(PublicData.getInstance().latitude);
                    dbIbeancon.setLon(PublicData.getInstance().longitude);
                    dbIbeancon.setLocationType(PublicData.LOCATE_GPS);
                }
                ArrayList<String> arrayList = dbIbeancon.getImgs();
                arrayList.clear();
                arrayList.addAll(imgId2Name.values());
                PublicData.getInstance().saveCheckBeacon2Db(dbIbeancon);
                PublicData.getInstance().configedBeacons.put(dbIbeancon.getMac(), dbIbeancon);
                PublicData.getInstance().checkBeaconSet.add(dbIbeancon.getMac());
                Toast.makeText(this,"数据已保存",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_chooseloc:
                if (PublicData.getInstance().locatingType == PublicData.LOCATE_LOCAL) {
                    intent = new Intent(MoreBeaconInfo.this, MapActivity.class);
                    intent.putExtra("mac", mac);
                    startActivityForResult(intent, REQUEST_CODE_LOCAL_LOCATION);
                    dbIbeancon.setLocationType(PublicData.LOCATE_LOCAL);
                }else{
                    dbIbeancon.setLat(PublicData.getInstance().latitude);
                    dbIbeancon.setLon(PublicData.getInstance().longitude);
                    dbIbeancon.setLocationType(PublicData.LOCATE_GPS);
                    locationVal.setText(String.format("Lat:%s-Lon:%s",dbIbeancon.getLat(),dbIbeancon.getLon()));
                }
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            for (Bitmap bitmap :beacon_imgs.values()){
                bitmap.recycle();
            }
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOCAL_LOCATION:
                if (resultCode == MapActivity.GET_LOCATION_SUCCESS) {
                    if (data != null) {
                        if (data.hasExtra("bd"))
                            bd = data.getStringExtra("bd");
                        if (data.hasExtra("fl"))
                            fl = data.getStringExtra("fl");
                        if (data.hasExtra("nx"))
                            x = data.getFloatExtra("nx", 0);
                        if (data.hasExtra("ny"))
                            y = data.getFloatExtra("ny", 0);
                        locationVal.setText(String.format("Building:%s-Floor:%s-X:%s-Y:%s", bd, fl, x, y));

                    }
                }else {
                    PublicData.getInstance().setLocationType(PublicData.LOCATE_GPS);
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("手机中无室内地图数据库，是否切换至GPS定位模式")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    beacon_loc_text.setText("GPS(百度)模式，点击获取位置->");
                                }
                            })
                            .create()
                            .show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    if(data !=null){ //可能尚未指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        //返回有缩略图
                        if(data.hasExtra("data")){
                            Bitmap thumbnail = data.getParcelableExtra("data");
                            if (thumbnail != null){
                                ImageView view = new ImageView(this);
                                String saveName = PublicData.getInstance().saveBeaconImg2File(mac, thumbnail);

                                int id = View.generateViewId();
                                view.setId(id);
                                view.setLayoutParams(newImgLayout);
                                view.setImageBitmap(thumbnail);
                                view.setScaleType(ImageView.ScaleType.FIT_XY);
                                beacon_imgs.put(id, thumbnail);
                                imgId2Name.put(id,saveName);
                                beacon_img_list.addView(view);
                                view.setClickable(true);
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View view) {
                                        new AlertDialog.Builder(MoreBeaconInfo.this)
                                                .setMessage("是否删除这张照片")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        int vid = view.getId();
                                                        beacon_img_list.removeView(view);
                                                        Bitmap bitmap = beacon_imgs.get(vid);
                                                        if (bitmap != null)
                                                            bitmap.recycle();
                                                    }
                                                })
                                                .setNegativeButton("取消",null)
                                                .create()
                                                .show();
                                    }
                                });
                            }
                        }
                    }
                }
        }
    }
}