package com.example.shu7817.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View;
import android.view.View.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;


public class MainActivity extends ActionBarActivity {
    private static final String[] sortByString={"Best Match","Price: highest first","Price + Shipping: highest first","Price + Shipping:\n" +
            "lowest first"};
    private static final String phpUrl = "http://ebayshopping-env.elasticbeanstalk.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button searchButton = (Button) findViewById(R.id.search);
        final Button clearButton = (Button) findViewById(R.id.clear);

        final EditText keywordText = (EditText) findViewById(R.id.keyword);
        final EditText lowPriceText = (EditText) findViewById(R.id.lowPrice);
        final EditText highPriceText = (EditText) findViewById(R.id.highPrice);
        final TextView alertText = (TextView) findViewById(R.id.alert);
        final Spinner spin = (Spinner) findViewById(R.id.sortedby);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sortByString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        searchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                final String keyword= keywordText.getText().toString();
                final String lowPrice = lowPriceText.getText().toString();
                final String highPrice = highPriceText.getText().toString();
                final String sortby = spin.getSelectedItem().toString().toLowerCase();
                if (keyword.length() == 0 || keyword.equals("")){
                    alertText.setText("Keyword needs to be entered!");
                    return;
                }
                else if(lowPrice.length() != 0 && Float.parseFloat(lowPrice) < 0 ) {
                    alertText.setText("Price from must be Positive number");
                    return;
                }
                else if(highPrice.length() != 0 && Float.parseFloat(highPrice) < 0 ) {
                    alertText.setText("Price to must be Positive number");
                    return;
                }
                else if(lowPrice.length() != 0 && highPrice.length() != 0 && Float.parseFloat(lowPrice) > Float.parseFloat(highPrice) ) {
                    alertText.setText("Price from much be smaller than Price to value!");
                    return;
                }
                else if (keyword.length() != 0){
                    String url = phpUrl + "?keywords="+keyword+"&pricerange1="+lowPrice+"&pricerange2="+highPrice+"&sortBy=BestMatch"+"&resultPerPage=5&pageNum=&maxShippingDays=";
                    String arr[] = new String [1];
                    arr[0] = url;
                    UrlRequest request = new UrlRequest();
                    request.execute(arr);
                    return;

                }
            }
        });

        clearButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                keywordText.setText("");
                lowPriceText.setText("");
                highPriceText.setText("");
                alertText.setText("");
                return;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public static String GET(String url){
        String result = "";
        InputStream content = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            content = response.getEntity().getContent();
            result= convertInputStreamToString(content);
        } catch (Exception e) {
            Log.d("InputStream", e.getMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public class UrlRequest extends AsyncTask <String, Integer, String> {
        @Override
        protected String doInBackground(String... link) {
            return GET(link[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent();
            intent.putExtra("results", result);
            intent.setClass(MainActivity.this,ResultActivity.class);
            startActivity(intent);
            return;
        }
    }
}


