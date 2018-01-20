package pantianlei.pathguider;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Time;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "main";

    private Button connectButton;
    private Button sendDEFButton;
    private Button sendFINDButton;
    private TextView response;
    private Client client;
    private String ip;
    private int port;
    private TextToSpeech tts;
    final AudioRecorder rec = new AudioRecorder();
    final SpeechClientREST speechClient = new SpeechClientREST();
    final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartVoiceRecorder/recording.wav";

    private boolean isConnected = true;

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton button = findViewById(R.id.button);
        final TextView result = findViewById(R.id.connectText);
        result.setTextColor(Color.BLACK);
        vocalize("Hello. Welcome to your path guider!");

        //Add button event listener
        button.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            v.animate().scaleXBy(1.5f).setDuration(500).start();
                            v.animate().scaleYBy(1.5f).setDuration(500).start();

                            final File audioFile = new File(filePath);
                            rec.startRecording();
                            return true;
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            v.animate().cancel();
                            v.animate().scaleX(1f).setDuration(500).start();
                            v.animate().scaleY(1f).setDuration(500).start();

                            rec.stopRecording();
                            try {
                                final InputStream input = new FileInputStream(filePath);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String jsonString = speechClient.process(input);
                                            JSONObject jsonObj = new JSONObject(jsonString);
                                            String resultText = jsonObj.get("DisplayText").toString();
                                            //result.setText(resultText);
                                            parse(resultText);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            return true;
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

    private void parse(String textt) {
        String text = textt.toLowerCase();
        if (text.contains("define")) {
            int s = text.indexOf("define") + 7;
            String obj = text.substring(s).replaceAll("[^a-z]","");
            vocalize("Do you want to define " + obj + "?");
            if (!confirm()) {
                vocalize("Please retry then.");
            } else {
                define(obj);
            }
        } else if (text.contains("find")) {
            int s = text.indexOf("find") + 5;
            String obj = text.substring(s).replaceAll("[^a-z]","");
            vocalize("Do you want to find " + obj + "?");
            if (!confirm()) {
                vocalize("Please retry then.");
            } else {
                find(obj);
            }
        }
    }

    private boolean confirm(){
        final boolean[] bool = {false};
        rec.startRecording();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rec.stopRecording();
        try {
            final InputStream input = new FileInputStream(filePath);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String jsonString = speechClient.process(input);
                        JSONObject jsonObj = new JSONObject(jsonString);
                        String resultText = jsonObj.get("DisplayText").toString();
                        if (resultText.toLowerCase().contains("yes")) {
                            bool[0] = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            t.join();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bool[0];
    }

    private void vocalize(String text) {
        final String textt = text;
         tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    tts.speak(textt, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    //define the object
    private void define(String obj) {
        System.out.println("Begin to define " + obj);
    }

    //find the object
    private void find(String obj) {
        System.out.println("Begin to find " + obj);
    }
}
