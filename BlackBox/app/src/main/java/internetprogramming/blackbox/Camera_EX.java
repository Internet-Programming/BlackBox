package  internetprogramming.blackbox;
import android.app.Activity;
import android.os.Bundle;
import android.media.*;
import android.view.*;
import android.util.*;
import android.os.*;
import java.io.*;
public class Camera_EX extends Activity
 implements SurfaceHolder.Callback, Handler.Callback 
{
 final private String TAG = "CamTest";
 
 //handler command
 final private int START_RECORDING = 1;
 final private int STOP_RECORDING = 2;
 final private int INIT_RECORDER  = 3;
 final private int RELEASE_RECORDER = 4;
 final private int START_INTERVAL_RECORD = 5;
 private SurfaceHolder mSurfaceHolder = null;
 private MediaRecorder mMediaRecorder = null;
 private Handler   mHandler;
 private CountDownTimer mTimer = null;

    @Override
    public void onStart() {
        super.onStart();
        //preview surface
        SurfaceView surView = (SurfaceView)findViewById(R.id.surfaceView);
//        surView.setAspectRatio(320.0f / 240.0f);
        SurfaceHolder holder = surView.getHolder();
        mSurfaceHolder = holder;

        //holder.addCallback(this);
        //holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceCreated(holder);
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);




        //handler
        //mHandler = new Handler(this);


        initMediaRecorder();
        startMediaRecorder();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopMediaRecorder();
        releaseMediaRecorder();
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
    
    int mIth = 0;
    
    protected void startIntervalRecording() {
     mTimer = new CountDownTimer(20000, 3000) {
      boolean recordStart = false;
      
      public void onTick(long millisUntilFinished) {
       if ( !recordStart) {
        recordStart = true;
        mHandler.sendEmptyMessage(START_RECORDING);
       }
      } 
      public void onFinish() {
    mHandler.sendEmptyMessage(STOP_RECORDING);
    mHandler.sendEmptyMessage(RELEASE_RECORDER);
    mHandler.sendEmptyMessage(INIT_RECORDER);
    mHandler.sendEmptyMessage(START_INTERVAL_RECORD);
      }
     };
     mTimer.start();
    }
    
    protected void initMediaRecorder() {
     
     if ( mSurfaceHolder==null ) {
      Log.e(TAG, "No Surface Holder");
      return;
     }
     mMediaRecorder = new MediaRecorder();
     mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
     mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
     mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//     mMediaRecorder.setMaxDuration(200000);
     mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() +"/testdd.mp4");
     mIth++;
     mMediaRecorder.setVideoFrameRate(16);
     mMediaRecorder.setVideoSize(320,240);
     mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
     mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
     mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
         try {
             mMediaRecorder.prepare();
             Thread.sleep(1000);
         } catch (IOException exception) {
             releaseMediaRecorder();
             return;
         } catch (InterruptedException e) {
             e.printStackTrace();
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
}