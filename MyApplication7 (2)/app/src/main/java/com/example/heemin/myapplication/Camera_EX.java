package com.example.heemin.myapplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
/*
현재 진척상황 2015/12/05 23:03

날짜로 파일 10초씩 끊어서 저장중

00초에 어플 구동시
##########미해결############
10:23초에 event 발생했을 경우
    실제 결과 -> file1)10:10~10:18 8초~9초
               file2)10:20~10:23
               file3)10:23~10:31 8초~9초
    와 같이 저장
    문제->
        1) event 발생시 촬영 중이던 파일 온전히 저장
        2) currenttime 으로 조절하니 1초~2초의 누락분 발생
       기대 결과 -> file1)10:10~10:20
                  file2)10:20~10:30
                  file3)10:30~10:40

#######추가사항##########
Shake 이벤트 발생시
기존 Stop_REC 버튼 클릭과 같은 이벤트 발생


 */
public class Camera_EX extends Activity implements SurfaceHolder.Callback, SensorEventListener {
    //충격감지 해볼까
    //------------충격부 선언 start-------------
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 500;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    public boolean SensorFlag=true;
    //--------------충격부 선언 End----------

    //-----------------타이머 부분 Start-------------------
    private TimerTask mTask;
    private Timer mTimer;
    public String lastFileName1=null;
    public String lastFileName2=null;
    public String lastFileName3=null;
    public boolean lastFlag = false;
        String saveName(String FileName){
        if(lastFileName1 !=null){
            if(lastFileName2!=null){
                lastFileName3=FileName;
                if(lastFlag==false) {
                    Log.e("FILENAME", "delete 1st File" + lastFileName1);
                    File mfile = new File(OUTPUT_FILE + lastFileName1);
                    mfile.delete();
                }
                lastFileName1=lastFileName2;
                lastFileName2=lastFileName3;
                Log.e("FILENAME", "save 3th time File"+lastFileName3);
                return lastFileName3;
            }
            else {
                lastFileName2 = FileName;
                Log.e("FILENAME", "save 2nd time File" + lastFileName2);
                return lastFileName2;
            }

        }
        else {
            lastFileName1 = FileName;
            Log.e("FILENAME","save 1st File"+lastFileName1);
           return lastFileName1;
        }
    }
    //--------------타이머 부분 end=-================

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
    private String OUTPUT_FILE = "/sdcard/camtest/";
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
            // 카메라 객체의 파라메터를 얻고 로테이션을 90도 꺽는다,옵Q의 경우 90회전을 필요로 한다 ,옵Q는 지원 안하는듯....
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setRotation(90);
            mCamera.setParameters(parameters);
            // 프리뷰 디스플레이를 담당한 서피스 홀더를 설정한다
            mCamera.setPreviewDisplay(holder);
            // 프리뷰 콜백을 설정한다 - 프레임 설정이 가능하다,
  /* mCamera.setPreviewCallback(new PreviewCallback() {
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
     // TODO Auto-generated method stub
    }
   });
   */
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // 서피스가 만들어졌을 때의 대응 루틴
        setCameraPreview(holder);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        // TODO Auto-generated method stub
        // 서피스 변경되었을 때의 대응 루틴
        if (mCamera !=null){
            Camera.Parameters parameters = mCamera.getParameters();
            // 프리뷰 사이즈 값 재조정
            parameters.setPreviewSize(width,height);
            mCamera.setParameters(parameters);
            // 프리뷰 다시 시작
            mCamera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

        //서피스 소멸시의 대응 루틴

        // 프리뷰를 멈춘다
        if (mCamera != null){
            mCamera.stopPreview();
            // 카메라 객체 초기화
            mCamera = null;
        }

    }
    // 프리뷰(카메라가 찍고 있는 화상을 보여주는 화면) 설정 함수
    private void setPreview()
    {
        // 1) 레이아웃의 videoView 를 멤버 변수에 매핑한다
        mVideoView = (VideoView) findViewById(R.id.videoView);
        // 2) surface holder 변수를 만들고 videoView로부터 인스턴스를 얻어온다
        final SurfaceHolder holder = mVideoView.getHolder();
        // 3)표면의 변화를 통지받을 콜백 객체를 등록한다
        holder.addCallback(this);
        // 4)Surface view의 유형을 설정한다, 아래 타입은 버퍼가 없이도 화면을 표시할 때 사용된다.카메라 프리뷰는 별도의 버퍼가 필요없다
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }
    private void setButtons()   {

        // Rec Start 버튼 콜백 설정
        Button recStart = (Button)findViewById(R.id.startREC);
        recStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("CAM TEST", "REC START");

                if (mVideoView.getHolder() == null) {
                    Log.e("CAM TEST", "View Error");
                }

                beginRecording(mVideoView.getHolder());

            }
        });
        // Rec Stop 버튼 콜백 설정
        Button recStop = (Button)findViewById(R.id.stopRec);
        recStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("CAM TEST", "Click RecStop");


                    mTimer.purge(); //타이머 정지시키고
                    mTimer.cancel();
                    mTimer=null;
                    //다음 10초 저장
                lastFlag=true;

                beginRecording(mVideoView.getHolder());
                Log.e("CAM TEST", "file names");
                Log.e("CAM TEST", "1:"+lastFileName1);
                Log.e("CAM TEST", "2:"+lastFileName2);
                Log.e("CAM TEST", "3:"+lastFileName3);

                // 프리뷰가 없을 경우 다시 가동 시킨다
                if (mCamera == null) {
                    Log.e("CAM TEST", "Preview Restart");
                    // 프리뷰 다시 설정
                    setCameraPreview(mVideoView.getHolder());
                    // 프리뷰 재시작t
                    Log.e("CAM TEST", "Preview Restart2");
                    //mCamera.startPreview();
                }

            }
        });
    }
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
        File outFile = new File(FILE_PATH);
        if (outFile.exists())
        {
            outFile.delete();
        }
        Log.e("CAM TEST", "#3 Release Camera");
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;

            Log.e("CAM TEST", "#3 Release Camera  _---> OK!!!");
        }



        try {

            recorder = new MediaRecorder();


            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
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
        }



    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //SCREEN_ORIENTATION_LANDSCAPE - 가로화면 고정
        //SCREEN_ORIENTATION_PORTRAIT - 세로화면 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setPreview();


        setButtons();
        //------------------------------------------------------
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerormeterSensor!=null){
            sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        //----------------------------------------------------

        mTask= new TimerTask() {
            @Override
            public void run() {
                beginRecording(mVideoView.getHolder());
            }
        };

        mTimer = new Timer();

        mTimer.schedule(mTask, 500, 10000);





    }
    @Override
    protected void onDestroy() {
        sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);
        mTimer.cancel();
        mCamera.release();
        super.onDestroy();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD && SensorFlag==true) { //이벤트 발생 부분
                    Log.e("CAM TEST", "Sensor Changed!");
                    SensorFlag=false;


                    mTimer.purge();
                    mTimer.cancel();
                    mTimer=null;
                    //다음 10초 저장
                    lastFlag=true;

                    beginRecording(mVideoView.getHolder());
                    Log.e("CAM TEST", "file names");
                    Log.e("CAM TEST", "1:"+lastFileName1);
                    Log.e("CAM TEST", "2:"+lastFileName2);
                    Log.e("CAM TEST", "3:"+lastFileName3);

                    // 프리뷰가 없을 경우 다시 가동 시킨다
                    if (mCamera == null) {
                        Log.e("CAM TEST", "Preview Restart");
                        // 프리뷰 다시 설정
                        setCameraPreview(mVideoView.getHolder());
                        // 프리뷰 재시작t
                        Log.e("CAM TEST", "Preview Restart2");
                        //mCamera.startPreview();
                    }
                    Log.e("CAM TEST", "Preview Restart3");

                }


                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}


