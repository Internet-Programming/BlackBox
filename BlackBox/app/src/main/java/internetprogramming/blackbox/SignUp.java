package internetprogramming.blackbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SignUp extends AppCompatActivity {

    private EditText inputVNumber;      //차량번호
    private EditText inputVPassword;    //비밀번호

    public JSONObject userInfo = new JSONObject();

    @Override
    public void onBackPressed(){
        Intent it = new Intent(getApplicationContext(), Main.class);

        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(it);
        finish();
  }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inputVNumber = (EditText) findViewById(R.id.VehicleNumber);
        inputVPassword = (EditText) findViewById(R.id.VehiclePassword);

        Button btnRequest = (Button) findViewById(R.id.buttonRequest);


        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    userInfo.put("Num", inputVNumber.getText().toString());
                    userInfo.put("PW", inputVPassword.getText().toString());

                    boolean isSuccess;

                    System.out.println(userInfo.toString());

                    RegisterTask rt = new RegisterTask();
                    isSuccess = rt.execute(userInfo).get();


                    AlertDialog.Builder alert = new AlertDialog.Builder(SignUp.this);

                    if(isSuccess){
                        /*회원가입 성공*/
                        alert.setMessage("회원가입에 성공하였습니다.");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }
                        );

                        alert.show();

                    }
                    else if(!isSuccess){
                        /*회원가입 실패*/
                        alert.setMessage("이미 존재하는 회원입니다.");
                        alert.setPositiveButton("확인",null);

                        alert.show();
                        System.out.println("Failed");
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

    }

    /*Subclass <ASYNCTASK>*/
    public class RegisterTask extends AsyncTask<JSONObject,JSONObject,Boolean> {

        ProgressDialog dialog = new ProgressDialog(SignUp.this) ;

        @Override
        protected void onPreExecute(){

            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setTitle("");
            this.dialog.setMessage("로그인 중입니다.");
            this.dialog.setCancelable(false);

            this.dialog.show();
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(JSONObject[] job){

            try {
                URL url = new URL("http://min.esy.es/SignUp.php");                //url 지정
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

        // doInBackground() 메서드의 수행이 모두 완료되면,
        // doInBackground() 메서드의 리턴값이 여기의 파라미터로 반환된다
        protected void onPostExecute(Boolean result) {
            if(this.dialog != null && this.dialog.isShowing() ){
                this.dialog.dismiss();
            }
            super.onPostExecute(result);
        }
    }

}
