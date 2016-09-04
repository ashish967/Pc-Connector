package com.simplesocketprogramming.ashish_til.pccommunicator;

import android.content.Intent;

/**
 * Created by ashish-til on 3/9/16.
 */
public class IntentUtils {


    private static final String SERVER_START = "server_start_msg";


    public static Intent createServerStartIntent(String msg){

        Intent intent= new Intent();
        intent.putExtra(SERVER_START,"Server has been started");
        return intent;
    }








}
