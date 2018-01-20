package pantianlei.pathguider;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pantianlei on 19/01/2018.
 */

public class Client {

    private final static String TAG = "client";
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Activity activity;
    private TextToSpeech tts;

    String ip;
    int port;
    private static Socket socket;
    PrintWriter pw;

    Client(String ip, int port, MainActivity activity) {
        this.ip = ip;
        this.port = port;
        this.activity = activity;
        notifyToast("Client initialized");
    }

    public void sendFIND(final String msg) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    notifyToast("attempt socket connection to " + ip + ":" + port);
                    socket = new Socket(ip, port);
                    notifyToast("socket started");
                    OutputStream dout = socket.getOutputStream();
                    pw = new PrintWriter(dout);
                    pw.print("FIND " + msg);
                    pw.flush();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    for (String inputLine; (inputLine = in.readLine()) != null; ) {
                        notifyToast("received" + inputLine);
                        vocalize(inputLine);
                    }
                    notifyToast("connection end");

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    public void sendDEF(final String msg) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    notifyToast("attempt socket connection to " + ip + ":" + port);
                    socket = new Socket(ip, port);
                    notifyToast("socket started");
                    OutputStream dout = socket.getOutputStream();
                    pw = new PrintWriter(dout);
                    pw.print("DEF " + msg);
                    pw.flush();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    for (String inputLine; (inputLine = in.readLine()) != null; ) {
                        notifyToast("received" + inputLine);
                        vocalize(inputLine);
                    }

                    notifyToast("connection end");

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void notifyToast(final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, msg);
            }
        });
    }

    public void vocalize(String text) {
        //text = text.substring(6, text.length());
        final String textt = text ;
        tts = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    tts.speak(textt, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

}