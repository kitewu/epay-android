package com.xbw.unman;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amap.map3d.demo.poisearch.PoiAroundSearchActivity;
import com.xbw.scancode.SaoYiSao;
import com.xbw.unman.Utils.Config;
import com.xbw.unman.Utils.SharedPreference;
import com.xbw.unman.adapter.ListGoodsAdapter;
import com.xbw.unman.model.GoodsModel;
import com.xbw.webview.WebActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private  SharedPreference sharedpreference;
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
    private void initList(){
        GoodsModel model=new GoodsModel();
        model.setGoodsName("黑人牙膏");
        model.setGoodsPrice(9.99f);
        model.setGoodsImg("http://img.ecfun.cc/QQ20170525-011647@2x.png");
        Config.listGoods.add(model);

        GoodsModel model1=new GoodsModel();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            String result_value = intent.getStringExtra("describe");
            if(result_value!=null){
                //result_value;
                Intent mIntent = new Intent();
                mIntent.setClass(this, PayActivity.class);
                startActivity(mIntent);
            }
            return;
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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
        } else if (id == R.id.nav_shop) {
            Intent mIntent=new Intent();
            mIntent.setClass(MainActivity.this,WebActivity.class);
            mIntent.putExtra("url","http://www.baidu.com");
            startActivity(mIntent);
        } else if (id == R.id.nav_active) {

        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_quit) {
            sharedpreference.DisconnectLogin();
            Intent mIntent = new Intent();
            mIntent.setClass(this, LoginActivity.class);
            startActivity(mIntent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
