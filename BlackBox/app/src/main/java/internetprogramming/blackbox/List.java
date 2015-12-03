package internetprogramming.blackbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;



/*
{
    "result":true,
    "data":{
        "filelist":[
            {"URI":"videos\/test4.mp4",
            "filename":"test4.mp4"},
            {"URI":"videos\/test.mp4",
            "filename":"test.mp4"}
         ],
      "success":true
   }
}
 */

public class List extends AppCompatActivity {
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
        setContentView(R.layout.activity_list);

        final ListView listView=(ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);

        listView.setAdapter(adapter);
        /*여기서 부터 리스트를 불러옵니다*/

        JSONObject userInfo = new JSONObject();

        try {
            userInfo.put("Num", Main.CARNUMBER);

            ListTask lt = new ListTask();
            JSONArray items =  lt.execute(userInfo).get(); //서버와 통신한 후 리스트를 불러온다.

            /*리스트 항목을 리스트뷰에 add*/
            for(int i = 0;i < items.length(); i++){
                adapter.add(items.getJSONObject(i).getString("filename"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //첫번째 파라미터 : 클릭된 아이템을 보여주고 있는 AdapterView 객체(여기서는 ListView객체)
            //두번째 파라미터 : 클릭된 아이템 뷰
            //세번째 파라미터 : 클릭된 아이템의 위치(ListView이 첫번째 아이템(가장위쪽)부터 차례대로 0,1,2,3.....)
            //네번재 파리미터 : 클릭된 아이템의 아이디(특별한 설정이 없다면 세번째 파라이터인 position과 같은 값)

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);

             /*
                JSONObject downloadInfo = new JSONObject();
                try {
                    downloadInfo.put("Num", Main.CARNUMBER);
                    downloadInfo.put("URI", "Videos/test.mp4");
                    DownLoadFileTask  downloadTask = new DownLoadFileTask();
                    boolean isSucess = downloadTask.execute(downloadInfo).get();
                    System.out.println(isSucess);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }*/
            }
        });

    }
///////////////////////////////////////////////////////////////////////////////////////////////
    /*Subclass (ASYNCTASK) */
    public class ListTask extends AsyncTask<JSONObject,JSONObject,JSONArray> {

        ProgressDialog dialog = new ProgressDialog(List.this) ;


        @Override
        protected void onPreExecute() {

            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setTitle("");
            this.dialog.setMessage("목록을 불러오는 중입니다.");
            this.dialog.setCancelable(false);

            this.dialog.show();
            super.onPreExecute();

        }

        @Override
        protected JSONArray doInBackground(JSONObject[] job) {

            try {
                URL url = new URL("http://min.esy.es/VideoList.php");                //url 지정
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
            /*서버로 값 전송 - Output Stream Writer*/ //CAR NUMBER 전송

                OutputStreamWriter osw = new OutputStreamWriter(huc.getOutputStream());
                StringBuffer sbW = new StringBuffer(job[0].toString());

                huc.connect(); // 주의!
                osw.write(sbW.toString());
                osw.flush();

            /*서버로부터 받기 - Input Stream Reader*/ //LIST 가져오기

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
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray jsonArr = ( jsonObj.getJSONObject("data") ).getJSONArray("filelist") ;

                    System.out.println(jsonObj);
                    System.out.println(jsonArr);

                    return jsonArr;
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


        protected void onPostExecute(JSONArray result) {
            if(this.dialog != null && this.dialog.isShowing() ){
                this.dialog.dismiss();
            }
            super.onPostExecute(result);

        }
    }

    /*Subclass (ASYNCTASK) */
    public class DownLoadFileTask extends AsyncTask<JSONObject,JSONObject,Boolean> {

        ProgressDialog dialog = new ProgressDialog(List.this) ;


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
                URL url = new URL("http://min.esy.es/DownLoadVideo.php");                //url 지정
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
                System.out.println("서버 연결");
                huc.connect();
                System.out.println("서버에 데이터 전송");
                osw.write(job[0].toString());
                osw.flush();
                System.out.println(huc.getResponseCode());

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

                    int read;
                    int len = huc.getContentLength();
                    byte[] tempByte = new byte[len];
                    InputStream is = huc.getInputStream();
                    System.out.println("파일 만들기");

                    File file = new File(Environment.getExternalStorageDirectory()+"/BlackBox/min.mp4");
                    FileOutputStream fos = new FileOutputStream(file);
                    System.out.println("파일 다운로드 시작");
                    while (!((read = is.read(tempByte)) <= 0) ) {
                        fos.write(tempByte, 0, read);
                    }
                    System.out.println("이제 파일에 쓴당");

                    fos.close();
                    is.close();

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
