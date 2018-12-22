package com.gmail.rajab1.ammar.seventhart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IMDBActivity extends AppCompatActivity {
    private WebView movie;
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_imdb);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        movie = (WebView) findViewById(R.id.webViewIMDB);
        movie.setWebViewClient(new WebViewClient());

        spinner=(ProgressBar)findViewById(R.id.progressBarIMDB);
        spinner.bringToFront();
        spinner.setVisibility(View.VISIBLE);

        Intent intent=getIntent();
        String movieName=intent.getStringExtra("Name");
        Thread getID=new Thread(new getFromIMDB(movieName));
        getID.start();
    }

    private class getFromIMDB implements Runnable{
        private String movieName;
        getFromIMDB(String m){
            this.movieName=m;
        }
        @Override
        public void run(){
            try {
                Document imdb_search= Jsoup.connect("http://www.imdb.com/find?q=" + this.movieName)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                        .timeout(60000).get();
                //Elements e=imdb_search.getElementsByClass("findList");
                Element el=imdb_search.select("table.findList a[href]").get(1);
                Log.i("HERE","Link is :");
                Log.i("HERE",el.attr("href"));
                final String IMDB_ID=el.attr("href");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        movie.loadUrl("http://m.imdb.com"+IMDB_ID);
                        spinner.setVisibility(View.GONE);
                    }
                });
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        spinner.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Please check your Internet connection",
                                Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivityForResult(myIntent, 0);
                    }
                });
                Log.i("HERE",e.toString());
                e.printStackTrace();
            }
        }
    }

}
