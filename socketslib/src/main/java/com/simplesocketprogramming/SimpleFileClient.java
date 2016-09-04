package com.simplesocketprogramming;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SimpleFileClient implements ConnectionListener {

  Socket mSocket;

  String mServerHomeDir;
  public void connect(String ip,int port) throws IOException {

      System.out.println("Connecting....");
      mSocket= new Socket(ip,port);
      System.out.println("Connected...");

  }


  public static void main (String [] args ) throws IOException {


      SimpleFileClient client= new SimpleFileClient();
      client.takeAction();


  }
// /home/ashish-til/Documents/reliance_bill_July_11.pdf
  private void takeAction() {

        System.out.println("Hello!!\nEnter your server ip address");
        Scanner scanner= new Scanner(System.in);
        String ip= scanner.nextLine();
        System.out.println("Enter your server listening port number");
        int port = scanner.nextInt();
        try {
            connect(ip,port);
            startSession();
            closeConnection();


        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void startSession() throws IOException {



        initMetaInfo(new DataInputStream(mSocket.getInputStream()));

        System.out.println("Welcome!!\nYou have below options\n1. push source destination\n source= path of file in your computer\n" +
                " destination=relative path of file in your remote device \n2. pull source destination\n source=relative path of file in your remote device \n destination=path of file in your computer \n ");

        loop:while (true){

            Scanner scanner = new Scanner(System.in);


            String  command = scanner.next();
            String source =scanner.next();
            String destination= scanner.next();


            int choice=ClientConnectionHandler.CLOSE_CONNECTION;

            if(command.equalsIgnoreCase("push")){

                choice= ClientConnectionHandler.SEND_FILE;
            }
            else if(command.equalsIgnoreCase("pull")){

                choice= ClientConnectionHandler.RCV_FILE;
            }

            switch (choice){

                case ClientConnectionHandler.SEND_FILE:{

                    String sourcePath= source;
                    String destPath= destination;

                    DataOutputStream dOs= new DataOutputStream(mSocket.getOutputStream());
                    dOs.writeInt(ClientConnectionHandler.SEND_FILE);
                    dOs.writeUTF(mServerHomeDir+"/"+destPath);
                    dOs.flush();
                    SocketFileUtils.push(new DataOutputStream(mSocket.getOutputStream()), sourcePath, this);
                }

                case ClientConnectionHandler.RCV_FILE:{

                    String sourcePath= source;
                    String destPath= destination;
                    DataOutputStream dOs= new DataOutputStream(mSocket.getOutputStream());
                    dOs.writeInt(ClientConnectionHandler.RCV_FILE);
                    dOs.writeUTF(mServerHomeDir+"/"+sourcePath);
                    dOs.flush();
                    SocketFileUtils.pull(new DataInputStream(mSocket.getInputStream()),destPath,this);

                }
                case ClientConnectionHandler.CLOSE_CONNECTION:{

                    break  loop;
                }


            }

        }

        closeConnection();
    }

    private void initMetaInfo(DataInputStream dataInputStream) throws IOException {

        mServerHomeDir= dataInputStream.readUTF();

    }

    private void closeConnection() throws IOException {

        mSocket.close();
    }


    @Override
    public void log(String message) {

        System.out.println(message);
    }

    @Override
    public void accepted(ClientConnectionHandler handler) {

    }

    @Override
    public void progress(int progress, String filepath) {

        System.out.println("File "+filepath+" Transferred: "+progress);
    }
}