package com.simplesocketprogramming.ashish_til.pccommunicator;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Formatter;
import android.util.Log;

import com.simplesocketprogramming.ClientConnectionHandler;
import com.simplesocketprogramming.ConnectionListener;
import com.simplesocketprogramming.SimpleServer;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by ashish-til on 3/9/16.
 */
public class ServerService extends IntentService {


    private static final String SERVER_RESPONSE = "RESPONSE";
    public static final String SERVER_LOG_CHANGED = "SERVER_LOG_CHANGED";
    private static final String TAG = ServerService.class.getSimpleName();

    public ServerService(String name) {
        super(name);
    }

    public ServerService(){
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG,"onHandleIntent");

        try {
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

            String ipAddr =  Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            SimpleServer server = SimpleServer.createSimpleServer(ipAddr,getHomeDir(),new ConnectionListener() {
                @Override
                public void log(String message) {

                    Log.d(TAG,message);
                    broadCastMessage(message);
                }

                @Override
                public void accepted(ClientConnectionHandler handler) {

                    broadCastMessage("Connection accepted \nYour root directory is "+ Environment.getExternalStorageDirectory().getAbsolutePath());
                }

                @Override
                public void progress(int progress, String filepath) {

                    broadCastMessage("File "+filepath+" Transferred "+progress);
                }
            });
            server.waitForConnection();

        }
        catch (IOException e){
            e.printStackTrace();;
        }

    }

    private String getHomeDir() {

        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    public void broadCastMessage(String message){

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(createServerResponseIntent(message));

    }

    public static Intent createServerResponseIntent(String msg){

        Intent intent= new Intent(SERVER_LOG_CHANGED);
        intent.putExtra(SERVER_RESPONSE,msg);
        return intent;
    }
    
    public static String getMessage(Intent intent) {
        

        return intent.getStringExtra(SERVER_RESPONSE);
    }
}
