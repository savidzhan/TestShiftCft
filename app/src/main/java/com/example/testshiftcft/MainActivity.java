package com.example.testshiftcft;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
    ArrayAdapter<String> listAdapter;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;

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
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infoDataList);
        binding.infoList.setAdapter(listAdapter);
        binding.infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "Clicked - " + infoDataList.get(i), Toast.LENGTH_SHORT).show();
            }
        });

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
                    Log.d("TAG", data);
                    JSONObject jsonObject  = new JSONObject(data);
                    JSONObject infoList = jsonObject.getJSONObject("Valute");

                    infoDataList.clear();

                    for (int i = 0; i < infoList.names().length(); i++) {

                        String str = "";
                        JSONObject valute = infoList.getJSONObject(infoList.names().getString(i));
                        str = valute.getString("Name") + " " + valute.getString("Value");
                        infoDataList.add(str);
                        
                    }

//                    String keys = infoList.keys().toString();
//                    ArrayL<String> keys = jsonObject.keys();
//
//                    for(String key : keys){
//
//                    }
//
////                    Iterator<String> keys = jsonObject.keys();
////
////                    while(keys.hasNext()) {
////                        String key = keys.next();
////                        if (jsonObject.get(key) instanceof JSONObject) {
////                            // do something with jsonObject here
////                        }
////                    }



                } else {
                    Log.d("TAG", "Нихуя нет!!!!");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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