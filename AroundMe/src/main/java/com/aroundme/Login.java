package com.aroundme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.enhanced_cable_88320.aroundmeapi.Aroundmeapi;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMeCollection;
import com.aroundme.messageEndpoint.Chat;
import com.aroundme.messageEndpoint.ChatDBHandler;
import com.aroundme.messageEndpoint.RowUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Login extends Activity {
    public final static String EXTRA_MESSAGE 	= "MESSAGE";
    private ListView lv;
    ArrayAdapter<String>arrayAdapter;
    List<UserAroundMe> usersList;

    public  ArrayList<RowUser> CustomListViewValuesArr = new ArrayList<RowUser>();

    private ListView 	list;
    private UserListAdapter adapter;

    String user_email;
    int usersListSize;
    int newMsgCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Button logOutBtn = (Button) findViewById(R.id.logOutBtn);
        Button mapBtn = (Button) findViewById(R.id.mapButton);

        lv = (ListView) findViewById(R.id.usersLv);
        list = (ListView)findViewById(R.id.usersLv);

        Resources res = getResources();
        adapter = new UserListAdapter(Login.this, CustomListViewValuesArr, res);
        list.setAdapter(adapter);
        List<String> arrList = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrList);
        lv.setAdapter(arrayAdapter);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        user_email = sharedPref.getString("email", "");

        new AsyncTask<Void,Void,Void>()
        {
            @Override
            protected Void doInBackground(Void... params) {
                showUsers(user_email);
                return null;
            }
        }.execute();

        logOutBtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString("email", "");
                        editor.putString("pw", "");
                        editor.apply();


                        AlertDialog.Builder bAlert = new AlertDialog.Builder(Login.this);
                        bAlert.setMessage(" Are you sure? \n All the DATA will be lost");
                        bAlert.setCancelable(true);
                        bAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                final ChatDBHandler cdb = new ChatDBHandler(getApplicationContext());
                                cdb.open(ChatDBHandler.OPEN_DB_FOR.WRITE);
                                cdb.deleteAllMessageTable();
                                cdb.close();

                                Intent curr_intent = new Intent(Login.this, MainActivity.class);
                                startActivity(curr_intent);
                                finish();
                            }
                        });
                        bAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = bAlert.create();
                        alert.show();
                    }

        });

        mapBtn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                      Intent curr_intent = new Intent(Login.this, MapsActivity.class);
                      Login.this.startActivity(curr_intent);

                    }
                }
        );

        newMsgCount = 0;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.d("newMsg", "Thread run");

                if (usersList != null) {

                    int size = usersList.size();
                    if (size != 0) {

                        UserAroundMe[] arr = new UserAroundMe[size + 1];
                        usersList.toArray(arr);

                        final ChatDBHandler cdb = new ChatDBHandler(getApplicationContext());
                        cdb.open(ChatDBHandler.OPEN_DB_FOR.READ);

                        for (int i = 0; i < (usersListSize-1); i++) {

                            final String email = arr[i].getMail();
                            int unReadmsgCount = cdb.getUnreadMessages(email);

                            if (unReadmsgCount > 0) {
                                Log.d("newMsg", String.valueOf(unReadmsgCount));
                                for (int j = 0; j < (usersListSize-1); j++) {

                                   if (CustomListViewValuesArr.get(j).getTitle().equals(arr[i].getDisplayName())) {
                                       RowUser r = CustomListViewValuesArr.get(j);

                                       if(r.getnewMsg().equals("")){r.setnewMsg("0");}
                                       if (Integer.valueOf(r.getnewMsg())==unReadmsgCount) {}
                                       else{
                                           r.setnewMsg(String.valueOf(unReadmsgCount));
                                           CustomListViewValuesArr.remove(j);
                                           CustomListViewValuesArr.add(0, r);

                                           runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                                                   //stuff that updates ui
                                                   adapter.notifyDataSetChanged();
                                               }
                                           });
                                       }
                                   }
                                }
                            }
                            if(unReadmsgCount==0){
                                Log.d("newMsg", String.valueOf(unReadmsgCount));
                                for (int j = 0; j < (usersListSize-1); j++) {

                                    if (CustomListViewValuesArr.get(j).getTitle().equals(arr[i].getDisplayName())){
                                        CustomListViewValuesArr.get(j).setnewMsg("");

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //stuff that updates ui
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                        cdb.close();
                    }
                }
            }
        }, 0, 3000);


    }

    //open the wanted task
    public void onItemClick(int Pos)
    {
        int size = usersList.size();

        UserAroundMe[] arr = new UserAroundMe[size + 1];

        usersList.toArray(arr);

        for (int i = 0; i < size; i++) {
            if (arr[i].getDisplayName() == CustomListViewValuesArr.get(Pos).getTitle()) {

                String mail = arr[i].getMail();
                String name = arr[i].getDisplayName();

                RowUser tempRow = (RowUser) CustomListViewValuesArr.get(Pos);
                Intent intent = new Intent(this, Chat.class);

                intent.putExtra(EXTRA_MESSAGE, Integer.toString(tempRow.getId()));
                intent.putExtra("item", mail);
                intent.putExtra("name", name);

                startActivity(intent);
            }
        }
    }

    public void showUsers(String user_email){
        try {

            CustomListViewValuesArr.clear();
            final ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

            Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec0);
            Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec1);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec2);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec3);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec4);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec5);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec6);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ec7);
            bMapScaled = Bitmap.createScaledBitmap(bMap, 70, 70, true);
            bitmapArray.add(bMapScaled);


            Aroundmeapi api = EndpointApiCreator.getApi(Aroundmeapi.class);

            UserAroundMeCollection a;
            a = api.getAllUsers(user_email).execute();

           usersList = a.getItems();

           usersListSize = usersList.size();

            UserAroundMe[] arr = new UserAroundMe[usersListSize+1];
            usersList.toArray(arr);

            final ChatDBHandler cdb = new ChatDBHandler(getApplicationContext());
            cdb.open(ChatDBHandler.OPEN_DB_FOR.READ);

            for (int i=0 ; i<usersListSize ; i++) {

                final String name = arr[i].getDisplayName();
                final String email = arr[i].getMail();

                if (email.equals(user_email)) {}

              else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            int unReadmsgCount = cdb.getUnreadMessages(email);

                            Random rand = new Random();
                            int i = rand.nextInt(7);

                            RowUser r = new RowUser();
                            r.setTitle(name);
                            r.setIcon(bitmapArray.get(i));

                            if (unReadmsgCount != 0) {
                                r.setnewMsg(String.valueOf(unReadmsgCount));
                                CustomListViewValuesArr.add(0, r);
                            } else {
                                r.setnewMsg("");
                                CustomListViewValuesArr.add(r);
                            }
                            list.setAdapter(adapter);
                        }
                    });
                }
            }
        }catch(Exception e){

            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
