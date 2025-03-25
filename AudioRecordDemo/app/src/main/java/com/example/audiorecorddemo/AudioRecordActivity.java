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

public class AudioRecordActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "AudioRecordActivity";
    private HandlerThread recordThread;
    private HandlerThread playThread;
    private Handler recordHandler;
    private Handler playHandler;
    private AudioRecord audioRecord;
    private volatile boolean  isRecording = false;
    private int sampleRate = 44100;
    private Button startRecord,stopRecord;

    
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initListener();

        
    }

    public void initListener(){
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
    public void startRecord(){
        recordThread = new HandlerThread("recordThread");
        recordHandler = new Handler(recordThread.getLooper());
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        Log.d(TAG, "bufferSize: "+bufferSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate, AudioFormat.CHANNEL_IN_MONO
                ,AudioFormat.ENCODING_PCM_16BIT,bufferSize);
        audioRecord.startRecording();
        isRecording = true;
        recordHandler.post(new Runnable() {
            byte[] buffer = new byte[1024];
            @Override
            public void run() {
                while(isRecording){

                }
            }
        });

    }

    /**
     * 停止录音
     */
      public void stopRecord(){
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
      if(v == startRecord){
          startRecord();
      }else if(v == stop){
          stopRecord();
      }
    }
}
