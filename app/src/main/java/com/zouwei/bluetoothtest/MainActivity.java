package com.zouwei.bluetoothtest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.com.ut.protocol.port.IPortBase;

public class MainActivity extends AppCompatActivity implements IPortBase.OnPortStatusChangedListener{

    TextView tvBlueStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBlueStatus = findViewById(R.id.tv_blue_status);
        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothManager.connect(MainActivity.this);
                BluetoothManager.registerListener(null,null,null,null
                ,null,MainActivity.this,null,null,null);
            }
        });

        findViewById(R.id.disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothManager.close();
                BluetoothManager.unregisterListener();
            }
        });
    }

    @Override
    public void onPortStatusChanged(final boolean bState) {
        if(tvBlueStatus==null){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bState) {
                    tvBlueStatus.setBackgroundColor(Color.parseColor("#AA3FEEFC"));
                    tvBlueStatus.setText("蓝牙已连接");
                    tvBlueStatus.setVisibility(View.VISIBLE);
                } else {
                    tvBlueStatus.setBackgroundColor(Color.parseColor("#ddff2222"));
                    tvBlueStatus.setText("蓝牙已断开");
                }
            }
        });
    }
}
