package com.p.BeaconScanPhoto.Activitis;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.lef.scanner.IBeacon;
import com.p.BeaconScanPhoto.DataType.BeaconListAdapter;
import com.p.BeaconScanPhoto.DataType.PublicData;
import com.p.BeaconScanPhoto.R;

/**
 * Created by p on 2015/7/1.
 */
public class ShowBeaconActivity extends Activity {
    private ListView beaconListView = null;
    private static BeaconListAdapter beaconAdaptor = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_beacon);
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
        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IBeacon beacon = (IBeacon) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(ShowBeaconActivity.this, MoreBeaconInfo.class);
                intent.putExtra("old",true);
                intent.putExtra("mac", beacon.getBluetoothAddress());
                startActivity(intent);
            }
        });
    }

}