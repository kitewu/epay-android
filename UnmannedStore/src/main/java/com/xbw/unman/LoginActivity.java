package com.xbw.unman;

import com.xbw.unman.MainActivity;
import com.xbw.unman.R;
import com.xbw.unman.Utils.SharedPreference;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class LoginActivity extends Activity{
    private EditText mUsernameET;
    private EditText mPasswordET;
    private Button mSigninBtn;
    private Button mForget;
    private Button mSignupTV;

    private VideoView mVideoView;
    public static final String VIDEO_NAME = "welcome_video.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreference sharedpreference = new SharedPreference(this);
        boolean islogin = sharedpreference.isLogin(this.getClass()
                .getName());
        if(islogin){
            Intent mIntent = new Intent();
            mIntent.setClass(this, MainActivity.class);
            startActivity(mIntent);
            finish();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//沉浸式状态栏
        setContentView(R.layout.activity_login);
        initView();
        File videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists()) {
            videoFile = copyVideoFile();
        }
        playVideo(videoFile);
    }
    private void initView()
    {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mUsernameET = (EditText) findViewById(R.id.chat_login_username);
        mPasswordET = (EditText) findViewById(R.id.chat_login_password);
        mSigninBtn = (Button) findViewById(R.id.chat_login_signin_btn);
        mSignupTV = (Button) findViewById(R.id.chat_login_signup);
        mForget=(Button) findViewById(R.id.chat_forgot_password);
        mSigninBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                final String userName = mUsernameET.getText().toString().trim();
                final String password = mPasswordET.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(getApplicationContext(), "请输入用户名",
                            Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "请输入密码",
                            Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreference sharedpreference=new SharedPreference(LoginActivity.this);
                    sharedpreference.KeepLogin(mUsernameET.getText().toString());//写入保持登陆状态
                    Intent mIntent = new Intent();
                    mIntent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            }
        });
        mForget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,
                        ForgetpwdActivity.class));
            }
        });
        mSignupTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(LoginActivity.this,
                        RegisterActivity.class));
            }
        });
    }

    private void playVideo(File videoFile) {
        mVideoView.setVideoPath(videoFile.getPath());
        mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
    }

    @NonNull
    private File copyVideoFile() {
        File videoFile;
        try {
            FileOutputStream fos = openFileOutput(VIDEO_NAME, MODE_PRIVATE);
            InputStream in = getResources().openRawResource(R.raw.welcome_video);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists())
            throw new RuntimeException("video file has problem, are you sure you have welcome_video.mp4 in res/raw folder?");
        return videoFile;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

}
