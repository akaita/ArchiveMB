package com.akaita.fda;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements UpdateDatabaseTask.UpdateDatabaseResponse {
    private final static String URL_1 = "http://i.img.co/data/data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        TempTest.testStoredData();

        try {
            URL url1 = new URL(URL_1);
            showProgressBar(true);
            new UpdateDatabaseTask(this).execute(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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

    @Override
    public void updateDatabaseFinish() {
        showProgressBar(false);
    }

    private void showProgressBar(boolean show) {
        ProgressBar progressBar = ((ProgressBar) findViewById(R.id.progressBar));
        progressBar.setVisibility(show?View.VISIBLE:View.INVISIBLE);
    }
}
