package com.example.desy.imagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.desy.imagesearch.adapters.EndlessScrollListener;
import com.example.desy.imagesearch.adapters.ImageResultsAdapter;
import com.example.desy.imagesearch.models.ImageResult;
import com.example.desy.imagesearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {
    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    //private static int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        // Creates the data source
        imageResults = new ArrayList<ImageResult>();
        // Attaches the data source to an adapter
        aImageResults = new ImageResultsAdapter(this, imageResults);
        // Link the adapter to the adapterview (gridview)
        gvResults.setAdapter(aImageResults);

    }

    private void setupViews() {
        etQuery = (EditText)findViewById(R.id.etQuery);
        gvResults = (GridView)findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Launch the image display activity
                //Creating on intent
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                //get the image result to display
                ImageResult result = imageResults.get(position);
                //Pass image result into the intent
                i.putExtra("result", result); //need to either be serializable or parcelable
                //Launch the new intent
                startActivity(i);
            }
        });


        gvResults.setOnScrollListener(new EndlessScrollListener() {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                page += 8;
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                super.onScrollStateChanged(view, scrollState);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    //linked the button
    public void onImageSearch(View view) {
        String query = etQuery.getText().toString();
        Toast.makeText(this,"Search for: " + query, Toast.LENGTH_SHORT).show();

        AsyncHttpClient client = new AsyncHttpClient();
        //view-source:https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query +"&rsz=8";
        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    imageResults.clear(); //clear the existing image from the array
                    // When you make to the adapter, it does modify the underlying data
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("INFO", imageResults.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ONFAILURE", "not Here!");
            }
        });

    }


    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter

        String query = etQuery.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();


        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8" + "&start=" + offset;
        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    //imageResults.clear(); //clear the existing image from the array
                    // When you make to the adapter, it does modify the underlying data
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });


    }


}
