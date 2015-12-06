package internetprogramming.blackbox;
import android.app.Activity;
import android.os.Bundle;
<<<<<<< HEAD
import android.media.*;
import android.view.*;
import android.util.*;
import android.os.*;
=======
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
>>>>>>> 998d7b3c9ee145dbe12a473d75b2bd13209b90c0
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
    public int MAXTIME = 1000*1000; //1000초

    final private int START_RECORDING = 1;
    final private int STOP_RECORDING = 2;
    final private int INIT_RECORDER  = 3;
    final private int RELEASE_RECORDER = 4;
    final private int START_INTERVAL_RECORD = 5;
    private SurfaceHolder mSurfaceHolder = null;
    private MediaRecorder mMediaRecorder = null;
    private Handler   mHandler;
    private CountDownTimer mTimer = null;

<<<<<<< HEAD
    /** Called when the activity is first created. */
=======
    //---------------비디오 부분 start---------------
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        // on Pause 상태에서 카메라 ,레코더 객체를 정리한다
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
        if (recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        super.onPause();
    }
    // Video View 객체
    private VideoView mVideoView=null;
    // 카메라 객체
    private Camera mCamera;
    // 레코더 객체 생성
    private MediaRecorder recorder = null;
    // 아웃풋 파일 경로
    private String OUTPUT_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    // 녹화 시간 - 10초
    private static final int RECORDING_TIME = 9900;

    //타이머용
    private int value = 0;
    private CountDownTimer timer;
    private int multiplebyTotalRecSecond = 1000;
    private int TotalRecSecond = 30;


    private String createName(long dateTaken){

        Date date = new Date(dateTaken);

        SimpleDateFormat dateFormat =

                new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.KOREAN);

        return saveName(dateFormat.format(date)+".mp4");

    }
    // 카메라 프리뷰를 설정한다
    private void setCameraPreview(SurfaceHolder holder){
        try {
            // 카메라 객체를 만든다
            mCamera = Camera.open();
            System.out.println("카메라 오픈"+ mCamera);
            // 카메라 객체의 파라메터를 얻고 로테이션을 90도 꺽는다,옵Q의 경우 90회전을 필요로 한다 ,옵Q는 지원 안하는듯....
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setRotation(90);
            mCamera.setParameters(parameters);
            // 프리뷰 디스플레이를 담당한 서피스 홀더를 설정한다
            mCamera.setPreviewDisplay(holder);
            // 프리뷰 콜백을 설정한다 - 프레임 설정이 가능하다,
  /* mCamera.setPreviewCallback(new PreviewCallback() {
>>>>>>> 998d7b3c9ee145dbe12a473d75b2bd13209b90c0
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
        mTimer = new CountDownTimer(MAXTIME, 1000) {//1초마다 검사
            public void onTick(long millisUntilFinished) {
                if ( mTimer%(10*1000)=0) {

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
<<<<<<< HEAD
=======
    private void beginRecording(final SurfaceHolder holder) {
        // 레코더 객체 초기화
        String tmpFileName = createName(System.currentTimeMillis());
        final String FILE_PATH=OUTPUT_FILE+tmpFileName;
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREAN );
        //Date currentTime = new Date ( );
        //String dTime = formatter.format ( currentTime );
        //매 1초 마다 증가할 정수값

        Log.e("CAM TEST", "#1 Begin REC");
        if(recorder!= null)
        {
            recorder.stop();
            recorder.release();
        }
        String state = android.os.Environment.getExternalStorageState();
        if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
            Log.e("CAM TEST", "I/O Exception");
        }
        // 파일 생성/초기화
        Log.e("CAM TEST", "#2 Create File");
        /*
        File outFile = new File(FILE_PATH);
        if (outFile.exists())
        {
            outFile.delete();
        }
        */
        Log.e("CAM TEST", "#3 Release Camera");
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
>>>>>>> 998d7b3c9ee145dbe12a473d75b2bd13209b90c0

    protected void initMediaRecorder() {
        Log.e("TAG", "initMediaRecorder");
        String tmpFileName = createName(System.currentTimeMillis());
        final String FILE_PATH = OUTPUT_FILE + tmpFileName;//파일경로 및 파일이름
        Log.e("TAG", "CreateFilePAth");
        if ( mSurfaceHolder==null ) {
            Log.e(TAG, "No Surface Holder");
            return;
        }
<<<<<<< HEAD
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
=======


        try {

            recorder = new MediaRecorder();
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 녹화 시간 한계 , 10초
            recorder.setMaxDuration(RECORDING_TIME);
            // 프리뷰를 보여줄 서피스 설정
            recorder.setPreviewDisplay(holder.getSurface());
            // 녹화할 대상 파일 설정
            recorder.setOutputFile(FILE_PATH);
            recorder.prepare();
            //timer.start();

            recorder.start();
            Log.e("CAM TEST", "#4 REC_START");


        } catch (Exception e) {
            // TODO: handle exception
            Log.e("CAM TEST","Error!");
            e.printStackTrace();
>>>>>>> 998d7b3c9ee145dbe12a473d75b2bd13209b90c0
        }
        Log.e("TAG", "finished init media Recorder");
    }

    protected void releaseMediaRecorder() {
        Log.e("TAG", "releaseMediaRecorder");
        if ( mMediaRecorder==null )
            return;
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    protected void startMediaRecorder() {
        Log.e("TAG", "StartMediaRecorder");
        if ( mMediaRecorder!=null ) {
            Log.v(TAG, "Before Record Start");
            mMediaRecorder.start();
            Log.v(TAG, "Record Started");
        }
    }

    protected void stopMediaRecorder() {
        Log.e("TAG", "StopMediaRecorder");
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
        Log.e("TAG", "surfaceCreated");
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
        Log.e("TAG", "handleMessage");
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
                MAXTIME=10*1000; //10초만 추가로촬영


            }
        });
    }

}