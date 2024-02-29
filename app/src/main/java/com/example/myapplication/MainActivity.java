package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import org.jsoup.*;
import org.jsoup.nodes.*;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyParseClass";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if  (!isConnected()){
            Toast.makeText(MainActivity.this, "Отсутствует интернет", Toast.LENGTH_SHORT).show();
        }

        TextView username = (TextView) findViewById(R.id.username);
        TextView pass_word = (TextView) findViewById(R.id.pass_word);
        MaterialButton login_btn = (MaterialButton) findViewById(R.id.login_btn);
        MaterialButton forgot_btn = (MaterialButton) findViewById(R.id.forgotPassword);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Отключаем кнопку на время выполнения задачи
                login_btn.setEnabled(false);

                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                Callable<Map<String,String>> callable = new Callable<Map<String,String>>() {
                    @Override
                    public Map<String,String> call() throws Exception {
                        // Здесь выполнение вашей асинхронной операции входа
                        Login task = new Login();
                        return (Map<String,String>) task.execute(username.getText().toString(), pass_word.getText().toString()).get();
                    }
                };

                FutureTask<Map<String,String>> future = new FutureTask<Map<String,String>>(callable);
                Thread thread = new Thread(future);
                thread.start();

                try {
                    // Получаем куки после завершения потока
                    Map<String,String> cookies = future.get();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (cookies != null) {
                                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "No valid", Toast.LENGTH_SHORT).show();
                            }



                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    // Обработка исключений, возможно, также стоит включить кнопку обратно здесь
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "No valid2", Toast.LENGTH_SHORT).show();
                            login_btn.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    e.printStackTrace();
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



    public class Login extends AsyncTask<String, Void, Object> {
        private static final String TAG = "MyParseClass";

        @Override
        protected Object doInBackground(String... params) {

            String login = params[0];
            String password = params[1];


            Map<String, String> headers = new HashMap<>();
            headers.put("exampleHeader", "value");

            // Fetch cookies first
            Map<String, String> cookies = loginAndGetCookies(login, password, headers);

            if(cookies == null) { // fetching cookies failed
                return "Login Failed2";
            }

            // Fetch page HTML


            return cookies; // return result of doInBackground method
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if(result instanceof Map) {
                @SuppressWarnings("unchecked") // Добавлено, чтобы подавить предупреждение компилятора о проверке типов
                Map<String, String> cookies = (Map<String, String>) result;
                Log.i(TAG, "Cookies fetched: " + cookies.toString());
            } else if(result instanceof String) {
                // Результат является строкой, значит не удалось получить cookies
                String errorMessage = (String) result;
                Log.e(TAG, "Failed to fetch cookies: " + errorMessage);
            } else {
                // Непредвиденная ошибка, result не String и не Map
                Log.e(TAG, "Unexpected result type.");
            }
        }

        protected Map<String, String> loginAndGetCookies(String login, String password, Map<String, String> headers) {
            Map<String, String> data = new HashMap<>();


            Map<String, String> cookies;
            try {
                Connection.Response response = Jsoup.connect("https://isu.uust.ru/login/")
                        .headers(headers)
                        .method(Connection.Method.GET)
                        .execute();
                Log.e(TAG, "1");
                Document document = Jsoup.connect("https://isu.uust.ru/login/")
                        .headers(headers)
                        .cookies(response.cookies())
                        .get();
                Log.e(TAG, "2");
                Element element = document.selectFirst("input");
                String value = element != null ? element.val() : "";


                data.put("form_num", value);
                data.put("login", login);
                data.put("password", password);
                Log.e(TAG, value+password+login);
                Connection.Response second_response = Jsoup.connect("https://isu.uust.ru")
                        .headers(headers)
                        .data(data)
                        .cookies(response.cookies())
                        .method(Connection.Method.POST)
                        .followRedirects(false)
                        .execute();
                Log.e(TAG, "3");
                String aaa = String.format("%d", second_response.statusCode());
                Log.e(TAG, aaa );
                cookies = second_response.cookies();
                Log.i(TAG, "Cookies fetched: " + cookies.toString());
                if (second_response.statusCode() == 302) {
                    Connection.Response third_response = Jsoup.connect("https://isu.uust.ru")
                            .headers(headers)
                            .data(data)
                            .cookies(response.cookies())
                            .method(Connection.Method.POST)
                            .execute();

                    cookies = third_response.cookies();
                    Log.e(TAG, "4");
                    return cookies;
                }
            } catch (Exception e) {
                Log.e(TAG, "5");
                e.printStackTrace();
            }
            return null;
        }


    }













    boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null){
            return networkInfo.isConnected();
        }else return false;


    }

}

