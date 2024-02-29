package com.example.myapplication;



import android.util.Log;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


class Parse{
    private static final String TAG = "MyParseClass";
    public static Map<String, String> loginAndGetCookies(String login, String password) {

        Map<String, String> cookies = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        headers.put("exampleHeader", "value");


        try {
            Connection.Response response = Jsoup.connect("https://isu.uust.ru/login/")
                    .headers(headers)
                    .method(Connection.Method.GET)
                    .execute();
            Log.i(TAG, "Trying to get cookiesAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            Document document = Jsoup.connect("https://isu.uust.ru/login/")
                    .headers(headers)
                    .cookies(response.cookies())
                    .get();



            Element element = document.selectFirst("input");
            String value = element != null ? element.val() : "";

            data.put("form_num", value);
            data.put("login", login);
            data.put("password", password);

            Connection.Response second_response = Jsoup.connect("https://isu.uust.ru")
                    .headers(headers)
                    .data(data)
                    .cookies(response.cookies())
                    .method(Connection.Method.POST)
                    .followRedirects(false)
                    .execute();

            System.out.println("Status code before redirect: " + second_response.statusCode());

            if (second_response.statusCode() == 302) {
                Connection.Response third_response = Jsoup.connect("https://isu.uust.ru")
                        .headers(headers)
                        .data(data)
                        .cookies(response.cookies())
                        .method(Connection.Method.POST)
                        .execute();



                cookies = third_response.cookies();
                return cookies;
            } else {
                return null; // в Java обычно используется null вместо false
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // в Java обычно используется null вместо false
    }

    public static void saveAndPrintHtml(Map<String, String> headers, Map<String, String> cookies, String url) {
        try {
            Connection.Response response = Jsoup.connect(url)
                    .headers(headers)
                    .cookies(cookies)
                    .method(Connection.Method.GET)
                    .execute();

            Document document = Jsoup.parse(response.body());

            PrintWriter writer = new PrintWriter(new FileWriter("output.html"));
            writer.print(document.toString());
            writer.close();

            System.out.println("HTML страница успешно сохранена в output.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
//public class Parse {
//    public static void main(String[] args) {
//        String login = "uralutyagan@yandex.ru";
//        String password = "Qwerty1";
//
//        Map<String, String> cookies = Main.loginAndGetCookies(login, password);
//        Map<String, String> headers = new HashMap<>();
//        headers.put("exampleHeader", "value");
//
//        Main.saveAndPrintHtml(headers, cookies, "https://isu.uust.ru/student_points_view/");
//    }
//}
