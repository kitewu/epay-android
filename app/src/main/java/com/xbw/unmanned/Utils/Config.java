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
    //内网服务器地址
    public static final String INTRANET = "192.168.1.104";
    //内网web root地址
    public static final String INTRA_COMMON_URL = "http://" + INTRANET + ":80";

    //公网服务器地址
    public static final String EXTRANET = "115.159.49.73";
    //公网web root地址
    public static final String EXTRA_COMMON_URL = "http://" + EXTRANET + ":8080";

    public static final int PORT = 12348;
}
