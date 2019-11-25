package com.wz.android.acckitdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    public static final String TAG = "AccDemoMainactivity";
    private HuaweiIdSignInClient mSignInClient;
    HuaweiIdSignInOptions mSignInOptions;
    TextView showinfoview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showinfoview = this.findViewById(R.id.showInfoView);


        findViewById(R.id.btn_idTokenSignIn).setOnClickListener(this);
        findViewById(R.id.btn_AuthSingIn).setOnClickListener(this);
        findViewById(R.id.btn_Logout).setOnClickListener(this);

        //Initialize the HuaweiIdSignInClient object by
        // calling the getClient method of Huawei Id Sign in

        mSignInOptions = new HuaweiIdSignInOptions.Builder(
                HuaweiIdSignInOptions.DEFAULT_SIGN_IN
        ).requestAccessToken().requestIdToken("").build();

        mSignInClient = HuaweiIdSignIn.getClient(MainActivity.this,mSignInOptions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_idTokenSignIn:
                idTokenSignIn();
                break;
            case R.id.btn_AuthSingIn:
                authSignIn();
                break;
            case R.id.btn_Logout:
                hwLogout();
                break;

                default:
                    break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN) {
            //login success
            //get user message by getSignedInAccountFromIntent
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                Log.i(TAG, "signIn success " + huaweiAccount.getDisplayName());
                Log.e(TAG,"\n AccessToken:"+ huaweiAccount.getAccessToken());
                String testviewstring = "signIn success " + huaweiAccount.getDisplayName();
                testviewstring = testviewstring + "\n AccessToken:"+ huaweiAccount.getAccessToken();
                testviewstring = testviewstring + "\n Display Photo url: "+ huaweiAccount.getPhotoUriString();

                showinfoview.setText(testviewstring);
            } else {
                Log.i(TAG, "signIn failed: " + ((ApiException) signInHuaweiIdTask.getException()).getStatusCode());
                showinfoview.setText("signIn failed: " + ((ApiException) signInHuaweiIdTask.getException()).getStatusCode());
            }
        }
        if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN_CODE) {
            //login success
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                Log.i(TAG, "signIn get code success.");
                Log.e(TAG,"\n ServerAuthCode:"+ huaweiAccount.getServerAuthCode());
                String testviewstring = "signIn success " + huaweiAccount.getDisplayName();
                testviewstring = testviewstring + "\n ServerAuthCode:"+ huaweiAccount.getServerAuthCode();
                testviewstring = testviewstring + "\n Display Photo url: "+ huaweiAccount.getPhotoUriString();

                showinfoview.setText(testviewstring);
                SecureRandom entropySource = new SecureRandom();
                byte[] randomBytes = new byte[64];
                entropySource.nextBytes(randomBytes);
                Log.e(TAG, "verifier:"+Base64.encodeToString(randomBytes, Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE));


            } else {
                Log.i(TAG, "signIn get code failed: " + ((ApiException) signInHuaweiIdTask.getException()).getStatusCode());
                showinfoview.setText("signIn get code failed: " + ((ApiException) signInHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    private void idTokenSignIn(){
        mSignInOptions = new HuaweiIdSignInOptions.Builder(
                HuaweiIdSignInOptions.DEFAULT_SIGN_IN
        ).requestAccessToken().requestIdToken("").build();

        mSignInClient = HuaweiIdSignIn.getClient(MainActivity.this,mSignInOptions);
        startActivityForResult(mSignInClient.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN);
    }

    private void authSignIn(){
        mSignInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN).requestServerAuthCode().build();
        mSignInClient = HuaweiIdSignIn.getClient(MainActivity.this, mSignInOptions);
        startActivityForResult(mSignInClient.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN_CODE);
    }

    private void hwLogout(){
        final Task<Void> signOutTask = mSignInClient.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "sign out Success!");
                showinfoview.setText("sign out Success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "sign out fail");
                showinfoview.setText("sign out fail!");
            }
        });

    }


}
