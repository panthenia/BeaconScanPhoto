package com.p.BeaconScanPhoto.DataType;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lef.scanner.IBeacon;
import com.p.BeaconScanPhoto.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by p on 2015/4/2.
 *
 * Beacon列表适配器
 */
public class BeaconListAdapter extends BaseAdapter {
    Context context = null;
    private ArrayList<IBeacon> mIBeaconDataset;
    LayoutInflater inflater = null;
    private ReentrantLock dataLock = null;
    public BeaconListAdapter(Context ctx){
        this.context = ctx;
        mIBeaconDataset = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        dataLock = new ReentrantLock();
    }
    public void updateData(Collection<IBeacon> data) {
        dataLock.lock();
        mIBeaconDataset.clear();
        mIBeaconDataset.addAll(data);
        dataLock.unlock();
    }
    public void setData(Collection<DBIbeancon> data){
        mIBeaconDataset.clear();
        mIBeaconDataset.addAll(data);
    }


    @Override
    public int getCount() {
        return mIBeaconDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return mIBeaconDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHoldler holder;
        dataLock.lock();
        if (position >= mIBeaconDataset.size()){
            dataLock.unlock();

            return inflater.inflate(R.layout.list_item_new,null);
        }
        IBeacon beacon = mIBeaconDataset.get(position);
        dataLock.unlock();
        //回收旧的view
        if (convertView != null){
            holder = (ViewHoldler) convertView.getTag();
            if (holder.major != null)
                holder.major.setText(String.valueOf(beacon.getMajor()));

            if (holder.rssi != null)
                holder.rssi.setText(String.valueOf(beacon.getRssi()));

            if (holder.minor != null)
                holder.minor.setText(String.valueOf(beacon.getMinor()));

            if (holder.mac != null) {
                if (PublicData.getInstance().uploadBeaconSet.contains(beacon.getBluetoothAddress())){
                    holder.mac.setTextColor(Color.GREEN);
                }else if(PublicData.getInstance().checkBeaconSet.contains(beacon.getBluetoothAddress())){
                    holder.mac.setTextColor(Color.RED);
                }else{
                    holder.mac.setTextColor(Color.argb(0xff,0x01,0x80,0xd5));
                }
                holder.mac.setText(beacon.getBluetoothAddress());
            }

            if (holder.uuid != null) {
                holder.uuid.setText(beacon.getProximityUuid());
            }

            if (holder.name != null) {
                holder.name.setText(beacon.getName());
            }
        }else{
            convertView = inflater.inflate(R.layout.list_item_new,null);
            holder = new ViewHoldler();
            holder.major = (TextView) convertView.findViewById(R.id.tv_major_val);
            holder.rssi = (TextView) convertView.findViewById(R.id.rssi_img);
            holder.minor = (TextView) convertView.findViewById(R.id.tv_minor_val);
            holder.mac = (TextView) convertView.findViewById(R.id.tv_id);
            holder.uuid = (TextView)convertView.findViewById(R.id.tv_uuid);
            holder.name = (TextView)convertView.findViewById(R.id.tv_cmp);
            holder.upload = (ImageView) convertView.findViewById(R.id.upload_img);
            if (PublicData.getInstance().uploadBeaconSet.contains(beacon.getBluetoothAddress())){
                holder.mac.setTextColor(Color.GREEN);
            }else if(PublicData.getInstance().checkBeaconSet.contains(beacon.getBluetoothAddress())){
                holder.mac.setTextColor(Color.RED);
            }
            if (holder.major != null)
                holder.major.setText(String.valueOf(beacon.getMajor()));

            if (holder.rssi != null)
                holder.rssi.setText(String.valueOf(beacon.getRssi()));

            if (holder.minor != null)
                holder.minor.setText(String.valueOf(beacon.getMinor()));

            if (holder.mac != null) {
                holder.mac.setText(beacon.getBluetoothAddress());
            }

            if (holder.uuid != null) {
                holder.uuid.setText(beacon.getProximityUuid());
            }

            if (holder.name != null) {
                holder.name.setText(beacon.getName());
            }

            convertView.setTag(holder);
        }

        return convertView;
    }
    private int getRSSIView(IBeacon beacon) {
        if (beacon.getRssi() <= -110) {
            return R.drawable.icon_rssi1;
        } else if (beacon.getRssi() <= -100) {
            return R.drawable.icon_rssi2;
        } else if (beacon.getRssi() <= -90) {
            return R.drawable.icon_rssi3;
        } else if (beacon.getRssi() <= -80) {
            return R.drawable.icon_rssi4;
        } else if (beacon.getRssi() <= -70) {
            return R.drawable.icon_rssi5;
        } else if (beacon.getRssi() > -70) {
            return R.drawable.icon_rssi6;
        }
        return R.drawable.icon_rssi1;
    }
    class ViewHoldler{
        TextView major = null,minor = null,uuid = null,name = null,mac = null,rssi = null;
        ImageView upload = null;
    }
}
