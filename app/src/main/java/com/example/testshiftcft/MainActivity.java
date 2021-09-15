package com.example.testshiftcft;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.testshiftcft.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<String> infoDataList;
    ArrayList<String> valuteNames;
    ArrayList<String> valuteIndex;
    ArrayList<Double> values;
    ArrayAdapter<String> listAdapter;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;
    SharedPreferences pos;
    public String fileName = "file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeInfoList();
        binding.fetchDatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new fetchData().start();
            }
        });
    }

    private void initializeInfoList() {

        infoDataList = new ArrayList<String>();
        valuteNames = new ArrayList<String>();
        valuteIndex = new ArrayList<String>();
        values = new ArrayList<Double>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infoDataList);
        binding.infoList.setAdapter(listAdapter);
        binding.infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openConvertDialog(valuteNames.get(i), valuteIndex.get(i), values.get(i));
            }
        });

        pos = getSharedPreferences(fileName, 0);
        String saveStr = pos.getString("pwd", "");
        if(!saveStr.isEmpty()){
            showData(saveStr);

        } else {
            Toast.makeText(MainActivity.this, "You have naver pressed Update", Toast.LENGTH_LONG).show();
        }


    }

    private void openConvertDialog(String valName, String valIndex, Double value) {

        ConvertDialog convertDialog = new ConvertDialog(valName, valIndex, value);
        convertDialog.show(getSupportFragmentManager(), "Converter");

    }

    public void showData(String jsonString){
        try {
            JSONObject jsonObject  = new JSONObject(jsonString);
            JSONObject infoList = jsonObject.getJSONObject("Valute");

            infoDataList.clear();
            valuteIndex.clear();
            values.clear();

            for (int i = 0; i < infoList.names().length(); i++) {

                String str = "";
                JSONObject valute = infoList.getJSONObject(infoList.names().getString(i));
                Double value;
                valuteNames.add(valute.getString("Name"));
                valuteIndex.add(valute.getString("CharCode"));
                value = Double.parseDouble(valute.getString("Value")) / Double.parseDouble(valute.getString("Nominal"));
                str = valute.getString("Name") + " - " + value + "RUB";
                values.add(value);
                infoDataList.add(str);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class fetchData extends Thread{

        String data ="";

        @Override
        public void run() {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Updating data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                }
            });

            try {
                URL url = new URL("https://api.npoint.io/dcf3ac1252c3dde6c584");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                httpURLConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while((line = bufferedReader.readLine()) != null){

                    data = data + line;

                }

                if(!data.isEmpty()){

                    pos = getSharedPreferences(fileName, 0);
                    SharedPreferences.Editor editor = pos.edit();
                    editor.putString("pwd",data);
                    editor.commit();

                    showData(data);

                } else {
                    Log.d("TAG", "Информация отсутствует");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    listAdapter.notifyDataSetChanged();

                }
            });

            super.run();
        }
    }

}