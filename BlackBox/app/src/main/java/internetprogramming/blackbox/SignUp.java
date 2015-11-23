package internetprogramming.blackbox;

import android.content.Intent;
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

                    System.out.println(userInfo.toString());

                    RegisterTask rt = new RegisterTask();
                    rt.execute(userInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
/*
    public void executeClient(JSONObject job) {

        try {
            URL url = new URL("http://min.esy.es/SignUp.php");                //url 지정

            //커넥션 오픈
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.setRequestProperty("Content-Type", "application/json");
            huc.setRequestProperty("Accept", "application/json");
            //huc.setRequestProperty("Cache-Control", "no-cache");
            huc.setRequestMethod("POST");//POST

            OutputStream oS = huc.getOutputStream();
            OutputStreamWriter wr = new OutputStreamWriter(oS);
            wr.write(job.toString());
            wr.flush();

            //display what returns the POST request

            StringBuilder sb = new StringBuilder();
            int HttpResult = huc.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                InputStream iS = huc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iS,"utf-8")) ;
                String line = null;
                while ( (line = br.readLine() ) != null){
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println(""+sb.toString());
            }
            else{
                System.out.println(huc.getResponseMessage());
            }
        }
        catch(MalformedURLException e){}
        catch(IOException e){}

    }
 */
}
