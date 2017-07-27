package com.xbw.unmanned;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xbw.unmanned.Utils.Config;
import com.xbw.unmanned.adapter.ListGoodsAdapter;
import com.xbw.unmanned.pay.AuthResult;
import com.xbw.unmanned.pay.PayResult;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xubowen on 2017/5/25.
 */
public class PayActivity extends AppCompatActivity {
    private ListView lis1;
    private Dialog dialog;
    private Button btn_pay;
    private Button btn_cancel;
    private ListGoodsAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        setTitle("账单结算");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        lis1 = (ListView) findViewById(R.id.listview_goods);
        btn_pay = (Button) findViewById(R.id.button);
        btn_cancel = (Button) findViewById(R.id.button1);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancleAlertDialog();
            }
        });
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                sendMsgToServerBySocket("1");
                Intent mIntent = new Intent();
                mIntent.setClass(PayActivity.this, PsucActivity.class);
                startActivity(mIntent);
                finish();
            }
        });
        initList();
    }

    /**
     * 初始化列表
     */
    private void initList() {
        mAdapter = new ListGoodsAdapter(this, Config.listGoods);
        lis1.setAdapter(mAdapter);
    }

    /**
     * 按两次返回退出
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            showCancleAlertDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理取消付款
     */
    private void showCancleAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否取消订单");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMsgToServerBySocket("0");//取消支付
                finish();
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        dialog.show();
    }

    /**
     * socket发送消息到服务器
     *
     * @param content
     * @return
     */
    public boolean sendMsgToServerBySocket(final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(Config.INTRA_COMMON_URL, Config.PORT);
                    OutputStream out = socket.getOutputStream();
                    out.write(content.getBytes(), 0, content.length());
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    /**
     * post方式向服务器发送信息
     *
     * @param msg
     */
    private void Post(final String msg) {
        RequestQueue mQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.INTRA_COMMON_URL + "Config.API", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("value", msg);
                return map;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Accept", "application/vnd.api+json");
                //headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Log.d("stauscode", response.statusCode + "");

                    String dataString = new String(response.data, "UTF-8");
                    Toast.makeText(PayActivity.this, dataString, Toast.LENGTH_LONG).show();
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(PayActivity.this, "未知错误", Toast.LENGTH_LONG).show();
                    return Response.error(new ParseError(e));
                }
            }
        };
        mQueue.add(stringRequest);
    }


    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2016082401792415";

    /**
     * 支付宝账户登录授权业务：入参pid值
     */
    public static final String PID = "2088512934649753";
    /**
     * 支付宝账户登录授权业务：入参target_id值
     */
    public static final String TARGET_ID = "2016082401792415";

    /**
     * 商户私钥，pkcs8格式
     */
    public static final String RSA_PRIVATE =
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOW/Luj5m/kQ8dBH" +
                    "g5PmAYPhNPeyRgdBiylEoLRRT9lqsvu9deEOeZ90y3se3CLzN1QLjUtqGZuQcGkp" +
                    "Z1BjLKq8Z2xxj23tw8PuQhm+CUz9nwE30tSFT5DAS/NVpy2aB6baEzjuLXBVas46" +
                    "oVHibya+Bj8w/uD6BeykgkAEAd8ZAgMBAAECgYBI3voPFVT+i/xXFsfKOnMSlHUL" +
                    "d7U0ifqMzWK3ru7HxEsfA/oGt2OmSxJba+9g+on7E2rul6mlqQuun15QphQ9PXIG" +
                    "pPLKFD5w8SSx1polrJ9wTb9QIZjubqU5rhwqEPr4ciTAKNVLsD9sfaYitPbf7CMu" +
                    "Z2iGkLY1+bVSDKYPXQJBAPsz9XHhpDP49eboP07QxuDAcHdvlvh5j59GmmZ6MSFO" +
                    "FK/VLqXs/WdwX3R56YsiNcoJKBxThcaYvkIWz+yxSF8CQQDqIlVBQMJvQ+/KUgVZ" +
                    "lTTCmKCTEl3kNn7ATM9BpDi9xt3HtFmybe+BYThdHt5e17yvuSCOa5AXserPl4rQ" +
                    "22uHAkEAgt24upFQl/rVMGxBvu+pDCF09ePehKffmJyRh12VBsLEqC2pVau3ABJc" +
                    "yDNS5bb2ysAmc0tX3/Rm+cskmyxohQJALV/7/8pqJCAZb69XpBXbhL8Jl5cepSS+" +
                    "icwY+pK2AGly/8n+/Cp8GSnbP4jE9cJ/qdqJ6bAE2+K6LAMlfvaXLwJBAKvsqlSz" +
                    "Al6M8u2+YxMxJ6UzZdhYgaaD1Pxz3jOQIcj69fzs9Tb7LuqpzRBR1To0rrLuGc8Q" +
                    "Q5BnrALHY3n2lqE=";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    }
                    //Post("1");
                    Intent mIntent = new Intent();
                    mIntent.setClass(PayActivity.this, PsucActivity.class);
                    startActivity(mIntent);
                    finish();
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(PayActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(PayActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


//    /**
//     * 支付方式2
//     *
//     * @param v
//     */
//    public void payV2(View v) {
//        if (TextUtils.isEmpty(APPID) || TextUtils.isEmpty(RSA_PRIVATE)) {
//            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialoginterface, int i) {
//                            finish();
//                        }
//                    }).show();
//            return;
//        }
//
//        /**
//         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
//         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
//         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
//         *
//         * orderInfo的获取必须来自服务端；
//         */
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, true);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//        String sign = OrderInfoUtil2_0.getSign(params, RSA_PRIVATE, true);
//        final String orderInfo = orderParam + "&" + sign;
//
//        Runnable payRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(PayActivity.this);
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                Log.i("msp", result.toString());
//
//                Message msg = new Message();
//                msg.what = SDK_PAY_FLAG;
//                msg.obj = result;
//                mHandler.sendMessage(msg);
//            }
//        };
//        Thread payThread = new Thread(payRunnable);
//        payThread.start();
//    }
}
