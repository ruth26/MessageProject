package com.example.ruth.messageproject;


import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.ruth.messageproject.SQLite.DBManager;

public class MainActivity extends Activity {

    String GCMSenderId;
    private static final String appId = "C0CFFC67-D468-7455-FFE7-E6619F034500", v="v1",key ="FE4B674B-3707-FBC6-FF26-708474F92400";
    EditText name, email, pass;
    String userName, userEmail ,userPass;
    SQLiteDatabase db;
    private static String userId;
    public static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GCMSenderId = getResources().getString(R.string.GCMSenderId);
        Backendless.initApp(this, appId, key, v);
        db = new DBManager(this).getWritableDatabase();

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);


    }

    public void register(View view) {
        userName = name.getText().toString();
        userPass = pass.getText().toString();
        userEmail = email.getText().toString();

        if (!userName.isEmpty() && !userEmail.isEmpty()&& !userPass.isEmpty()) {
            BackendlessUser backendlessUser = new BackendlessUser();
            backendlessUser.setProperty("name",userName);
            backendlessUser.setEmail(userEmail);
            backendlessUser.setPassword(userPass);
            Backendless.UserService.register(backendlessUser, new AsyncCallback<BackendlessUser>(){
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {
                    Toast.makeText(getBaseContext(),"click login",Toast.LENGTH_LONG).show();
                    db.execSQL("INSERT INTO users (name, email, password) VALUES ('userName','userEmail','userPass')");
                    Toast.makeText(getBaseContext(),"SQL done",Toast.LENGTH_LONG).show();
                }
                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    if(userEmail.equals(email))
                        Toast.makeText(getBaseContext(),email + "you registered, try login",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void login(View view) {
        userName = name.getText().toString();
        userPass = pass.getText().toString();
        userEmail = email.getText().toString();

        if (!userName.isEmpty() && !userEmail.isEmpty()&& !userPass.isEmpty()) {

            //db.execSQL("SELECT email FROM users WHERE name='"+userName+ "' AND password='"+userPass+"'");
            Toast.makeText(this, "login SQL", Toast.LENGTH_SHORT).show();

            Backendless.UserService.login(userEmail, userPass, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser loggedUser) {
                    userId = loggedUser.getUserId();
                    Intent intent = new Intent(MainActivity.this, UsersActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("myEmail",userEmail);
                    extras.putString("myName",userName);
                    intent.putExtras(extras);
                    startActivityForResult(intent,REQUEST_CODE);
                }

                @Override
                public void handleFault(BackendlessFault e) {
                    //check why - not?
                    Toast.makeText(getBaseContext(), "failed, please try write again", Toast.LENGTH_LONG).show();
                }
            }, true);
        }
    }

}
