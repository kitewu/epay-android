package com.xbw.unman.Utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.xbw.unman.model.GoodsModel;

import java.util.ArrayList;
import java.util.List;

public class Config {

	public static AMapLocation locs = null;
	public static NaviLatLng start_nav = null;
	public static NaviLatLng end_nav = null;
	public static List<GoodsModel> listGoods=new ArrayList<GoodsModel>();

	public static String IP="http://192.168.0.192:8181";
	public static String API="";
}
