package com.p.BeaconScanPhoto.Activitis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lxr.overflot.OverlayIcon;
import com.lxr.overflot.OverlayLayout;
import com.p.BeaconScanPhoto.DataType.PublicData;
import com.p.BeaconScanPhoto.R;
import com.wxq.draw.MapControler;

/**
 * Created by p on 2015/1/29.
 */
public class MapActivity extends Activity {
    MapControler mapLayout;

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCoordx() {
        return coordx;
    }

    public void setCoordx(String coordx) {
        this.coordx = coordx;
    }

    public String getCoordy() {
        return coordy;
    }

    public void setCoordy(String coordy) {
        this.coordy = coordy;
    }

    String building, floor, coordx, coordy;
    float newcx;
    boolean newLocSeted = false;
    public float getNewcy() {
        return newcy;
    }

    public void setNewcy(float newcy) {
        this.newcy = newcy;
    }

    public float getNewcx() {
        return newcx;
    }

    public void setNewcx(float newcx) {
        this.newcx = newcx;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    String mac;
    float newcy;
    boolean located = false;
    public String getNewBuilding() {
        return newBuilding;
    }

    public void setNewBuilding(String newBuilding) {
        this.newBuilding = newBuilding;
    }

    String newBuilding;

    public String getNewFloor() {
        return newFloor;
    }

    public void setNewFloor(String newFloor) {
        this.newFloor = newFloor;
    }

    String newFloor;
    OverlayIcon overlaypointer;

    public String getBeaconDescription() {
        return beaconDescription;
    }

    public void setBeaconDescription(String beaconDescription) {
        this.beaconDescription = beaconDescription;
    }

    String beaconDescription;
    private OverlayLayout overlaylayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String macd = "Beacon";
        if (intent.hasExtra("mac")) {
            setMac(intent.getStringExtra("mac"));
            macd = mac;
        }
        getActionBar().setTitle("标记"+macd+"的位置");
        if (intent.hasExtra("loc")){
            located = true;
            setBuilding(intent.getStringExtra("building"));
            setFloor(intent.getStringExtra("floor"));
            setCoordx(intent.getStringExtra("coordx"));
            setCoordy(intent.getStringExtra("coordy"));
        }
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor_norock));
        setContentView(R.layout.map_activity);


        mapLayout = (MapControler) findViewById(R.id.mapLayout);
        if (mapLayout == null || !mapLayout.isSuccess()) {
            finish();
        }

        initNewLocationView();
    }

    private void initNewLocationView() {
        overlaypointer = new OverlayIcon(mapLayout, R.drawable.loc_pointer,
                0.8f, false, -1);
        overlaylayout = new OverlayLayout(mapLayout,
                R.layout.navigation_overlay_modify, 2);
        Button btn = (Button) overlaylayout.layoutView
                .findViewById(R.id.bt_locates);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mapLayout.getFloatView().clear();
                newLocSeted = true;
                //Log.d(getClass().getName(), "draw a circle");
                setNewBuilding(mapLayout.getDbfile());
                setNewFloor(mapLayout.getFloor());
                setNewcx(overlaylayout.MapCoord.x);
                setNewcy(overlaylayout.MapCoord.y);
                drawLocation(overlaylayout.MapCoord.x, overlaylayout.MapCoord.y);

            }
        });

        mapLayout.setMapclicklistener(new MapControler.IMapClickListener() {

            @Override
            public void mapClicked(float arg0, float arg1) {
                // TODO Auto-generated method stub
                overlaypointer.pinAtMapWithScreenCoord(new PointF(arg0, arg1));
                overlaylayout.pinAtMapWithScreenCoord(new PointF(arg0, arg1));
            }

            @Override
            public void mapChanged() {
                // TODO Auto-generated method stub
                // String newdb=mapLayout.getDbfile();
                // if(MallDBpath!=null && MallDBpath.equals(newdb))
                // return ;
                // MallDBpath=newdb;
                // DrawDBTool dbTool = new DrawDBTool(MainActivity.this);
                // dbTool.setDBName(MallDBpath);
                // mallName = dbTool.getMallName();
                // setTitle(mallName);
            }

            @Override
            public void floorChanged() {
                // TODO Auto-generated method stub

            }
        });
    }

    private void drawLocation(float x, float y) {
        Paint paintEmptyB = new Paint();
        paintEmptyB.setStyle(Paint.Style.FILL);
        paintEmptyB.setColor(Color.BLUE);
        paintEmptyB.setAlpha(20);

        Paint paintEmptyc = new Paint();
        paintEmptyc.setStyle(Paint.Style.FILL);
        paintEmptyc.setColor(Color.BLACK);


        Paint paintCricleB = new Paint();
        paintCricleB.setStyle(Paint.Style.STROKE);
        paintCricleB.setColor(Color.BLUE);

        mapLayout.getFloatView().addCircle("ha",
                x,
                y,
                5,
                paintCricleB);

        mapLayout.getFloatView()
                .addCircle("hah",
                        x,
                        y,
                        5,
                        paintEmptyB);
        mapLayout.getFloatView()
                .addCircle("h",
                        x,
                        y,
                        1,
                        paintEmptyc);
    }
    public void onChangeMap(View v) {
        if(getBuilding() == null || getFloor() == null){
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("该beacon尚未标记地图位置")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create().show();
        }else{
            try {
                if(getBuilding().length() > 0 && getFloor().length() > 0) {
                    mapLayout.changedbmap(getBuilding(), getFloor());
                    drawLocation(coordx, coordy);
                }else{
                    Toast.makeText(this,"无此beacon位置信息",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(this,"无此beacon位置信息",Toast.LENGTH_SHORT).show();
            }

            /*FloatView fView=mapLayout.getFloatView();
            Paint p=new Paint();
            p.setColor(Color.RED);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            fView.addCircle("hah", Float.valueOf(coordx), Float.valueOf(coordy), 1, p);
            */

        }
    }
    private void drawLocation(String x, String y) {
        Paint paintEmptyB = new Paint();
        paintEmptyB.setStyle(Paint.Style.FILL);
        paintEmptyB.setColor(Color.BLUE);
        paintEmptyB.setAlpha(20);

        Paint paintEmptyc = new Paint();
        paintEmptyc.setStyle(Paint.Style.FILL);
        paintEmptyc.setColor(Color.BLACK);


        Paint paintCricleB = new Paint();
        paintCricleB.setStyle(Paint.Style.STROKE);
        paintCricleB.setColor(Color.BLUE);

        try{
            mapLayout.getFloatView().addCircle("ha",
                    Float.valueOf(x),
                    Float.valueOf(y),
                    5,
                    paintCricleB);

            mapLayout.getFloatView()
                    .addCircle("hah",
                            Float.valueOf(x),
                            Float.valueOf(y),
                            5,
                            paintEmptyB);
            mapLayout.getFloatView()
                    .addCircle("h",
                            Float.valueOf(x),
                            Float.valueOf(y),
                            1,
                            paintEmptyc);
        }catch (Exception e){
            Toast.makeText(this,"无该beacon位置信息",Toast.LENGTH_SHORT).show();
        }
    }

    public void onSaveMap(View v) {
        String msg = "选择的位置为：\nbuilding:%s\nfloor:%s\nx:%s\ny:%s";
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(String.format(msg,newBuilding,newFloor,newcx,newcy))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        if (newLocSeted) {
                            intent.putExtra("bd", getNewBuilding());
                            intent.putExtra("fl", getNewFloor());
                            intent.putExtra("nx", newcx);
                            intent.putExtra("ny", newcy);
                            setResult(0,intent);
                            MapActivity.this.finish();
                        } else {
                            Toast.makeText(MapActivity.this,"尚未选择位置！",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create().show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}