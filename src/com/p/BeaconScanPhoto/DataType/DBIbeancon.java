package com.p.BeaconScanPhoto.DataType;

import android.graphics.Bitmap;
import com.lef.scanner.IBeacon;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by p on 2015/1/23.
 */
public class DBIbeancon extends IBeacon {
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


    public String getSumury() {
        return sumury;
    }

    public void setSumury(String sumury) {
        this.sumury = sumury;
    }
    public void setMac(String mac){
        super.bluetoothAddress = mac;
    }
    public String getMac(){
        return super.getBluetoothAddress();
    }
    public void setMajor(int major){
        super.setMajor(major);
    }
    public int getMajor(){
        return super.getMajor();
    }
    public void setMinor(int minor){
        super.setMinor(minor);
    }
    public int getMinor(){
        return super.getMinor();
    }
    public void setUuid(String uuid){
        super.proximityUuid = uuid;
    }
    public String getUuid(){
        return super.getProximityUuid();
    }
    public void setName(String name){
        super.name = name;
    }
    public String getName(){
        return super.getName();
    }
    public void setRssi(int rssi){
        super.rssi = rssi;
    }
    public int getRssi(){
        return super.rssi;
    }
    private String detail,sumury;

    public boolean getLocationType() {
        return locationType;
    }

    public void setLocationType(boolean locationType) {
        this.locationType = locationType;
    }
    //¼ÇÂ¼beaconÍ¼Æ¬µÄÃû
    private ArrayList<String> imgs = new ArrayList<>();
    public ArrayList<String> getImgs(){
        return imgs;
    }
    public String getImgNames(){
        if (imgs.size() > 0){
            String result = imgs.get(0);
            for (int i=1 ;i<imgs.size();++i){
                result += ("|"+imgs.get(i));
            }
            return result;
        }else return "";
    }
    private boolean locationType;
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    private double lat,lon;

    public String getBuilding() {
        return this.building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    private String building,floor;
    public DBIbeancon(IBeacon beacon){
        super.bluetoothAddress = beacon.getBluetoothAddress();
        super.proximityUuid = beacon.getProximityUuid();
        super.major = beacon.getMajor();
        super.minor = beacon.getMinor();
        super.rssi = beacon.getRssi();
        super.name = beacon.getName();
    }
    public DBIbeancon(){

    }

}
