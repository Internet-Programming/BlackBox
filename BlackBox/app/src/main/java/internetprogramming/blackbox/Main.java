package internetprogramming.blackbox;

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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Main extends AppCompatActivity {

    private EditText inputVNumber;      //차량번호
    private EditText inputVPassword;    //비밀번호

    private Button btnSignUp;           //회원가입
    private Button btnSignIn;           //로그인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputVNumber = (EditText) findViewById(R.id.VehicleNumber);
        inputVPassword = (EditText) findViewById(R.id.VehiclePassword);

        btnSignIn = (Button) findViewById(R.id.buttonSignIn); //로그인  버튼
        btnSignUp = (Button) findViewById(R.id.buttonSignUp); //회원가입 버튼

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent it = new Intent(getApplicationContext(), SignIn.class);
                try{
                    JSONObject userInfo = new JSONObject();

                    userInfo.put("Num", inputVNumber.getText().toString());
                    userInfo.put("PW", inputVPassword.getText().toString());
                    boolean isSuccess;

                    System.out.println(userInfo.toString());

                    SignTask st = new SignTask();
                    isSuccess = st.execute(userInfo).get();

                    if(isSuccess){
                        /*로그인 성공*/
                        System.out.println("Success");
                        Intent cit = new Intent(getApplicationContext(), SignIn.class);
                        startActivity(cit);
                        finish();
                    }
                    else if(!isSuccess){
                        /*로그인 실패*/
                        System.out.println("Failed");
                    }

                }
                catch(JSONException e){
                    e.printStackTrace();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), SignUp.class);
                startActivity(it);
                finish();
            }
        });
    }

    /*Subclass (ASYNCTASK) */
    public class SignTask extends AsyncTask<JSONObject,JSONObject,Boolean> {

        // 스레드가 동작하기 전, 초기화등의 작업에 사용하면 유용
        // @이 메서드는 메인스레드에 의해 수행된다!@
        // @UI 제어 가능@
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // 하위 스레드에 의해 동작하는 메서드
        // 메인 스레드와는 독립적으로 수행 할 영역은 이 메서드를 이용한다.
        // @UI 제어 불가@

        @Override
        protected Boolean doInBackground(JSONObject[] job) {

            try {
                URL url = new URL("http://min.esy.es/SignIn.php");                //url 지정
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


                OutputStream os = huc.getOutputStream();

                huc.connect();
                osw.write(job[0].toString());
                osw.flush();


            /*서버로부터 받기 - Input Stream Read=er*/
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


                    if ((jsonObj.getJSONObject("data")).getBoolean("success") == false) {
                        return false;
                    } else {
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
            super.onPostExecute(result);
        }
    }

}
