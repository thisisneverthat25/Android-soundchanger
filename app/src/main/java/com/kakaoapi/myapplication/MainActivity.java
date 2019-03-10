package com.kakaoapi.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SpeechRecognizeListener {

    private String TAG = "MainActivity";
    private static final int REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 0;

    private Switch switchBtn;
    private TextView result;
    private ImageView voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);
        voice = (ImageView) findViewById(R.id.voiceIcon);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE);
                Log.d(TAG, "onCreate==> 성공 ");
            } else {
                // 유저가 거부하면서 다시 묻지 않기를 클릭.. 권한이 없다고 유저에게 직접 알림.
                Log.d(TAG, "onCreate==> 권환이 없습니다....");
            }
        } else {
            //startUsingSpeechSDK();
            Log.d(TAG, " onCreate==> startUsingSpeechSDK ");
        }


        switchBtn = (Switch) findViewById(R.id.switchBtn);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 클라이언트 생성
                SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().
                        setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB);
                //.setUserDictionary("Sound");  // optional

                SpeechRecognizerClient client = builder.build();

                if (isChecked) {
                    client.startRecording(true);
                    client.setSpeechRecognizeListener(MainActivity.this);
                } else {
                    client.stopRecording();
                }

            }
        });
    }

    public void getDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        // PhoneStyle.getDisplay(width, height);
    }

    public void getKeyHash() {
        /*
         *  key number :: ==> XM7mo2JQGdoMkKB2vhTuIynWL6k=
         * */
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.kakaoapi.myapplication", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /*
     *  SpeechRecognizeListener method
     * */
    @Override
    public void onReady() {
        Log.d(TAG, "onReady ==> start now");
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        String msg = null;

        switch (errorCode) {
            case SpeechRecognizerClient.ERROR_AUDIO_FAIL:
                msg = "음성입력이 불가능하거나 마이크 접근이 허용되지 않았을 경우";
                break;
            case SpeechRecognizerClient.ERROR_AUTH_FAIL:
                msg = "apikey 인증이 실패한 경우";
                break;
            case SpeechRecognizerClient.ERROR_NETWORK_FAIL:
                msg = "네트워크 오류가 발생한 경우";
                break;
            case SpeechRecognizerClient.ERROR_NETWORK_TIMEOUT:
                msg = "네트워크 타임아웃이 발생한 경우";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_FAIL:
                msg = "서버에서 오류가 발생한 경우";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_TIMEOUT:
                msg = "서버 응답 시간이 초과한 경우";
                break;
            case SpeechRecognizerClient.ERROR_NO_RESULT:
                msg = "인식된 결과 목록이 없는 경우";
                break;
            case SpeechRecognizerClient.ERROR_CLIENT:
                msg = "클라이언트 내부 로직에서 오류가 발생한 경우";
                break;
            case SpeechRecognizerClient.ERROR_RECOGNITION_TIMEOUT:
                msg = "전체 소요시간에 대한 타임아웃이 발생한 경우";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_UNSUPPORT_SERVICE:
                msg = "제공하지 않는 서비스 타입이 지정됐을 경우";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_USERDICT_EMPTY:
                msg = "입력된 사용자 사전에 내용이 없는 경우";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_ALLOWED_REQUESTS_EXCESS:
                msg = "요청 허용 횟수 초과";
                break;
            default:
                msg = "기본 에러 입니다";
                break;
        }
        Log.d(TAG, "onError ==> " + msg);
    }//onError end

    @Override
    public void onPartialResult(String partialResult) {

    }

    @Override
    public void onResults(Bundle results) {

        final StringBuilder builder = new StringBuilder();

        final ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
        ArrayList<Integer> confs = results.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES);

        final String[] resultMsg = {null};


        //모든 콜백함수들은 백그라운드에서 돌고 있기 때문에 메인 UI를 변경할려면 runOnUiThread를 사용해야 한다.
        final Activity activity = MainActivity.this;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (activity.isFinishing()) return;
                String setText = "";
                if (texts == null) {
                    Log.d(TAG, "run: null ");
                } else {

                    for (String s : texts) {
                        String tmp = s;
                        setText += s;
                    }

                    texts.clear();
                }

                result.setText(setText);
            }
        });
    }

    @Override
    public void onAudioLevel(float audioLevel) {
        //데쉬벨 함수
        //Log.d(TAG, "onAudioLevel ==> " + audioLevel);
    }

    @Override
    public void onFinished() {
        Log.d(TAG, "onFinished ==> success");
    }


    public void randomBackground() {

        Random rand = new Random();
        int r = rand.nextInt(256);
      /*  int g = rand.nextInt(256);
        int b = rand.nextInt(256);*/
/*
        voice.setBackgroundColor(Color.rgb(r, g, b));
        Log.d(TAG, "randomBackground: " + r + " :: " + g + " :: " + b);*/
    }
}
