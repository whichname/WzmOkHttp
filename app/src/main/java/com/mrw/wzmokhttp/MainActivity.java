package com.mrw.wzmokhttp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.mrw.wzmokhttp.network.HttpRequestCallBack;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);

    }


    public void onClick(View v) {
      switch (v.getId()) {
          case R.id.btn_clear:
              tv.setText("");
              break;
          case R.id.btn_login:
              new LoginRequest().startPost(new HttpRequestCallBack() {
                  @Override
                  public void onSucceed(JSONObject jsonObject) {
                      tv.setText(jsonObject.toString());
                  }

                  @Override
                  public void onError(int errorType, int errorCode, String errorMsg) {
                        tv.setText(errorMsg);
                  }
              });
              break;
          case R.id.btn_get_user_info:
              new UserInfoRequest("561f765540be33f6166be3cb").startPost(new HttpRequestCallBack() {
                  @Override
                  public void onSucceed(JSONObject jsonObject) {
                      tv.setText(jsonObject.toString());
                  }

                  @Override
                  public void onError(int errorType, int errorCode, String errorMsg) {
                      tv.setText(errorMsg);
                  }
              });
              break;
          case R.id.btn_upload:

              break;
          case R.id.btn_wv:
              break;
      }
    }


}
