package com.example.ruth.messageproject;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.Message;
import com.example.ruth.messageproject.Adapters.ListAdapter;
import com.example.ruth.messageproject.SQLite.DBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ruth on 07/06/2017.
 */

public class UsersActivity extends Activity {

    private static final String appId = "C0CFFC67-D468-7455-FFE7-E6619F034500", v = "v1", key = "FE4B674B-3707-FBC6-FF26-708474F92400";
    SQLiteDatabase db;
    TextView uName;
    String GCMSenderId;
    String chatId, userId, helloName;
    ListAdapter adapter;
    private ArrayList<String> names = new ArrayList<String>();
    Message[] response;
    String usersId;
    public static final int REQUEST_CODE = 2;
    String thisEmail, thisName,UserEmailItem,ownerId;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        GCMSenderId = getResources().getString(R.string.GCMSenderId);
        Backendless.initApp(this, appId, key, v);
        db = new DBManager(this).getWritableDatabase();
        adapter = new ListAdapter(names, this);
        uName = (TextView) findViewById(R.id.etName);

        final Bundle bundle = getIntent().getExtras();
        String myUserName = bundle.getString("myName");
        String myUserEmail = bundle.getString("myEmail");

        uName.setText(myUserName);
        thisEmail = myUserEmail+" ";
        thisName = myUserName+" ";


        final ListView lv = (ListView) findViewById(R.id.listView);
        lv.setTextFilterEnabled(true);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        Backendless.Data.of(BackendlessUser.class).find(new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> users) {
                Iterator<BackendlessUser> userIterator = users.getCurrentPage().iterator();
                while (userIterator.hasNext()) {
                    BackendlessUser user = userIterator.next();
                    names.add(user.getEmail());
                }
                lv.setAdapter(adapter);
            }
            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                System.out.println("Server reported an error - " + backendlessFault.getMessage());
            }
        });
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserEmailItem =  lv.getAdapter().getItem(i).toString();
                 createChat();
            }
        });
    }
    private void createChat(){
        Map chat = new HashMap();
        //After user is logged in - you can get his id by:
        final String userId = Backendless.UserService.CurrentUser().getUserId();
        chat.put("ownerId",userId);//logged user is the owner
        chat.put("chatName","myChat");
        chat.put("channel","default");//same as channel name - by chosen architecture
        Backendless.Data.of("Chats").save(chat, new AsyncCallback<Map>() {
            public void handleResponse(Map map) {
                String chatId = map.get("objectId").toString();//Id of created chat
                //Assume that xxx - is logged user
                addUserToChat(chatId,userId);//add self
                Toast.makeText(getApplicationContext(), "createChat", Toast.LENGTH_LONG).show();
            }
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(getApplicationContext(), "createChat failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addUserToChat(final String chatId, String userId){
        Map chatter = new HashMap();
        chatter.put("chatId",chatId);
        chatter.put("userId",userId);//Id of logged user
        Backendless.Data.of("Chatters").save(chatter, new AsyncCallback<Map>() {
            public void handleResponse(Map map) {
                Toast.makeText(getApplicationContext(), "addUserToChat", Toast.LENGTH_LONG).show();
            }
            public void handleFault(BackendlessFault e) {
                Toast.makeText(getApplicationContext(), "addUserToChat failed", Toast.LENGTH_LONG).show();
            }
        });
        Intent intent = new Intent(UsersActivity.this, ChatsActivity.class);
        Bundle extras = new Bundle();
        extras.putString("thisUEmail",thisEmail);
        extras.putString("thisUName",thisName);
        extras.putString("thisEmailItem",UserEmailItem);
        extras.putString("chatId",chatId);
        extras.putString("userId",userId);
        extras.putString("ownerId",userId);
        intent.putExtras(extras);
        startActivityForResult(intent,REQUEST_CODE);
    }

    public void logOut(View view) {
        // ??
    }

    public void delete(View view) {
    }
}
