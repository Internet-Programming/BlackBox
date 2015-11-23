package internetprogramming.blackbox;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by Jin-Seok on 2015-11-16.
 */
public class RegisterTask extends AsyncTask<JSONObject,JSONObject,String>{

    // 스레드가 동작하기 전, 초기화등의 작업에 사용하면 유용
    // @이 메서드는 메인스레드에 의해 수행된다!@
    // @UI 제어 가능@
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    // 하위 스레드에 의해 동작하는 메서드
    // 메인 스레드와는 독립적으로 수행 할 영역은 이 메서드를 이용한다.
    // @UI 제어 불가@

    @Override
    protected String doInBackground(JSONObject[] job){

        try {
            URL url = new URL("http://min.esy.es/SignUp.php");                //url 지정
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



            OutputStream os = huc.getOutputStream();

            huc.connect();
            osw.write(job[0].toString());
            osw.flush();


            /*서버로부터 받기 - Input Stream Read=er*/
            int HttpResult = huc.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                InputStreamReader isr = new InputStreamReader(huc.getInputStream());
                StringBuffer sbR = new StringBuffer();
                BufferedReader br = new BufferedReader(isr) ;
                String line = null;
                while ( (line = br.readLine() ) != null){
                    sbR.append(line + "\n");
                }
                br.close();

                System.out.println(""+sbR.toString());
            }
            else{
                System.out.println(huc.getResponseMessage());
            }

    /*통신 여부 확인 보내기 등등*/
            huc.disconnect();
            return response;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 개발자가 정의한 스레드가 doInBackground() 메서드를 수행하는 동안
    // 중간 중간에 UI를 제어하는 등의 작업이 가능!
    // @이 메서드는 메인스레드에 의해 수행된다!@
    // @UI 제어 가능@

    protected void onProgressUpdate(JSONObject[] values) {
        super.onProgressUpdate(values);
    }

    // doInBackground() 메서드의 수행이 모두 완료되면,
    // doInBackground() 메서드의 리턴값이 여기의 파라미터로 반환된다
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
}
