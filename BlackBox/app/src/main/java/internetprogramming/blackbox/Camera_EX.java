package  internetprogramming.blackbox;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

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
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button recordBtn = (Button) findViewById(R.id.RecStart);
        Button recordStopBtn = (Button) findViewById(R.id.RecStop);
        //Button playBtn = (Button) findViewById(R.id.playBtn);
        //Button playStopBtn = (Button) findViewById(R.id.playStopBtn);
        setButtons();

        Button btnOther = (Button) findViewById(R.id.buttonOther); //회원가입 버튼

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*What should I do */
                AlertDialog.Builder alert = new AlertDialog.Builder(Camera_EX.this);
                alert.setTitle("");
                alert.setMessage("맞은편 차량번호를 입력하세요.");
                // Set an EditText view to get user input
                final EditText input = new EditText(Camera_EX.this);

                input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI); //키보드가 화면을 덮지 않도록
                input.setSingleLine(true); // 한줄만

                alert.setView(input);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        try {

                            JSONObject value = new JSONObject();
                            value.put("Num", input.getText().toString());
                            // {"Num" : input.getText().toString() };
                            // Do something with value!
                            ClientCheckTask ot = new ClientCheckTask();

                            boolean checkClient = ot.execute(value).get();
                            if (checkClient == true) {
                                JSONObject connectValue = new JSONObject();
                                connectValue.put("MyNum", Main.MYCARNUMBER);
                                Main.YOURCARNUMBER = input.getText().toString();
                                connectValue.put("YourNum", Main.YOURCARNUMBER);

                                ConnectClientTask connectClientTask = new ConnectClientTask();
                                boolean connectClientResult = connectClientTask.execute(connectValue).get();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                });
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
            }
        });

        // 녹화 시작 버튼
        /* setButtons()로 이벤트 설정
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

                    // 녹화 준비,시작
                    recorder.prepare();
                    recorder.start();

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
        */
        /*
        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // 영상 재생 방법
                if (player == null) {
                    // 영상 플레이를 위해 MediaPlayer 클래스 객체를 생성한다
                    player = new MediaPlayer();
                }

                try {
                    // 플레이할 파일 설정
                    player.setDataSource(filename);

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
        }
    }

    //파일 경로&이름 Start
    private String createName(long dateTaken) {

        Date date = new Date(dateTaken);

        SimpleDateFormat dateFormat =

                new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss", Locale.KOREAN);

        return saveName(dateFormat.format(date) + ".mp4");

    }

    String saveName(String FileName) {


        if (Main.fileName1 != null) {
            if (Main.fileName2 != null) {
                if(Main.fileName3!=null){
                    Main.fileName4=FileName;
                    if(Main.lastFlag==false){
                        Log.e("FILENAME", "delete 1st File" + Main.fileName1);
                        File mfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/BlackBox/" + Main.fileName1);
                        mfile.delete();
                    }
                    Main.fileName1=Main.fileName2;
                    Main.fileName2=Main.fileName3;
                    Main.fileName3=Main.fileName4;
                    return Environment.getExternalStorageDirectory().getAbsolutePath()+"/BlackBox/" + Main.fileName3;
                }
                Main.fileName3 = FileName;
                Log.e("FILENAME", "save 3th time File" + Main.fileName3);
                return Environment.getExternalStorageDirectory().getAbsolutePath()+"/BlackBox/" + Main.fileName3;
            } else {
                Main.fileName2 = FileName;
                Log.e("FILENAME", "save 2nd time File" + Main.fileName2);
                return Environment.getExternalStorageDirectory().getAbsolutePath()+"/BlackBox/" + Main.fileName2;
            }

        } else {
            Main.fileName1 = FileName;
            Log.e("FILENAME", "save 1st File" + Main.fileName1);
            return Environment.getExternalStorageDirectory().getAbsolutePath()+"/BlackBox/" +  Main.fileName1;
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

                Main.lastFlag = false;

                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        CheckEveryTask ot = new CheckEveryTask();
                        System.out.println("CheckEveryTask를 합시다");
                        try {
                            Main.JSONDATA.put("MyNum", Main.MYCARNUMBER);
                            Main.JSONDATA.put("YourNum", Main.YOURCARNUMBER);
                            //while (Main.lastFlag == false) {
                                //////여기작업
                                JSONObject checkEveryTask = ot.execute(Main.JSONDATA).get();
                                if(checkEveryTask.getBoolean("result")) {
                                    if (checkEveryTask.getString("Order").equals( "solo")) {
                                        Main.YOURCARNUMBER = Main.JSONDATA.getString("YourNum");
                                    } else if (checkEveryTask.getString("Order").equals("checkShock")) {
                                        if (checkEveryTask.getBoolean("shock")) {
                                            Thread.sleep(2000);
                                            UploadFile upload = new UploadFile();
                                            upload.execute(Main.fileName1);
                                            upload.execute(Main.fileName2);
                                            upload.execute(Main.fileName3);


                                        } else if (checkEveryTask.getBoolean("connect") == false) {
                                            Main.YOURCARNUMBER = null;
                                        }
                                    }
                                }


                            //}
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (ExecutionException e1) {
                            e1.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },5000);


                try {
                    if (Main.YOURCARNUMBER == null) {
                        Main.JSONDATA.put("Order","solo");
                    } else {

                        Main.JSONDATA.put("Order","checkShock");

                        startService(new Intent("SensorService"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mTask2 = new TimerTask() {
                    @Override
                    public void run() {//카메라 1회 저장 루틴 실행 부분
                        try {
                            // 녹화 시작을 위해  MediaRecorder 객체 recorder를 생성한다.
                            if (recorder == null) {
                                recorder = new MediaRecorder();
                            } else {
                                recorder.stop();
                            }
                            // 오디오와영상 입력 형식 설정
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                            // 오디오와영상 인코더 설정
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

                            recorder.setMaxDuration(9900);

                            // 녹화도중에 녹화화면을 뷰에다가 출력하게 해주는 설정
                            recorder.setPreviewDisplay(holder.getSurface());


                            // 저장될 파일 지정

                            filename = createName(System.currentTimeMillis());
                            System.out.println(filename+"씨발!");
                            recorder.setOutputFile(filename);



                            // 녹화 준비,시작
                            recorder.prepare();
                            recorder.start();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            recorder.release();
                            recorder = null;
                        }
                    }
                };
                mTimer2 = new Timer();
                mTimer2.schedule(mTask2, 500, 10000); //0.5초 딜레이 10초마다 run 실행

            }
        });

        //recStop 부분
        Button recStop = (Button) findViewById(R.id.RecStop);
        recStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("CAM TEST", "Timer Stop");
                if (Main.YOURCARNUMBER == null) {

                } else {
                    stopService(new Intent("SensorService"));
                    JSONObject value = new JSONObject();

                    try {
                        value.put("MyNum", Main.MYCARNUMBER);
                        value.put("YourNum", Main.YOURCARNUMBER);

                        // {"Num" : input.getText().toString() };
                        // Do something with value!
                        DisconnectTask ot = new DisconnectTask();

                        boolean disconectTask = ot.execute(value).get();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                Main.lastFlag = true;
                mTimer2.purge();
                mTimer2.cancel();
                mTimer2=null;
                try {
                    // 녹화 시작을 위해  MediaRecorder 객체 recorder를 생성한다.
                    if (recorder == null) {
                        recorder = new MediaRecorder();
                    } else {
                        recorder.stop();
                    }
                    // 오디오와영상 입력 형식 설정
                    if (recorder == null) {
                        recorder = new MediaRecorder();
                    }
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                    // 오디오와영상 인코더 설정
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                    recorder.setMaxDuration(9900);
                    // 저장될 파일 지정
                    filename = createName(System.currentTimeMillis());
                    recorder.setOutputFile(filename);

                    // 녹화도중에 녹화화면을 뷰에다가 출력하게 해주는 설정
                    recorder.setPreviewDisplay(holder.getSurface());

                    // 녹화 준비,시작
                    recorder.prepare();
                    recorder.start();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    recorder.release();
                    recorder = null;
                }
                //카메라 1회 저장 실행 부분


            }
        });
    }
    //////수정필요

    public class ClientCheckTask extends AsyncTask<JSONObject,JSONObject,Boolean> {

        ProgressDialog dialog = new ProgressDialog(Camera_EX.this) ;


        @Override
        protected void onPreExecute() {

            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setTitle("");
            this.dialog.setMessage("서버와 통신 중입니다.");
            this.dialog.setCancelable(false);

            this.dialog.show();
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(JSONObject[] job) {

            try {
                URL url = new URL("http://min.esy.es/CheckClient.php");                //url 지정
                //커넥션 오픈
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(10000 /*ms*/);
                huc.setConnectTimeout(15000 /*ms*/);
                huc.setRequestProperty("Content-Type", "application/json");
                huc.setRequestProperty("Accept", "application/json");
                huc.setRequestProperty("Cache-Control", "no-cache");
                huc.setRequestMethod("POST");//POST

                huc.setDoOutput(true);
                huc.setDoInput(true);
            /*서버로 값 전송 - Output Stream Writer*/

                OutputStreamWriter osw = new OutputStreamWriter(huc.getOutputStream());
                StringBuffer sbW = new StringBuffer(job[0].toString());

                huc.connect();
                osw.write(sbW.toString());

                System.out.println(sbW.toString());

                osw.flush();

            /*서버로부터 받기 - Input Stream Reader*/
                int HttpResult = huc.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    InputStreamReader isr = new InputStreamReader(huc.getInputStream());
                    StringBuffer sbR = new StringBuffer();
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sbR.append(line + "\n");
                    }
                    br.close();

                    String jsonStr = sbR.toString();
                    System.out.println(jsonStr);

                    JSONObject jsonObj = new JSONObject(jsonStr);


                    if( jsonObj.getBoolean("result") == false ){
                        return false;
                    }
                    else{
                        return true;
                    }
                }
                else {
                    System.out.println(huc.getResponseMessage());
                }

     /*통신 여부 확인 보내기 등등*/
                huc.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Boolean result) {
            if(this.dialog != null && this.dialog.isShowing() ){
                this.dialog.dismiss();
            }
            super.onPostExecute(result);

        }
    }


    /*Subclass (ASYNCTASK) */
    public class ConnectClientTask extends AsyncTask<JSONObject,JSONObject,Boolean> {

        ProgressDialog dialog = new ProgressDialog(Camera_EX.this) ;


        @Override
        protected void onPreExecute() {

            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setTitle("");
            this.dialog.setMessage("서버와 통신 중입니다.");
            this.dialog.setCancelable(false);

            this.dialog.show();
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(JSONObject[] job) {

            try {
                URL url = new URL("http://min.esy.es/ConnectClient.php");                //url 지정
                //커넥션 오픈
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(10000 /*ms*/);
                huc.setConnectTimeout(15000 /*ms*/);
                huc.setRequestProperty("Content-Type", "application/json");
                huc.setRequestProperty("Accept", "application/json");
                huc.setRequestProperty("Cache-Control", "no-cache");
                huc.setRequestMethod("POST");//POST

                huc.setDoOutput(true);
                huc.setDoInput(true);
            /*서버로 값 전송 - Output Stream Writer*/

                OutputStreamWriter osw = new OutputStreamWriter(huc.getOutputStream());
                StringBuffer sbW = new StringBuffer(job[0].toString());

                huc.connect();
                osw.write(sbW.toString());

                System.out.println(sbW.toString());

                osw.flush();

            /*서버로부터 받기 - Input Stream Reader*/
                int HttpResult = huc.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    InputStreamReader isr = new InputStreamReader(huc.getInputStream());
                    StringBuffer sbR = new StringBuffer();
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sbR.append(line + "\n");
                    }
                    br.close();

                    String jsonStr = sbR.toString();
                    System.out.println(jsonStr);

                    JSONObject jsonObj = new JSONObject(jsonStr);


                    if( jsonObj.getBoolean("result") == false ){
                        return false;
                    }
                    else{
                        return true;
                    }
                }
                else {
                    System.out.println(huc.getResponseMessage());
                }

     /*통신 여부 확인 보내기 등등*/
                huc.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Boolean result) {
            if(this.dialog != null && this.dialog.isShowing() ){
                this.dialog.dismiss();
            }
            super.onPostExecute(result);

        }
    }

    /*Subclass <ASYNCTASK>*/
    public class DisconnectTask extends AsyncTask<JSONObject,JSONObject,Boolean> {


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(JSONObject[] job){

            try {
                URL url = new URL("http://min.esy.es/DisconnectClient.php");                //url 지정
                String response = null;
                //커넥션 오픈
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(10000 /*ms*/);
                huc.setConnectTimeout(15000 /*ms*/);
                huc.setRequestProperty("Content-Type", "application/json");
                huc.setRequestProperty("Accept", "application/json");
                huc.setRequestProperty("Cache-Control", "no-cache");
                huc.setRequestMethod("POST");//POST

                huc.setDoOutput(true);
                huc.setDoInput(true);
            /*서버로 값 전송 - Output Stream Writer*/

                OutputStreamWriter osw = new OutputStreamWriter(huc.getOutputStream());
                StringBuffer sbW = new StringBuffer(job[0].toString());

                huc.connect();
                osw.write(sbW.toString());

                osw.flush();


            /*서버로부터 받기 - Input Stream Read=er*/
                int HttpResult = huc.getResponseCode();
                if(HttpResult == HttpURLConnection.HTTP_OK){
                    InputStreamReader isr = new InputStreamReader(huc.getInputStream());
                    StringBuffer sbR = new StringBuffer();
                    BufferedReader br = new BufferedReader(isr) ;
                    String line = null;
                    while ( (line = br.readLine() ) != null){
                        sbR.append(line + "\n");
                    }
                    br.close();

                    String jsonStr = sbR.toString();
                    System.out.println(jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    if( jsonObj.getBoolean("result") == false ){
                        return false;
                    }
                    else{
                        return true;
                    }

                }
                else{
                    System.out.println(huc.getResponseMessage());
                }

            /*통신 여부 확인 보내기 등등*/
                huc.disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }


            return null;
        }

        // 개발자가 정의한 스레드가 doInBackground() 메서드를 수행하는 동안
        // 중간 중간에 UI를 제어하는 등의 작업이 가능!
        // @이 메서드는 메인스레드에 의해 수행된다!@
        // @UI 제어 가능@

        protected void onProgressUpdate(JSONObject[] values) {
            super.onProgressUpdate(values);
        }

    }

    /*Subclass <ASYNCTASK>*/
    public class CheckEveryTask extends AsyncTask<JSONObject,JSONObject,JSONObject> {


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(JSONObject[] job){

            try {
                URL url = new URL("http://min.esy.es/CheckEverySeconds.php");                //url 지정
                String response = null;
                //커넥션 오픈
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(10000 /*ms*/);
                huc.setConnectTimeout(15000 /*ms*/);
                huc.setRequestProperty("Content-Type", "application/json");
                huc.setRequestProperty("Accept", "application/json");
                huc.setRequestProperty("Cache-Control", "no-cache");
                huc.setRequestMethod("POST");//POST

                huc.setDoOutput(true);
                huc.setDoInput(true);
            /*서버로 값 전송 - Output Stream Writer*/

                OutputStreamWriter osw = new OutputStreamWriter(huc.getOutputStream());
                StringBuffer sbW = new StringBuffer(job[0].toString());

                huc.connect();
                osw.write(sbW.toString());

                osw.flush();


            /*서버로부터 받기 - Input Stream Read=er*/
                int HttpResult = huc.getResponseCode();
                if(HttpResult == HttpURLConnection.HTTP_OK){
                    InputStreamReader isr = new InputStreamReader(huc.getInputStream());
                    StringBuffer sbR = new StringBuffer();
                    BufferedReader br = new BufferedReader(isr) ;
                    String line = null;
                    while ( (line = br.readLine() ) != null){
                        sbR.append(line + "\n");
                    }
                    br.close();

                    String jsonStr = sbR.toString();
                    System.out.println(jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    return jsonObj;

                }
                else{
                    System.out.println(huc.getResponseMessage());
                }

            /*통신 여부 확인 보내기 등등*/
                huc.disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }


            return null;
        }

        // 개발자가 정의한 스레드가 doInBackground() 메서드를 수행하는 동안
        // 중간 중간에 UI를 제어하는 등의 작업이 가능!
        // @이 메서드는 메인스레드에 의해 수행된다!@
        // @UI 제어 가능@

        protected void onProgressUpdate(JSONObject[] values) {
            super.onProgressUpdate(values);
        }

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



