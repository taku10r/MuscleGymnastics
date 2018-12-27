package com.example.takuto.musclegymnastics;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


public class AbdominalExecutionActivity extends AppCompatActivity implements SensorEventListener {

    public final static String TAG ="SensorTest2";
    protected final static double RAD2DEG = 180/Math.PI;
    public final static int PITCH_THRESHOLD = 30;
    private SoundPool soundPool;
    private int soundStart, soundStop, soundEnd;

    SensorManager sensorManager;

    float[] rotationMatrix = new float[9];
    float[] gravity = new float[3];
    float[] geomagnetic = new float[3];
    float[] attitude = new float[3];

    TextView pitchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abdominalexecution);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(3)
                .build();

        soundStart = soundPool.load(this, R.raw.start, 1);
        soundStop = soundPool.load(this, R.raw.stop, 1);
        soundEnd = soundPool.load(this, R.raw.end, 1);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("debug", "sampleId=" + sampleId);
                Log.d("debug", "status=" + sampleId);
            }
        });

        findViews();
        initSensor();
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void findViews() {
        pitchText = (TextView)findViewById(R.id.pitch);
    }

    protected void initSensor() {
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    boolean flag1 = true;
    boolean flag2 = false;
    int count = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
        }

        if(geomagnetic != null && gravity != null) {
            SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
            SensorManager.getOrientation(rotationMatrix, attitude);

            pitchText.setText(Integer.toString((int) (attitude[1] * -RAD2DEG)));

            if ((int) (attitude[1] * -RAD2DEG) > PITCH_THRESHOLD) {
                if (flag1 == true) {
                    Log.d("test", "OK");
                    count++;
                    soundPool.play(soundStart, 1.0f, 1.0f, 0, 0, 1);

                    if (count < 3) {//?-1回目まで
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                soundPool.play(soundStop, 1.0f, 1.0f, 0, 0, 1);
                                flag2 = true;
                            }
                        }, 2000);
                        flag1 = false;
                    } else if (count == 3) {//?回目までで終了
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                soundPool.play(soundEnd, 1.0f, 1.0f, 0, 0, 1);
                                flag2 = true;
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);
                        flag1 = false;
                    }
                }
            } else {
                if (flag2 == true) {
                    flag1 = true;
                    flag2 = false;
                }
            }
        }
    }
}


