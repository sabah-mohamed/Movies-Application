package com.sabahmohamed.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ba7 on 12/4/16.
 */

public class MovieDetailsFragment extends Fragment {

    ImageView moviePoster, favBtn;
    TextView movieTitle, movieOverview, movieRating, movieYear;
    ListView trailersLV, reviewsLV;

    ArrayList<String> trailersArrayList;
    LinkedHashMap<String, String> trailersHashMap;
    String[][]reviewsArrayList;

    SharedPreferences sharedPreferences;
    Set<String> favMoviesSet;
    SharedPreferences.Editor editor;


    MoviesItem moviesItem = null;

    public MovieDetailsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.movie_item_details, container, false);

        sharedPreferences = getActivity().getSharedPreferences("FavMovies", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        favMoviesSet = sharedPreferences.getStringSet("favs", new HashSet<String>());
        moviePoster = (ImageView) view.findViewById(R.id.moviePoster);
        favBtn = (ImageView) view.findViewById(R.id.movieFav);
        movieTitle = (TextView) view.findViewById(R.id.movieTitle);
        movieOverview = (TextView) view.findViewById(R.id.movieOverview);
        movieRating = (TextView) view.findViewById(R.id.movieRating);
        movieYear = (TextView) view.findViewById(R.id.movieYear);

        trailersLV = (ListView) view.findViewById(R.id.trailersLV);
        reviewsLV = (ListView) view.findViewById(R.id.reviewsLV);

        getTrailers();
        getReviews();


        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(favMoviesSet.contains(moviesItem.getId())){
                    favBtn.setImageResource(R.mipmap.ic_star_not);
                    favMoviesSet.remove(moviesItem.getId());
                    editor.putStringSet("favs", favMoviesSet);
                    editor.apply();
                }
                else {
                    favBtn.setImageResource(R.mipmap.ic_star);
                    favMoviesSet.add(moviesItem.getId());
                    editor.putStringSet("favs", favMoviesSet);
                    editor.apply();
                }
            }
        });
        trailersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + ((trailersHashMap.keySet().toArray()) [position] )));
                getActivity().startActivity(intent);
            }
        });

        reviewsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(reviewsArrayList[position][2]));
                getActivity().startActivity(intent);
            }
        });

        initMovieDetails(moviesItem);
        return view;
    }

    public void initMovieItem(MoviesItem moviesItem){
        this.moviesItem = moviesItem;
    }

    public void initMovieDetails(MoviesItem moviesItem){
        Picasso.with(getActivity()).load(moviesItem.getImageResource()).into(moviePoster);
        movieTitle.setText(moviesItem.getTitle());
        movieOverview.setText(moviesItem.getDescription());
        movieRating.setText(moviesItem.getRating());
        movieYear.setText(moviesItem.getYear());

        if(favMoviesSet.contains(moviesItem.getId())){
            favBtn.setImageResource(R.mipmap.ic_star);
        }
        else {
            favBtn.setImageResource(R.mipmap.ic_star_not);
        }
    }

    public void getTrailers(){
        try{
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("api_key", "c4a5984bfe83be4b58ec7672605a211f");
            asyncHttpClient.get("https://api.themoviedb.org/3/movie/" + moviesItem.getId() + "/videos", params, new JsonHttpResponseHandler(){


                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    trailersArrayList = new ArrayList<String>();
                    trailersHashMap = new LinkedHashMap<String, String>();
                    Log.d("jsonRespTrailers", response.toString());

                    try{
                        JSONArray resultsArr = response.getJSONArray("results");
                        System.out.println(response.length());
                        // If no of array elements is not zero
                        if(resultsArr.length() != 0){

                            Log.d("resultsArray", resultsArr.toString());
                            // Loop through each array element, get JSON object
                            for (int i = 0; i < resultsArr.length(); i++) {
                                // Get JSON object
                                JSONObject obj = (JSONObject) resultsArr.get(i);

                                trailersHashMap.put(obj.get("key").toString(), obj.get("name").toString());

                            }

                            trailersLV.setAdapter(new BaseAdapter() {

                                @Override
                                public int getCount() {
                                    return trailersHashMap.size();
                                }

                                @Override
                                public Object getItem(int position) {
                                    return null;
                                }

                                @Override
                                public long getItemId(int position) {
                                    return 0;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = inflater.inflate(R.layout.trailer_item, null);

                                    TextView trailerName = (TextView) v.findViewById(R.id.trailerName);

                                    trailerName.setText(((trailersHashMap.values().toArray())[position]).toString());

                                    return v;
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){

        }
    }

    public void getReviews(){
        try{
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("api_key", "c4a5984bfe83be4b58ec7672605a211f");
            asyncHttpClient.get("https://api.themoviedb.org/3/movie/" + moviesItem.getId() + "/reviews", params, new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    reviewsArrayList = new String[response.length()][3];

                    Log.d("jsonRespTrailers", response.toString());

                    try{
                        JSONArray resultsArr = response.getJSONArray("results");
                        System.out.println(response.length());
                        // If no of array elements is not zero
                        if(resultsArr.length() != 0){

                            Log.d("resultsArray", resultsArr.toString());
                            // Loop through each array element, get JSON object
                            for (int i = 0; i < resultsArr.length(); i++) {
                                // Get JSON object
                                JSONObject obj = (JSONObject) resultsArr.get(i);

                                reviewsArrayList[i][0] = obj.get("author").toString();
                                reviewsArrayList[i][1] = obj.get("content").toString();
                                reviewsArrayList[i][2] = obj.get("url").toString();

                            }

                            reviewsLV.setAdapter(new BaseAdapter() {

                                @Override
                                public int getCount() {
                                    return reviewsArrayList.length;
                                }

                                @Override
                                public Object getItem(int position) {
                                    return null;
                                }

                                @Override
                                public long getItemId(int position) {
                                    return 0;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = inflater.inflate(R.layout.review_item, null);

                                    TextView reviewerName = (TextView) v.findViewById(R.id.reviewerName);
                                    TextView reviewerReview = (TextView) v.findViewById(R.id.reviewerReview);
                                    reviewerName.setText(reviewsArrayList[position][0]);
                                    reviewerReview.setText(reviewsArrayList[position][1]);

                                    return v;
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){

        }
    }

}
