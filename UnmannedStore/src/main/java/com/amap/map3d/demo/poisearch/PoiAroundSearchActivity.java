package com.amap.map3d.demo.poisearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.amap.map3d.demo.route.RouteActivity;
import com.xbw.unman.R;
import com.xbw.unman.Utils.Config;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
public class PoiAroundSearchActivity extends Activity implements OnClickListener, 
OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnMarkerClickListener, 
OnPoiSearchListener,AMapLocationListener,LocationSource{
	private MapView mapview;
	private AMap mAMap;

	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private Marker locationMarker; // 选择的点
	private Marker detailMarker;
	private Marker mlastMarker;
	private PoiSearch poiSearch;
	private myPoiOverlay poiOverlay;// poi图层
	private List<PoiItem> poiItems;// poi数据
	private PoiItem mPoi;
	
	private RelativeLayout mPoiDetail;
	private TextView mPoiName, mPoiAddress;
	private String keyWord = "";
	private EditText mSearchText;
	
	private AMapLocation lp;
	private LatLonPoint lps = new LatLonPoint(36.007033, 116.126987);
	private LatLonPoint lpl;
	private Marker location; // 选择的点
	private String judge;


	//声明AMapLocationClient类对象
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poiaroundsearch_activity);
		mapview = (MapView)findViewById(R.id.mapView);
		mapview.onCreate(savedInstanceState);


		judge = getIntent().getStringExtra("judge");
		init();
	}
	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO 自动生成的方法存根
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			// 设置定位监听
			mlocationClient.setLocationListener(this);
			// 设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			// 设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mlocationClient.startLocation();
		}
	}
	@Override
	public void deactivate() {
		// TODO 自动生成的方法存根
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mapview.getMap();
			mAMap.setOnMapClickListener(this);
			mAMap.setLocationSource(this);// 设置定位监听
			mAMap.setOnMarkerClickListener(this);
			mAMap.setOnInfoWindowClickListener(this);
			mAMap.setInfoWindowAdapter(this);
			mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
			mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
			// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
			mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
			TextView searchButton = (TextView) findViewById(R.id.btn_search);
			searchButton.setOnClickListener(this);
			if(Config.locs!=null){
				lp=Config.locs;
				lpl=new LatLonPoint(lp.getLatitude(), lp.getLongitude());
				locationMarker = mAMap.addMarker(new MarkerOptions()
				.anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory
						.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tips_1)))
				.position(new LatLng(lp.getLatitude(), lp.getLongitude())));
				locationMarker.showInfoWindow();
				mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 14));
				initdoSearchQuery("超市");
			}
		}
		UiSettings uiSettings = mAMap.getUiSettings();
		uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
		uiSettings.setScaleControlsEnabled(true);
		setup();
	}
	private void setup() {
		mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
		mPoiName = (TextView) findViewById(R.id.poi_name);
		mPoiAddress = (TextView) findViewById(R.id.poi_address);
		mSearchText = (EditText)findViewById(R.id.input_edittext);
		mPoiDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(judge.equals("true")){
					Intent mIntent = new Intent(PoiAroundSearchActivity.this,
							RouteActivity.class);
					 	mIntent.putExtra("endlatitude", location.getPosition().latitude+"");
				        mIntent.putExtra("endlongitude", location.getPosition().longitude+"");
				        if(Config.locs!=null){
				        	mIntent.putExtra("startlatitude", lp.getLatitude()+"");
					        mIntent.putExtra("startlongitude",lp.getLongitude()+"");
				        }else{
				        	mIntent.putExtra("startlatitude", lps.getLatitude()+"");
					        mIntent.putExtra("startlongitude",lps.getLongitude()+"");
				        }
					startActivity(mIntent);
				}else if(judge.equals("false")){

				}
			}
		});
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;

		switch (requestCode) {
			case 0:
				break;
		}
	}
	/**
	 * 开始进行poi搜索
	 */
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		keyWord = mSearchText.getText().toString().trim();
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lpl, 2000, true));//
			// 设置搜索区域为以lp点为圆心，其周围10000米范围
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}
	protected void initdoSearchQuery(String s) {
		keyWord = s;
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lpl, 2000, true));//
			// 设置搜索区域为以lp点为圆心，其周围10000米范围
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapview.onResume();
		whetherToShowDetailInfo(false);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
		if (null != mlocationClient) {
			mlocationClient.onDestroy();
		}

	}
	
	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPoiSearched(PoiResult result, int rcode) {
		if (rcode == 1000) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						//清除POI信息显示
						whetherToShowDetailInfo(false);
						//并还原点击marker样式
						if (mlastMarker != null) {
							resetlastmarker();
						}				
						//清理之前搜索结果的marker
						if (poiOverlay !=null) {
							poiOverlay.removeFromMap();
						}
						mAMap.clear();
						poiOverlay = new myPoiOverlay(mAMap, poiItems);
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
						mAMap.addMarker(new MarkerOptions()
						.anchor(0.5f, 0.5f)
						.icon(BitmapDescriptorFactory
								.fromBitmap(BitmapFactory.decodeResource(
										getResources(), R.drawable.point4)))
						.position(new LatLng(lp.getLatitude(), lp.getLongitude())));
							
						mAMap.addCircle(new CircleOptions()
						.center(new LatLng(lp.getLatitude(),
								lp.getLongitude())).radius(1000)
						.strokeColor(Color.BLUE)
						.fillColor(Color.argb(50, 1, 1, 1))
						.strokeWidth(2));

					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						ToastUtil.show(PoiAroundSearchActivity.this,
								R.string.no_result);
					}
				}
			} else {
				ToastUtil
						.show(PoiAroundSearchActivity.this, R.string.no_result);
			}
		}
	}


	@Override
	public boolean onMarkerClick(Marker marker) {
		location=marker;
		if (marker.getObject() != null) {
			whetherToShowDetailInfo(true);
			try {
				PoiItem mCurrentPoi = (PoiItem) marker.getObject();
				if (mlastMarker == null) {
					mlastMarker = marker;
				} else {
					// 将之前被点击的marker置为原来的状态
					if(keyWord.equals("超市")){
						jiayouresetlastmarker();
					}else{
						resetlastmarker();
					}
					mlastMarker = marker;
				}
				detailMarker = marker;
				detailMarker.setIcon(BitmapDescriptorFactory
									.fromBitmap(BitmapFactory.decodeResource(
											getResources(),
											R.drawable.poi_marker_pressed)));

				setPoiItemDisplayContent(mCurrentPoi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else {
			whetherToShowDetailInfo(false);
			resetlastmarker();
		}


		return true;
	}

	// 将之前被点击的marker置为原来的状态
	private void resetlastmarker() {
		int index = poiOverlay.getPoiIndex(mlastMarker);
		if (index < 10) {
			mlastMarker.setIcon(BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeResource(
							getResources(),
							markers[index])));
		}else {
			mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
			BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
		}
		mlastMarker = null;
		
	}
	// 将之前被点击的marker置为原来的状态
		private void jiayouresetlastmarker() {
			int index = poiOverlay.getPoiIndex(mlastMarker);
			if (index < 10) {
				mlastMarker.setIcon(BitmapDescriptorFactory
						.fromBitmap(BitmapFactory.decodeResource(
								getResources(),
								R.drawable.navi_along_search_parking_big_icon)));
			}else {
				mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
			}
			mlastMarker = null;
			
		}

	private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
		mPoiName.setText(mCurrentPoi.getTitle());
		mPoiAddress.setText(mCurrentPoi.getSnippet());
	}


	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			doSearchQuery();
			break;

		default:
			break;
		}
		
	}
	
	private int[] markers = {R.drawable.poi_marker_1,
			R.drawable.poi_marker_2,
			R.drawable.poi_marker_3,
			R.drawable.poi_marker_4,
			R.drawable.poi_marker_5,
			R.drawable.poi_marker_6,
			R.drawable.poi_marker_7,
			R.drawable.poi_marker_8,
			R.drawable.poi_marker_9,
			R.drawable.poi_marker_10
			};
	
	private void whetherToShowDetailInfo(boolean isToShow) {
		if (isToShow) {
			mPoiDetail.setVisibility(View.VISIBLE);

		} else {
			mPoiDetail.setVisibility(View.GONE);

		}
	}


	@Override
	public void onMapClick(LatLng arg0) {
		whetherToShowDetailInfo(false);
		if (mlastMarker != null) {
			resetlastmarker();
		}
	}
	
	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(this, infomation);

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
// TODO 自动生成的方法存根
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getErrorCode() == 0) {
				// mLocationErrText.setVisibility(View.GONE);
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				if(Config.locs==null){
					Config.locs = amapLocation;
					lp=Config.locs;
					lpl=new LatLonPoint(lp.getLatitude(), lp.getLongitude());
					initdoSearchQuery("超市");

				}
				//mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 14));
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode() + ": "
						+ amapLocation.getErrorInfo();
				Log.e("AmapErr", errText);
				Toast.makeText(this, errText, Toast.LENGTH_LONG).show();
			}
		}

	}


	/**
	 * 自定义PoiOverlay
	 *
	 */
	
	private class myPoiOverlay {
		private AMap mamap;
		private List<PoiItem> mPois;
	    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
		public myPoiOverlay(AMap amap ,List<PoiItem> pois) {
			mamap = amap;
	        mPois = pois;
		}

	    /**
	     * 添加Marker到地图中。
	     * @since V2.1.0
	     */
	    public void addToMap() {
	        for (int i = 0; i < mPois.size(); i++) {
	            Marker marker = mamap.addMarker(getMarkerOptions(i));
	            PoiItem item = mPois.get(i);
				marker.setObject(item);
	            mPoiMarks.add(marker);
	        }
	    }

	    /**
	     * 去掉PoiOverlay上所有的Marker。
	     *
	     * @since V2.1.0
	     */
	    public void removeFromMap() {
	        for (Marker mark : mPoiMarks) {
	            mark.remove();
	        }
	    }

	    /**
	     * 移动镜头到当前的视角。
	     * @since V2.1.0
	     */
	    public void zoomToSpan() {
	        if (mPois != null && mPois.size() > 0) {
	            if (mamap == null)
	                return;
	            LatLngBounds bounds = getLatLngBounds();
	            mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
	        }
	    }

	    private LatLngBounds getLatLngBounds() {
	        LatLngBounds.Builder b = LatLngBounds.builder();
	        for (int i = 0; i < mPois.size(); i++) {
	            b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
	                    mPois.get(i).getLatLonPoint().getLongitude()));
	        }
	        return b.build();
	    }

	    private MarkerOptions getMarkerOptions(int index) {
	        return new MarkerOptions()
	                .position(
	                        new LatLng(mPois.get(index).getLatLonPoint()
	                                .getLatitude(), mPois.get(index)
	                                .getLatLonPoint().getLongitude()))
	                .title(getTitle(index)).snippet(getSnippet(index))
	                .icon(getBitmapDescriptor(index));
	    }

	    protected String getTitle(int index) {
	        return mPois.get(index).getTitle();
	    }

	    protected String getSnippet(int index) {
	        return mPois.get(index).getSnippet();
	    }

	    /**
	     * 从marker中得到poi在list的位置。
	     *
	     * @param marker 一个标记的对象。
	     * @return 返回该marker对应的poi在list的位置。
	     * @since V2.1.0
	     */
	    public int getPoiIndex(Marker marker) {
	        for (int i = 0; i < mPoiMarks.size(); i++) {
	            if (mPoiMarks.get(i).equals(marker)) {
	                return i;
	            }
	        }
	        return -1;
	    }

	    /**
	     * 返回第index的poi的信息。
	     * @param index 第几个poi。
	     * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
	     * @since V2.1.0
	     */
	    public PoiItem getPoiItem(int index) {
	        if (index < 0 || index >= mPois.size()) {
	            return null;
	        }
	        return mPois.get(index);
	    }

		protected BitmapDescriptor getBitmapDescriptor(int arg0) {		
			if (arg0 < 10) {
				BitmapDescriptor icon=null;
				if(keyWord.equals("超市")){
					 icon= BitmapDescriptorFactory.fromBitmap(
							BitmapFactory.decodeResource(getResources(), R.drawable.navi_along_search_parking_big_icon));
				}else{
					icon= BitmapDescriptorFactory.fromBitmap(
							BitmapFactory.decodeResource(getResources(), markers[arg0]));
				}
				return icon;
				
			}else {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
						BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
				return icon;
			}	
		}
	}

}
