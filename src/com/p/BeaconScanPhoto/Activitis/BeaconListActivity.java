package com.p.BeaconScanPhoto.Activitis;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.lef.scanner.*;
import com.p.BeaconScanPhoto.DataType.BeaconListAdapter;
import com.p.BeaconScanPhoto.DataType.PublicData;
import com.p.BeaconScanPhoto.R;
import com.p.BeaconScanPhoto.Utils.NetWorkService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

public class BeaconListActivity extends Activity implements IBeaconConsumer {
    /**
     * Called when the activity is first created.
     */
    public static final int UPDATE_BEACON_DATA = 0;
    public static final int REQUEST_FINISH_SUCCESS = 1;
    public static final int KEY_TIME_OUT = 2;
    public static final int REQUEST_FINISH_FAIL = 3;
    public static final int LOGIN_SUCCESS = 4;

    private IBeaconManager iBeaconManager = null;
    private static BeaconListAdapter beaconAdaptor = null;
    private static ArrayList<IBeacon> UIBeaconList = new ArrayList<>();
    //UI元素
    private ListView beaconListView = null;

    Handler mhandler = new MyHandler(this);
    private static class MyHandler extends Handler{
        private final WeakReference<BeaconListActivity> mActivity;

        public MyHandler(BeaconListActivity activity) {
            mActivity = new WeakReference<BeaconListActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            BeaconListActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what){
                    case UPDATE_BEACON_DATA:
                        beaconAdaptor.updateData(UIBeaconList);
                        beaconAdaptor.notifyDataSetChanged();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_list_layout);
        iBeaconManager = IBeaconManager.getInstanceForApplication(this);
        initUI();
    }


    //初始化界面UI元素
    private void initUI(){
        ActionBar bar = getActionBar();
        if (bar != null)
            bar.setTitle("周围的Beacons");
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }

        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor_norock));
        beaconListView = (ListView)findViewById(R.id.beacon_list_view);
        beaconAdaptor = new BeaconListAdapter(this);
        beaconListView.setAdapter(beaconAdaptor);
        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IBeacon beacon = (IBeacon)adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(BeaconListActivity.this,MoreBeaconInfo.class);
                if (PublicData.getInstance().configedBeacons.get(beacon.getBluetoothAddress()) != null){
                    intent.putExtra("old",true);
                }else {
                    intent.putExtra("new",true);
                    intent.putExtra("rssi",beacon.getRssi());
                    intent.putExtra("major",beacon.getMajor());
                    intent.putExtra("minor",beacon.getMinor());
                    intent.putExtra("uuid",beacon.getProximityUuid());
                    intent.putExtra("name",beacon.getName() == null?"":beacon.getName());
                }
                intent.putExtra("mac",beacon.getBluetoothAddress());
                startActivity(intent);
            }
        });
    }


    /**
     * 请求打开蓝牙，如果不打开，将退出程序
     */
    private void initBluetooth() {
        // TODO Auto-generated method stub
        final BluetoothAdapter blueToothEable = BluetoothAdapter
                .getDefaultAdapter();
        if (!blueToothEable.isEnabled()) {
            new AlertDialog.Builder(BeaconListActivity.this)
                    .setTitle("蓝牙开启")
                    .setMessage("需要开启蓝牙").setCancelable(false)
                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            blueToothEable.enable();
                            iBeaconManager.bind(BeaconListActivity.this);
                        }
                    }).setNegativeButton("退出", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    BeaconListActivity.this.finish();
                }
            }).create().show();
        } else {
            //设置beacon扫描间隔时间
            //Log.d("times",String.valueOf(PublicData.getInstance().beaconExpirationPeriod));
            //Log.d("times",String.valueOf(PublicData.getInstance().beaconScanPeriod));
            iBeaconManager.setInside_expiration_millis(PublicData.getInstance().beaconExpirationPeriod);
            iBeaconManager.setForegroundScanPeriod(PublicData.getInstance().beaconScanPeriod);
            iBeaconManager.bind(this);
        }
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
         if (iBeaconManager.isBound(this)) {
            iBeaconManager.unBind(this);
        }
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (iBeaconManager != null && !iBeaconManager.isBound(this)) {
            initBluetooth();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (iBeaconManager != null && iBeaconManager.isBound(this)) {
            iBeaconManager.unBind(this);
        }
    }
    @Override
    public void onIBeaconServiceConnect() {
        // TODO Auto-generated method stub
        // 启动Range服务
        iBeaconManager.setRangeNotifier(new RangeNotifier() {

            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons,
                                                Region region) {
            }

            @Override
            public void onNewBeacons(Collection<IBeacon> iBeacons, Region region) {
                // TODO Auto-generated method stub
                java.util.Iterator<IBeacon> iterator = iBeacons.iterator();
                while (iterator.hasNext()) {
                    IBeacon temp = iterator.next();
                    if (!UIBeaconList.contains(temp)) {
                        UIBeaconList.add(temp);
                    }
                }
                mhandler.sendEmptyMessage(UPDATE_BEACON_DATA);
            }

            @Override
            public void onGoneBeacons(Collection<IBeacon> iBeacons,
                                      Region region) {
                // TODO Auto-generated method stub

                java.util.Iterator<IBeacon> iterator = iBeacons.iterator();
                while (iterator.hasNext()) {
                    IBeacon temp = iterator.next();
                    if (UIBeaconList.contains(temp)) {
                        UIBeaconList.remove(temp);
                    }
                }
                mhandler.sendEmptyMessage(UPDATE_BEACON_DATA);
            }

            @Override
            public void onUpdateBeacon(Collection<IBeacon> iBeacons,
                                       Region region) {
                // TODO Auto-generated method stub
                java.util.Iterator<IBeacon> iterator = iBeacons.iterator();
                while (iterator.hasNext()) {
                    IBeacon temp = iterator.next();
                    if (UIBeaconList.contains(temp)) {
                        UIBeaconList.set(UIBeaconList.indexOf(temp), temp);
                    }
                }
                mhandler.sendEmptyMessage(UPDATE_BEACON_DATA);
            }

        });
        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didExitRegion(Region region) {
                // TODO Auto-generated method stub
            }

            @Override
            public void didEnterRegion(Region region) {
                // TODO Auto-generated method stub

            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                // TODO Auto-generated method stub

            }
        });
        try {
            Region myRegion = new Region("myRangingUniqueId", null, null, null);
            iBeaconManager.startRangingBeaconsInRegion(myRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void onButtonClicked(View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.bt_config:
                intent = new Intent(this,DefaultConfigActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_see:
                intent = new Intent(this,ShowBeaconActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_reset:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("是否重置状态，所有已配置的数据将会清除！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PublicData.getInstance().resetStatus();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                break;
        }
    }
}
