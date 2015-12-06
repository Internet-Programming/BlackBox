package internetprogramming.blackbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;
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

public class Test extends AppCompatActivity {

    private Button btnOther;

    @Override
    public void onBackPressed(){
        Intent it = new Intent(getApplicationContext(), SignIn.class);

        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(it);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnOther = (Button) findViewById(R.id.buttonOther); //회원가입 버튼

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*What should I do */
                AlertDialog.Builder alert = new AlertDialog.Builder(Test.this);
                alert.setTitle("");
                alert.setMessage("맞은편 차량번호를 입력하세요.");
                // Set an EditText view to get user input
                final EditText input = new EditText(Test.this);

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
                            OtherTask ot = new OtherTask();

                            ot.execute(value);
                        } catch (JSONException e) {
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

    }


    /*Subclass (ASYNCTASK) */
    public class OtherTask extends AsyncTask<JSONObject,JSONObject,Boolean> {

        ProgressDialog dialog = new ProgressDialog(Test.this) ;


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
                }
                else {
                    System.out.println(huc.getResponseMessage());
                }

     /*통신 여부 확인 보내기 등등*/
                huc.disconnect();

            } catch (IOException e) {
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

}
