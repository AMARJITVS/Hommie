package com.cloud.amar.home_automation;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    ImageButton iv;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String key;
    String on="1",off="0",value,cvalue;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isOnline()) {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Room");

            iv = findViewById(R.id.btnSwitch);

            myRef.child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cvalue = dataSnapshot.getValue(String.class);
                    Log.d("----------",""+cvalue);
                    if(cvalue.equals("1"))
                    {
                        Log.d("----------","ON"+cvalue);
                        iv.setImageResource(R.drawable.btn_switch_on);
                        iv.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Log.d("----------","OFF"+cvalue);
                        iv.setImageResource(R.drawable.btn_switch_off);
                        iv.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Error occured!!!",Toast.LENGTH_SHORT).show();
                }
            });

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myRef.child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            value = dataSnapshot.getValue(String.class);
                            Log.d("***************",""+value);
                            key = myRef.push().getKey();
                            if(value.equals("1"))
                            {
                                Log.d("***************","1--OFF--"+value);
                                playSound(false);
                                toggleButtonImage(false);
                                myRef.child("Status").setValue(off);
                                Toast.makeText(getApplicationContext(), "Switched OFF", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Log.d("***************","0--ON--"+value);
                                playSound(true);
                                toggleButtonImage(true);
                                myRef.child("Status").setValue(on);
                                Toast.makeText(getApplicationContext(), "Switched ON", Toast.LENGTH_SHORT).show();

                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Error occured!!!",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });


        }
        else
        {
            try {
                AlertDialog alert = new AlertDialog.Builder(this,R.style.MyDialogTheme).create();
                alert.setTitle("Info");
                alert.setCancelable(false);
                alert.setIcon(android.R.drawable.ic_dialog_alert);
                alert.setMessage(Html.fromHtml("<font color='#DFDEDE'>Internet not available, Check your internet connectivity and try again !!!</font>"));
                alert.setButton(Dialog.BUTTON_POSITIVE, Html.fromHtml("<font color='#DFDEDE'>OK</font>"),new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alert.show();
            }
            catch(Exception e)
            {
                Log.d("-----------", "Show Dialog: "+e.getMessage());
            }
        }

    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    private void toggleButtonImage(boolean a){
        if(a){
            iv.setImageResource(R.drawable.btn_switch_on);
        }else{
            iv.setImageResource(R.drawable.btn_switch_off);
        }
    }
    private void playSound(boolean a){
        if(a){
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
        }else{
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }
}