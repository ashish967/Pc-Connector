package com.simplesocketprogramming;

public interface ConnectionListener {

    public void log(String message);
    public void accepted(ClientConnectionHandler handler);
    public void progress(int progress,String filepath);

}