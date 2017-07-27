package com.xbw.unmanned;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.map3d.demo.poisearch.PoiAroundSearchActivity;
import com.xbw.scancode.SaoYiSao;
import com.xbw.unmanned.Utils.Config;
import com.xbw.unmanned.Utils.SharedPreference;
import com.xbw.unmanned.adapter.ListGoodsAdapter;
import com.xbw.unmanned.model.GoodsModel;
import com.xbw.webview.WebActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreference sharedpreference;
    private ListView lis1;
    private ListGoodsAdapter mAdapter;
    SwipeRefreshLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("无人商店");
        toolbar.setTitleTextColor(Color.BLACK);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sharedpreference = new SharedPreference(this);
        layout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayouts);
        lis1 = (ListView) findViewById(R.id.listview_goods);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
            }
        });
        initList();
    }

    private void initList() {
        GoodsModel model = new GoodsModel();
        model.setGoodsName("黑人牙膏");
        model.setGoodsPrice(9.99f);
        model.setGoodsImg("http://img.ecfun.cc/QQ20170525-011647@2x.png");
        Config.listGoods.add(model);

        GoodsModel model1 = new GoodsModel();
        model1.setGoodsName("怡宝矿泉水");
        model1.setGoodsPrice(2.00f);
        model1.setGoodsImg("http://img.ecfun.cc/QQ20170525-011728@2x.png");
        Config.listGoods.add(model1);
        mAdapter = new ListGoodsAdapter(this,
                Config.listGoods);
        lis1.setAdapter(mAdapter);
        layout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_scan) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SaoYiSao.class);
            startActivityForResult(intent, 1001);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == 1001) {
//            String result_value = intent.getStringExtra("describe");
//            if (result_value != null) {
//                Intent mIntent = new Intent();
//                mIntent.setClass(this, PayActivity.class);
//                startActivity(mIntent);
//            }
            Toast.makeText(MainActivity.this, "绑定成功", Toast.LENGTH_LONG).show();
            Intent mIntent = new Intent();
            mIntent.setClass(MainActivity.this, WebActivity.class);
            mIntent.putExtra("url", Config.INTRA_COMMON_URL + "/epay/index.html");
            startActivity(mIntent);
            return;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_near) {
            Intent mIntent = new Intent();
            mIntent.setClass(this, PoiAroundSearchActivity.class);
            mIntent.putExtra("judge", "true");
            startActivity(mIntent);
        } else if (id == R.id.nav_scan) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SaoYiSao.class);
            startActivityForResult(intent, 1001);
        } else if (id == R.id.nav_shop) {//购买记录
            Intent mIntent = new Intent();
            mIntent.setClass(MainActivity.this, WebActivity.class);
            mIntent.putExtra("url", "http://" + Config.EXTRANETIP + ":8080/historyformobile");
            startActivity(mIntent);
        } else if (id == R.id.nav_shop_car) {//购物车
            Intent mIntent = new Intent();
            mIntent.setClass(MainActivity.this, WebActivity.class);
           // Toast.makeText(MainActivity.this, "http://" + Config.INTRANETIP + ":80/epay/index.html", Toast.LENGTH_LONG).show();
            mIntent.putExtra("url", "http://" + Config.INTRANETIP + ":80/epay/index.html");
            startActivity(mIntent);
        } else if (id == R.id.nav_active) {//积分活动
            Intent mIntent = new Intent();
            mIntent.setClass(MainActivity.this, PsucActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_account) {//账户

        } else if (id == R.id.nav_about) {//关于

        } else if (id == R.id.nav_quit) {//注销
            sharedpreference.DisconnectLogin();
            Intent mIntent = new Intent();
            mIntent.setClass(this, LoginActivity.class);
            startActivity(mIntent);
            finish();
        } else if (id == R.id.nav_setip) {//设置ip
            Intent mIntent = new Intent();
            mIntent.setClass(MainActivity.this, SetIPActivity.class);
            startActivity(mIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
