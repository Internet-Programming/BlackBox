package internetprogramming.blackbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Capture extends AppCompatActivity {

    ProgressDialog dialog = null;
    @Override
    public void onBackPressed(){
        Intent it = new Intent(getApplicationContext(), Contents.class);

        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(it);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        /*카메라 불러오기 테스트*/
        Intent intent = new Intent(this,Camera_EX.class);
        startActivity(intent);
        finish();
    }
}
