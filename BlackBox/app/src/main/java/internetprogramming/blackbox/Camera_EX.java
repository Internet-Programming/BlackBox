
package  internetprogramming.blackbox;
<<<<<<< HEAD
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
=======
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Camera_EX extends Activity {

    private static String EXTERNAL_STORAGE_PATH = "";
    private static String RECORDED_FILE = "video_recorded";
    private static int fileIndex = 0;
    private static String filename = "";

    MediaPlayer player;
    MediaRecorder recorder;

    // 카메라 상태를 저장하고 있는 객체
    private Camera camera = null;

    SurfaceView surfaceView;
    SurfaceHolder holder;

>>>>>>> 268f9ea7d6059243dfb0175317c6de4ee02d1bdf
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
<<<<<<< HEAD
        setButtons();
        //preview surface
        SurfaceView surView = (SurfaceView)findViewById(R.id.surfaceView);
//        surView.setAspectRatio(320.0f / 240.0f);
        SurfaceHolder holder = surView.getHolder();
        holder.addCallback(this);
=======

        // 외장메모리가 있는지 확인한다.
        // Environment.getExternalStorageState() 를 통해서 현재 외장메모리를 상태를 알수있다.
        String state = Environment.getExternalStorageState();
        // Environment.MEDIA_MOUNTED 외장메모리가 마운트 flog
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "외장 메모리가 마운트 되지않았습니다.", Toast.LENGTH_LONG).show();
        } else {
            EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        }


        // SurfaceView 클래스 객체를 이용해서 카메라에 받은 녹화하고 재생하는데 쓰일것이다.
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        // SurfaceView 클래스를 컨트롤하기위한 SurfaceHolder 생성
        holder = surfaceView.getHolder();
        // 버퍼없음
>>>>>>> 268f9ea7d6059243dfb0175317c6de4ee02d1bdf
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button recordBtn = (Button) findViewById(R.id.RecStart);
        Button recordStopBtn = (Button) findViewById(R.id.RecStop);
        //Button playBtn = (Button) findViewById(R.id.playBtn);
        //Button playStopBtn = (Button) findViewById(R.id.playStopBtn);

        // 녹화 시작 버튼
        recordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    // 녹화 시작을 위해  MediaRecorder 객체 recorder를 생성한다.
                    if (recorder == null) {
                        recorder = new MediaRecorder();
                    }
                    // 오디오와영상 입력 형식 설정
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                    // 오디오와영상 인코더 설정
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

                    // 저장될 파일 지정
                    filename = createFilename();
                    recorder.setOutputFile(filename);

                    // 녹화도중에 녹화화면을 뷰에다가 출력하게 해주는 설정
                    recorder.setPreviewDisplay(holder.getSurface());

<<<<<<< HEAD
    protected void startIntervalRecording() {
        mTimer = new CountDownTimer(10000, 1000) {//A초동안 B초마다
            boolean recordStart = false;
=======
                    // 녹화 준비,시작
                    recorder.prepare();
                    recorder.start();
>>>>>>> 268f9ea7d6059243dfb0175317c6de4ee02d1bdf

                } catch (Exception ex) {
                    ex.printStackTrace();
                    recorder.release();
                    recorder = null;
                }
            }
        });

        recordStopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recorder == null)
                    return;
                // 녹화 중지
                recorder.stop();

                // 영상 재생에 필요한 메모리를 해제한다.
                recorder.release();
                recorder = null;

                ContentValues values = new ContentValues(10);

                values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo");
                values.put(MediaStore.Audio.Media.ALBUM, "Video Album");
                values.put(MediaStore.Audio.Media.ARTIST, "Mike");
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Video");
                values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Audio.Media.DATA, filename);

                Uri videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                if (videoUri == null) {
                    Log.d("SampleVideoRecorder", "Video insert failed.");
                    return;
                }

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri));

            }
        });
        /*
        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // 영상 재생 방법
                if (player == null) {
                    // 영상 플레이를 위해 MediaPlayer 클래스 객체를 생성한다
                    player = new MediaPlayer();
                }

<<<<<<< HEAD
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
=======
                try {
                    // 플레이할 파일 설정
                    player.setDataSource(filename);
>>>>>>> 268f9ea7d6059243dfb0175317c6de4ee02d1bdf

                    // 플레이할 뷰 설정
                    player.setDisplay(holder);

                    // 플레이 준비,시작
                    player.prepare();
                    player.start();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "영상이 재생 도중 예외가 발생했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });


        playStopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // 영상 중지 방법
                if (player == null)
                    return;
                // 영상 중지
                player.stop();

                // 메모리 해제
                player.release();
                player = null;
            }
        });
        */

    }


    private String createFilename() {
        fileIndex++;

        String newFilename = "";
        if (EXTERNAL_STORAGE_PATH == null || EXTERNAL_STORAGE_PATH.equals("")) {
            // 내장 메모리를 사용합니다.
            newFilename = RECORDED_FILE + fileIndex + ".mp4";
        } else {
            // 외장 메모리를 사용합니다.
            newFilename = EXTERNAL_STORAGE_PATH + "/" + RECORDED_FILE + fileIndex + ".mp4";
        }

        return newFilename;
    }


<<<<<<< HEAD
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
=======
    // 액티비티가 onPause 상태일때 녹화,재생에 필요한 모든 객체들의 메모리를 해제한다
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }

        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
>>>>>>> 268f9ea7d6059243dfb0175317c6de4ee02d1bdf
        }
    }
<<<<<<< HEAD
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
=======


}
>>>>>>> 268f9ea7d6059243dfb0175317c6de4ee02d1bdf
