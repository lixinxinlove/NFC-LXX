package com.lixinxin.nfcdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class NFCActivity extends AppCompatActivity {


    private NfcDao dao = null;

    private TextView mTvView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        dao = new NfcDao(this);
        initView();
    }

    private void initView() {
        mTvView = findViewById(R.id.nfc_activity_tv_info);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //当该Activity接收到NFC标签时，运行该方法
        //调用工具方法，读取到的NFC数据
        String str = "";
        String id = "";
        String data = "";

        if (dao == null) {
            dao = new NfcDao(this);
        }

        try {
            // str = dao.readFromTag(intent);
            // id = dao.readIdFromTag(intent);
            data = dao.readTag(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.e("NFCActivity", data);


        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

    /**
     * 开启检测,检测到卡后，onNewIntent() 执行
     * enableForegroundDispatch()只能在onResume() 方法中，否则会报：
     * Foreground dispatch can only be enabled when your activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();

        //开启前台调度系统
        NfcDao.mNfcAdapter.enableForegroundDispatch(this, NfcDao.mPendingIntent, NfcDao.mIntentFilter, NfcDao.mTechList);


        try {
            String str = dao.readFromTag(getIntent());
            String id = dao.readIdFromTag(getIntent());

            Log.e("NFC--LEE", str + "--" + id);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    /**
     * 关闭检测
     */
    @Override
    protected void onPause() {
        super.onPause();
        //关闭前台调度系统
        NfcDao.mNfcAdapter.disableForegroundDispatch(this);


//    public void onCheckNFC(View view) {
//
//        if (mNfcAdapter == null) {
//            Toast.makeText(this, "eeeeeeee手机不支持NCFeeeeee", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "--------手机支持NCF--------", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    public void onReadNfcData(View view) {
//
//        if (mNfcAdapter == null) {
//            Toast.makeText(this, "手机不支持NCF", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        if (mNfcAdapter.isEnabled() == false) {
//            Toast.makeText(this, "请打开NFC功能", Toast.LENGTH_SHORT).show();
//        } else {
//
//        }
//    }

    }
}
