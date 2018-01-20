package pantianlei.pathguider;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final String key = "274f23b628ba487abac7d06c5c3b99c8";

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
        button.setOnTouchListener(new View.OnTouchListener() {
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
        });
    }
}
