package com.pdumanager.slawek.pdumanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.cert.LDAPCertStoreParameters;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mLoginButton;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    public static final String Username = "username";
    public static final String Password = "password";
    SharedPreferences sharedPrefs;
    private boolean backPressedToExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = (Button) findViewById(R.id.button_login);
        mUsernameEditText = (EditText) findViewById(R.id.username_input);
        mPasswordEditText = (EditText) findViewById(R.id.password_input);

        mLoginButton.setOnClickListener(this);

        sharedPrefs = getSharedPreferences(MenuActivity.MyPREFERENCES, Context.MODE_PRIVATE);
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

    @Override
    public void onClick(View view) {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if(username.equals("mwojdyla") && password.equals("zaq12wsx"))
        {
            SharedPreferences.Editor editor =  sharedPrefs.edit();

            editor.putString(Username, username);
            editor.putString(Password, password);
            editor.commit();

            Toast.makeText(getApplicationContext(), "Logged: " + username, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Wrong credentials!", Toast.LENGTH_LONG).show();
        }
    }
}
