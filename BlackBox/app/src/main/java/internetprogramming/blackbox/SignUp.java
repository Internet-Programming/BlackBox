package internetprogramming.blackbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                    userInfo.put("Num", inputVNumber.toString());
                    userInfo.put("PW", inputVPassword.toString());

                    executeClient(userInfo);

                } catch (JSONException e) {
                }
            }
        });

    }

    public void executeClient(JSONObject job) {

        try {
            URL url = new URL("http://min.esy.es/SignUp.php");                //url 지정
            OutputStream oS = null;
            //InputStream iS = null;

            //커넥션 오픈
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("POST");//POST
            //InputStream 으로 서버로부터 응답 헤더와 메시지를 읽어들인다.
            //huc.setDoInput(true);
            //OutputStream 으로 서버에 POST 데이터를 넘겨주겠다.
            huc.setDoOutput(true);

            huc.setRequestProperty("Cache-Control", "no-cache");
            huc.setRequestProperty("Content-Type","application/json");
            huc.setRequestProperty("Accept","application/json");

            oS = huc.getOutputStream();
            oS.write(job.toString().getBytes());
            oS.flush();
        }
        catch(MalformedURLException e){}
        catch(IOException e){}

    }
}
