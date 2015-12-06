package internetprogramming.blackbox;
import android.app.Activity;
import android.os.Bundle;
import android.media.*;
import android.view.*;
import android.util.*;
import android.os.*;
import android.widget.Button;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Camera_EX extends Activity
        implements SurfaceHolder.Callback, Handler.Callback
{
    final private String TAG = "CamTest";

    //handler command
    public String lastFileName1 = null;
    public String lastFileName2 = null;
    public String lastFileName3 = null;
    public boolean lastFlag = false;
    private static final String OUTPUT_FILE = "/sdcard/camtest/";
    public int MAXTIME = 10*1000; //10초

    final private int START_RECORDING = 1;
    final private int STOP_RECORDING = 2;
    final private int INIT_RECORDER  = 3;
    final private int RELEASE_RECORDER = 4;
    final private int START_INTERVAL_RECORD = 5;
    private SurfaceHolder mSurfaceHolder = null;
    private MediaRecorder mMediaRecorder = null;
    private Handler   mHandler;
    private CountDownTimer mTimer = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //preview surface
        SurfaceView surView = (SurfaceView)findViewById(R.id.surfaceView);
//        surView.setAspectRatio(320.0f / 240.0f);
        SurfaceHolder holder = surView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //handler
        mHandler = new Handler(this);
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if ( mTimer!=null ) {
            mTimer.cancel();
            mTimer = null;
        }
        stopMediaRecorder();
        releaseMediaRecorder();
    }


    protected void startIntervalRecording() {
        mTimer = new CountDownTimer(MAXTIME, 1000) {//10초마다 카운트
            boolean recordStart = false;

            public void onTick(long millisUntilFinished) {
                Log.e(TAG,"TIME is : "+MAXTIME);
                if ( !recordStart) {
                    recordStart = true;
                    mHandler.sendEmptyMessage(START_RECORDING);
                }
            }
            public void onFinish() {
                Log.e(TAG,"mTimer Finish");
                mHandler.sendEmptyMessage(STOP_RECORDING);
                mHandler.sendEmptyMessage(RELEASE_RECORDER);
                mHandler.sendEmptyMessage(INIT_RECORDER);
                mHandler.sendEmptyMessage(START_INTERVAL_RECORD);
            }
        };
        mTimer.start();
    }

    protected void initMediaRecorder() {
        String tmpFileName = createName(System.currentTimeMillis());
        final String FILE_PATH = OUTPUT_FILE + tmpFileName;//파일경로 및 파일이름
        if ( mSurfaceHolder==null ) {
            Log.e(TAG, "No Surface Holder");
            return;
        }
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //     mMediaRecorder.setMaxDuration(200000);
        mMediaRecorder.setOutputFile(FILE_PATH);
        mMediaRecorder.setVideoFrameRate(16);
        mMediaRecorder.setVideoSize(320,240);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        try {
            mMediaRecorder.prepare();
        } catch (IOException exception) {
            releaseMediaRecorder();
            return;
        }
    }

    protected void releaseMediaRecorder() {
        if ( mMediaRecorder==null )
            return;
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    protected void startMediaRecorder() {
        if ( mMediaRecorder!=null ) {
            Log.v(TAG, "Before Record Start");
            mMediaRecorder.start();
            Log.v(TAG, "Record Started");
        }
    }

    protected void stopMediaRecorder() {
        if ( mMediaRecorder!=null ) {
            Log.v(TAG, "Before Record Stop");
            mMediaRecorder.stop();
            Log.v(TAG, "Record Stopped");
        }
    }


    //--------------------------------------------------------------------
    // SurfaceHolder.Callback Implementation
    public void  surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //do something
    }
    public void  surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        //init video
        initMediaRecorder();
        startIntervalRecording();
    }
    public void  surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
    }

    //--------------------------------------------------------------------
    // Handler.Callback Implementation
    public boolean  handleMessage(Message msg) {
        switch (msg.what ) {
            case START_RECORDING:
                startMediaRecorder();
                return true;
            case STOP_RECORDING:
                stopMediaRecorder();
                return true;
            case INIT_RECORDER:
                initMediaRecorder();
                return true;
            case RELEASE_RECORDER:
                releaseMediaRecorder();
                return true;
            case START_INTERVAL_RECORD:
                startIntervalRecording();
                return true;
        }
        return false;
    }
    //파일 경로&이름 Start
    private String createName(long dateTaken) {

        Date date = new Date(dateTaken);

        SimpleDateFormat dateFormat =

                new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.KOREAN);

        return saveName(dateFormat.format(date) + ".mp4");

    }

    String saveName(String FileName) {
        if (lastFileName1 != null) {
            if (lastFileName2 != null) {
                lastFileName3 = FileName;
                if (lastFlag == false) {
                    Log.e("FILENAME", "delete 1st File" + lastFileName1);
                    File mfile = new File(OUTPUT_FILE + lastFileName1);
                    mfile.delete();
                }
                lastFileName1 = lastFileName2;
                lastFileName2 = lastFileName3;
                Log.e("FILENAME", "save 3th time File" + lastFileName3);
                return lastFileName3;
            } else {
                lastFileName2 = FileName;
                Log.e("FILENAME", "save 2nd time File" + lastFileName2);
                return lastFileName2;
            }

        } else {
            lastFileName1 = FileName;
            Log.e("FILENAME", "save 1st File" + lastFileName1);
            return lastFileName1;
        }
    }
    private void setButtons()   {
        // Rec Stop 버튼 콜백 설정
        Button recStop = (Button)findViewById(R.id.stopRec);
        recStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.e("CAM TEST", "Click RecStop");
                MAXTIME=MAXTIME%(10*1000)+10*1000; //10초만 추가로촬영
                Log.e(TAG,"TIME is : "+MAXTIME);

            }
        });
    }

}