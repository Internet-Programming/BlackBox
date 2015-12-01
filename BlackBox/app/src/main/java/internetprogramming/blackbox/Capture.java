package internetprogramming.blackbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.ExecutionException;

public class Capture extends AppCompatActivity {

    ProgressDialog dialog = null;
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
        setContentView(R.layout.activity_capture);

        dialog = ProgressDialog.show(Capture.this, "", "Uploading file....", true);

        String isSuccess = null;
        String filePath = Environment.getExternalStorageDirectory()+"/BlackBox/test.mp4";

        UploadFile rt = new UploadFile();
        try {
            isSuccess = rt.execute(filePath).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(isSuccess);
        dialog.dismiss();

        /*카메라 불러오기 테스트*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
