package com.simplesocketprogramming;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ashish-til on 3/9/16.
 */
public class SocketFileUtils {







    public static void push(DataOutputStream dos, String filepath,ConnectionListener listener) throws IOException {

        System.out.print("Writing file...");

        File file= new File(filepath);

        if(!file.exists()){

            System.out.print("No file exist with path "+filepath);
            return;
        }


        FileInputStream fis = new FileInputStream(filepath);

        dos.writeLong(file.length());
        dos.writeUTF(file.getName());
        dos.flush();

        byte[] buffer = new byte[1024*10];

        int read=0;
        long totalRead=0;
        long length= file.length();
        double percentage=0;
        while ((read=fis.read(buffer)) > 0) {

            dos.write(buffer);
            totalRead+=read;

            if(listener!=null) {
                double newpercentage = totalRead * 1.0 / length;

                if (newpercentage - percentage > 0.01) {
                    percentage = newpercentage;
                    listener.progress((int) (percentage * 100), filepath);
                }
            }
        }

        System.out.print("Written file...");
        fis.close();
        dos.flush();
    }


    public static void pull(DataInputStream dIn, String filepath,ConnectionListener listener) throws IOException {



        File file= new File(filepath);

        if(!file.exists()){

            file.mkdirs();
            System.out.print("No file directory exist with path "+filepath);
//            return;
        }

        long length= dIn.readLong();
        String filename=dIn.readUTF();



        FileOutputStream fos = new FileOutputStream(filepath+"/"+filename);
        System.out.print("Read start...");


        byte[] buffer = new byte[1024*10];
        int read = 0;
        int totalRead = 0;
        long remaining = length;
        double percentage=0;
        while((read = dIn.read(buffer, 0, (int)Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);

            if(listener!=null){

                double newpercentage = totalRead * 1.0 / length;

                if (newpercentage - percentage > 0.01) {
                    percentage = newpercentage;
                    listener.progress((int) (percentage * 100), filepath+"/"+filename);
                }
            }
        }

        System.out.print("Read complete...");


        fos.close();

    }
}
