
package  internetprogramming.blackbox;
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
        import java.util.Timer;
        import java.util.TimerTask;

public class Camera_EX extends Activity
        implements SurfaceHolder.Callback, Handler.Callback
{

    final private String TAG = "CamTest";
    String OUTPUT_FILE = "/sdcard/ttmp/";
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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setButtons();
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

    int mIth = 0;

    protected void startIntervalRecording() {
        mTimer = new CountDownTimer(10000, 1000) {//A초동안 B초마다
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
        mMediaRecorder.setMaxDuration(10000);
        mMediaRecorder.setOutputFile(createName(System.currentTimeMillis()));
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
       // startIntervalRecording();
    }
    public void  surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
        mMediaRecorder=null;
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
               // startIntervalRecording();
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
        //수정 요


        if (Main.fileName1 != null) {
            if (Main.fileName2 != null) {
                Main.fileName3 = FileName;
                if (Main.lastFlag == false) {
                    Log.e("FILENAME", "delete 1st File" + Main.fileName1);
                    File mfile = new File(OUTPUT_FILE + Main.fileName1);
                    mfile.delete();
                }
                Main.fileName1=Main.fileName2;
                Main.fileName2=Main.fileName3;
                Main.fileName3=null;
                Log.e("FILENAME", "save 3th time File" + Main.fileName2);
                return Main.fileName2;
            } else {
                Main.fileName2 = FileName;
                Log.e("FILENAME", "save 2nd time File" + Main.fileName2);
                return Main.fileName2;
            }

        } else {
            Main.fileName1 = FileName;
            Log.e("FILENAME", "save 1st File" + Main.fileName1);
            return Main.fileName1;
        }
    }
    private TimerTask mTask2;
    private Timer mTimer2;
    private void setButtons() {
        // Rec Start 버튼 콜백 설정
        Button recStart = (Button) findViewById(R.id.RecStart);
        recStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("setButtons#1", "REC START");

                mTask2=new TimerTask() {
                    @Override
                    public void run() {//카메라 1회 저장 루틴 실행 부분
                        initMediaRecorder();
                    }
                };
                mTimer2=new Timer();
                mTimer2.schedule(mTask2,500,10000); //0.5초 딜레이 10초마다 run 실행

            }
        });

        //recStop 부분
        Button recStop = (Button) findViewById(R.id.RecStop);
        recStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("CAM TEST", "Timer Stop");

                Main.lastFlag = true;
                mTimer2.purge();
                mTimer2.cancel();
                mTimer2=null;
                initMediaRecorder();
                //카메라 1회 저장 실행 부분


            }
        });
    }
}
//아래 처럼 Log는 뜨는데 문제는 서피스가 실행이 안되서 실제 저장이 안되는 것 같음.
/*
12-07 02:11:57.208 19007-19007/internetprogramming.blackbox E/FILENAME: save 1st File2015-12-07 02.11.57.mp4
 처음 눌럿을 떄 10초 녹화함<-어디서 시작하는질 모르겟음
12-07 02:12:01.898 19007-19007/internetprogramming.blackbox E/setButtons#1: REC START
 스타트 버튼 클릭
12-07 02:12:02.408 19007-19205/internetprogramming.blackbox E/FILENAME: save 2nd time File2015-12-07 02.12.02.mp4
12-07 02:12:12.398 19007-19205/internetprogramming.blackbox E/FILENAME: delete 1st File2015-12-07 02.11.57.mp4
12-07 02:12:12.398 19007-19205/internetprogramming.blackbox E/FILENAME: save 3th time File2015-12-07 02.12.12.mp4
12-07 02:12:22.398 19007-19205/internetprogramming.blackbox E/FILENAME: delete 1st File2015-12-07 02.12.02.mp4
12-07 02:12:22.398 19007-19205/internetprogramming.blackbox E/FILENAME: save 3th time File2015-12-07 02.12.22.mp4
  10초 간격으로 밀어내며 저장
12-07 02:12:28.508 19007-19007/internetprogramming.blackbox E/CAM TEST: Timer Stop
 스탑 버튼 클릭시 타이머 멈추고
12-07 02:12:28.508 19007-19007/internetprogramming.blackbox E/FILENAME: save 3th time File2015-12-07 02.12.28.mp4
 10초 한번 더 저장
 */