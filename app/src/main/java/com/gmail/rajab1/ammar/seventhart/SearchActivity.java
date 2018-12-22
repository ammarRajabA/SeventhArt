package com.gmail.rajab1.ammar.seventhart;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.Image;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SearchActivity extends AppCompatActivity {

    private JSONObject dbs=null;
    private String configURL=null;
    private String dbExcelURL=null;
    private String masterURL="https://drive.google.com/uc?export=download&id=0B3mAnKL8DobVUV9LYmVZeHUwMlU";
    private ProgressBar spinner;
    private SearchView searchView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ConstraintLayout cl=(ConstraintLayout)findViewById(R.id.mainLayout);
        cl.getBackground().setAlpha(40);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.bringToFront();
        searchView=(SearchView)findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.setQueryHint("Search in Seventh Art");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                Thread findMovieTest=new Thread(new findMovie());
                spinner.setVisibility(View.VISIBLE);
                findMovieTest.start();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setQuery("",false);
                return true;
            }
        });

        listView=(ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String movieName=adapterView.getItemAtPosition(i).toString().split(",")[1]
                        .split("=")[1];
                movieName=movieName.substring(0,movieName.length()-1);
                Log.i("HERE",movieName);
                Log.i("HERE","Trying to get IMDB ID");

                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                boolean online=activeNetworkInfo != null && activeNetworkInfo.isConnected();

                if (!online)
                    Toast.makeText(getBaseContext(), "Please check your Internet connection",
                            Toast.LENGTH_LONG).show();
                else {
                    Intent showIMDB = new Intent(SearchActivity.this, IMDBActivity.class);
                    showIMDB.putExtra("Name", movieName);
                    SearchActivity.this.startActivity(showIMDB);
                }
            }
        });

        ImageView imageFB=(ImageView)findViewById(R.id.imageView);
        imageFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/seventhart.lattakia"));
                startActivity(browserIntent);
            }
        });
    }

    //thread for search/view
    private class findMovie implements Runnable{
        //// tools ////
        ////////////////////////////////////
        // read file from cloud
        private String readFromURL(String urlString){
            URL url = null;
            HttpURLConnection connection=null;
            try {
                url = new URL(urlString);
                connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                BufferedReader inputBuffer=new BufferedReader(new InputStreamReader(input));
                StringBuilder data=new StringBuilder();
                String line;
                while ((line=inputBuffer.readLine())!=null) {
                    data.append(line).append('\n');
                }
                Log.i("HERE", "We've got the data");
                input.close();
                return data.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
        // read from file
        private String readFromFile(String fileName){
            InputStream currentConfig= null;
            try {
                currentConfig = getBaseContext().openFileInput(fileName);
                BufferedReader currentConfigRead=new BufferedReader(new InputStreamReader(currentConfig));
                StringBuilder currentData=new StringBuilder();
                String line;
                while((line=currentConfigRead.readLine())!=null){
                    currentData.append(line).append('\n');
                }
                return currentData.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
        // read from url to file
        private void readFromURLToFile(String urlString,String fileName){
            URL url = null;
            HttpURLConnection connection=null;
            try {
                url = new URL(urlString);
                connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                byte data[] = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                }
                Log.i("HERE", "File seems to be downloaded");
                input.close();
                outputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // check if there's an internet connection
        private boolean networkAvailable(){
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        /////////////////////////////////////////////
        // retrieve the master settings
        private void retrieveSettings(){
            String data=readFromURL(masterURL);
            JSONObject jsonData= null;
            try {
                jsonData = new JSONObject(data);
                configURL=jsonData.getString("configURL");
                dbExcelURL=jsonData.getString("dbExcelURL");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /////////////////////////////////////////////
        private boolean new_version(){
            // download the config file, parse it and check if this version is new
            File file=getBaseContext().getFileStreamPath("config.json");
            if (file.exists()) {
                Log.i("HERE", "Config file is already here, we'll download and compare");
                try {
                    //check the current version
                    String currentData = readFromFile("config.json");
                    JSONObject currentConfigJson = null;
                    currentConfigJson = new JSONObject(currentData.toString());
                    double currentVersion = currentConfigJson.getDouble("version");
                    Log.i("HERE","current version is " + currentData);
                    /////
                    //read the config file from Google Drive
                    String newData = readFromURL(configURL);
                    JSONObject newConfigJson = new JSONObject(newData.toString());
                    double newVersion = newConfigJson.getDouble("version");
                    Log.i("HERE","current version is " + newData);
                    if (newVersion>currentVersion){
                        Log.i("HERE", "We've got a new version");
                        return true;
                    }
                    else{
                        Log.i("HERE", "Same version, you're up-to-date");
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.i("HERE", "this is the first time of download");
                readFromURLToFile(configURL,"config.json");
                return true;
            }
            return true;
        }
        @Override
        public void run() {
            final String fileName = "archive.xls";
            // assuming there's an Internet connection
            if (this.networkAvailable()){
                if (dbExcelURL==null && configURL==null){
                    this.retrieveSettings();
                }
                if (new_version()){
                    readFromURLToFile(dbExcelURL,fileName);
                    readFromURLToFile(configURL,"config.json");
                }
            }
            // no Internet connection then read offline version (try from Data else from Assets)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputStream excelFile = null;
                    try {
                        File file=getBaseContext().getFileStreamPath("archive.xls");
                        if (!file.exists()){
                            AssetManager am = getBaseContext().getAssets();
                            excelFile = am.open("archive.xls");
                            Log.i("HERE","Read from Assets");
                        }
                        else{
                            excelFile = getBaseContext().openFileInput(fileName);
                            Log.i("HERE","Read from Data");
                        }
                    } catch (FileNotFoundException e) {
                        Log.i("HERE", "File not found");
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Workbook wb;
                    try {
                        wb = Workbook.getWorkbook(excelFile);
                        Sheet sheet = wb.getSheet("1-5000");

                        ListView resultMovieName = (ListView) findViewById(R.id.listView);
                        SearchView movieName = (SearchView) findViewById(R.id.searchView);
                        String target = movieName.getQuery().toString();
                        if (target.length() < 4) {
                            Toast.makeText(getBaseContext(), "Please use more characters",
                                    Toast.LENGTH_LONG).show();
                            spinner.setVisibility(View.GONE);
                            return;
                        }

                        Pattern pattern = Pattern.compile("(?i).*(" + target + ").*");
                        String resultID = "";
                        String resultName = "";
                        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
                        int rowStart = 1;
                        Cell cellResult = null;
                        boolean finished = false;
                        while (!finished) {
                            cellResult = sheet.findCell(pattern, 7, rowStart, 9, 999999, false);
                            if (cellResult != null) {
                                Map<String, String> result = new HashMap<String, String>(2);
                                resultID = sheet.getCell(11, cellResult.getRow()).getContents();
                                resultName = sheet.getCell(8, cellResult.getRow()).getContents();
                                result.put("ID", resultID);
                                result.put("Name", resultName);
                                results.add(result);
                                rowStart = cellResult.getRow() + 1;
                            } else {
                                finished = true;
                            }
                        }

                        if (!results.isEmpty()) {
                            SimpleAdapter resultsAdapter = new SimpleAdapter(
                                    getBaseContext(), results,
                                    android.R.layout.simple_list_item_2, new String[]{"Name", "ID"},
                                    new int[]{android.R.id.text1, android.R.id.text2});
                            resultMovieName.setAdapter(resultsAdapter);
                            resultsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getBaseContext(), "Movie not found",
                                    Toast.LENGTH_LONG).show();
                        }


                        //resultMovieName.setText(sheet.getCell(8, sheet.findCell(movieName.getText().toString()).getRow()).getContents());
                        Log.i("HERE", target);
                        excelFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BiffException e) {
                        e.printStackTrace();
                    }
                    Log.i("HERE", "got a workbook");
                    spinner.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            startActivity(new Intent(SearchActivity.this,AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
