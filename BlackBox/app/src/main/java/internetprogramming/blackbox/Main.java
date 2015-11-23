package internetprogramming.blackbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

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


                    System.out.println(userInfo.toString());

                    SignTask st = new SignTask();
                    st.execute(userInfo);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
//                startActivity(it);
  //              finish();
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
}
