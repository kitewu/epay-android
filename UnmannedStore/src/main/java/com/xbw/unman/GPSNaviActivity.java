package com.xbw.unman;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.xbw.unman.Utils.Config;
import com.xbw.unman.Utils.TTSController;

public class GPSNaviActivity extends Activity implements AMapNaviListener,
		AMapNaviViewListener {
	AMapNaviView mAMapNaviView;
	AMapNavi mAMapNavi;
	TTSController mTtsManager;
	NaviLatLng mEndLatlng = Config.end_nav;
	NaviLatLng mStartLatlng = Config.start_nav;
	List<NaviLatLng> mStartList = new ArrayList<NaviLatLng>();
	List<NaviLatLng> mEndList = new ArrayList<NaviLatLng>();
	List<NaviLatLng> mWayPointList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic_navi);
		mTtsManager = TTSController.getInstance(getApplicationContext());
		mTtsManager.init();
		mTtsManager.startSpeaking();
		mStartList.add(mStartLatlng);
		mEndList.add(mEndLatlng);
		mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
		mAMapNaviView.onCreate(savedInstanceState);
		mAMapNaviView.setAMapNaviViewListener(this);
		//从单例获取
		mAMapNavi = AMapNavi.getInstance(getApplicationContext());
		mAMapNavi.addAMapNaviListener(this);
		mAMapNavi.addAMapNaviListener(mTtsManager);
		noStartCalculate();
		/*if (!mAMapNavi.isGpsReady()) {
			AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
			// 有起点算路
			mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,
					PathPlanningStrategy.DRIVING_DEFAULT);
			Toast.makeText(this, "GPS正常", 8000).show();
		} else {
			AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
			AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
			mAMapNavi.calculateDriveRoute(mEndList, mWayPointList,
					PathPlanningStrategy.DRIVING_DEFAULT);
			Toast.makeText(this, "GPS信号弱", 8000).show();
		}*/

	}
	@SuppressLint("ShowToast") @SuppressWarnings("deprecation")
	private void noStartCalculate() {
		// 无起点算路须知：
		// AMapNavi在构造的时候，会startGPS，但是GPS启动需要一定时间
		// 在刚构造好AMapNavi类之后立刻进行无起点算路，会立刻返回false
		// 给人造成一种等待很久，依然没有算路成功 算路失败回调的错觉
		// 因此，建议，提前获得AMapNavi对象实例，并判断GPS是否准备就绪
		if (mAMapNavi.isGpsReady()){
			AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
			mAMapNavi.calculateDriveRoute(mStartList,mEndList, mWayPointList,
					PathPlanningStrategy.DRIVING_DEFAULT);
			Toast.makeText(this, "GPS正常", 8000).show();
		}else{
			AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
			AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
			mAMapNavi.calculateDriveRoute(mStartList,mEndList, mWayPointList,
					PathPlanningStrategy.DRIVING_DEFAULT);
			Toast.makeText(this, "GPS信号弱", 8000).show();
		}

	}

	@Override
	public void onCalculateRouteSuccess() {
		mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mAMapNaviView.onResume();
		//mAMapNavi = Singleton.getInstance().getmAMapNavi();
		mStartList.add(mStartLatlng);
		mEndList.add(mEndLatlng);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAMapNaviView.onPause();
		mTtsManager.stopSpeaking();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAMapNaviView.onDestroy();
		mAMapNavi.stopNavi();
		mAMapNavi.destroy();
		mTtsManager.destroy();
	}

	@Override
	public void onInitNaviFailure() {
		Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInitNaviSuccess() {
		mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,
				PathPlanningStrategy.DRIVING_DEFAULT);
	}

	@Override
	public void onNaviViewLoaded() {
		Log.d("wlx", "导航页面加载成功");
		Log.d("wlx",
				"请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑");
	}

	/**
	 * 导航界面左下角返回按钮回调
	 *
	 */
	public void onNaviCancel() {
		finish();
	}

	@Override
	public void onLockMap(boolean arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public boolean onNaviBackClick() {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onNaviSetting() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onNaviTurnClick() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onNextRoadClick() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void hideCross() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void hideLaneInfo() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void notifyParallelRoad(int arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onArriveDestination() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onCalculateMultipleRoutesSuccess(int[] arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void showCross(AMapNaviCross arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void updateAimlessModeStatistics(AimLessModeStat arg0) {
		// TODO 自动生成的方法存根

	}
}
/*
 * package com.example.matrix;
 *
 * import android.os.Bundle;
 *
 * import com.amap.api.navi.AMapNavi; import com.amap.api.navi.AMapNaviView;
 * import com.amap.api.navi.enums.NaviType; import
 * com.amap.api.navi.enums.PathPlanningStrategy;
 *
 *
 * public class GPSNaviActivity extends BaseActivity {
 *
 *
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 *
 * setContentView(R.layout.activity_basic_navi); mAMapNaviView = (AMapNaviView)
 * findViewById(R.id.navi_view); mAMapNaviView.onCreate(savedInstanceState);
 * mAMapNaviView.setAMapNaviViewListener(this); }
 *
 * private void noStartCalculate() { //无起点算路须知：
 * //AMapNavi在构造的时候，会startGPS，但是GPS启动需要一定时间
 * //在刚构造好AMapNavi类之后立刻进行无起点算路，会立刻返回false //给人造成一种等待很久，依然没有算路成功 算路失败回调的错觉
 * //因此，建议，提前获得AMapNavi对象实例，并判断GPS是否准备就绪
 * 
 * 
 * if (mAMapNavi.isGpsReady()) mAMapNavi.calculateDriveRoute(mEndList,
 * mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT); }
 * 
 * 
 * @SuppressWarnings("deprecation")
 * 
 * @Override public void onCalculateRouteSuccess() {
 * mAMapNavi.startNavi(AMapNavi.GPSNaviMode); } java.lang.RuntimeException:
 * Unable to start activity
 * ComponentInfo{com.example.matrix/com.example.matrix.GPSNaviActivity}:
 * java.lang.NullPointerException at
 * android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2306) at
 * android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2364) at
 * android.app.ActivityThread.access$900(ActivityThread.java:162) at
 * android.app.ActivityThread$H.handleMessage(ActivityThread.java:1266) at
 * android.os.Handler.dispatchMessage(Handler.java:102) at
 * android.os.Looper.loop(Looper.java:157) at
 * android.app.ActivityThread.main(ActivityThread.java:5387) at
 * java.lang.reflect.Method.invokeNative(Native Method) at
 * java.lang.reflect.Method.invoke(Method.java:515) at
 * com.android.internal.os.ZygoteInit$MethodAndArgsCaller
 * .run(ZygoteInit.java:1265) at
 * com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1081) at
 * dalvik.system.NativeStart.main(Native Method) Caused by:
 * java.lang.NullPointerException at
 * com.example.matrix.GPSNaviActivity.onCreate(GPSNaviActivity.java:50) at
 * android.app.Activity.performCreate(Activity.java:5431) at
 * android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1105)
 * at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2270)
 * ... 11 more
 * 
 * 
 * }
 */
