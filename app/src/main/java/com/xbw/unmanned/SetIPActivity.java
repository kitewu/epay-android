package com.xbw.unmanned;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xbw.unmanned.Utils.Config;

/**
 * Created by xubowen on 2017/5/24.
 */
public class SetIPActivity extends Activity {
    private EditText mIPET;
    private Button mOKBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//沉浸式状态栏
        setContentView(R.layout.activity_setip);
        initView();
    }

    private void initView() {
        mIPET = (EditText) findViewById(R.id.interip);
        mOKBtn = (Button) findViewById(R.id.btn_interip_ok);
        mOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Config.INTRANETIP = mIPET.getText().toString().trim();
                Toast.makeText(SetIPActivity.this, "设置成功: " + Config.INTRANETIP, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
