package com.pdumanager.slawek.pdumanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
//import org.spongycastle.crypto.PBEParametersGenerator;
//import org.spongycastle.crypto.digests.SHA256Digest;
//import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
//import org.spongycastle.crypto.params.KeyParameter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mLoginButton;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    private boolean backPressedToExit;

    public static final String Username = "username";
    public static final String Password = "password";

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = (Button) findViewById(R.id.button_login);
        mUsernameEditText = (EditText) findViewById(R.id.username_input);
        mPasswordEditText = (EditText) findViewById(R.id.password_input);

        sharedPrefs = getSharedPreferences(MenuActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        if(!isConnected()) {
            Toast.makeText(this, "Connection is not available. Please check your network and restart app.", Toast.LENGTH_LONG).show();
        }
        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if(backPressedToExit){
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } else {
            backPressedToExit = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SimpleDateFormat") @Override
    public void onClick(View view) {
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        editor = sharedPrefs.edit();

        try {
            String response = new LoginAttempt().execute(username, password).get();

            if(response == null)
            {
                Toast.makeText(this, "Unknown error, please try again...", Toast.LENGTH_LONG).show();
            }
            else if(response != null && response.equals("true"))
            {
                editor.putString(Username, username);
                editor.putString(Password, password);
                editor.commit();

                Toast.makeText(this, "Logged: " + username, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            }
            else if(response != null && response.equals("false"))
            {
                Toast.makeText(this, "Wrong credentials!", Toast.LENGTH_LONG).show();
            }
            else if(response != null && response.equals("refused"))
            {
                Toast.makeText(this, "Connection to server refused.", Toast.LENGTH_LONG).show();
            }
            else if(response != null && response.equals("time_out")) {
                Toast.makeText(this, "Connection timed out, please try again...", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                Log.i("isConnected", "Connected to mobile type network");
            else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                Log.i("isConnected", "Connected to wi-fi type network");
            else if (networkInfo.getType() == ConnectivityManager.TYPE_VPN)
                Log.i("isConnected", "Connected to VPN type network");
            return true;
        }
        else
            return false;
    }

    class LoginAttempt extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String username = params[0];
            String password = params[1];

//            //1
//            PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
//            try {
//                gen.init("password".getBytes("UTF-8"), "salt".getBytes(), 4096);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            byte[] dk = ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
//
//            //2
//            PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
//            generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes((char[])password), salt, iterations);
//            KeyParameter key = (KeyParameter)generator.generateDerivedMacParameters(keySizeInBits);

            String modifiedURL = Constants.LOGIN_URL + "username=" + username + "&password=" + password;

            HttpGet httpGet = new HttpGet(modifiedURL);
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            HttpClient httpClient = new DefaultHttpClient(httpParams);

            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                if (httpEntity != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(httpEntity);
                }
            } catch(HttpHostConnectException e) {
                Log.w("LoginAttempt", "Connection to server refused");
                result = "refused";
                return result;
            } catch (ConnectTimeoutException e) {
                Log.w("LoginAttempt", "Connection timed out");
                result = "time_out";
                return result;
            } catch (IOException e) {
                Log.e("LoginAttempt", "Caught IOException");
                e.printStackTrace();
                return result;
            }

            return result;
        }

    }

}
