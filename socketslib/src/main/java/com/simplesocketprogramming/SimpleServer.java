package com.simplesocketprogramming;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {


    ServerSocket mServerSocket;
    ConnectionListener mListener;
    private String mIp;

    private String mHomeDir;

    private SimpleServer(){

    }


    public  static SimpleServer createSimpleServer(String  ip,String homeDir,ConnectionListener listener) throws IOException {

        SimpleServer server= new SimpleServer();
        server.mListener= listener;
        server.mIp= ip;
        server.mHomeDir=homeDir;
        server.startServer();
        return server;
    }



    public void waitForConnection() throws IOException {

        while (true) {
                System.out.println("Waiting...");
                mListener.log("Waiting for connection");
                Socket sock = mServerSocket.accept();
                System.out.println("Accepted connection : " + sock);
                mListener.log("Accepted connection : " + sock);
                ClientConnectionHandler handler= ClientConnectionHandler.createConnectionHandler(sock,mHomeDir,mListener);
                mListener.accepted(handler);
                handler.start();

        }
    }



    public void startServer() throws IOException {
        InetAddress ipAddr = InetAddress.getByName(mIp);
        mServerSocket = new ServerSocket(0,10,ipAddr);
        mListener.log("Server started "+ipAddr.getHostAddress()+":"+mServerSocket.getLocalPort());
        System.out.println("Server started "+mServerSocket.getInetAddress()+":"+mServerSocket.getLocalPort());

    }

    public static void main (String [] args ) throws IOException {

        SimpleServer server= new SimpleServer();
        server.startServer();
        server.waitForConnection();

    }
}
