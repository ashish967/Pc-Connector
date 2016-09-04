package com.simplesocketprogramming.ashish_til.pccommunicator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ashish_til.pccommunicator.R;
import com.example.ashish_til.pccommunicator.databinding.ActivityHomeScreenBinding;
import com.simplesocketprogramming.SimpleFileClient;

import java.io.IOException;

public class HomeScreen extends Activity {


    private static final String TAG = HomeScreen.class.getSimpleName();
    ActivityHomeScreenBinding mBinding;



    BroadcastReceiver mReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message=ServerService.getMessage(intent);

            mBinding.tvDeviceInfo.append(message+"\n");

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);

        mBinding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleConnectAction();
            }
        });

       LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter(ServerService.SERVER_LOG_CHANGED));

    }


    @Override
    protected void onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void handleConnectAction() {

            Log.d(TAG,"handleConnectionAction");
            Intent intent= new Intent(this,ServerService.class);
            startService(intent);
    }



}
