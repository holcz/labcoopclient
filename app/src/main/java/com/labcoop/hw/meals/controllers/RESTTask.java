package com.labcoop.hw.meals.controllers;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Created by holcz on 15/03/16.
 */
public class RESTTask extends AsyncTask<String, Integer, String> {

    private final static String ERROR_PREFIX = "error: ";

    protected RESTTaskCallback callback;

    public RESTTask(RESTTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String httpMethod, url, data;
        try{
            httpMethod = params[0].toUpperCase();
            switch (httpMethod){
                case "GET":
                    url = params[1];
                    return getHttp(url);
                case "POST": case "PUT":
                    url = params[1];
                    data = params[2];
                    return postOrPut(httpMethod, url, data);
                case "DELETE":
                    url = params[1];
                    return  deleteHttp(url);
                default:
            }
        }catch (IOException e) {
            Log.e("restTask",e.getMessage(), e);
            String err = ERROR_PREFIX;
            //TODO: should get "Unauthorized" message. Debug it
            if (e.getMessage().contains("Too many follow-up requests: 21")){
                err += "Authentication failed";
            }else{
                err += e.getMessage();
            }
            return err;
        }catch (Exception e){
            Log.e("restTask",e.getMessage(), e);
            return ERROR_PREFIX + e.getMessage();
        }
        return "error: unknown";
    }

    protected String postOrPut(String httpMethod, String serverUrl, String data) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = connect(serverUrl);
            conn.setDoOutput(true);
            conn.setRequestMethod(httpMethod);
            writeOutputStream(conn.getOutputStream(), data);
            if (conn.getResponseCode() != 200){
                Log.d("restTask", "The response is: " + conn.getResponseMessage());
                //TODO: Proper handling with exception
            }
            String dataReceived = readInputStream(conn.getInputStream());
            return dataReceived;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    protected String deleteHttp(String serverUrl) throws IOException{
        HttpURLConnection conn = null;
        try {
            conn = connect(serverUrl);
            conn.setRequestMethod("DELETE");
            conn.connect();
            if (conn.getResponseCode() != 200){
                Log.d("restTask", "The response is: " + conn.getResponseMessage());
                //TODO: Proper handling with exception
            }
            String dataReceived = readInputStream(conn.getInputStream());
            return dataReceived;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    protected String getHttp(String serverUrl) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = connect(serverUrl);
            conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode() != 200){
                Log.d("restTask", "The response is: " + conn.getResponseMessage());
                //TODO: Proper handling with exception
            }
            String dataReceived = readInputStream(conn.getInputStream());
            return dataReceived;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.startsWith(ERROR_PREFIX)){
            callback.onDataReceived(null,s.substring(ERROR_PREFIX.length()));
        }else{
            callback.onDataReceived(s,null);
        }

        super.onPostExecute(s);
    }

    protected HttpURLConnection connect(String serverUrl) throws IOException{
        URL url = new URL(serverUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(4000);
        conn.setConnectTimeout(5000);
        return conn;
    }

    protected void writeOutputStream(OutputStream os, String data) throws IOException {
        try{
            BufferedOutputStream buf = new BufferedOutputStream(os);
            buf.write(data.getBytes());
            buf.flush();
        }finally {
            if (os != null){
                os.close();
            }
        }
    }

    protected String readInputStream(InputStream is) throws IOException {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }finally {
            if (is != null){
                is.close();
            }
        }
    }
}
