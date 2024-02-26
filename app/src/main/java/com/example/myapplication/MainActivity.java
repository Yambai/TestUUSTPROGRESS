package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if  (!isConnected()){
            Toast.makeText(MainActivity.this, "Отсутствует интернет", Toast.LENGTH_SHORT).show();
        }

        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.pass_word);
        MaterialButton login_btn = (MaterialButton) findViewById(R.id.login_btn);
        MaterialButton forgot_btn = (MaterialButton) findViewById(R.id.forgotPassword);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
                    Toast.makeText(MainActivity.this, "URA", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "FUUUUU", Toast.LENGTH_SHORT).show();

                }
            }
        });

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    String url = "https://isu.uust.ru/re_password/";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

// Устанавливаем пакет для Google Chrome
                    browserIntent.setPackage("com.android.chrome");

                    try {
                        startActivity(browserIntent);
                    } catch (ActivityNotFoundException ex) {
                        // Chrome браузер не установлен
                        browserIntent.setPackage(null);
                        startActivity(browserIntent);
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Отсутствует интернет", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null){
            return networkInfo.isConnected();
        }else return false;


    }





















}

