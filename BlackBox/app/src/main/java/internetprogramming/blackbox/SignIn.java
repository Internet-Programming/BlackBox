package internetprogramming.blackbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SignIn extends AppCompatActivity {

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

        Intent intent = getIntent();
        String VehicleName = intent.getExtras().getString("VehicleName");
        String VehiclePassword = intent.getExtras().getString("VehiclePassword");


    }
}
