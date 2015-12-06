package internetprogramming.blackbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dd.processbutton.iml.SubmitProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import internetprogramming.blackbox.utils.ProgressGenerator;

public class Main extends AppCompatActivity  implements ProgressGenerator.OnCompleteListener  {

    private EditText inputVNumber;      //차량번호
    private EditText inputVPassword;    //비밀번호

    private Button btnSignUp;           //회원가입
    private Button btnSignIn;           //로그인

    static String MYCARNUMBER;
    static String YOURCARNUMBER;

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent("SensorService"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputVNumber = (EditText) findViewById(R.id.VehicleNumber);
        inputVPassword = (EditText) findViewById(R.id.VehiclePassword);

        btnSignIn = (Button) findViewById(R.id.buttonSignIn); //로그인  버튼
        btnSignUp = (Button) findViewById(R.id.buttonSignUp); //회원가입 버튼

        final EditText editMessage = (EditText) findViewById(R.id.editMessage);

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final SubmitProcessButton btnSend = (SubmitProcessButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressGenerator.start(btnSend);
                btnSend.setEnabled(false);
                editMessage.setEnabled(false);
            }
        });

        //삭제 꼭 필요....
        startService(new Intent("SensorService"));

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent it = new Intent(getApplicationContext(), SignIn.class);
                try {
                    JSONObject userInfo = new JSONObject();

                    userInfo.put("Num", inputVNumber.getText().toString());
                    userInfo.put("PW", inputVPassword.getText().toString());
                    boolean isSuccess;

                    System.out.println(userInfo.toString());

                    SignTask st = new SignTask();
                    isSuccess = st.execute(userInfo).get();

                    if (isSuccess) {
                        /*로그인 성공*/
                        System.out.println("Success");

                        /*BlackBox 폴더 생성*/
                        String env = Environment.getExternalStorageDirectory().getAbsolutePath();
                        System.out.println(env);

                        File dir = new File(env + "/BlackBox/");
                        if (!dir.exists()) {
                            dir.mkdir();
                        } else {
                            System.out.println("the Directory Already Exists");
                        }

                        Intent cit = new Intent(getApplicationContext(), SignIn.class);
                        MYCARNUMBER = userInfo.getString("Num");
                        startActivity(cit);
                        finish();
                    } else if (!isSuccess) {
                        /*로그인 실패*/

                        AlertDialog.Builder alert = new AlertDialog.Builder(Main.this);

                        alert.setMessage("잘못된 회원 정보입니다.");
                        alert.setPositiveButton("확인", null);
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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), SignUp.class);
                startActivity(it);
            }
        });


    }

    @Override
    public void onComplete() {

    }

    /*Subclass (ASYNCTASK) */
    public class SignTask extends AsyncTask<JSONObject,JSONObject,Boolean> {

        ProgressDialog dialog = new ProgressDialog(Main.this) ;


        @Override
        protected void onPreExecute() {

            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setTitle("");
            this.dialog.setMessage("로그인 중입니다.");
            this.dialog.setCancelable(false);

            this.dialog.show();
            super.onPreExecute();

        }

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

                huc.connect();
                osw.write(sbW.toString());

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

        protected void onProgressUpdate(JSONObject[] values) {
            super.onProgressUpdate(values);
        }


        protected void onPostExecute(Boolean result) {
            if(this.dialog != null && this.dialog.isShowing() ){
                this.dialog.dismiss();
            }
            super.onPostExecute(result);

        }
    }

}
