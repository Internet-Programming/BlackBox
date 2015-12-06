package internetprogramming.blackbox;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaRecorder;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
public class Camera_EX extends Activity implements SurfaceHolder.Callback {
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
    private static final String OUTPUT_FILE = "/sdcard/camtest/";
    // 녹화 시간 - 10초
    private static final int RECORDING_TIME = 10000;
    public String lastFileName1 = null;
    public String lastFileName2 = null;
    public String lastFileName3 = null;
    public boolean lastFlag = false;

    // 카메라 프리뷰를 설정한다
    private void setCameraPreview(SurfaceHolder holder){
        try {
            // 카메라 객체를 만든다
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setRotation(90);
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        setCameraPreview(holder);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        // TODO Auto-generated method stub
          if (mCamera !=null){
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(width,height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
       if (mCamera != null){
            mCamera.stopPreview();
           mCamera = null;
        }

    }
    // 프리뷰(카메라가 찍고 있는 화상을 보여주는 화면) 설정 함수
    private void setPreview()
    {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        final SurfaceHolder holder = mVideoView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void setButtons()
    {
        // Rec Start 버튼 콜백 설정
        Button recStart = (Button)findViewById(R.id.RecStart);
        recStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("CAM TEST", "REC START!!!");

                if (mVideoView.getHolder() == null)
                {
                    Log.e("CAM TEST","View Err!!");
                }
                beginRecording(mVideoView.getHolder());

            }
        });
        // Rec Stop 버튼 콜백 설정
        Button recStop = (Button)findViewById(R.id.RecStop);
        recStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 레코더 객체가 존재할 경우 이를 스톱시킨다
                if ( recorder !=null){
                    Log.e("CAM TEST","CAMERA STOP!!!!!");
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }
                // 프리뷰가 없을 경우 다시 가동 시킨다
                if ( mCamera == null ) {
                    Log.e("CAM TEST","Preview Restart!!!!!");
                    // 프리뷰 다시 설정
                    setCameraPreview(mVideoView.getHolder());
                    // 프리뷰 재시작
                    mCamera.startPreview();
                }

            }
        });
    }
    private void beginRecording(SurfaceHolder holder) {
        String tmpFileName = createName(System.currentTimeMillis());
        final String FILE_PATH = OUTPUT_FILE + tmpFileName;//파일경로 및 파일이름
        // 레코더 객체 초기화
        Log.e("CAM TEST","#1 Begin REC!!!");
        if(recorder!= null)
        {
            recorder.stop();
            recorder.release();
        }
        String state = android.os.Environment.getExternalStorageState();
        if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
            Log.e("CAM TEST","I/O Exception");
        }
        // 파일 생성/초기화
        Log.e("CAM TEST","#2 Create File!!!");
        File outFile = new File(OUTPUT_FILE);
        if (outFile.exists())
        {
            outFile.delete();
        }
        Log.e("CAM TEST","#3 Release Camera!!!");
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
            Log.e("CAM TEST","#3 Release Camera  _---> OK!!!");
        }

        try {
            recorder = new MediaRecorder();
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setMaxDuration(RECORDING_TIME);
            recorder.setPreviewDisplay(holder.getSurface());
            recorder.setOutputFile(FILE_PATH);
            recorder.prepare();
            recorder.start();

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("CAM TEST","Error Occur???!!!");
            e.printStackTrace();
        }

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
    //파일 경로 End--------------------------------
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setPreview();
        setButtons();
    }





}