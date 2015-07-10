package com.p.BeaconScanPhoto.Activitis;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.p.BeaconScanPhoto.DataType.PublicData;
import com.p.BeaconScanPhoto.R;

/**
 * Created by p on 2015/7/10.
 */
public class BMapActivity extends Activity {
    public MapView mMapView = null;
    public BaiduMap mBaiduMap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmaplayout);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        if (mBaiduMap != null) {
            LatLng latLng = new LatLng(PublicData.getInstance().latitude, PublicData.getInstance().longitude);
            mBaiduMap.setMyLocationEnabled(true);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(PublicData.getInstance().radius)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(latLng.latitude)
                    .longitude(latLng.longitude).build();
            mBaiduMap.setMyLocationData(locData);
//            MarkerOptions options = new MarkerOptions();
//            Bitmap bitmap = createBeaconLocationBitmap(ibeancon.getBeaconNumber()+1);
//            BitmapDescriptor descriptor = null;
//            if (bitmap != null) {
//                descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
//                beaconIconHahsmap.put(ibeancon.getBluetoothAddress(),descriptor);
//            }
//            if (descriptor != null) {
//                options.position(latLng)
//                        .icon(descriptor).zIndex(5);
//                mBaiduMap.addOverlay(options);
//            }
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
        }
    }
}