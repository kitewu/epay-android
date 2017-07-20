package com.xbw.unmanned.Utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.xbw.unmanned.model.GoodsModel;

import java.util.ArrayList;
import java.util.List;

public class Config {


    public static AMapLocation locs = null;
    public static NaviLatLng start_nav = null;
    public static NaviLatLng end_nav = null;
    public static List<GoodsModel> listGoods = new ArrayList<GoodsModel>();

    //public static String IP = "http://192.168.0.192:8181";
    public static String API = "";
    public static final int PORT = 8181;
    public static final String IP = "192.168.0.192";
}
