package com.example.ruth.messageproject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by ruth on 21/06/2017.
 */

public class ChattersActivity extends Activity{

    ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatters);

        listView = (ListView)findViewById(R.id.listChatters);

    }
}
