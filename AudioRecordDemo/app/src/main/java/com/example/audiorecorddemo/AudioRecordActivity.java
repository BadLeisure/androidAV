package com.example.audiorecorddemo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;

public class AudioRecordActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "AudioRecordActivity";
    private HandlerThread recordThread;
    private HandlerThread playThread;
    private Handler recordHandler;
    private Handler playHandler;
    private AudioRecord audioRecord;
    private volatile boolean isRecording = false;
    private int sampleRate = 44100;
    private Button startRecord, stopRecord;
    private File file;
    private FileOutputStream fileOutputStream;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initListener();
        try{
            file = new File(getFilesDir(), "test.pcm");
            fileOutputStream = new FileOutputStream(file);
            Log.d(TAG, "getFilesDir:"+getFilesDir());
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }



    }

    public void initListener() {
        startRecord = findViewById(R.id.btn_start_record);
        stopRecord = findViewById(R.id.btn_stop_record);
        startRecord.setOnClickListener(this);
        stopRecord.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        recordThread = new HandlerThread("recordThread");
        recordHandler = new Handler(recordThread.getLooper());
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.d(TAG, "bufferSize: " + bufferSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO
                , AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioRecord.startRecording();
        isRecording = true;
        recordHandler.post(new Runnable() {
            byte[] buffer = new byte[1024];

            @Override
            public void run() {
                while (isRecording) {
                    int byteRead = audioRecord.read(buffer, 0, buffer.length);
                    if (byteRead > 0) {
                        try{
                            fileOutputStream.write(buffer, 0, byteRead);

                        }catch (Exception e){
                            Log.d(TAG, "run:write "+e.getMessage());
                        }
                    }
                }

            }

        });

    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (recordThread != null) {
            recordThread.quitSafely(); // 安全退出线程
            recordThread = null;
        }

    }


    @Override
    public void onClick(View v) {
        if (v == startRecord) {
            startRecord();
        } else if (v == stopRecord) {
            stopRecord();
        }
    }
}