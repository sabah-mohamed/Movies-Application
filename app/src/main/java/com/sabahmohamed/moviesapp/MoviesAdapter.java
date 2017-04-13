package com.sabahmohamed.moviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ba7 on 12/3/2016.
 */

public class MoviesAdapter extends BaseAdapter {

    ArrayList<MoviesItem> list;
    Context context;

    public MoviesAdapter(Context context, ArrayList<MoviesItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView img;

        if (convertView == null){
            View v = inflater.inflate(R.layout.movie_grid_item,parent, false);
            img = (ImageView) v.findViewById(R.id.moviePoster);
            v.setTag(img);
            convertView = v;
        }
        else {
            img = (ImageView) convertView.getTag();
        }

        if(list.get(position) != null){
            Picasso.with(context).load(list.get(position).getImageResource()).into(img);
        }


        return convertView;
    }


}