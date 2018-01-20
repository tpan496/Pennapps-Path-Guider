package pantianlei.pathguider;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final String key = "274f23b628ba487abac7d06c5c3b99c8";

    private final static String TAG = "main";

    private Button connectButton;
    private Button sendDEFButton;
    private Button sendFINDButton;
    private TextView response;
    private Client client;
    private String ip;
    private int port;

    private boolean isConnected = true;

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton button = findViewById(R.id.button);
        final MediaRecorder mediaRecorder = new MediaRecorder();
        final File dirPath = new File(Environment.getExternalStorageDirectory(), "SmartVoiceRecorder");
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
//        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pennapps";
        final File audioFile = new File(dirPath, "recording.wav");
        final String filePath = audioFile.getAbsolutePath();

        //Add button event listener
        button.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            final File audioFile = new File(filePath);
                            mediaRecorder.setAudioSamplingRate(16000);
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(AudioFormat.CHANNEL_OUT_MONO);
                            mediaRecorder.setAudioEncoder(AudioFormat.ENCODING_PCM_16BIT);
                            mediaRecorder.setOutputFile(audioFile);
                            try {
                                mediaRecorder.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaRecorder.start();

                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            mediaRecorder.stop();
                            mediaRecorder.reset();
                            mediaRecorder.release();

                            final SpeechClientREST client = new SpeechClientREST(new Authentication(key));
                            try {
                                final InputStream input = new FileInputStream(filePath);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            System.out.println(client.process(input));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            //new File(filePath).delete();
                        }
                        return true;
                    }
                }
        );

        connectButton = (Button) findViewById(R.id.connectButton);
        sendDEFButton = (Button) findViewById(R.id.sendDEFButton);
        final EditText DEFText = (EditText) findViewById(R.id.defText);
        sendFINDButton = (Button) findViewById(R.id.sendFINDButton);
        final EditText FINDText = (EditText) findViewById(R.id.findText);
        response = (TextView) findViewById(R.id.connectText);

        final PathGuiderApplication app = (PathGuiderApplication) getApplication();
        final EditText IPText = (EditText) findViewById(R.id.IPText);
        final EditText portText = (EditText) findViewById(R.id.PortText);

        final MainActivity activity = this;
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = IPText.getText().toString();
                int port = Integer.parseInt(portText.getText().toString());
                client = new Client(ip, port, activity);
            }
        });

        sendDEFButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.sendDEF(DEFText.getText().toString());
            }
        });

        sendFINDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.sendFIND(FINDText.getText().toString());
            }
        });
    }
}
