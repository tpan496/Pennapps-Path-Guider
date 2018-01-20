package pantianlei.pathguider;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pantianlei on 19/01/2018.
 */

public class Client {

    private final static String TAG = "client";
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    String ip;
    int port;
    private static Socket socket;
    PrintWriter pw;

    Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG,"attempt socket connection to "+ip+":"+port);
                    socket = new Socket(ip, port);
                    Log.e(TAG, "socket started");
                    OutputStream dout= socket.getOutputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    for (String inputLine; (inputLine = in.readLine()) != null;) {
                        Log.e(TAG, "received"+inputLine);
                    }
                    Log.e(TAG,"connection end");

                }catch (Exception e){
                    Log.e(TAG, e.toString());
                }finally {
                    //Closing the socket
                    try {
                        socket.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void sendDEF(final String msg){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if(socket != null) {
                        socket.close();
                    }
                    Log.e(TAG,"attempt socket connection to "+ip+":"+port);
                    socket = new Socket(ip, port);
                    Log.e(TAG, "socket started");
                    OutputStream dout= socket.getOutputStream();
                    pw = new PrintWriter(dout);
                    pw.print("DEF " + msg);
                    pw.flush();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    for (String inputLine; (inputLine = in.readLine()) != null;) {
                        Log.e(TAG, "received"+inputLine);
                    }
                    Log.e(TAG,"connection end");

                }catch (Exception e){
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

}