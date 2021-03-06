package com.example.socketclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    private Handler mHandler;
    Socket socket;
    private String ip = "ws://echo.websocket.org/";
    private int port = 9999;
    EditText et;
    TextView msgTV;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        et = (EditText) findViewById(R.id.EditText01);
        Button btn = (Button) findViewById(R.id.Button01);
        Button btnCon = (Button)findViewById(R.id.button02);
        final TextView tv = (TextView) findViewById(R.id.TextView01);
        msgTV = (TextView)findViewById(R.id.chatTV);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (et.getText().toString() != null || !et.getText().toString().equals("")) {
                    ConnectThread th =new ConnectThread();
                    th.start();
                }
            }
        });
    }

    class ConnectThread extends Thread{
        public void run(){
            try{
                //?????? ??????
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket =  new Socket(serverAddr,port);
                //?????? ?????????
                String sndMsg = et.getText().toString();
                Log.d("=============", sndMsg);
                //????????? ??????`
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                out.println(sndMsg);
                //????????? ??????
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String read = input.readLine();
                //?????? ??????
                mHandler.post(new msgUpdate(read));
                Log.d("=============", read);
                socket.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    // ?????? ????????? ??????
    class msgUpdate implements Runnable {
        private String msg;
        public msgUpdate(String str) {
            this.msg = str;
        }
        public void run() {
            msgTV.setText(msgTV.getText().toString() + msg + "\n");
        }
    };
}