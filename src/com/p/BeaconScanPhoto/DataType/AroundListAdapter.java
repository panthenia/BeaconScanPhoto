package com.p.BeaconScanPhoto.DataType;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lef.scanner.IBeacon;
import com.p.BeaconScanPhoto.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

public class AroundListAdapter extends RecyclerView.Adapter<AroundListAdapter.ViewHolder> {
    private ArrayList<IBeacon> mIBeaconDataset;
    private PublicData publicData;

    private volatile boolean iswork = false;
    private ReentrantLock dataLock = null;
    private int showType;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View myview;

        public ViewHolder(View v) {
            super(v);
            myview = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AroundListAdapter(Context context) {
        mIBeaconDataset = new ArrayList<IBeacon>();
        publicData = PublicData.getInstance();
        this.context = context;
        dataLock = new ReentrantLock();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AroundListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_new, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        dataLock.lock();
        if (position >= mIBeaconDataset.size()){
            dataLock.unlock();
            return;
        }
        IBeacon beacon = mIBeaconDataset.get(position);
        dataLock.unlock();
        TextView tv_major = (TextView) holder.myview.findViewById(R.id.tv_major_val);
        if (tv_major != null)
            tv_major.setText(String.valueOf(beacon.getMajor()));
        ImageView rssi_txt = (ImageView) holder.myview.findViewById(R.id.rssi_img);
        if (rssi_txt != null)
            rssi_txt.setImageResource(getRSSIView(beacon));
        TextView tv_minor = (TextView) holder.myview.findViewById(R.id.tv_minor_val);
        if (tv_minor != null)
            tv_minor.setText(String.valueOf(beacon.getMinor()));
        TextView tv_id = (TextView) holder.myview.findViewById(R.id.tv_id);
        if (tv_id != null) {
            tv_id.setText(beacon.getBluetoothAddress());
        }
        TextView tv_uuid = (TextView)holder.myview.findViewById(R.id.tv_uuid);
        if (tv_uuid != null) {
            tv_uuid.setText(beacon.getProximityUuid());
        }

        TextView tv_type = (TextView)holder.myview.findViewById(R.id.tv_cmp);
        if (tv_type != null) {
            tv_type.setText(beacon.getName());
        }
        ImageView upload_img = (ImageView) holder.myview.findViewById(R.id.upload_img);
        if(publicData.uploadBeaconSet.contains(beacon.getBluetoothAddress())){
            upload_img.setImageResource(R.drawable.upload);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mIBeaconDataset.size();
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

    public void updateData(Collection<IBeacon> data) {
        dataLock.lock();

        while (mIBeaconDataset.size() > 0){
            mIBeaconDataset.remove(0);
            notifyItemChanged(0);
        }
        mIBeaconDataset.addAll(data);
        dataLock.unlock();
    }

}