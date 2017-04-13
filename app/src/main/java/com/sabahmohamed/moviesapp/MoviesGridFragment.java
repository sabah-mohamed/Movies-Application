package com.sabahmohamed.moviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ba7 on 12/3/2016.
 */

public class MoviesGridFragment extends Fragment {

    ArrayList<MoviesItem> moviesItemArrayList;
    GridView moviesGridView;
    MoviesAdapter moviesAdapter;
    SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.movies_grid, container, false);
        setHasOptionsMenu(true);

        moviesGridView = (GridView) view.findViewById(R.id.moviesGridView);
        fetchTask("https://api.themoviedb.org/3/movie/" + "popular?" + "api_key=c4a5984bfe83be4b58ec7672605a211f");

        sharedPreferences = getActivity().getSharedPreferences("FavMovies", Context.MODE_PRIVATE);

       // String result = new FetchMoviesTask().execute("https://api.themoviedb.org/3/movie/" + "popular?" + "api_key=c4a5984bfe83be4b58ec7672605a211f").get();
        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

                movieDetailsFragment.initMovieItem(moviesItemArrayList.get(position));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    fragmentTransaction.replace(R.id.detailsFrameLayout, movieDetailsFragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                    Toast.makeText(getActivity(), moviesItemArrayList.get(position).getTitle(), Toast.LENGTH_LONG).show();
                }
                else {

                    fragmentTransaction.replace(R.id.gridFrameLayout, movieDetailsFragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    Toast.makeText(getActivity(), moviesItemArrayList.get(position).getTitle(), Toast.LENGTH_LONG).show();
                }

            }
        });
        return view;
    }

    public void fetchTask(String url){
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        try {

            String result = fetchMoviesTask.execute(url).get();
            Log.d("moviesResp", result);
            parsJSON(result);
            Log.i("Movies of URL ", result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void parsJSON(String jsonResponse){
        moviesItemArrayList = new ArrayList<MoviesItem>();
        try {
            // Extract JSON array from the response
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray resultsArr = jsonObject.getJSONArray("results");
            System.out.println(jsonObject.length());
            // If no of array elements is not zero
            if(resultsArr.length() != 0){

                Log.d("resultsArray", resultsArr.toString());
                // Loop through each array element, get JSON object
                for (int i = 0; i < resultsArr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) resultsArr.get(i);

                    moviesItemArrayList.add(
                            new MoviesItem(obj.get("id").toString(),
                                    obj.get("original_title").toString(),
                                    obj.get("overview").toString(),
                                    obj.get("release_date").toString().substring(0, 4),
                                    obj.get("vote_average").toString(),
                                    "https://image.tmdb.org/t/p/w185" + obj.get("poster_path").toString()));

                }

                moviesAdapter = new MoviesAdapter(getActivity(), moviesItemArrayList);
                moviesGridView.setAdapter(moviesAdapter);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getFavDetails(){
        Set<String> favSet = sharedPreferences.getStringSet("favs", new HashSet<String>());

        moviesItemArrayList = new ArrayList<MoviesItem>();
        for(String movieID: favSet){
            Log.d("FavMovieResp", "https://api.themoviedb.org/3/movie/" + movieID + "?api_key=c4a5984bfe83be4b58ec7672605a211f");
            try{
                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("api_key", "c4a5984bfe83be4b58ec7672605a211f");

                asyncHttpClient.get("https://api.themoviedb.org/3/movie/" + movieID , params, new JsonHttpResponseHandler(){


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject obj) {
                        super.onSuccess(statusCode, headers, obj);

                        try {
                            moviesItemArrayList.add(
                                    new MoviesItem(obj.get("id").toString(),
                                            obj.get("original_title").toString(),
                                            obj.get("overview").toString(),
                                            obj.get("release_date").toString().substring(0, 4),
                                            obj.get("vote_average").toString(),
                                            "https://image.tmdb.org/t/p/w185" + obj.get("poster_path").toString()));
                            Log.d("MovieAdded", obj.get("id").toString()+ " " +
                                    obj.get("original_title").toString()+ " " +
                                    obj.get("overview").toString()+ " " +
                                    obj.get("release_date").toString().substring(0, 4)+ " " +
                                    obj.get("vote_average").toString()+ " " +
                                    "https://image.tmdb.org/t/p/w185" + obj.get("poster_path").toString());
                            moviesAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("jsonRespTrailers", obj.toString());

                    }

                });
            }catch (Exception e){

            }
        }

        moviesAdapter = new MoviesAdapter(getActivity(), moviesItemArrayList);
        moviesGridView.setAdapter(moviesAdapter);
        Log.d("adaoterSet", "yesISA");
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.popular){
            fetchTask("https://api.themoviedb.org/3/movie/" + "popular?" + "api_key=c4a5984bfe83be4b58ec7672605a211f");
            return true;
        }
        else if(item.getItemId() == R.id.topRated){
            fetchTask("https://api.themoviedb.org/3/movie/" + "top_rated?" + "api_key=c4a5984bfe83be4b58ec7672605a211f");
            return true;
        }
        else if(item.getItemId() == R.id.favorite){
            getFavDetails();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }

    }
}
