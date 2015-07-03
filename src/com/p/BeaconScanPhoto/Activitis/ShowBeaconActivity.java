package com.p.BeaconScanPhoto.Activitis;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import com.lef.scanner.IBeacon;
import com.p.BeaconScanPhoto.DataType.BeaconListAdapter;
import com.p.BeaconScanPhoto.DataType.PublicData;
import com.p.BeaconScanPhoto.R;
import com.p.BeaconScanPhoto.Utils.NetWorkService;

import java.lang.ref.WeakReference;

/**
 * Created by p on 2015/7/1.
 *  已配置的Beacon显示
 */
public class ShowBeaconActivity extends Activity {
    public static final int REQUEST_FINISH_SUCCESS = 1;
    public static final int KEY_TIME_OUT = 2;
    public static final int REQUEST_FINISH_FAIL = 3;
    public static final int LOGIN_SUCCESS = 4;
    private ListView beaconListView = null;
    private Button upload_button;
    private static BeaconListAdapter beaconAdaptor = null;
    public Handler mhandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<ShowBeaconActivity> mActivity;

        public MyHandler(ShowBeaconActivity activity) {
            mActivity = new WeakReference<ShowBeaconActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            ShowBeaconActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what){
                    case REQUEST_FINISH_SUCCESS:
                        beaconAdaptor.notifyDataSetChanged();
                        Toast.makeText(activity, "巡检数据上报成功！", Toast.LENGTH_LONG).show();
                        break;
                    case REQUEST_FINISH_FAIL:
                        Toast.makeText(activity, "巡检数据上报失败！", Toast.LENGTH_LONG).show();
                        break;
                    case KEY_TIME_OUT:

                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_beacon);
        PublicData.getInstance().getHandlerHashMap().put(getClass().getName(), mhandler);
        initUI();
    }
    private void initUI(){
        ActionBar bar = getActionBar();
        if (bar != null)
            bar.setTitle("已配置的Beacons");
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
        beaconAdaptor.setData(PublicData.getInstance().configedBeacons.values());
        beaconListView.setAdapter(beaconAdaptor);
        upload_button = (Button)findViewById(R.id.bt_upload);
        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IBeacon beacon = (IBeacon) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(ShowBeaconActivity.this, MoreBeaconInfo.class);
                intent.putExtra("old", true);
                intent.putExtra("mac", beacon.getBluetoothAddress());
                startActivity(intent);
            }
        });
    }
    public void onButtonClicked(View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.bt_upload:
                if(PublicData.getInstance().isNetworkAvailable()) {
                    if(PublicData.getInstance().isLogin()){
                        intent = new Intent(ShowBeaconActivity.this, NetWorkService.class);
                        intent.putExtra("ActivityName", ShowBeaconActivity.class.getName());
                        intent.putExtra("ReuqestType", "upload_checked");
                        startService(intent);
                        Toast.makeText(this, "开始上传...", Toast.LENGTH_SHORT).show();
                    }else {
                        intent = new Intent(ShowBeaconActivity.this,LoginActivity.class);
                        startActivityForResult(intent,LoginActivity.REQUEST_LOGIN_CODE);
                    }

                }else{
                    Toast.makeText(ShowBeaconActivity.this, "当前无网络连接！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //登录返回
        if (requestCode == LoginActivity.REQUEST_LOGIN_CODE){
            if (resultCode == LoginActivity.LOGIN_SUCCESS)
                onButtonClicked(upload_button);
        }else super.onActivityResult(requestCode, resultCode, data);
    }
}