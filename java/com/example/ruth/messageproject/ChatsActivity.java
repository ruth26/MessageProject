package com.example.ruth.messageproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.messaging.Message;
import com.backendless.messaging.PublishOptions;
import com.backendless.services.messaging.MessageStatus;
import com.example.ruth.messageproject.Adapters.ListAdapter;
import com.example.ruth.messageproject.SQLite.DBManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruth on 06/06/2017.
 */

public class ChatsActivity extends UsersActivity {

    private static final String appId = "C0CFFC67-D468-7455-FFE7-E6619F034500", v = "v1", key = "FE4B674B-3707-FBC6-FF26-708474F92400";
    SQLiteDatabase db;
    EditText etChat;
    Button btnSend;
    TextView MyMessage,myUserId, sendMessage;
    TextClock time;
    ListAdapter adapter;
    private ArrayList<String> names = new ArrayList<String>();
    Message[] response;
    static final int CAMERA = 1, WRITE = 2;
    ImageView img;
    File imgFile;
    String  MyUserEmail, MyUserName, UserEmailItem, MyOwnerId, MyChatId, MyUserId, senderId, currentChatId, usererNickName;

    NotificationManager ntfMngr;
    public static int NTF_ONE=1;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        PermissionManager.check(this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ntfMngr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        GCMSenderId = getResources().getString(R.string.GCMSenderId);
        Backendless.initApp(this, appId, key, v);
        db = new DBManager(this).getWritableDatabase();
        adapter = new ListAdapter(names, this);

        final ListView lv = (ListView) findViewById(R.id.listView);
        etChat = (EditText) findViewById(R.id.etChat);
        btnSend = (Button) findViewById(R.id.btnSend);
        MyMessage = (TextView)findViewById(R.id.message);
        sendMessage = (TextView)findViewById(R.id.sendMessage);
        time = (TextClock)findViewById(R.id.time);
        myUserId = (TextView)findViewById(R.id.myUserId);
        img = (ImageView)findViewById(R.id.image);

        final Bundle bundle = getIntent().getExtras();
        MyUserName = bundle.getString("thisUName");
        MyUserEmail = bundle.getString("thisUEmail");
        UserEmailItem = bundle.getString("thisEmailItem");
        MyChatId = bundle.getString("chatId");
        MyUserId = bundle.getString("userId");
        MyOwnerId = bundle.getString("ownerId");


        usererNickName = UserEmailItem+" ";


        subscribeForChat();

    }

    private void subscribeForChat(){
        Backendless.Messaging.registerDevice(GCMSenderId, "default", new AsyncCallback<Void>() {
            public void handleResponse(Void aVoid) {
               Backendless.Messaging.subscribe( "default", new AsyncCallback<List<Message>>() {
                    @Override
                    public void handleResponse(List<Message> messages) {
                        for( Message message : messages ) {
                            String currentChatId = MyUserId+" ";
                            String MyChatId = message.getHeaders().get("chatId");
                            message.getHeaders().get("sendUserId");
                            String time = message.getHeaders().get("time");
                                String senderId = message.getHeaders().get("senderNickName");
                                String msg = message.getData().toString();
                                sendMessage.setText(msg);
                                addMsgToChat(senderId, msg, time);
                                Toast.makeText(getApplicationContext(), "subscribeForChat "+msg, Toast.LENGTH_LONG).show();
                        }
                    }
                    public void handleFault(BackendlessFault e) {
                        //Toast.makeText(getApplicationContext(), "subscribeForChat11111 failed "+e.toString(), Toast.LENGTH_LONG).show();
                        sendMessage.setText(e.toString()+"subscribeForChat1 failed");
                        //failed
                    }}, new AsyncCallback<Subscription>() {
                   @Override
                   public void handleResponse(Subscription e) {
                       Toast.makeText(getApplicationContext(), "subscribeForChat gooooooood ", Toast.LENGTH_LONG).show();
                   }

                   @Override
               public void handleFault(BackendlessFault e) {
                       ///Toast.makeText(getApplicationContext(), "subscribeForChat2 failed "+e.toString(), Toast.LENGTH_LONG).show();
                       sendMessage.setText(e.toString()+"subscribeForChat2 failed");
                   }
               });
            }
            public void handleFault(BackendlessFault e) {
                //Toast.makeText(getApplicationContext(), "subscribeForChat33333 failed "+e.toString(), Toast.LENGTH_LONG).show();
                sendMessage.setText(e.toString()+"subscribeForChat3 failed");
                //failed
            }
        });

    }


    private void addMsgToChat(String senderId, String msg, String time) {
        if (validImageUrl(msg)) {
            Toast.makeText(getApplicationContext(),"addMsgToChat", Toast.LENGTH_LONG).show();
            //use as image url - load image
            MyMessage.setText(msg);
        }
        else {
            //just show the string
        }
    }


    public void sendToChat(View view) {
        final String userId = Backendless.UserService.CurrentUser().getUserId();
        final String msg = etChat.getText().toString();
        PublishOptions pOpts = new PublishOptions();
        pOpts.putHeader("sendUserId", userId);
        pOpts.putHeader("chatId", MyChatId);
        pOpts.putHeader("thisUName", MyUserName);
        pOpts.putHeader("senderId",usererNickName);
        pOpts.putHeader("time", time.toString());
        //MyMessage.setText(userId+ "/n "+MyChatId+ "/n "+MyUserName+ " /n"+senderId+" /n"+time);
            Backendless.Messaging.publish( "default", msg, pOpts, new AsyncCallback<MessageStatus>() {
                public void handleResponse(MessageStatus messageStatus) {
                    Toast.makeText(getApplicationContext(), " sendToChat", Toast.LENGTH_LONG).show();
                    Map msgObj = new HashMap();
                    //msgObj.put("ownerId", userId);
                    msgObj.put("sendUserId", userId);
                    msgObj.put("chatId", MyChatId);
                    msgObj.put("senderId",usererNickName);
                    msgObj.put("content", msg);
                    msgObj.put("time", time.toString());
                    MyMessage.setText(msg);
                    etChat.setText("");
                    myUserId.setText("from: "+ MyUserName);
                    Backendless.Data.of("Messages").save(msgObj, new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(Map map) {
                            Toast.makeText(getApplicationContext(), "good response Messages ", Toast.LENGTH_LONG).show();
                            sendNtf();
                        }
                        @Override
                        public void handleFault(BackendlessFault e) {
                            Toast.makeText(getApplicationContext(), " sendToChat failed1", Toast.LENGTH_LONG).show();

                        }
                    });
                }
                public void handleFault(BackendlessFault e) {
                    Toast.makeText(getApplicationContext(), " sendToChat failed2", Toast.LENGTH_LONG).show();
                    //failed
                }
            });

    }

    public void pickImg(View v) {
        //implicit Intent for Image picker from camera
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempFile();//create temporary File container for img - and pass to camera
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
        //open camera and wait for for result
        startActivityForResult(i,CAMERA);
        Toast.makeText(getApplicationContext()," pickImg", Toast.LENGTH_LONG).show();
    }

    //save File to Backend-less
    public void saveImg(View v) {
        Backendless.Files.upload(imgFile, "/goldDiggers/" + imgFile.getName(), new AsyncCallback<BackendlessFile>() {
            public void handleResponse(BackendlessFile res) {//success handling
                final String url = res.getFileURL();
                Log.i("File upload","Successfully saved on "+url);
                //save in Data - messages with given file
                Map msg = new HashMap();
                msg.put("content",url);
                msg.put("chatId", MyChatId);
                msg.put("sendUserId", MyUserId);
                msg.put("senderId", usererNickName);
                msg.put("time", time.toString());
                //Toast.makeText(getApplicationContext(),"/n  "+MyChatId+"/n  "+MyUserId+ "/n  "+usererNickName, Toast.LENGTH_LONG).show();

                Backendless.Data.of("Messages").save(msg, new AsyncCallback<Map>() {
                    public void handleResponse(Map res) {
                        Toast.makeText(getApplicationContext()," saveImg", Toast.LENGTH_LONG).show();
                        validImageUrl(url);
                        sendNtf();
                        //msg - successfully saved
                       // sendToChat(url);
                    }
                    public void handleFault(BackendlessFault e) {
                        Toast.makeText(getApplicationContext()," saveImg failed1" , Toast.LENGTH_LONG).show();
                        //failed to save
                    }
                });
            }
            public void handleFault(BackendlessFault e) {//error handling
                Log.e("File upload","FAILED with error "+e.getCode());
                Toast.makeText(getApplicationContext()," saveImg failed2", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void tempFile(){
        String someName="IMG_"+Math.random()+System.currentTimeMillis()+"USERNAME.jpg";
        String fileName="IMG_"+System.currentTimeMillis()+".jpg";
        imgFile=new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),fileName);
        Toast.makeText(getApplicationContext()," tempFile", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        //user has taken picture
        if(resultCode == RESULT_OK && requestCode==CAMERA){
            Bitmap bm;
            //bm = (Bitmap) i.getExtras().get("data");//Bitmap thumbnail
            bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());//full quality img
            img.setImageBitmap(bm);//show to user
            saveImg(img);
            Toast.makeText(getApplicationContext()," onActivityResult", Toast.LENGTH_LONG).show();

        }
    }

    private boolean validImageUrl(String url){
        String baseUrl = "https://developer.backendless.com";
        //example without REGEX
        //return ((url.startsWith(baseUrl) && url.contains(appId)) && url.endsWith(".jpg") || url.endsWith(".png"));
        //example with REGEX
        String validChars="[a-zA-Z.0-9_/\\-]{1,300}";//lower case OR upper case letters OR . OR digits (0-9) OR / OR - OR _
        Toast.makeText(getApplicationContext()," validImageUrl", Toast.LENGTH_LONG).show();
        return url.matches("^"+baseUrl+validChars+appId+validChars+"jpg|png$");

    }

    private void sendNtf() {
        Notification ntf = new Notification.Builder(this)
                .setContentTitle("you have a new message")
                .setSmallIcon(R.drawable.images)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.images))
                .setAutoCancel(true)
                .build();
        ntfMngr.notify(NTF_ONE, ntf);
        Toast.makeText(getApplicationContext()," sendNtf", Toast.LENGTH_LONG).show();
    }
}



