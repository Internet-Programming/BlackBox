package internetprogramming.blackbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SignIn extends AppCompatActivity {

    ProgressDialog dialog = null;

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
        setContentView(R.layout.activity_sign_in);

        ImageButton btnCapture = (ImageButton) findViewById(R.id.imageButton); //캡쳐  버튼
        ImageButton btnList = (ImageButton) findViewById(R.id.imageButton2); //리스트 버튼

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itC = new Intent(getApplicationContext(), Capture.class);
                startActivity(itC);
                finish();
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itL = new Intent(getApplicationContext(), List.class);
                startActivity(itL);
                finish();
            }
        });

    }
}
