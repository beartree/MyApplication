package com.example.shu7817.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.view.View;
import android.view.View.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



public class ResultActivity extends ActionBarActivity {

    private Context context=ResultActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String result = intent.getStringExtra("results");
        try {
            JSONObject json = new JSONObject(result);
            JSONObject results = json.getJSONObject("searchResult");
            JSONArray resultArray = results.getJSONArray("item");
            for (int i = 0; i < resultArray.length(); i++) {


                String titleId = "title" + i;
                TextView title = (TextView)findViewById(getResources().getIdentifier(titleId, "id", getPackageName()));
                JSONObject item = (JSONObject) resultArray.get(i);
                title.setText(item.getString("title"));

                String priceId = "price" + i;
                TextView price = (TextView)findViewById(getResources().getIdentifier(priceId, "id", getPackageName()));
                Double currentPrice = item.getJSONObject("sellingStatus").getDouble("currentPrice");
                Double shippingCost = item.getJSONObject("shippingInfo").getDouble("shippingServiceCost");

                if (shippingCost == 0.0) {
                    price.setText("Price: $" + currentPrice + " (Free Shipping)");
                } else {
                    price.setText("Price: $" + currentPrice + " (+$" + shippingCost + " Shipping)");
                }

                String imageId = "imageView" + i;
                ImageView imgView = (ImageView) findViewById(getResources().getIdentifier(imageId, "id", getPackageName()));
                String imgURL = item.getString("galleryURL");
                new DownloadImageTask (imgView).execute(imgURL);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
