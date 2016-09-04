package com.simplesocketprogramming;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ashish-til on 3/9/16.
 */
public class ClientConnectionHandler extends Thread {


    Socket mSocket;
    ConnectionListener mListener;
    boolean mAsync;

    Bundle mBundle;


    public static final int SEND_FILE=1;
    public static final int RCV_FILE=2;
    public static final int CLOSE_CONNECTION=3;
    private String mHomeDir;


    public static class Bundle{

        Message mMessage;
        String filepath;

        public static Bundle createBundle(Message message,String path){

            Bundle bundle= new Bundle();
            bundle.mMessage= message;
            bundle.filepath= path;
            return bundle;
        }

    }

    enum Message{

        SEND_FILE,RCV_FILE
    }

    private ClientConnectionHandler(){

    }



    public static ClientConnectionHandler createConnectionHandler(Socket socket,String homDir, ConnectionListener listener){

        ClientConnectionHandler handler= new ClientConnectionHandler();
        handler.mSocket= socket;
        handler.mListener= listener;
        handler.mHomeDir= homDir;
        return handler;
    }

    @Override
    public void run() {



        handleSync();

    }


    public  void initServerInfo(DataOutputStream stream) throws IOException {

        stream.writeUTF(mHomeDir);
        stream.flush();
    }

    private void handleSync() {

        try {


            initServerInfo(new DataOutputStream(mSocket.getOutputStream()));

            loop:while (true){


                DataInputStream dIn = new DataInputStream(mSocket.getInputStream());
                int action= dIn.readInt();
                String filepath;
                mListener.log("Action  "+action);
                System.out.print("Action  "+action);

                switch (action){

                    case SEND_FILE:
                        filepath= dIn.readUTF();
                        mListener.log("File name to be rcvd "+filepath);
                        handleFileReceiveAction(filepath);
                        break loop;
                    case RCV_FILE:
                        filepath= dIn.readUTF();
                        mListener.log("File name to be rcvd "+filepath);
                        handleSendFileAction(filepath);
                        break loop;
                    case CLOSE_CONNECTION:
                        mListener.log("Close connection!!");
                        closeConnection();
                        break loop;

                    default:
                        System.out.println("Invalid choice try again");
                }

            }

        }
        catch (Exception e){

            e.printStackTrace();

        }



    }

    private void closeConnection() {

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
    }

    private void handleFileReceiveAction(String filepath) {

        try {
            SocketFileUtils.pull(new DataInputStream(mSocket.getInputStream()),filepath,mListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSendFileAction(String filepath) {

        try {
            SocketFileUtils.push(new DataOutputStream(mSocket.getOutputStream()),filepath,mListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
