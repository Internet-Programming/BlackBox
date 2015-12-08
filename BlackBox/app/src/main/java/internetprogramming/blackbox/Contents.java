package internetprogramming.blackbox;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Contents extends ActivityGroup {

    @Override
    public void onBackPressed(){

        AlertDialog.Builder alert = new AlertDialog.Builder(Contents.this);

            alert.setMessage("로그아웃 하시겠습니까?");
            alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent it = new Intent(getApplicationContext(), Main.class);

                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(it);
                            finish();
                        }
                    }
            );
            alert.setNegativeButton("아니오",null);

            alert.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TabHost tabhost = (TabHost)findViewById(R.id.tabHost);

        LayoutInflater.from(this).inflate(R.layout.activity_list, tabhost.getTabContentView(), false);
        LayoutInflater.from(this).inflate(R.layout.activity_capture, tabhost.getTabContentView(), false);

        tabhost.setup(getLocalActivityManager());

        tabhost.addTab(tabhost.newTabSpec("리스트")
                .setIndicator("List")
                .setContent(new Intent(this, List.class)));

        tabhost.addTab(tabhost.newTabSpec("촬영")
                .setIndicator("Capture")
                .setContent(new Intent(this, Capture.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }
}
