package com.pdumanager.slawek.pdumanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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

            if(response != null && response.equals("true"))
            {
                editor.putString(Username, username);
                editor.putString(Password, password);
                editor.commit();

                Toast.makeText(this, "Logged: " + username, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Wrong credentials!", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    class LoginAttempt extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String username = params[0];
            String password = params[1];
            String modifiedURL = Constants.LOGIN_URL + "username=" + username + "&password=" + password;

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(modifiedURL);

            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                if(httpEntity != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(httpEntity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

}
