package com.pdumanager.slawek.pdumanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SimpleBindRequest;

import java.sql.Date;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mLoginButton;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    private boolean loginFlag = true;
    private boolean backPressedToExit;

    public static final String Username = "username";
    public static final String Password = "password";

    String password = "secret";
    String address = "ldaps://ed-p-gl.emea.nsn-net.net";
    String bindDN = "uid=%(user)s,ou=users,dc=example,dc=com";
    int port = 389;

    LDAPConnection connection;
    BindRequest bindRequest;
    BindResult bindResult;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = (Button) findViewById(R.id.button_login);
        mUsernameEditText = (EditText) findViewById(R.id.username_input);
        mPasswordEditText = (EditText) findViewById(R.id.password_input);

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

//        try {
//            connection = new LDAPConnection(address, port, bindDN, password);
//            connection.setConnectionName("LDAPConnection");
//            bindRequest = new SimpleBindRequest(username, password);
//            bindResult = connection.bind(bindRequest);
//
//            if(bindResult.getResultCode().isConnectionUsable()) {
//                String conName = connection.getConnectionName();
//                long time = connection.getConnectTime();
//                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd-MM-yy HH:mm:ss");
//                String dateString = formatter.format(new Date(time));
//                Toast.makeText(getBaseContext(),
//                               "Connected to LDAP server....connection_name="+conName+" at time"+dateString,
//                               Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Wrong credentials!", Toast.LENGTH_LONG).show();
//            }
//        } catch(LDAPException e) {
//            loginFlag = false;
//            e.printStackTrace();
//            Toast.makeText(getBaseContext(),"No connection was established" , Toast.LENGTH_LONG).show();
//        } catch(Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(loginFlag) {
//                connection.close();
//                Toast.makeText(getBaseContext(), "Connection Closed successfully", Toast.LENGTH_LONG).show();
//            }
//        }

        if(username.equals("mwojdyla") && password.equals("zaq12wsx"))
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
    }
}
