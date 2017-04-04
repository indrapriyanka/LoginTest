package com.example.priyanka.logintest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {
    TextView txtStatus;
    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initializeControls();
        loginWithFB();
    }
    private void initializeControls()
    {
        callbackManager = CallbackManager.Factory.create();
        txtStatus = (TextView)findViewById(R.id.txtstatus);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends"); //get user friends //it will retrieve only friends using this app
    }

    private void loginWithFB()
    {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result){
                    GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                    login_result.getAccessToken(),
                    "/me/friends",
                   //         "/{friend-list-id}",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                                Intent intent = new Intent(MainActivity.this,FriendsList.class);
                            try {
                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    intent.putExtra("jsondata", rawName.toString());
                                    startActivity(intent);
                            } catch (JSONException e) {
                                    e.printStackTrace();
                            }
                        }
                    }).executeAsync();
            }

            @Override
            public void onCancel() {
                txtStatus.setText("Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                txtStatus.setText("Error occured on Login." + error.getMessage());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
