package com.example.ruth.messageproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ruth.messageproject.R;

import java.util.ArrayList;

/**
 * Created by ruth on 20/06/2017.
 */

public class ListAdapter extends BaseAdapter {
    final ArrayList<String> names;
    final Context context;

    public ListAdapter(ArrayList <String> names,  Context context) {
        this.names = names;
        this.context = context;
    }

    @Override
    public int getCount() {
        return names.size();
    }
    @Override
    public String getItem(int i) {
        return names.get(i).toString();
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       if(view==null)view= LayoutInflater.from(context).inflate(R.layout.chats_view,null);
        TextView chat = (TextView)view.findViewById(R.id.chat);
        chat.setText(getItem(i));
        return view;
    }


}
