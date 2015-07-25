package com.aroundme.messageEndpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.Message;

import com.aroundme.EndpointApiCreator;
import com.aroundme.MessagesListAdapter;
import com.aroundme.Msg;
import com.aroundme.R;

import com.aroundme.SignUp;
import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

public class Chat extends Activity {

    private ArrayList<String> arrayListToDo;
    private ArrayAdapter<String> arrayAdapterToDo;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_chat);
       // Intent intent = getIntent();

        arrayListToDo = new ArrayList<String>();
        arrayAdapterToDo = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayListToDo);
        final ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
        listViewToDo.setAdapter(arrayAdapterToDo);
        TextView name = (TextView) findViewById(R.id.textView);

        final EditText sendEt = (EditText)findViewById(R.id.sendEt);
        Button sendBtn = (Button)findViewById(R.id.sendBtn);
        registerForContextMenu(listViewToDo);

        SharedPreferences sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        final String user_email = sharedPref.getString("email","");

        final ArrayList<Msg> listMessage = new ArrayList<Msg>();


        //create a list view adapter and throw inside all the messages with the sender as prefix
        Intent chatIntent = getIntent();

        final String item = chatIntent.getStringExtra("item");
        final String titleName = chatIntent.getStringExtra("name");

        name.setText(titleName);

        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Log.d("newMsg","chat run");

                                ChatDBHandler cdb = new ChatDBHandler(getApplicationContext());

                               // cdb.open(ChatDBHandler.OPEN_DB_FOR.WRITE);
                                cdb.open(ChatDBHandler.OPEN_DB_FOR.READ);

                                arrayAdapterToDo.clear();

                                ArrayList<Message> msgs_from_db = null;
                                try {
                                    msgs_from_db = cdb.getMessages(item);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Message msg;

                                if ( (msgs_from_db.size()) > (listMessage.size())){

                                    listMessage.clear();

                                if (msgs_from_db == null)
                                    Toast.makeText(getApplicationContext(), "No msg got from db..", Toast.LENGTH_LONG).show();
                                else {
                                    for (int i = 0; i < msgs_from_db.size(); i++) {
                                        msg = msgs_from_db.get(i);

                                        //1 shared prefences
                                        //0 from


                                        if (msg.getTo().equals("1")) {            // message is sent from my user
                                            listMessage.add(new Msg(user_email, "unkown", msg.getContnet(), "", "1"));
                                        } else {
                                            listMessage.add(new Msg(item, "unkown", msg.getContnet(), "", "0"));
                                        }
                                    }
                                }
                                MessagesListAdapter adapter = new MessagesListAdapter(getApplicationContext(), listMessage);

                                listViewToDo.setAdapter(adapter);
                                listViewToDo.setSelection(adapter.getCount() - 1);

                                cdb.close();
                            }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();


        sendBtn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){


                        final String msg = sendEt.getText().toString();
                        Message m = new Message();
                        m.setContnet(msg);
                        m.setTo(item);//** who u send to: yaron@gmail.com | nezer14@gmail.com **
                        m.setFrom(user_email);
                        m.setTimestamp(new DateTime(new Date()));
                        sendMessage(m);
                        sendEt.setText("");

                        //save msg to db and to adapter
                        ChatDBHandler cdb = new ChatDBHandler(getApplication());
                        cdb.open(ChatDBHandler.OPEN_DB_FOR.WRITE);


                        cdb.saveUser(item,"unknown");//** who u send to: yaron@gmail.com | nezer14@gmail.com **
                        cdb.saveMessage(item,msg, ChatDBHandler.WHO_SEND.ME); //** who u send to: yaron@gmail.com | nezer14@gmail.com **
                        cdb.close();

                        arrayAdapterToDo.add(user_email +" : "+msg);

                    }
                }
        );
    }

    private void sendMessage(final Message m)
    {
        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Aroundmeapi api =  EndpointApiCreator.getApi(Aroundmeapi.class);
                    api.sendMessage(m).execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }


        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause(){
        super.onPause();
       t.interrupt();
    };


}
