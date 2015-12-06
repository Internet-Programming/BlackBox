package internetprogramming.blackbox;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SensorService extends Service implements SensorEventListener {

    private static final String TAG = "SensorService";

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    int count=0;

    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;


    public SensorService() {
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void onSensorChanged(SensorEvent event){

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    // 이벤트발생!!
                    System.out.println("센서:" +speed);

                    JSONObject value = new JSONObject();

                    try {
                        value.put("MyNum", Main.MYCARNUMBER);
                        value.put("YourNum", Main.YOURCARNUMBER);

                        // {"Num" : input.getText().toString() };
                        // Do something with value!
                        RegisterTask ot = new RegisterTask();

                        boolean registerShock = ot.execute(value).get();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }


                    count++;
                }

                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "OnStartCommand() 호출");

        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);

        //Toast.makeText(getApplicationContext(), count, Toast.LENGTH_LONG).show();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"OnCreate() 호출");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"OnDestroy() 호출");

        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*Subclass <ASYNCTASK>*/
    public class RegisterTask extends AsyncTask<JSONObject,JSONObject,Boolean> {


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(JSONObject[] job){

            try {
                URL url = new URL("http://min.esy.es/RegisterShock.php");                //url 지정
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

                    System.out.println("가속도 센서 작동 및 통신");

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
}