package com.example.desy.imagesearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by desy on 2/23/15.
 */
public class ImageResult implements Serializable {
    private static final long serialVersionUID = 6647300317863881389L;
    public String fullUrl;
    public String thumbUrl;
    public String title;

    //new ImageResult(..raw item json..)
    public ImageResult(JSONObject json) {
        try{
            this.fullUrl = json.getString("url");
            this.thumbUrl = json.getString("tbUrl");
            this.title = json.getString("title");
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    //Take a array of json images and return arraylist of image results
    //ImageResult. romJSONArray([...,...])
    public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new ImageResult(array.getJSONObject(i)));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return results;
    }
}
